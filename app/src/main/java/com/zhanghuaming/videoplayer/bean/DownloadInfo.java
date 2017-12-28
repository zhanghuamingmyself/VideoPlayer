package com.zhanghuaming.videoplayer.bean;

import okhttp3.ResponseBody;

/**
 * Created by zhang on 2017/12/25.
 */

public class DownloadInfo {

    public DownloadInfo(String fileName, ResponseBody responseBody) {
        this.fileName = fileName;
        this.responseBody = responseBody;
    }

    public String fileName;
    public ResponseBody responseBody;
}
