package rabbit.discovery.api.common.test;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPatternParser;
import rabbit.discovery.api.common.exception.TooManyWildcardException;
import rabbit.discovery.api.common.utils.Element;
import rabbit.discovery.api.common.utils.PathParser;
import rabbit.discovery.api.common.utils.PathPattern;

@RunWith(JUnit4.class)
public class PathPatternTest {

    @Test
    public void patternTest() {
        String p = "/{name}-{age}--{gender}/t{n}/{g}t";
        org.springframework.web.util.pattern.PathPattern springPattern = new PathPatternParser().parse(p);
        PathPattern pattern = PathParser.parsePattern(p);
        String path = "/z.3-10-f/tzs/t";
        TestCase.assertFalse(pattern.match(path));
        TestCase.assertFalse(springPattern.matches(PathContainer.parsePath(path)));

        path = "/z.3-10--f/tzs/t";
        TestCase.assertTrue(pattern.match(path));
        TestCase.assertTrue(springPattern.matches(PathContainer.parsePath(path)));

        path = "/z.3-10--f/tzs/1t";
        TestCase.assertTrue(pattern.match(path));
        TestCase.assertTrue(springPattern.matches(PathContainer.parsePath(path)));

        path = "/z.3-10--f/tzs/t1";
        TestCase.assertFalse(pattern.match(path));
        TestCase.assertFalse(springPattern.matches(PathContainer.parsePath(path)));

        path = "/1*&@({z.3-10--f/tzs/t";
        TestCase.assertTrue(pattern.match(path));
        TestCase.assertTrue(springPattern.matches(PathContainer.parsePath(path)));

        path = "/1*&@({z.3-10--f/tzs/t?a=b";
        TestCase.assertTrue(pattern.match(path));
    }

    @Test
    public void elementTest() {
        TestCase.assertEquals(5, new Element("{name}--{age}-{gender}").getElementCount());
        TestCase.assertEquals(6, new Element("{name}--{age}-{gender}{}").getElementCount());
        TestCase.assertEquals(7, new Element("1{name}--{age}-{gender}{}").getElementCount());
        TestCase.assertEquals(5, new Element("1{name}-2@{-{gender}{}").getElementCount());
    }

    @Test
    public void wildcardTest() {
        String patternUri = "/abc/**";
        PathPattern pattern = PathParser.parsePattern(patternUri);
        TestCase.assertTrue(pattern.match("/abc/"));
        TestCase.assertTrue(pattern.match("/abc/123"));
        TestCase.assertTrue(pattern.match("/abc/123//345"));
        TestCase.assertFalse(pattern.match("/ab/123/345"));

        patternUri = "/abc/**/a";
        pattern = PathParser.parsePattern(patternUri);
        TestCase.assertTrue(pattern.match("/abc/123/234/a"));
        TestCase.assertFalse(pattern.match("/ab/123/345/ab"));
        TestCase.assertFalse(pattern.match("/ab/123/345/b"));
        TestCase.assertFalse(pattern.match("/ab/123/345/ba"));

        try {
            PathParser.parsePattern("/a/**/**");
            throw new RuntimeException();
        } catch (Exception e) {
            TestCase.assertTrue(e instanceof TooManyWildcardException);
        }
    }
}
