package com.zhanghuaming.videoplayer.http;

import com.zhanghuaming.videoplayer.bean.VideoURLBean;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import rx.Observable;

import static com.zhanghuaming.videoplayer.config.StaticCfg.downBaseUrl;
import static com.zhanghuaming.videoplayer.config.StaticCfg.getURLBaseUrl;

/**
 * Created by zhang on 2017/12/14.
 */

public interface RetrofixServiceInteface {

    @GET(getURLBaseUrl)
    Observable<List<VideoURLBean>> getVideoURL();

    @Streaming
    @GET(downBaseUrl)
    Observable<ResponseBody> downloadVideo(@Query("path") String path);
}
