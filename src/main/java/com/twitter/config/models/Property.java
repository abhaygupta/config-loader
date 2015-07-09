package com.twitter.config.models;

public interface Property<T> {
    public T getValue();

    public String getKey();
}
