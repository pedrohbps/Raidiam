package com.raidiamproject.automation.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public enum Environment {

    LOCAL("local") {
        @Override
        public String getProp(String value) {
            return getPropertiesFile(EnvironmentProperties.PROPERTIESLOCAL).getProperty(value);
        }

    },

    QA("qa") {
        @Override
        public String getProp(String value) {
            return getPropertiesFile(EnvironmentProperties.PROPERTIESQA).getProperty(value);
        }

    };

    private String name;

    private Environment(String name) {
        this.name = name;
    }

    public abstract String getProp(String value);

    private static Properties getPropertiesFile(String file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return properties;
    }

    public static Environment getByName(String name) {
        for (Environment env : values()) {
            if (env.name.equals(name)) {
                return env;
            }
        }
        throw new IllegalArgumentException("Environment variable not configured!");
    }

}
