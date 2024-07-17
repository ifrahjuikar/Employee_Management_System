package com.springbootemployeedata.springbootemployeedata.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

public class LoggingWarnUtility
{

    private static final Logger logger = LoggerFactory.getLogger(LoggingWarnUtility.class);
    private static final Properties logMessages = new Properties();

    static {
        try {
            logMessages.load(new ClassPathResource("warnlogging.properties").getInputStream());
        } catch (IOException e) {
            logger.error("Failed to load warnlogging.properties", e);
        }
    }

    public static void warnLogging(String key, Object... params) {
        String message = logMessages.getProperty(key, "Unknown error");
        logger.warn( message, params);
    }
}