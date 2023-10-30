package rabbit.discovery.api.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbit.discovery.api.common.utils.JsonUtils;
import rabbit.discovery.api.config.ValueChangeListener;
import rabbit.discovery.api.config.anno.FlexibleValue;
import rabbit.discovery.api.config.exception.InvalidFormatException;
import rabbit.discovery.api.config.exception.UnSupportedTypeException;
import rabbit.flt.common.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rabbit.discovery.api.config.context.InjectType.UPDATE;
import static rabbit.flt.common.utils.StringUtils.isEmpty;

/**
 * 需要动态更新的属性的meta值
 */
public class FlexibleValueMeta {

    protected Logger logger = LoggerFactory.getLogger("flexibleValueMeta");

    /**
     * 匹配"${}"内的配置项
     */
    private static final String REG = "^\\$\\{([^}]*)\\}$";

    protected static final Map<Class<?>, Function> funcMap = new HashMap<>();

    /**
     * 属性类的meta，key是字段名
     */
    protected Map<String, FlexibleValueMeta> childBeanMeta = new ConcurrentHashMap<>();

    // spring bean
    protected Object bean;

    /**
     * 是否允许更新
     */
    private boolean updatable = true;

    /**
     * 配置项和字段的映射
     */
    protected Map<String, List<Field>> propertyNameMap = new HashMap<>();

    /**
     * 默认值
     */
    protected Map<Field, String> defaultValueMap = new HashMap<>();

    /**
     * 当前属性值
     */
    protected Map<Field, String> currentValueMap = new HashMap<>();

    protected ValueChangeListener valueChangeListener;

    public FlexibleValueMeta(Object bean) {
        this(bean, null);
    }

    public FlexibleValueMeta(Object bean, ValueChangeListener valueChangeListener) {
        this.bean = bean;
        this.valueChangeListener = valueChangeListener;
        if (null != bean) {
            resolvePropertyMeta();
        }
    }

    /**
     * 解析动态注解字段，并缓存
     */
    private void resolvePropertyMeta() {
        Class<?> beanClass = bean.getClass();
        while (true) {
            for (Field field : beanClass.getDeclaredFields()) {
                FlexibleValue fv = field.getAnnotation(FlexibleValue.class);
                if (null == fv) {
                    continue;
                }
                String name = readConfigName(fv.value());
                field.setAccessible(true);
                propertyNameMap.computeIfAbsent(name, n -> new ArrayList<>()).add(field);
                String defaultValue = readDefaultValue(fv.value());
                if (null != defaultValue) {
                    defaultValueMap.put(field, defaultValue);
                }
            }
            if (Object.class == beanClass.getSuperclass()) {
                break;
            }
            beanClass = beanClass.getSuperclass();
        }
    }

    /**
     * 解析bean的所有字段
     *
     * @param prefix 配置的前缀
     */
    public void resolvePropertyMeta(String prefix) {
        resolvePropertyMeta(prefix, bean.getClass());
    }

    /**
     * 解析beanClz的所有字段
     *
     * @param prefix  配置的前缀
     * @param beanClz
     */
    protected void resolvePropertyMeta(String prefix, Class<?> beanClz) {
        List<Class<?>> ignore = Arrays.asList(Byte.class, byte.class, List.class, Set.class, Map.class);
        if (funcMap.containsKey(beanClz) || ignore.contains(beanClz)) {
            return;
        }
        if (0 != ignore.stream().filter(c -> c.isAssignableFrom(beanClz)).count()) {
            return;
        }
        Class<?> clz = beanClz;
        while (true) {
            for (Field field : clz.getDeclaredFields()) {
                field.setAccessible(true);
                String realPrefix = isEmpty(prefix) ? field.getName() : prefix.concat(".").concat(field.getName());
                if (funcMap.containsKey(field.getType())) {
                    propertyNameMap.computeIfAbsent(realPrefix, n -> new ArrayList<>()).add(field);
                } else {
                    resolveBeanFieldMeta(field, realPrefix);
                }
            }
            if (clz.getSuperclass() == Object.class) {
                break;
            }
            clz = clz.getSuperclass();
        }
    }

    /**
     * 解析 java bean 类型的字段
     *
     * @param field
     * @param prefix
     */
    private void resolveBeanFieldMeta(Field field, String prefix) {
        Class<?> type = field.getType();
        if (!type.isInterface()) {
            if (type.isArray()) {
                logger.warn("ignore field[{}], unsupported type [{}]", field.getName(), field.getGenericType().getTypeName());
                return;
            }
            Object subBean = ReflectUtils.newInstance(type);
            if (subBean instanceof Collection) {
                Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                FlexibleCollectionMeta meta = new FlexibleCollectionMeta((Collection) subBean, genericType, valueChangeListener);
                meta.resolvePropertyMeta(prefix, (Class<?>) genericType);
                meta.setField(field);
                meta.setPrefix(prefix);
                meta.setBean(this.bean);
                childBeanMeta.put(field.getName(), meta);
            } else {
                FlexibleValueMeta meta = new FlexibleValueMeta(subBean, valueChangeListener);
                meta.resolvePropertyMeta(prefix);
                childBeanMeta.put(field.getName(), meta);
            }
            setBeanFieldValue(field, subBean);
        } else {
            Collection collection = getCollectionBean((Class<? extends Collection>) type);
            if (null == collection) {
                return;
            }
            setBeanFieldValue(field, collection);
            Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            FlexibleCollectionMeta meta = new FlexibleCollectionMeta(collection, genericType, valueChangeListener);
            meta.resolvePropertyMeta(prefix, (Class<?>) genericType);
            meta.setPrefix(prefix);
            meta.setField(field);
            meta.setBean(this.bean);
            childBeanMeta.put(field.getName(), meta);
        }
    }

