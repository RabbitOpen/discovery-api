package rabbit.discovery.api.common.utils;

import java.util.ArrayList;
import java.util.List;

public class Element {

    // 是变量
    private boolean variable;

    private String value;

    // 通配符
    private boolean wildcard;

    // 变量元素的静态文本段
    private List<Element> elements = new ArrayList<>();

    public Element(String textValue) {
        this.value = textValue.trim();
        this.wildcard = "**".equals(this.value);
        String start = "{";
        String end = "}";
        String text = this.value;
        if (hasVariable(text, start, end)) {
            this.variable = true;
            while (hasVariable(text, start, end)) {
                if (0 != text.indexOf(start)) {
                    String et = text.substring(0, text.indexOf(start));
                    elements.add(new Element(et, false));
                    text = text.substring(et.length());
                } else {
                    elements.add(new Element("*", true));
                    text = text.substring(text.indexOf(end) + 1);
                }
            }
            if (0 != text.length()) {
                elements.add(new Element(text, false));
            }
        } else {
            this.variable = false;
        }
    }

    public Element(String value, boolean variable) {
        this.variable = variable;
        this.value = value;
        this.wildcard = false;
    }

    /**
     * 判断是否包含变量
     *
     * @param value
     * @param start
     * @param end
     * @return
     */
    private boolean hasVariable(String value, String start, String end) {
        int startIndex = value.indexOf(start);
        int endIndex = value.indexOf(end);
        return -1 != startIndex && -1 != endIndex && endIndex > startIndex;
    }

    /**
     * 判断是否匹配
     *
     * @param text
     * @return
     */
    public boolean match(String text) {
        if (isWildcard()) {
            // 通配符匹配所有字符
            return true;
        }
        if (!isVariable()) {
            // 静态文本必须相同才算匹配
            return value.equals(text);
        }
        return doVariableMatch(text);
    }

    /**
     * 检查包含变量的匹配
     * @param text
     * @return
     */
    private boolean doVariableMatch(String text) {
        StringBuilder sb = new StringBuilder(text);
        boolean variablePrefix = false;
        for (int i = 0; i < elements.size(); i++) {
            Element el = elements.get(i);
            if (el.isVariable()) {
                variablePrefix = true;
                continue;
            }
            int index = sb.indexOf(el.getValue());
            if (variablePrefix) {
                if (-1 == index) {
                    return false;
                }
            } else {
                if (index > 0) {
                    return false;
                }
            }
            sb.delete(0, index + el.getValue().length());
            variablePrefix = false;
        }
        if (variablePrefix) {
            return true;
        }
        return sb.length() == 0 || sb.toString().equals(elements.get(elements.size() - 1).getValue());
    }

    public int getElementCount() {
        return elements.size();
    }

    private boolean isVariable() {
        return variable;
    }

    public String getValue() {
        return value;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
