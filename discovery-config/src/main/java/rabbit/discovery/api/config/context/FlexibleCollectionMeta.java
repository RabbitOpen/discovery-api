package rabbit.discovery.api.config.context;

import rabbit.discovery.api.common.utils.ReflectUtils;
import rabbit.discovery.api.config.ValueChangeListener;
import rabbit.flt.common.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

import static rabbit.discovery.api.config.context.InjectType.INIT;
import static rabbit.discovery.api.config.context.InjectType.UPDATE;


class FlexibleCollectionMeta extends FlexibleValueMeta {

    private Collection collectionObj;

    // 泛型类型
    private Type paramType;

    private String prefix;

    private Field field;

    // constructor
    public FlexibleCollectionMeta(Collection collection, Type paramType, ValueChangeListener valueChangeListener) {
        super(null, valueChangeListener);
        this.collectionObj = collection;
        this.paramType = paramType;
    }

    @Override
    public void inject(Function<String, String> propertyReader, InjectType type) {
        if (UPDATE == type && !isUpdatable()) {
            return;
        }
        Collection temp = (Collection) ReflectUtils.newInstance((Class<?>) collectionObj.getClass());
        temp.addAll(collectionObj);
        collectionObj.clear();
        if (!funcMap.containsKey(this.paramType)) {
            // 集合类型只支持基础类型数据
            return;
        }
        int i = 0;
        while (true) {
            StringBuilder keyBuilder = new StringBuilder(prefix).append("[").append(i).append("]");
            String value = propertyReader.apply(keyBuilder.toString());
            if (StringUtil.isEmpty(value)) {
                break;
            }
            collectionObj.add(funcMap.get(paramType).apply(value));
            i++;
        }
        if (null == valueChangeListener || INIT == type) {
            return;
        }
        if (!(collectionObj.containsAll(temp) && temp.containsAll(collectionObj))) {
            valueChangeListener.valueChanged(this.bean, field, temp, collectionObj);
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
