package rabbit.discovery.api.test.mvc;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import rabbit.discovery.api.starter.listener.SpringMvcStartListener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TomcatContainer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WebApplicationContext applicationContext;

    private Tomcat tomcat = new Tomcat();

    // 部署根路径
    private String baseUrl;

    public TomcatContainer(int port) throws IOException, ServletException, LifecycleException {
        File file = createWorkDirectory();
        this.baseUrl = file.getAbsolutePath();
        initTomcat(port);
        // 退出时关闭tomcat
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                tomcat.getConnector().stop();
                tomcat.stop();
                deleteFile(file);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }));
        applicationContext = deployApplication();
    }

    /**
     * 执行任务
     * @param task
     */
    public void execute(Consumer<ApplicationContext> task) {
        task.accept(applicationContext);
    }

    private WebApplicationContext deployApplication() throws LifecycleException, ServletException {
        File deployDir = new File(this.baseUrl.concat("\\webapps\\root"));
        logger.info("deploy dir: {}", deployDir.getAbsolutePath());
        deployDir.mkdirs();
        // 添加当前应用到根目录
        Context context = tomcat.addWebapp("", deployDir.getAbsolutePath());
        // 添加servlet listener
        context.addServletContainerInitializer((sci, ctx) -> ctx.addListener(SpringMvcStartListener.class), null);

        DispatcherServlet servlet = createDispatcherServlet();

        Wrapper wrapper = tomcat.addServlet("", "dispatcherServlet", servlet);
        //拦截所有请求
        wrapper.addMapping("/");
        tomcat.start();
        wrapper.load();
        return servlet.getWebApplicationContext();
    }

    private DispatcherServlet createDispatcherServlet() {
        Map<String, String> map = new HashMap<>();
        map.put("contextConfigLocation", "classpath:application.xml");
        DefaultServlet config = new DefaultServlet() {
            @Override
            public String getInitParameter(String name) {
                return map.get(name);
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.enumeration(map.keySet());
            }
        };
        return new DispatcherServlet() {
            @Override
            public void init(ServletConfig servletConfig) throws ServletException {
                config.init(servletConfig);
                super.init(config);
            }

            @Override
            public ServletConfig getServletConfig() {
                return config;
            }

            @Override
            public void destroy() {
                logger.info("dispatcherServlet is closed!");
            }
        };
    }

    private void initTomcat(int port) {
        tomcat.setBaseDir(this.baseUrl);
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        tomcat.setConnector(connector);
        // 不添加默认web.xml
        tomcat.setAddDefaultWebXmlToWebapp(false);
    }

    /**
     * 创建工作目录
     *
     * @return
     * @throws IOException
     */
    private File createWorkDirectory() throws IOException {
        File file = Files.createTempDirectory("springmvc").toFile();
        file.mkdirs();
        return file;
    }

    private void deleteFile(File file) {
        if (null == file) {
            return;
        }
        if (0 != file.listFiles().length) {
            for (File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else {
                    if (0 == f.listFiles().length) {
                        f.delete();
                    } else {
                        deleteFile(f);
                    }
                }
            }
        }
        file.delete();
    }
}
