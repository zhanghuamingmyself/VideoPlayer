package com.zhanghuaming.commonlib.update;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;



public class CommonUtils {

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd HH:mm:ss");
	}


	// Date(1435737511093) 转换成 Date----------------

	public static Date CSharpDateToDate(String cdate){

		Date dt = new Date();
		int start = cdate.indexOf('(');
		int end = cdate.indexOf(')');
		if(start > 0 && end > 0){
			String sub = cdate.substring(start+1,end);

			try {
				long t = Long.parseLong(sub);
				dt.setTime(t);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return dt;
	}


	//  /Date(1435737511093)/
	public static String CSharpDateToStr(String cdate,boolean toGMT){

		return getTimeStr(CSharpDateToDate(cdate),toGMT);

	}


	public static String getTimeStr(Date date) {
		return getTimeStr(date,"yyyy-MM-dd HH:mm",false);
	}

	public static String getTimeStr(Date date,boolean toGMT) {
		return getTimeStr(date,"yyyy-MM-dd HH:mm",toGMT);
	}

	public static String getTimeStr(Date date,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getTimeStr(Date date,String format,boolean toGMT) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		if(toGMT){
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		String currentTime = sdf.format(date);
		return currentTime;
	}


	public static Date getDateByStr(String dateStr){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			Date dt = sdf.parse(dateStr);
			return dt;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}



	//-------------------------

	public static double DoubleFomat(double d,int n){
		int wei = 1;
		for(int i = 0 ; i< n ;i++){
			wei=wei*10;
		}
		double  tmp = (int )(d * wei);
		return tmp /wei;
	}

	public static double DoubleFomatRound(double d,int n){
		int wei = 1;
		for(int i = 0 ; i< n ;i++){
			wei=wei*10;
		}
		double  tmp = (int )(((d * wei*10)+5)/10); //四舍五入   （乘10 ， 加5， 除以10）
		return tmp /wei;
	}

	//------------------

	public static   String intToStr(int val ,int strLen){
		String ret = String.valueOf(val);
		int cnt = ret.length();
		for(int i = cnt;i< strLen ; i++ ){
			ret = "0"+ret;
		}
		return ret;
	}


	public static  int strToInt(String data,int start,int len){

		int i =-1;
		if(data == null){
			return i;
		}

		if(data.length() < start){
			return i;
		}

		if(data.length() < start + len){
			return i;
		}

		try {
			String sub = data.substring(start,start+len);
			i = Integer.parseInt(sub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	public static  int byteToInt(byte[] data,int start,int len){

		int i =-1;
		if(data == null){
			return i;
		}

		if(data.length < start){
			return i;
		}

		if(data.length < start + len){
			return i;
		}

		try {
			String sub = new String(data,start,len);
			i = Integer.parseInt(sub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	public static  boolean moveBuf(byte[] data,int srcIndex,int desIndex,int len){


		if(data == null){
			return false;
		}

		int max = srcIndex > desIndex? srcIndex:desIndex;
		if(data.length < max ){
			return false;
		}


		if(data.length < max + len){
			return false;
		}

		for(int i = 0; i < len;i++){
			data[desIndex+i] = data[srcIndex+i];
		}

		return true;
	}





	///
	/**
	 * 用来判断服务是否运行.
	 * @param context
	 * @param className 判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext,String className) {
		boolean isRunning = false;
        try {
            ActivityManager activityManager = (ActivityManager)
                    mContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> serviceList
                    = activityManager.getRunningServices(60);
            if (serviceList.size()<=0) {
                return false;
            }
            for (int i=0; i<serviceList.size(); i++) {
                if (serviceList.get(i).service.getClassName().equals(className)) {
                    isRunning = true;
                    break;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return isRunning;
	}


	public static boolean isAppInstalled(Context context,String packageName){

		{
			final PackageManager packageManager = context.getPackageManager();
			// 获取所有已安装程序的包信息
			List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
			for ( int i = 0; i < pinfo.size(); i++ )
			{
				if(pinfo.get(i).packageName.equalsIgnoreCase(packageName))
					return true;
			}
			return false;
		}
	}
}
