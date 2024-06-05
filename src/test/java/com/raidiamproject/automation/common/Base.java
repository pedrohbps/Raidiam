package com.raidiamproject.automation.common;

public class Base {

    public static boolean isValidUnixTimestamp(double timestamp) {
        long seconds = (long) timestamp; // Get integer part
        double fraction = timestamp - seconds; // Get fractional part

        return seconds >= 0 && fraction >= 0 && fraction < 1; 
    }
}

