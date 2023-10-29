package rabbit.discovery.api.common.utils;

import java.util.ArrayList;
import java.util.List;

public class PathPattern {

    private List<Element> elements;

    protected PathPattern() {
        this(new ArrayList<>());
    }

    protected PathPattern(List<Element> elements) {
        this.elements = elements;
    }

    /**
     * 是否匹配
     *
     * @param path
     * @return
     */
    public boolean match(String path) {
        if (-1 != path.indexOf('?')) {
            return match(PathParser.parseText(path.substring(0, path.indexOf('?'))));
        } else {
            return match(PathParser.parseText(path));
        }
    }

    private boolean match(PathPattern pathPattern) {
        if (getElements().size() > pathPattern.getElements().size()) {
            return false;
        }
        if (getElements().size() < pathPattern.getElements().size()) {
            if (hasWildcard()) {
                return doWildcardMatch(pathPattern);
            }
            return false;
        } else {
            for (int i = 0; i < getElements().size(); i++) {
                if (!getElements().get(i).match((pathPattern.getElements().get(i).getValue()))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 通配符匹配
     *
     * @param pathPattern
     * @return
     */
    private boolean doWildcardMatch(PathPattern pathPattern) {
        boolean wildcardFound = false;
        for (int i = 0; i < getElements().size(); i++) {
            Element element = getElements().get(i);
            if (element.isWildcard()) {
                wildcardFound = true;
                continue;
            }
            if (wildcardFound) {
                int index = pathPattern.getElements().size() - getElements().size() + i;
                String path = pathPattern.getElements().get(index).getValue();
                if (!element.match(path)) {
                    return false;
                }
            } else {
                if (!element.match(pathPattern.getElements().get(i).getValue())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否包含通配符
     *
     * @return
     */
    private boolean hasWildcard() {
        for (Element element : getElements()) {
            if (element.isWildcard()) {
                return true;
            }
        }
        return false;
    }

    private List<Element> getElements() {
        return elements;
    }
}
