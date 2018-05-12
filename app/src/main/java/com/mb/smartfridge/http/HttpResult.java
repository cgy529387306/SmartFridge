package com.mb.smartfridge.http;

/**
 * Created by liukun on 16/3/5.
 */
public class HttpResult<T> {

    /** 服务器返回结果状态码 */
    private int status;

    /** 服务器返回的具体业务数据，为了统一处理，此处是指成功返回的结果模型 */
    private T data;

    /** 服务器错误信息 */
    private String text;

    public HttpResult(int status, T data, String text) {
        this.status = status;
        this.data = data;
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return "HttpResult{" +
                "status=" + status +
                ", data=" + data +
                ", text='" + text + '\'' +
                '}';
    }
}
