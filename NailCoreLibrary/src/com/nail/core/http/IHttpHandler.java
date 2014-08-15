package com.nail.core.http;

public interface IHttpHandler {
    public void sendRequest(AsyncHttpRequest request);
    public void cancelRequest(int id);
    public void cancelRequests();
}