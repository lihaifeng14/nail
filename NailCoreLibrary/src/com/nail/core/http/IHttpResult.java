package com.nail.core.http;

public interface IHttpResult {
    public void onRequestSuccess(int id, IBaseContent content);
    public void onRequestFailed(int id, HttpException e);
}