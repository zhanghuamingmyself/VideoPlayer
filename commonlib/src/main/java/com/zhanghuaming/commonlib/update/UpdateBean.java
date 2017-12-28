package com.zhanghuaming.commonlib.update;


import com.eto.commonlib.base.JsonBean;

public class UpdateBean extends JsonBean {

    public int rstId;
    public SoftInfo object;

    public static class SoftInfo extends JsonBean{

        public String appName; //
        public String packageName; //包名
        public String versionName;      //当前版本
        public String updateInfo;  //当前版本更新说明
        public String downloadUrl;  // 升级下载地址
        public String md5;
        public int fileSize;// kb
    }





}
