package com.zhanghuaming.commonlib.update;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.eto.commonlib.http.BaseLoad;
import com.eto.commonlib.http.LoadData;
import com.eto.commonlib.http.NetHandler;

public class UpdateApk {
	
	 static String _dirName = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS;
	 static boolean _isDown = false;
//	 public static  String UPDATE_URL = "http://dev.klplus.easyto.cc";
	 public static  String UPDATE_URL = "http://klplus.easyto.cc";
//	 public static  String UPDATE_URL = "http://192.168.99.145:3000";
	public static final String UPDATE_METHOD = "KLPlus/appVersion/downloadApp.do";
	 static final String UPDATE_TOKEN = "xxxx";



	public void setUpdateUrl(String url){
		UPDATE_URL = url;
	}

	static  int mInstallType = 0;   //每个apk调用次模块升级自身应app
	
	
	/**
	 * 主activity 调用此接口
	 * @param context
	 */

	public static boolean isDownloading(){
		return _isDown;
	}

	public static void CheckUpdate(Context context ){
		MyAppInfo info = getAppInfo_BySelf(context);
		if(info == null){
			return;
		}
		CheckUpdate_OtherApk(context, info.packageName, info.versionName, 0);
	}
	public static void CheckUpdate(Context context,int installType ){
		MyAppInfo info = getAppInfo_BySelf(context);
		if(info == null){
			return;
		}
		CheckUpdate_OtherApk(context, info.packageName, info.versionName, installType);
	}
	
