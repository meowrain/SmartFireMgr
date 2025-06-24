package com.fire.common.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.InputStream;
import java.util.Properties;

/**
 * 应用启动监听器
 * 职责：在应用启动时打印一个包含版本和环境信息的美观横幅。
 *
 * @WebListener 事件监听器接口
 * <p>
 * 它是一个 事件监听器接口，专门用来监听整个 Web 应用的“生命周期”事件。您可以把它想象成是您 Web 应用的 main
 * 方法。
 * <p>
 * ServletContext: 代表您的整个 Web 应用。它是一个全局的、唯一的对象。
 * 生命周期事件:
 * 应用启动时: Tomcat 加载并初始化您的应用后，会触发
 * contextInitialized(ServletContextEvent sce) 方法。这是执行 一次性初始化
 * 任务的绝佳位置，比如：
 * 建立数据库连接池。
 * 启动后台定时任务。
 * 加载全局配置。
 * 打印启动横幅（就像您现在做的）。
 */
@WebListener
public class StartupArtListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(StartupArtListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String version = getVersion();
        String appName = getAppName();
        String contextPath = sce.getServletContext().getContextPath();
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");

        String banner = "\n" +
                "=======================================================================================\n" +
                "                                                                                       \n" +
                "   🔥🔥🔥   Welcome to 智慧防火管理系统   🔥🔥🔥   \n" +
                "                                                                                       \n" +
                "   Version:     " + String.format("%-50s", version) + "\n" +
                "   ContextPath: " + String.format("%-50s", contextPath) + "\n" +
                "   Java:        " + String.format("%-50s", javaVersion) + "\n" +
                "   OS:          " + String.format("%-50s", osName) + "\n" +
                "                                                                                       \n" +
                "   🚀 Application has started successfully! Access at: http://localhost:8666" + contextPath + "   \n" +
                "                                                                                       \n" +
                "=======================================================================================";

        System.out.println(banner);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("================= Application is shutting down. =================");
    }

    /**
     * 从 classpath 中的 version.properties 文件读取版本号。
     *
     * @return 版本号字符串，如果找不到则返回 "N/A"
     */
    private String getVersion() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                log.warn("app.properties not found in classpath.");
                return "N/A";
            }
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("app.version", "N/A");
        } catch (Exception ex) {
            log.error("Error reading version.properties", ex);
            return "N/A";
        }
    }

    private String getAppName() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
                log.warn("app.properties not found in classpath.");
                return "N/A";
            }
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty("app.name", "N/A");
        } catch (Exception ex) {
            log.error("Error reading version.properties", ex);
            return "N/A";
        }
    }
}
