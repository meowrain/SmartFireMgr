package com.xszx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.Test;

public class ColorLogTest {
    private static final Logger logger = LogManager.getLogger(ColorLogTest.class);

    @Test
    void test() {
        AnsiConsole.systemInstall();
        System.out.println(Ansi.ansi().fgRed().a("红色文本").reset());
        AnsiConsole.systemUninstall();
        logger.trace("这是 TRACE 级别日志");
        logger.debug("这是 DEBUG 级别日志");
        logger.info("这是 INFO 级别日志");
        logger.warn("这是 WARN 级别日志");
        logger.error("这是 ERROR 级别日志");
        logger.fatal("这是 FATAL 级别日志");

        try {
            throw new RuntimeException("测试异常");
        } catch (Exception e) {
            logger.error("捕获到异常", e);
        }
    }
}