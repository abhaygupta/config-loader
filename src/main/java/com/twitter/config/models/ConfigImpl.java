package com.twitter.config.models;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.twitter.config.exception.ConfigLoaderError;
import com.twitter.config.exception.ConfigLoaderException;
import com.twitter.config.factory.PropertyFactory;

@SuppressWarnings("rawtypes")
public class ConfigImpl implements Config {

    private static final String PROPERTY_KEY_DELIMITER = ".";

    private static final String OVERRIDE_END_TAG = ">";

    private static final String OVERRIDE_START_TAG = "<";

    private static final String PROPERTY_TOKENIZER = "=";

    private static final String GROUP_END_TAG = "]";

    private static final String GROUP_START_TAG = "[";

    private static final String COMMENT_TAG = ";";

    private static final Logger logger = LoggerFactory.getLogger(ConfigImpl.class);

    /**
     * group name and property key are case-sensitive 1st hashKey: group name; 2nd hashKey: property key; HashMap - O(1)
     * complexity to read property by key
     */
    private Map<String, Map<String, Property>> properties;

    public ConfigImpl(String configFilePath, List<String> overrides) throws ConfigLoaderException {
        load(configFilePath, overrides);
    }

    /**
     * Time complexity - O(N) - line entries Storage - O(N) - Hash<String, Hash> - will have N nodes (excluding comment
     * nodes)
     * 
     * @throws ConfigLoaderException
     */
    @Override
    public void load(String configFilePath, List<String> overrides) throws ConfigLoaderException {

        if (StringUtils.isBlank(configFilePath)) {
            logger.error("Invalid empty or null config file path");
            throw new ConfigLoaderException(ConfigLoaderError.INVALID_CONFIG_FILE_PATH_VALUE,
                    "Invalid empty or null config file path");
        }

        File configFile = new File(configFilePath);

        if (!configFile.exists()) {
            logger.error("Can't find config file at path=" + configFilePath);
            throw new ConfigLoaderException(ConfigLoaderError.CONFIG_FILE_NOT_FOUND, "Can't find config file at path="
                    + configFilePath);
        }

        List<String> lines;
        try {
            lines = FileUtils.readLines(configFile);
        } catch (IOException e) {
            logger.error("Error reading configFile=" + configFile);
            throw new ConfigLoaderException(ConfigLoaderError.FAILED_TO_READ_CONFIG_FILE, "Error reading configFile="
                    + configFile);
        }

        String propertyGroup = null;
        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isNotBlank(line)) {
                if (!isComment(line)) {
                    if (isGroup(line)) {
                        propertyGroup = line.substring(1, line.length() - 1);
                    } else {

                        // error case blank property group
                        if (StringUtils.isBlank(propertyGroup)) {
                            logger.error("Property can't have blank group");
                            throw new ConfigLoaderException(ConfigLoaderError.PROPERTY_GROUP_NULL,
                                    "Property can't have blank group");
                        }

                        // error case property don't have =
                        if (!line.contains(PROPERTY_TOKENIZER)) {
                            logger.error("Found config line={}, which is niether a group, comment or prpoperty. "
                                    + "Please check property file", line);
                            throw new ConfigLoaderException(ConfigLoaderError.INVALID_PROPERTY_DEFINITION,
                                    "Found config line={}, which is niether a group, comment or prpoperty. "
                                            + "Please check property file");
                        }

                        String[] property = line.split(PROPERTY_TOKENIZER);
                        // error case
                        if (property.length != 2) {
                            throw new ConfigLoaderException(ConfigLoaderError.INVALID_PROPERTY_DEFINITION,
                                    "Failed to read property from property file. Invalid value at line=" + line);
                        }

                        String key = property[0];
                        String value = property[1];

                        // if value contains comment trim it off
                        if (containsComment(value)) {
                            value = value.substring(0, value.indexOf(COMMENT_TAG));
                        }

                        if (isOverrideApplicable(overrides, key)) {
                            for (String override : overrides) {
                                if (key.contains(override)) {
                                    key = key.substring(0, key.indexOf(OVERRIDE_START_TAG));
                                }
                            }
                        }

                        // override key when override is not provided
                        if (key.indexOf(OVERRIDE_START_TAG) == -1) {
                            // check if group is already present in hash
                            Map<String, Property> groupMap = getProperties().get(propertyGroup);
                            if (groupMap == null) {
                                groupMap = new HashMap<String, Property>();
                            }
                            
                            key = key.trim();
                            value = value.trim();
                            
                            groupMap.put(key, PropertyFactory.getProperty(key, value));
                            getProperties().put(propertyGroup, groupMap);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if override is present and applicable over key
     * 
     * @param overrides
     * @param key
     * @return
     */
    private boolean isOverrideApplicable(List<String> overrides, String key) {
        return overrides != null && !overrides.isEmpty() && key.indexOf(OVERRIDE_START_TAG) > 0
                && key.indexOf(OVERRIDE_END_TAG) > 0;
    }

    /**
     * O(1) get property should never throw exception return null value if not found
     */
    @Override
    public Object get(String key) {
        if (StringUtils.isNotBlank(key) && key.contains(PROPERTY_KEY_DELIMITER)) {
            String[] propertyKeys = key.split("\\.");
            if (propertyKeys.length != 2) {
                return null;
            }
            Map<String, Property> groupMap = getProperties().get(propertyKeys[0]);
            if (groupMap == null) {
                return null;
            }
            Property prop = groupMap.get(propertyKeys[1]);
            return prop == null ? null : prop.getValue();
        } else {
            return null;
        }
    }

    public Map<String, Map<String, Property>> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, Map<String, Property>>();
        }
        return properties;
    }

    public void setProperties(Map<String, Map<String, Property>> properties) {
        this.properties = properties;
    }

    private static boolean isGroup(String line) {
        return line.startsWith(GROUP_START_TAG) && line.endsWith(GROUP_END_TAG);
    }

    private static boolean isComment(String line) {
        return line.startsWith(COMMENT_TAG);
    }

    private static boolean containsComment(String line) {
        return line.contains(COMMENT_TAG);
    }

}
