package com.zhanghuaming.commonlib.update;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/10/17.
 */
public class MyAppInfo {

    public String appName;
    public String packageName;
    public String versionName;
    public int versionCode;
    public Drawable appIcon;

    public String sourceDir;



    void setAppInfo(PackageInfo packageInfo,PackageManager pm) {

        if (packageInfo != null) {

            this.packageName = packageInfo.packageName;
            this.versionName = packageInfo.versionName;
            this.versionCode = packageInfo.versionCode;

            if (pm != null && packageInfo.applicationInfo != null) {
                CharSequence cs = packageInfo.applicationInfo.loadLabel(pm);
                System.out.println("setAppInfo: appName=" + cs);
                if (cs != null) {
                    this.appName = cs.toString();
                }
                this.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                this.sourceDir = packageInfo.applicationInfo.sourceDir;
            }

        }
    }

    void setAppInfo(PackageInfo packageInfo,PackageManager pm,boolean isApkFile){

        if(packageInfo !=null) {

            this.packageName = packageInfo.packageName;
            this.versionName = packageInfo.versionName;
            this.versionCode = packageInfo.versionCode;

            if(!isApkFile) {
                if (pm != null && packageInfo.applicationInfo != null) {

                    CharSequence cs = packageInfo.applicationInfo.loadLabel(pm);

                    System.out.println("setAppInfo: appName=" + cs);
                    if (cs != null) {
                        this.appName = cs.toString();
                    }


                    this.appIcon = packageInfo.applicationInfo.loadIcon(pm);
                    this.sourceDir = packageInfo.applicationInfo.sourceDir;
                }
            }

        }


    }

}
