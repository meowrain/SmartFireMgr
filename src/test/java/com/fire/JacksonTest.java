package com.fire;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fire.util.JacksonHolderSingleton;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonTest {
    private static final Logger logger = LoggerFactory.getLogger(JacksonTest.class);

    @Test
    void test() {
        ObjectMapper objectMapper = JacksonHolderSingleton.getObjectMapper();
        logger.info("fasdfdassdaf");
    }
}
