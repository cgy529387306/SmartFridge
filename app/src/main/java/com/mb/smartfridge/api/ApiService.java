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

    @GET("top250")
    Observable<HttpResult<List<Subject>>> getTopMovie(@Query("start") int start, @Query("count") int count);
}
