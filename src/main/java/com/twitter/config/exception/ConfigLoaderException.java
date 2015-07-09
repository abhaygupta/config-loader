package com.twitter.config.exception;

public class ConfigLoaderException extends Exception {

    private static final long serialVersionUID = -7682800756377969180L;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String errorCode;

    private String errorMsg;

    public ConfigLoaderException(ConfigLoaderError error, String errorMsg) {
        this.errorCode = error.name();
        this.errorMsg = errorMsg;
    }

    public ConfigLoaderException(ConfigLoaderError error) {
        this.errorCode = error.name();
    }
}
