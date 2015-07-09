package com.twitter.config.models;

import java.util.List;

import com.twitter.config.exception.ConfigLoaderException;

public interface Config {

    Object get(String key);

    void load(String configFilePath, List<String> overrides) throws ConfigLoaderException;
}
