package com.zhanghuaming.videoplayer.utlis;





import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/8/22.
 */

public class LogFileUtils {
    private static final String TAG =LogFileUtils.class.getSimpleName();
    static String path= Environment.getExternalStorageDirectory().getPath()+"/vLog/";
    static String curFileName="" ;
    static FileOutputStream fos;
    static boolean isDebug = SLog.DEBUG;
    public static void logTxt(String txt){
        if(!isDebug){
            return;
        }

        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String name = "video-"+year + "-"+month +"-"+day+".log";

        String hour =  c.get(Calendar.HOUR_OF_DAY)+"";
        String min = c.get(Calendar.MINUTE)+"";
        String sec = c.get(Calendar.SECOND)+"";
        if(hour.length() < 2){
            hour="0"+hour;
        }
        if(min.length() < 2){
            min="0"+min;
        }
        if(sec.length() < 2){
            sec="0"+sec;
        }

        String strTime = hour+":"+min+":"+sec+"->";



        if(!curFileName.equals(name)){
            try {
                fos = new FileOutputStream(new File(path+name),true);
                curFileName = name;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            fos.write(strTime.getBytes());
            fos.write(txt.getBytes());
            fos.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
