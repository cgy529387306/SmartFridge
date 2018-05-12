package com.mb.smartfridge.http.interceptor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCommonInterceptor implements Interceptor {

    private Map<String, String> headers;

    public HttpCommonInterceptor() {
    }

    public HttpCommonInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = addHeaders(request);
        return chain.proceed(request);
    }

    private Request addHeaders(Request request) {
        Request.Builder builder = request.newBuilder();
//        builder.addHeader("User-Agent", CommonUtils.getMobileInfo(MyApplication.getContext()));
//        builder.addHeader("deviceID", CommonUtils.getIMEI(MyApplication.getContext()));
//        builder.addHeader("Content-Language", MyApplication.getContext().getResources().getConfiguration().locale.getLanguage());
//        String token = UserInfoManager.getInstance().getToken();
//        if(!TextUtils.isEmpty(token)) {
//            builder.addHeader("Authorization", token);
//            LogUtils.d("SetToken", "Token = "+token);
//        }
//        builder.addHeader("version", String.valueOf(CommonUtils.getVersionCode(MyApplication.getContext())));
//        builder.addHeader("Version_Name", String.valueOf(CommonUtils.getVersionName(MyApplication.getContext())));

        if (headers != null) {
            Iterator iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry) iterator.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != null && !key.isEmpty() && value != null) {
                    builder.removeHeader(key);
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }

        return builder.build();
    }
}
