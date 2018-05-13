package com.mb.smartfridge.api;


import com.mb.smartfridge.entity.Subject;
import com.mb.smartfridge.entity.UserData;
import com.mb.smartfridge.http.HttpResult;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by liukun on 16/3/9.
 */
public interface ApiService {


    @POST("v1/user/login/mobile")
    Observable<HttpResult<UserData>> doLogin(@Query("mobile") String mobile, @Query("password") String password);
    @POST("v1/user/auth/register")
    Observable<HttpResult<UserData>> doRegister(@Query("mobile") String mobile, @Query("password") String password, @Query("code") String code);
    @POST("v1/user/sms/send")
    Observable<HttpResult<Object>> sendSms(@Query("mobile") String mobile, @Query("action") String action);
    @POST("v1/user/auth/getpw")
    Observable<HttpResult<Object>> getpw(@Query("mobile") String mobile, @Query("password") String password,@Query("code") String code);

    @GET("top250")
    Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);
}
