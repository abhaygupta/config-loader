package com.twitter.config.exception;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.twitter.config.models.BooleanProperty;
import com.twitter.config.models.DoubleProperty;
import com.twitter.config.models.ListProperty;
import com.twitter.config.models.LongProperty;
import com.twitter.config.models.Property;
import com.twitter.config.models.StringProperty;

public class PropertyFactory {

    private static final String FALSE = "false";

    private static final String TRUE = "true";

    private static final String LIST_OF_PROPERTY_DELIMITER = ",";

    /**
     * Factory method returns Property object base of value type
     * 
     * @param key
     * @param value
     * @return
     */
    public static Property getProperty(String key, String value) {
        if (StringUtils.isBlank(key) || value == null) {
            throw new RuntimeException("Invalid property key=" + key + " value=" + value);
        }
        
        if (isLong(value)) {
            return new LongProperty(key, Long.parseLong(value));
        } else if (isDouble(value)) {
            return new DoubleProperty(key, Double.parseDouble(value));
        } else if (isBoolean(value)) {
            return new BooleanProperty(key, Boolean.parseBoolean(value));
        } else if (isListOfValues(value)) {
            return new ListProperty(key, Arrays.asList(value.split(LIST_OF_PROPERTY_DELIMITER)));
        } else {
            return new StringProperty(key, value);
        }
    }

    private static boolean isListOfValues(String value) {
        return value.contains(LIST_OF_PROPERTY_DELIMITER);
    }

    private static boolean isLong(String value) {
        try {
            Long.parseLong(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean isBoolean(String value) {
        return TRUE.equalsIgnoreCase(value) || FALSE.equalsIgnoreCase(value);
    }
}
