package com.twitter.config.models;

public class BooleanProperty implements Property<Boolean> {
    private Boolean value;

    private String key;

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    public BooleanProperty(String key, Boolean value) {
        this.key = key;
        this.value = value;
    }
}
