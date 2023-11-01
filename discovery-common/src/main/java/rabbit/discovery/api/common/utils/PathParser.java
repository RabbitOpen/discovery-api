package rabbit.discovery.api.common.utils;

import rabbit.discovery.api.common.exception.TooManyWildcardException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static rabbit.flt.common.utils.StringUtils.isEmpty;

public class PathParser {

    private static final String SEPARATOR = "/";

    private PathParser() {}

    /**
     * path路径解析
     *
     * @param path 静态路径
     * @return
     */
    protected static PathPattern parseText(String path) {
        if (-1 != path.indexOf('?')) {
            return parse(path.substring(0, path.indexOf('?')), false);
        } else {
            return parse(path, false);
        }
    }

    /**
     * path路径解析
     *
     * @param path
     * @return
     */
    public static PathPattern parsePattern(String path) {
        return parse(path, true);
    }

    /**
     * 解析path
     *
     * @param path
     * @param parseAsPattern
     * @return
     */
    private static PathPattern parse(String path, boolean parseAsPattern) {
        if (isEmpty(path)) {
            return new PathPattern();
        }
        String text = removeRepeatedSeparator(path);
        String[] split = text.split(SEPARATOR);
        List<Element> elementList = new ArrayList<>();
        int wildcardCount = 0;
        for (int i = 0; i < split.length; i++) {
            if (0 == i && "".equals(split[i])) {
                continue;
            }
            Element element = parseAsPattern ? new Element(split[i]) : new Element(split[i], false);
            elementList.add(element);
            if (element.isWildcard()) {
                wildcardCount++;
            }
            if (wildcardCount > 1) {
                throw new TooManyWildcardException(text);
            }
        }
        if (text.endsWith(SEPARATOR)) {
            elementList.add(new Element("", false));
        }
        return new PathPattern(elementList);
    }

    /**
     * 去掉path中重复的 '/'
     * @param path
     * @return
     */
    public static String removeRepeatedSeparator(String path) {
        String reg = String.format("(%s)\\1+", SEPARATOR);
        return path.replaceAll(reg, SEPARATOR);
    }

    /**
     * url encode
     * @param value
     * @return
     */
    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public static String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
