package com.twitter.config.loader.factory;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.twitter.config.exception.ConfigLoaderException;
import com.twitter.config.factory.PropertyFactory;
import com.twitter.config.models.BooleanProperty;
import com.twitter.config.models.DoubleProperty;
import com.twitter.config.models.ListProperty;
import com.twitter.config.models.LongProperty;
import com.twitter.config.models.StringProperty;

public class PropertyFactoryTest {

    @Test(enabled = true, groups = "unit")
    public void testPropertyFactorySuccess() {
        try {
            Assert.assertTrue(PropertyFactory.getProperty("test", "123") instanceof LongProperty);
            Assert.assertTrue(PropertyFactory.getProperty("test", "123.2343") instanceof DoubleProperty);
            Assert.assertTrue(PropertyFactory.getProperty("test", "false") instanceof BooleanProperty);
            Assert.assertTrue(PropertyFactory.getProperty("test", "hello world") instanceof StringProperty);
            Assert.assertTrue(PropertyFactory.getProperty("test", "hello, world") instanceof ListProperty);
        } catch (ConfigLoaderException ex) {
            Assert.fail("Failed testPropertyFactorySuccess. ErrorCode=" + ex.getErrorCode() + " ErrorMsg="
                    + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit")
    public void testPropertyFactoryEmptyValue() {
        try {
            Assert.assertTrue(PropertyFactory.getProperty("test", "") instanceof StringProperty);
        } catch (ConfigLoaderException ex) {
            Assert.fail("Failed testPropertyFactoryEmptyValue. ErrorCode=" + ex.getErrorCode() + " ErrorMsg="
                    + ex.getMessage());
        }
    }

    @Test(enabled = true, groups = "unit", expectedExceptions = { ConfigLoaderException.class })
    public void testPropertyFactoryNullKey() throws ConfigLoaderException {
        PropertyFactory.getProperty(null, "test val");
    }

    @Test(enabled = true, groups = "unit", expectedExceptions = { ConfigLoaderException.class })
    public void testPropertyFactoryNullValue() throws ConfigLoaderException {
        PropertyFactory.getProperty("test", null);
    }
}