	public static void CheckUpdate_OtherApk(final Context context,String packageName,String versionName,int installType){
		if (_isDown) {
		  return;
		}
		if(!DeviceUtils.isNetworkConnected(context)){
			return;
		}
		mInstallType = installType;

		new BaseLoad.GetThread(UPDATE_URL,UPDATE_METHOD,new String[]{"packageName","verNum"},new String[]{packageName,versionName},
				new NetHandler(context, new NetHandler.NetHandlerListener() {
			@Override
			public void OnSuccess(int agr1, int arg2, Object obj) {
				try {
					UpdateBean msg = (UpdateBean) obj;

					int installType = mInstallType;

					System.out.println("mInstallType"+installType);



					if(msg.rstId == 1){// 需要更新


						if(installType == 0) {

							//提示下载
							if (msg.object != null) {
								showDialog_Update(context, msg);
							}
						}else{
							//不弹提示框
							DownloadAndUpdate(context,msg);
						}

					}else{

						if(installType == 0) {
							myToast(context,"已经是最新版本");
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void OnFail(int value) {

			}
		}).getHandler("", false),UPDATE_TOKEN,new LoadData.GetObjResult<UpdateBean>(new UpdateBean())).start();

	}

	public static  void NotifyDownloadApk(Context context,String url,String md5){
		if (_isDown) {
			System.out.println("正在下载，请稍后再下载......");
			return;
		}

		UpdateBean tmp = new UpdateBean();
		tmp.object =new UpdateBean.SoftInfo();
		tmp.object.md5  = md5;
		tmp.object.downloadUrl = url;
		mInstallType = 1;
		DownloadAndUpdate(context,tmp);
	}


	static  void showDialog_Update(final Context context,final UpdateBean soft) {


		String msg = "新版本："+soft.object.versionName+"\n"+"更新信息\n"+soft.object.updateInfo;
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("升级提醒");
		builder.setMessage(msg);
		builder.setPositiveButton("现在升级", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				DownloadAndUpdate(context,soft);
			}
		});
		builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
//					setTitle("");
			}
		});
		builder.show();
	}



	static  void DownloadAndUpdate(final Context context,final UpdateBean soft){
		_isDown = true;
		new Thread() {
			public void run() {

				String fileName = DownloadApk(soft.object.downloadUrl,soft.object.md5);
				try {
					String fMd5 = MD5Util.fileMD5(fileName);
					System.out.println("fmd5:"+fMd5+",webmd5:"+soft.object.md5);
					if (fMd5.equalsIgnoreCase(soft.object.md5)) { //文件没有下载出错
						int installType = mInstallType;

						if (installType == 0) {
							InstallApk.openFile(context, new File(fileName));
						} else {
							InstallApk.InstallApk_ByOther(context, new File(fileName));
						}
					} else {

						myToast(context, "下载文件出错，md5校验失败！");

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					myToast(context, "下载文件出错！");

				}

			}


		}.start();

	}



	static  String DownloadApk(String _urlStr,String md5){
		String newFilename = _urlStr.substring(_urlStr.lastIndexOf("/")+1);
		newFilename = _dirName +"/" + newFilename;
		File file = new File(newFilename);
		//如果目标文件已经存在，则删除。产生覆盖旧文件的效果
		if(file.exists())
		{

			try {
				String fmd5 = MD5Util.fileMD5(new FileInputStream(file));  //不重复下载
				if(md5.equalsIgnoreCase(fmd5)){
					return newFilename;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			file.delete();


		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			_isDown = true;
	         // 构造URL   
	         URL url = new URL(_urlStr);   
	         // 打开连接   
	         URLConnection con = url.openConnection();
	         //获得文件的长度
	         int contentLength = con.getContentLength();
	         System.out.println("长度 :"+contentLength);
	         // 输入流   
	         InputStream is = con.getInputStream();  
	         // 1K的数据缓冲   
	         byte[] bs = new byte[1024*8];   
	         // 读取到的数据长度   
	         int len;   
	         // 输出的文件流   
	         OutputStream os = new FileOutputStream(newFilename);   
	         // 开始读取   
	         while ((len = is.read(bs)) != -1) {   
	             os.write(bs, 0, len);   
	         }  
	         // 完毕，关闭所有链接   
	         os.close();  
	         is.close();
	            
	} catch (Exception e) {
	        e.printStackTrace();
	}
		
		
		_isDown = false;
		return newFilename;
	}




	//--------------------------apk-----------------start---------------------------------------
	public static MyAppInfo getAppInfo_BySelf(Context context) {
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			MyAppInfo tmpInfo = new MyAppInfo();
			tmpInfo.setAppInfo(packageInfo,pm);

			return tmpInfo;
		} catch (Exception e) {
			Log.e("getAppInfo_BySelf", "Exception", e);
		}

		return null;
	}

	public static MyAppInfo getAppInfo_ByPackageName(Context context,String packageName) {
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
			MyAppInfo tmpInfo = new MyAppInfo();
			tmpInfo.setAppInfo(packageInfo,pm);
			return tmpInfo;
		} catch (Exception e) {
			Log.e("getAppInfo_pName", "Exception", e);
		}

		return null;
	}

	public static  MyAppInfo getApkInfo_ByApkFile(Context context,String apkFileName){
		try {
			PackageManager pm = context.getPackageManager();

			PackageInfo packageInfo = pm.getPackageArchiveInfo(apkFileName, PackageManager.GET_ACTIVITIES);
			if(packageInfo == null){
				packageInfo = pm.getPackageArchiveInfo(apkFileName, PackageManager.GET_SERVICES);
			}

			if(packageInfo != null){
				MyAppInfo tmpInfo = new MyAppInfo();
				tmpInfo.setAppInfo(packageInfo,pm,true);
				return tmpInfo;
			}
		} catch (Exception e) {
			Log.e("getApkInfo_ByApkFile", "Exception", e);
		}

		return null;
	}





	public static String getAppVersionName(Context context) {
		    String versionName = "";  
		    int versioncode = 0;  
		    try {  
		        // ---get the package info---  
		        PackageManager pm = context.getPackageManager();  
		        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
		        versionName = pi.versionName;
		        versioncode = pi.versionCode;
		        if (versionName == null || versionName.length() <= 0) {  
		            return "";  
		        }  
		    } catch (Exception e) {  
		        Log.e("VersionInfo", "Exception", e);  
		    }  
		    return versionName;  
		}

	public static String getAppPackageName(Context context) {
		String packageName = "";

		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			packageName = pi.packageName;

			if (packageName == null || packageName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("packageName", "Exception", e);
		}
		return packageName;
	}


	/**
	 * 用来判断服务是否运行.
	 * @param context
	 * @param className 判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context context,String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(30);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}








	void getInstallApkInfos(Context context){
		ArrayList<MyAppInfo> appList = new ArrayList<MyAppInfo>(); //用来存储获取的应用信息数据
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);

		for(int i=0;i<packages.size();i++) {
			PackageInfo packageInfo = packages.get(i);


			MyAppInfo tmpInfo = new MyAppInfo();
			tmpInfo.setAppInfo(packageInfo,pm);
			appList.add(tmpInfo);
		}
	}





	/////----------------apk--------------------------------------end ---------------------------
	 
	 



	static void myToast(final Context context,final String txt){

		if(context instanceof Activity){
			((Activity)context).runOnUiThread(new Runnable() {
				@Override
				public void run() {

					try {
						Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		}

		System.out.println(txt);
	}




	public static String  getRawFile(Context context,int rId){

		String ret = null;
		File target = new File(_dirName+"/tool.apk");



		InputStream is = null;
		FileOutputStream fos = null;

		try{
			//////////////////////////////////////////////////////////////////////

			// rId  R.raw.aa

			is = context.getResources().openRawResource(rId);
			fos = new FileOutputStream(target);
			byte[] b = new byte[1024*4];
			int len =0;
			while ((len = is.read(b)) >0){
				fos.write(b, 0, len);
			}
			ret = target.getAbsolutePath();


//            exec("chmod 777 "+target.getAbsolutePath());
		}
		catch (Exception e){

			e.printStackTrace();
		}finally
		{
			try {
				if(is != null) {
                    is.close();
                }
				if(fos != null) {
                    fos.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

}
