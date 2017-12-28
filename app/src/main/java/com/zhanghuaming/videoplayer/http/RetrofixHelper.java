package com.zhanghuaming.videoplayer.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhanghuaming.videoplayer.bean.DownloadInfo;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhanghuaming.videoplayer.config.StaticCfg.baseUrl;

/**
 * Created by zhang on 2017/12/14.
 */

public class RetrofixHelper {

    private static Retrofit retrofit;
    private static Gson gson;
    private static RetrofixServiceInteface serviceInteface;

    public static void checkRetrofix() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd hh:mm:ss")
                    .create();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        if (serviceInteface == null) {
            serviceInteface = retrofit.create(RetrofixServiceInteface.class);
        }
    }

    public static Observable getVideoURL() {
        checkRetrofix();
        return serviceInteface.getVideoURL()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());

    }

    public static Observable downloadVideo(final String name) {
        checkRetrofix();
        return serviceInteface.downloadVideo(name)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).flatMap(new Func1<ResponseBody, Observable<?>>() {
                    @Override
                    public Observable<DownloadInfo> call(final ResponseBody responseBody) {
                        return Observable.create(new Observable.OnSubscribe<DownloadInfo>() {
                            @Override
                            public void call(Subscriber<? super DownloadInfo> subscriber) {
                                subscriber.onNext(new DownloadInfo(name, responseBody));
                            }
                        });
                    }
                });
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
