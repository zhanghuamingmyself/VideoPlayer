package com.zhanghuaming.videoplayer.config;

import android.os.Environment;

/**
 * Created by zhang on 2017/12/14.
 *配置文件
 * http://localhost:8080/gmsystem/videofile/findbypath.do?path=1.mp4
 */

public class StaticCfg {
    private static final String serviceIP ="http://"+ "192.168.1.103";//视频服务器ip或域名
    private static final String servicePort = "8080";//视频服务器端口号
    public static final String baseUrl = serviceIP+":"+servicePort+"/gmsystem/";
    public static final String getURLBaseUrl = "video/findall.do";//服务器查看全部视频名
    public static final String downBaseUrl = "videofile/findbypath.do";//服务器视频下载路径
    public static final String videoPath = Environment.getExternalStorageDirectory().getPath()+"/video/";//视频存储位置

    public static final int BAUDRATE = 19200;//串口波特率
    public static final String TTY_PATH = "/dev/ttyS3";//串口位置;
}