    /**
     * 构建collection对象
     *
     * @param collectionType
     * @return
     */
    private <T> T getCollectionBean(Class<T> collectionType) {
        if (List.class.isAssignableFrom(collectionType)) {
            return (T) new ArrayList();
        }
        if (Set.class.isAssignableFrom(collectionType)) {
            return (T) new HashSet();
        }
        return null;
    }

    private void setBeanFieldValue(Field field, Object value) {
        if (null == this.bean) {
            return;
        }
        ReflectUtils.setValue(this.bean, field, value);
    }

    protected void setBean(Object bean) {
        this.bean = bean;
    }

    /**
     * 读取配置属性名
     *
     * @param content
     * @return
     */
    public final String readConfigName(String content) {
        if (content.startsWith("${") && content.endsWith("}")) {
            Pattern p = Pattern.compile(REG);
            Matcher matcher = p.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).split(":")[0];
            }
        }
        throw new InvalidFormatException(content);
    }

    /**
     * 读取默认值
     *
     * @param content
     * @return
     */
    public final String readDefaultValue(String content) {
        Pattern p = Pattern.compile(REG);
        Matcher matcher = p.matcher(content);
        if (matcher.find()) {
            String[] split = matcher.group(1).split(":");
            if (split.length >= 2) {
                return split[1];
            }
        }
        return null;
    }

    /**
     * 注入属性值
     *
     * @param propertyReader
     */
    public final void inject(Function<String, String> propertyReader) {
        inject(propertyReader, InjectType.INIT);
    }

    /**
     * 注入属性值
     *
     * @param propertyReader
     * @param type
     */
    public void inject(Function<String, String> propertyReader, InjectType type) {
        if (UPDATE == type && !isUpdatable()) {
            // 不允许更新直接返回
            return;
        }
        getPropertyNameMap().forEach((key, fields) -> {
            String value = propertyReader.apply(key);
            fields.forEach(field -> {
                if (null != value) {
                    injectFieldValue(field, value, type);
                    currentValueMap.put(field, value);
                    return;
                }
                // 注入默认值
                if (defaultValueMap.containsKey(field)) {
                    String defaultValue = defaultValueMap.get(field);
                    injectFieldValue(field, defaultValue, type);
                    currentValueMap.put(field, defaultValue);
                }
            });
        });
        getChildBeanMeta().forEach((name, meta) -> meta.inject(propertyReader, type));
    }

    /**
     * 给字段注入值
     *
     * @param field
     * @param textValue
     * @param type
     */
    private void injectFieldValue(Field field, String textValue, InjectType type) {
        if (funcMap.containsKey(field.getType())) {
            setBeanFieldValue(field, funcMap.get(field.getType()).apply(textValue));
        } else {
            if (field.getAnnotation(FlexibleValue.class).json()) {
                setBeanFieldValue(field, JsonUtils.readValue(textValue, field.getGenericType()));
            } else {
                throw new UnSupportedTypeException(field.getType());
            }
        }
        if (UPDATE == type) {
            String oldValue = currentValueMap.get(field);
            if(!Objects.equals(oldValue, currentValueMap.get(field)) && null != valueChangeListener) {
                valueChangeListener.valueChanged(this.bean, field, oldValue, textValue);
            }
        }
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    protected boolean isUpdatable() {
        return updatable;
    }

    public Map<String, FlexibleValueMeta> getChildBeanMeta() {
        return childBeanMeta;
    }

    public Map<String, List<Field>> getPropertyNameMap() {
        return propertyNameMap;
    }

    static {
        funcMap.put(Integer.class, o -> Integer.parseInt(o.toString()));
        funcMap.put(int.class, o -> Integer.parseInt(o.toString()));
        funcMap.put(String.class, o -> o.toString());
        funcMap.put(Long.class, o -> Long.parseLong(o.toString()));
        funcMap.put(long.class, o -> Long.parseLong(o.toString()));
        funcMap.put(Short.class, o -> Short.parseShort(o.toString()));
        funcMap.put(short.class, o -> Short.parseShort(o.toString()));
        funcMap.put(Boolean.class, o -> Boolean.parseBoolean(o.toString()));
        funcMap.put(boolean.class, o -> Boolean.parseBoolean(o.toString()));
        funcMap.put(Float.class, o -> Float.parseFloat(o.toString()));
        funcMap.put(float.class, o -> Float.parseFloat(o.toString()));
        funcMap.put(Double.class, o -> Double.parseDouble(o.toString()));
        funcMap.put(double.class, o -> Double.parseDouble(o.toString()));
    }
}
