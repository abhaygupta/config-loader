package com.twitter.config.models;

public class StringProperty implements Property<String> {

    private String value;

    private String key;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    public StringProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
