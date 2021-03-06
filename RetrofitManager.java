package com.android.driveclub.api;


import android.os.Build;
import android.util.ArrayMap;

import com.android.driveclub.ShareData;
import com.android.library.core.retrofit.interceptor.HeaderInterceptor;
import com.android.library.core.retrofit.interceptor.ParamsInterceptor;
import com.android.library.utils.SystemUtil;
import com.android.library.utils.Utils;

import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dugang on 2017/3/15.Retrofit管理类
 */

public class RetrofitManager {
    /**
     * 配置网络请求的url
     */
    private static String getBaseUrl() {
        return "http://192.168.1.11/driveclub/";
    }

    /**
     * 配置网络请求缓存
     */
    private static Cache getCache() {
        return new Cache(Utils.getContext().getCacheDir(), 1024 * 1024 * 50);
    }

    /**
     * 配置网络请求头
     */
    private static ArrayMap<String, String> getRequestHeader() {
        ArrayMap<String, String> header = new ArrayMap<>();
        header.put("app_version", SystemUtil.getVersionName());
        header.put("app_build", "" + SystemUtil.getVersionCode());
        header.put("device_name", Build.MODEL);
        header.put("device_platform", "Android");
        header.put("client_id", JPushInterface.getRegistrationID(Utils.getContext()));

        if (ShareData.getInstance().getAccount() != null)
            header.put("token", ShareData.getInstance().getAccount().getToken());

        return header;
    }

    /**
     * 配置网络请求体
     */
    public static ArrayMap<String, String> getRequestParams() {
        return null;
    }

    /**
     * 获取Retrofit
     */
    public static Retrofit getInstance() {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        //debug模式添加log信息拦截
        if (Utils.isDebug()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpBuilder.addInterceptor(interceptor);
        }
        okHttpBuilder.addNetworkInterceptor(new HeaderInterceptor(getRequestHeader()));
        okHttpBuilder.addNetworkInterceptor(new ParamsInterceptor(getRequestParams()));
        okHttpBuilder.cache(getCache());
        //设置连接超时
        okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS);
        //设置写超时
        okHttpBuilder.writeTimeout(10, TimeUnit.SECONDS);
        //设置读超时
        okHttpBuilder.readTimeout(10, TimeUnit.SECONDS);


        Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(getBaseUrl());
        retrofitBuilder.client(okHttpBuilder.build());
        retrofitBuilder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create());
        return retrofitBuilder.build();
    }
}
