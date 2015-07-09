package com.twitter.config.models;

public class LongProperty implements Property<Long> {

    private Long value;

    private String key;

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    public LongProperty(String key, Long value) {
        this.key = key;
        this.value = value;
    }
}
