package com.zhanghuaming.commonlib.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;



import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/10/17.
 */
public class InstallApk {

    public static final String ACTION_INSTALL = "com.eto.upgrade.action.install";

    public static final String EXTRA_PARAM1 = "com.eto.upgrade.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.eto.upgrade.extra.PARAM2";

    public static final String INTALL_APP_PACKAGENAME= "com.eto.upgrade";
    public static final String INTALL_APP_SERVICENAME= "com.eto.upgrade.UpdateService";




    public static void InstallApk_ByOther(Context context,File file){

        //启动第三方安装服务进行安装

        try {

           MyAppInfo info = UpdateApk.getAppInfo_ByPackageName(context,INTALL_APP_PACKAGENAME);
            if(info != null) {
                Intent in = new Intent();
                in.setClassName(INTALL_APP_PACKAGENAME, INTALL_APP_SERVICENAME);
                in.setAction(ACTION_INSTALL);
                in.putExtra(EXTRA_PARAM1, file.getAbsolutePath());
                in.putExtra(EXTRA_PARAM2,"0");

                context.startService(in);

                //失去焦点
                Intent intent = new Intent();
                intent.setClassName("com.eto.advvideo", "com.eto.advvideo.service.FloatService");
                intent.putExtra("cmd",1003);   // FloatService.MSG_SETFOCUS);
                context.startService(intent);
            }else{
                Toast.makeText(context,"upgrade not  install",Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MSG_CHECK_FLOATSERVICE start fail");
        }


    }


    // 提示安装
    public static void openFile(Context context, File file) {
        // TODO Auto-generated method stub
        Log.i("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }


    //---------------另一个一个apk来执行这个函数-----


    public static boolean installApk_Cmd(Context context ,String apkFilePath){
        MyAppInfo info = UpdateApk.getApkInfo_ByApkFile(context,apkFilePath);
        if(info != null) {
            try {
                if(!info.packageName.equals(context.getPackageName())){
                    System.out.println("install apk " + apkFilePath);
                    String  ret = CommandUtils.hpcmdExec("pm install -r "+apkFilePath);
                    System.out.println(ret);
                    return true;
                }else{
                    myToast(context, "不能自我安装！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                myToast(context, "安装" + info.appName + "失败！");
            }
        }else{
            myToast(context,"安装 apk 失败！");

        }
        return false;

    }

    public static boolean installApk_Cmd_Sh(Context context ,String apkFilePath){
        MyAppInfo info = UpdateApk.getApkInfo_ByApkFile(context,apkFilePath);
        if(info != null) {
            try {
                if(!info.packageName.equals(context.getPackageName())){
                    System.out.println("install apk " + apkFilePath);
                    String  ret = CommandUtils.execCommand("pm install -r "+apkFilePath);
                    System.out.println(ret);
                    return true;
                }else{
                    myToast(context, "不能自我安装！");
                }

            } catch (Exception e) {
                e.printStackTrace();
                myToast(context, "安装" + info.appName + "失败！");
            }
        }else{
            myToast(context,"安装 apk 失败！");

        }
        return false;

    }

    public static void uninstallApk_Cmd(Context context ,String apkFilePath){
        MyAppInfo info = UpdateApk.getApkInfo_ByApkFile(context,apkFilePath);
        if(info != null) {
            try {
                if(!info.packageName.equals(context.getPackageName())){
                    System.out.println("uninstall apk "+info.packageName);
                    CommandUtils.execCommand("pm uninstall  "+info.packageName);
                }else{
                    myToast(context, "不能自我卸载！");

                }

            } catch (IOException e) {
                e.printStackTrace();
                myToast(context, "卸载" + info.appName + "失败！");
            }
        }else{
            myToast(context, "卸载 apk 失败！");

        }

    }

    static void myToast(final Context context,final String txt){

        if(context instanceof Activity){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
                }
            });

        }

        System.out.println(txt);
    }

}
