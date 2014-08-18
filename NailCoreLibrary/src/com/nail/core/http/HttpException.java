package com.nail.core.http;

public class HttpException extends Exception {
    private int mErrorCode;
    private String errorJson;

    public final String getErrorJson() {
        return errorJson;
    }

    public final void setErrorJson(String errorJson) {
        this.errorJson = errorJson;
    }

    public final int getmErrorCode() {
        return mErrorCode;
    }

    public final void setmErrorCode(int mErrorCode) {
        this.mErrorCode = mErrorCode;
    }

    public HttpException(int errorCode) {
        mErrorCode = errorCode;
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }
}