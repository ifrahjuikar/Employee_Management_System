package com.springbootemployeedata.springbootemployeedata.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

public class LoggingErrorUtility {

    private static final Logger logger = LoggerFactory.getLogger(LoggingErrorUtility.class);
    private static final Properties logMessages = new Properties();

    static {
        try {
            logMessages.load(new ClassPathResource("error.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Failed to load error.properties", e);
        }
    }

    public static void errorlogging(String key, Object... params) {
        String message = logMessages.getProperty(key, "Unknown error");
        logger.error(message, params);
    }
}