package com.twitter.config.models;

public class DoubleProperty implements Property<Double> {

    private Double value;

    private String key;

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    public DoubleProperty(String key, Double value) {
        this.key = key;
        this.value = value;
    }
}
