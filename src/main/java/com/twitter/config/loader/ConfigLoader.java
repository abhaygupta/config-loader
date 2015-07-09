package com.twitter.config.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.twitter.config.exception.ConfigLoaderException;
import com.twitter.config.models.Config;
import com.twitter.config.models.ConfigImpl;

/**
 * ConfigLoader is a wrapper over Config that can cache config object so we need not to goto file system again. Cached
 * config is currently kept in application memory if huge OR same file is used by multiple services/applications can be
 * moved to Memcache.
 * 
 * @author agupta13
 * 
 */
public class ConfigLoader {

    private static final String SEPARATOR = "-";

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private Map<String, Config> configCache;

    /*
     * added in memory cache to speed up repeated config load - tested in
     * ConfigLoaderTest#testLoadSamplePropertyCacheTest
     */
    public Config load(String configFilePath, List<String> overrides) throws ConfigLoaderException {
        long startTime = System.currentTimeMillis();
        logger.info("Load configuration called with file path={} and overrides={}", configFilePath, overrides);

        String hashKey = null;
        if (StringUtils.isNotBlank(configFilePath)) {
            hashKey = generateHashKey(configFilePath, overrides);
        }

        Config config = null;
        // load config from application cache
        if (StringUtils.isNotBlank(hashKey)) {
            config = getConfigCache().get(hashKey);
        }

        if (config == null) {
            logger.info("Loading config from FileSystem");
            config = new ConfigImpl(configFilePath, overrides);
        } else {
            logger.info("Found config in cache");
        }

        // put config in cache for later use
        if (StringUtils.isNotBlank(hashKey) && config != null) {
            getConfigCache().put(hashKey, config);
        }

        logger.info("Loaded config succesfully, total time taken={} msec", (System.currentTimeMillis() - startTime));
        return config;
    }

    /**
     * create hash key .i.e. filePath + overrides (appended by -)
     * 
     * @param filePath
     * @param overrides
     * @return
     */
    private String generateHashKey(String filePath, List<String> overrides) {
        String overrideKey = null;
        if (overrides != null && !overrides.isEmpty()) {
            StringBuffer hashKey = new StringBuffer();
            for (String override : overrides) {
                hashKey.append(override + SEPARATOR);
            }
            overrideKey = hashKey.toString();
        }
        return filePath + overrideKey;
    }

    public Map<String, Config> getConfigCache() {
        if (configCache == null) {
            configCache = new HashMap<String, Config>();
        }
        return configCache;
    }

    public void setConfigCache(Map<String, Config> configCache) {
        this.configCache = configCache;
    }

}
