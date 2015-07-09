package com.twitter.config.models;

import java.util.List;

public class ListProperty implements Property<List<String>> {

    private List<String> value;

    private String key;

    @Override
    public List<String> getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    public ListProperty(String key, List<String> value) {
        this.key = key;
        this.value = value;
    }
}
