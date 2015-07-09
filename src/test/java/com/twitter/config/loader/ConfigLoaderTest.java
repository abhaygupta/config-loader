package com.twitter.config.loader;

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

// FIXME: same test repeated at ConfigTest
public class ConfigLoaderTest {

    private static final String SAMPLE_CONFIG_FILE = "sample.properties";

    private static final String EMPTY_GROUP_NAME_PROPERTIES = "empty-group-name.properties";

    private static final String INVALID_PROPERTY_DEF_PROPERTIES = "invalid-property-def.properties";

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoaderTest.class);

    /* cache logic testing */
    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyCacheTest() {
        String me = "testLoadSamplePropertyCacheTest";
        ConfigLoader loader = new ConfigLoader();
        try {

            // 1st load from file
            long startMs = System.nanoTime();
            Config config = loader.load(getResourceFilePath(SAMPLE_CONFIG_FILE), null);
            Assert.assertNotNull(config);
            long timeFromFS = System.nanoTime() - startMs;

            // 2nd time should come from cache
            startMs = System.nanoTime();
            config = loader.load(getResourceFilePath(SAMPLE_CONFIG_FILE), null);
            long timeFromAppMemoryCache = System.nanoTime() - startMs;
            logger.info("time taken from cache=" + timeFromAppMemoryCache + " nanosec, time taken from FileSystem=" + timeFromFS
                    + " nanosec");
            System.out.println("time taken from cache=" + timeFromAppMemoryCache + " nanosec, time taken from FileSystem="
                    + timeFromFS + " nanosec");
            Assert.assertTrue(timeFromAppMemoryCache < timeFromFS);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE, ex);
            Assert.fail(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE + ". Error=" + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyWithoutOverrideFilePositiveTest() {
        String me = "testLoadSamplePropertyWithoutOverrideFilePositiveTest";
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load(getResourceFilePath(SAMPLE_CONFIG_FILE), null);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE, ex);
            Assert.fail(me + "Failed to load config file=" + SAMPLE_CONFIG_FILE + ". Error=" + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testLoadSamplePropertyWithOverridesFilePositiveTest() {
        String me = "testLoadSamplePropertyWithOverridesFilePositiveTest";
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load(getResourceFilePath(SAMPLE_CONFIG_FILE), new ArrayList<String>() {
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
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load("test/tmp/wrong-file", new ArrayList<String>() {
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
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load("", new ArrayList<String>() {
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
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load(null, new ArrayList<String>() {
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
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load(getResourceFilePath(EMPTY_GROUP_NAME_PROPERTIES), new ArrayList<String>() {
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
        ConfigLoader loader = new ConfigLoader();
        try {
            Config config = loader.load(getResourceFilePath(INVALID_PROPERTY_DEF_PROPERTIES), new ArrayList<String>() {
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

}
