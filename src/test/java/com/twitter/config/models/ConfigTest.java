package com.twitter.config.models;

import static com.twitter.config.utils.ConfigLoaderUtils.getResourceFilePath;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.twitter.config.exception.ConfigLoaderError;
import com.twitter.config.exception.ConfigLoaderException;
import com.twitter.config.models.Config;

/**
 * 
 * @author agupta13
 * 
 */

public class ConfigTest {

    private static final String SAMPLE_CONFIG_FILE = "sample.properties";

    private static final String EMPTY_GROUP_NAME_PROPERTIES = "empty-group-name.properties";

    private static final String INVALID_PROPERTY_DEF_PROPERTIES = "invalid-property-def.properties";

    private static final Logger logger = LoggerFactory.getLogger(ConfigTest.class);

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyWithoutOverrideFilePositiveTest() {
        String me = "testLoadSamplePropertyWithoutOverrideFilePositiveTest";
        try {
            Config config = new ConfigImpl(getResourceFilePath(SAMPLE_CONFIG_FILE), null);
            Assert.assertNotNull(config);
            Assert.assertTrue("/tmp/".equals(config.get("ftp.path")));
            Assert.assertTrue("/srv/var/tmp/".equals(config.get("common.path")));
            Long paidUsers = (Long) config.get("common.paid_users_size_limit");
            Assert.assertTrue(paidUsers.compareTo(new Long("2147483648")) == 0);
            List<String> httpParams = (List<String>) config.get("http.params");
            Assert.assertEquals(httpParams.size(), 3);
            Assert.assertTrue(httpParams.contains("array"));
            Assert.assertTrue(httpParams.contains("of"));
            Assert.assertTrue(httpParams.contains("values"));
            Assert.assertNull(config.get("wrong-key"));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE, ex);
            Assert.fail(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE + ". Error=" + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyWithOverridesFilePositiveTest() {
        String me = "testLoadSamplePropertyWithOverridesFilePositiveTest";
        try {
            Config config = new ConfigImpl(getResourceFilePath(SAMPLE_CONFIG_FILE), new ArrayList<String>() {
                {
                    add("itscript");
                    add("ubuntu");
                    add("test");
                }
            });
            Assert.assertNotNull(config);
            Assert.assertTrue("/etc/var/uploads".equals(config.get("ftp.path")));
            Assert.assertTrue("/srv/tmp/".equals(config.get("common.path")));
            Long paidUsers = (Long) config.get("common.paid_users_size_limit");
            Assert.assertTrue(paidUsers.compareTo(new Long("2147483648")) == 0);
            List<String> httpParams = (List<String>) config.get("http.params");
            Assert.assertEquals(httpParams.size(), 3);
            Assert.assertTrue(httpParams.contains("array"));
            Assert.assertTrue(httpParams.contains("of"));
            Assert.assertTrue(httpParams.contains("values"));
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE, ex);
            Assert.fail(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE + ". Error=" + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyInvalidFilePathNegativeTest() {
        String me = "testLoadSamplePropertyInvalidFilePathNegativeTest";
        try {
            Config config = new ConfigImpl("test/tmp/wrong-file", new ArrayList<String>() {
                {
                    add("itscript");
                    add("ubuntu");
                    add("test");
                }
            });
            Assert.fail("Invalid config file should throw exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ConfigLoaderException);
            ConfigLoaderException e = (ConfigLoaderException) ex;
            Assert.assertTrue(e.getErrorCode().equals(ConfigLoaderError.CONFIG_FILE_NOT_FOUND.name()));
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyEmptyFilePathNegativeTest() {
        String me = "testLoadSamplePropertyEmptyFilePathNegativeTest";
        try {
            Config config = new ConfigImpl("", new ArrayList<String>() {
                {
                    add("itscript");
                    add("ubuntu");
                    add("test");
                }
            });
            Assert.fail("Invalid config file should throw exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ConfigLoaderException);
            ConfigLoaderException e = (ConfigLoaderException) ex;
            Assert.assertTrue(e.getErrorCode().equals(ConfigLoaderError.INVALID_CONFIG_FILE_PATH_VALUE.name()));
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyNullFilePathNegativeTest() {
        String me = "testLoadSamplePropertyNullFilePathNegativeTest";
        try {
            Config config = new ConfigImpl(null, new ArrayList<String>() {
                {
                    add("itscript");
                    add("ubuntu");
                    add("test");
                }
            });
            Assert.fail("Invalid config file should throw exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ConfigLoaderException);
            ConfigLoaderException e = (ConfigLoaderException) ex;
            Assert.assertTrue(e.getErrorCode().equals(ConfigLoaderError.INVALID_CONFIG_FILE_PATH_VALUE.name()));
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadEmptyPropertyGroupFileNegativeTest() {
        String me = "testLoadEmptyPropertyGroupFileNegativeTest";
        try {
            Config config = new ConfigImpl(getResourceFilePath(EMPTY_GROUP_NAME_PROPERTIES), new ArrayList<String>() {
                {
                    add("itscript");
                    add("ubuntu");
                    add("test");
                }
            });
            Assert.fail("Invalid config file should throw exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ConfigLoaderException);
            ConfigLoaderException e = (ConfigLoaderException) ex;
            Assert.assertTrue(e.getErrorCode().equals(ConfigLoaderError.PROPERTY_GROUP_NULL.name()));
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadInvalidPropertyDefFileNegativeTest() {
        String me = "testLoadInvalidPropertyDefFileNegativeTest";
        try {
            Config config = new ConfigImpl(getResourceFilePath(INVALID_PROPERTY_DEF_PROPERTIES),
                    new ArrayList<String>() {
                        {
                            add("itscript");
                            add("ubuntu");
                            add("test");
                        }
                    });
            Assert.fail("Invalid config file should throw exception");
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof ConfigLoaderException);
            ConfigLoaderException e = (ConfigLoaderException) ex;
            Assert.assertTrue(e.getErrorCode().equals(ConfigLoaderError.INVALID_PROPERTY_DEFINITION.name()));
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testGetPropertyWithInvalidKeyNegativeTest() {
        String me = "testGetPropertyWithInvalidKeysNegativeTest";
        try {
            Config config = new ConfigImpl(getResourceFilePath(SAMPLE_CONFIG_FILE), null);
            Assert.assertNull(config.get("invalid-key"));
            Assert.assertNull(config.get("invalidgrp.invakidkey"));
            Assert.assertNull(config.get(""));
            Assert.assertNull(config.get(null));
        } catch (Exception ex) {
            Assert.fail(me + " Exception while get config. Error=" + ex.getMessage());
        }
    }

}
