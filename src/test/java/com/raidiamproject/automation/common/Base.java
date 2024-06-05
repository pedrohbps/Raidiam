package com.raidiamproject.automation.common;

import java.util.UUID;

public class Base {

    public static boolean isValidUnixTimestamp(double timestamp) {
        long seconds = (long) timestamp; // Get integer part
        double fraction = timestamp - seconds; // Get fractional part

        return seconds >= 0 && fraction >= 0 && fraction < 1; 
    }

    public static boolean assertValidUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}

