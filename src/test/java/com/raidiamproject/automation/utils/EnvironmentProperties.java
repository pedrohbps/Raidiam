package com.raidiamproject.automation.utils;

import java.util.Properties;

public class EnvironmentProperties {

    static final String PROPERTIESA = "./src/test/resources/environmentA.properties";
    static Properties properties;

    public static String getValue(String value) {
        String environmentName = System.getProperty("environment");
        return Environment.getByName(environmentName).getProp(value);
    }

    public static int getInt(String property){
        String environmentName = System.getProperty("environment");

        try{
            String value = Environment.getByName(environmentName).getProp(property);
            if(value == null){
                throw new IllegalArgumentException(String.format("The property %s doesn't exist.", property));
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("The property %s is not an int.", property));
        }
    }
}
