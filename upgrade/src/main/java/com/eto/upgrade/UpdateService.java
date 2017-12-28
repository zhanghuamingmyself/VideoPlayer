package com.eto.upgrade;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;



import java.io.File;

public class UpdateService extends Service
{  
  

	public static final  int MSG_CHECK_FLOATSERVICE = 1001;
	public static final  int DELAY_TIME = 5000;



      
    private static final String TAG = "UpdateService";

    boolean isRunning = false;
    Context mContext;

	int checkTime = 0; // =0 时，进行检查 指定服务是否启动

    @Override  
    public void onCreate()   
    {  
        // TODO Auto-generated method stub  
        super.onCreate();  
        mContext = this;
        isRunning = true;
		mHandler.sendEmptyMessageDelayed(MSG_CHECK_FLOATSERVICE, DELAY_TIME);
		LogFileUtils.setDebug(true);

		LogFileUtils.logTxt("Service Create()");
		checkInstalled(this);


    }  
    
	   @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

           LogFileUtils.logTxt("onStartCommand flags=" + flags + ",startId=" + startId + ",intent=" + intent);

		   if(intent != null) {
			   int cmd = intent.getIntExtra("cmd", 0);

			   if (cmd != 0) {
				   mHandler.sendEmptyMessage(cmd);
			   }

			   String action = intent.getAction();
			   if (InstallApk.ACTION_INSTALL.equals(action)){
				   final String param1 = intent.getStringExtra(InstallApk.EXTRA_PARAM1);
				   final String param2 = intent.getStringExtra(InstallApk.EXTRA_PARAM2);
				   checkTime = 4;
				   install(param1, param2);
				   LogFileUtils.logTxt("Install "+param1+","+param2);

			   }
		   }
		   
		   
			return super.onStartCommand(intent, flags, startId);
	}


	void install(String path,String type){

		if("0".equals(type)){
			InstallApk.openFile(this, new File(path));
		}else if("1".equals(type)){
			InstallApk.installApk_Cmd(this, path);
		}


	}
  
    @Override  
    public IBinder onBind(Intent intent)  
    {  
        // TODO Auto-generated method stub  
        return null;  
    }  
  
  
      
    @Override  
    public void onDestroy()   
    {  
        // TODO Auto-generated method stub
        LogFileUtils.logTxt("onDestroy");
        super.onDestroy();  
      
    }  
    

    Handler mHandler = new Handler(){
    	public void dispatchMessage(android.os.Message msg) {
    		switch (msg.what) {
			case MSG_CHECK_FLOATSERVICE:
				if(checkTime > 0){
				checkTime --;

				}else {
					checkRunning(mContext);
				}
				mHandler.sendEmptyMessageDelayed(MSG_CHECK_FLOATSERVICE,DELAY_TIME);
//				System.out.println("MSG_CHECK_FLOATSERVICE");
				break;

			default:
				break;
			}
    	};
    };
    
    

    static boolean package1_installed =  false;
    static boolean package2_installed =  false;
	static final String package1_name = "com.eto.advvideo";
	static final String package2_name = "com.eto.louyu";

	public static void checkInstalled(Context context){

		if(CommonUtils.isAppInstalled(context,package1_name)){
			package1_installed = true;
		}else{
			package1_installed = false;
		}

		if(CommonUtils.isAppInstalled(context,package2_name)){
			package2_installed = true;
		}else{
			package2_installed = false;
		}

		LogFileUtils.logTxt(package1_name+": installed="+package1_installed+","+package2_name+": installed="+package2_installed);
	}

    public static void checkRunning(Context context){
		try {

			if(package1_installed){
				boolean isRunning = CommonUtils.isServiceRunning(context, package1_name+".service.FloatService");
				if (!isRunning) {
					Intent in = new Intent();
					in.setClassName(package1_name, package1_name+".service.FloatService");
					context.startService(in);
					LogFileUtils.logTxt("start service "+package1_name+".service.FloatService");
				}
				System.out.println("upgrade: "+package1_name+" is installed And service run state="+isRunning);
			}else if(package2_installed){
				boolean isRunning = CommonUtils.isServiceRunning(context, package2_name+".service.FloatService");
				if (!isRunning) {
					Intent in = new Intent();
					in.setClassName(package2_name, package2_name+".service.FloatService");
					context.startService(in);
					LogFileUtils.logTxt("start service "+package2_name+".service.FloatService");
				}
				System.out.println("upgrade: "+package2_name+" is installed And service run state="+isRunning);
			}else{
				System.out.println("upgrade: all listen app is not installed");
			}



		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("upgrade:  start fail");
		}
	}
      
}  
