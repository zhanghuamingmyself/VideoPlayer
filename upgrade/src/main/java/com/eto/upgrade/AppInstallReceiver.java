package com.eto.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2017/10/18.
 */

public class AppInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogFileUtils.logTxt("AppInstallReceiver onReceive");
        PackageManager manager = context.getPackageManager();
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            //Toast.makeText(context, "安装成功"+packageName, Toast.LENGTH_LONG).show();
            LogFileUtils.logTxt("安装成功"+packageName);
            UpdateService.checkInstalled(context);
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            UpdateService.checkInstalled(context);
            LogFileUtils.logTxt("卸载成功"+packageName);
           // Toast.makeText(context, "卸载成功"+packageName, Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            LogFileUtils.logTxt("替换成功"+packageName);
           // Toast.makeText(context, "替换成功"+packageName, Toast.LENGTH_LONG).show();
        }


    }

}
