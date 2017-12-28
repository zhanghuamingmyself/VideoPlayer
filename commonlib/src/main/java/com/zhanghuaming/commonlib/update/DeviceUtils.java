package com.zhanghuaming.commonlib.update;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class DeviceUtils {


	public static  boolean AP_NEED_PWD = true;

	/**
	 * 是否连接上网络
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {

		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}



	public static String getMac(Context context){

		if (context != null) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = wifi.getConnectionInfo();

			return info.getMacAddress();
		}else{
			return "";
		}
	}




	public static String getIP(Context context){

		if (context != null) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

			WifiInfo info = wifi.getConnectionInfo();
			int ipI = info.getIpAddress();
			return intToIp(ipI);
		}else{
			return "";
		}
	}

	private static String intToIp(int i) {

		return (i & 0xFF ) + "." +
				((i >> 8 ) & 0xFF) + "." +
				((i >> 16 ) & 0xFF) + "." +
				( i >> 24 & 0xFF) ;
	}


	public static boolean checkIpFormat(String ip) {
		if (ip == null)
			return false;
		String[] sections = ip.split("\\.");
		if (sections.length == 4) {
			if (sections[0].length() <= 3 && sections[1].length() <= 3
					&& sections[2].length() <= 3){// && sections[3].length() <= 3) {
				try {
					Integer.parseInt(sections[0]);
					Integer.parseInt(sections[1]);
					Integer.parseInt(sections[2]);
//						Integer.parseInt(sections[3]); //可以跟端口号  192.169.100:2244
					return true;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return false;
	}

	public static String getIp4(String ip) {
		if (ip == null)
			return "";
		String[] sections = ip.split("\\.");
		if (sections.length == 4) {
			if (sections[0].length() <= 3 && sections[1].length() <= 3
					&& sections[2].length() <= 3){// && sections[3].length() <= 3) {
				try {
//						Integer.parseInt(sections[0]);
//						Integer.parseInt(sections[1]);
//						Integer.parseInt(sections[2]);
					String tmp = sections[3];
					int index = tmp.indexOf(':');
					if(index >=0){
						tmp = tmp.substring(0,index);
					}
//						Integer.parseInt(sections[3]); //可以跟端口号  192.169.100:2244
					return tmp;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return "";
	}
	public static String getIpGetway(String ip) {
		if (ip == null)
			return "";
		String[] sections = ip.split("\\.");
		if (sections.length == 4) {
			return sections[0]+"."+sections[1]+"."+sections[2]+".1";

		}

		return "";
	}


	public static String getIp3(String ip) {
		if (ip == null)
			return "";
		String[] sections = ip.split("\\.");
		if (sections.length == 4) {
			if (sections[0].length() <= 3 && sections[1].length() <= 3
					&& sections[2].length() <= 3){// && sections[3].length() <= 3) {
				try {
//						Integer.parseInt(sections[0]);
//						Integer.parseInt(sections[1]);
//						Integer.parseInt(sections[2]);
//						Integer.parseInt(sections[3]); //可以跟端口号  192.169.100:2244
					String tmp = sections[2];

					return tmp;
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		return "";
	}


	//////
	public static  String getLocalIpAddress()
	{

		String ips = "";
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()))
					{
						Log.e("IpAddress", inetAddress.getHostAddress());
						ips+=inetAddress.getHostAddress()+"\n";
						inetAddress.getAddress();
					}
				}
			}
		}
		catch (Exception ex)
		{
			Log.e(" IpAddress", ex.toString());
		}
		return ips;
	}

	public static String getLanIp(){
		String ret  = "";
		String ips = getLocalIpAddress();
		String[] iparr = ips.split("\n");
		for(String ip : iparr){
			if(!ip.endsWith(".1")){
				ret =ip;
			}
		}

		return ret;
	}

	public static String getLocalMacAddressFromIp(Context context) {
		String mac_s= "";
		try {
			byte[] mac;

			NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(getLanIp()));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mac_s;
	}

	public static String getMac12(Context context){

		String ret = getLocalMacAddressFromIp(context);
		return ret.replace(":", "");
	}

	public static  String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}

	// wifi热点开关
	public static boolean setWifiApEnabled(Context context,boolean enabled,String ssid,String pwd) {

		if(!AP_NEED_PWD){
			return setWifiApEnabled( context, enabled, ssid );
		}

		String networkSSID = ssid;
		String networkPass = pwd;


		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (enabled) { // disable WiFi in any case
			//wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi

			wifiManager.setWifiEnabled(false);
		}
		try {
			//热点的配置类
			WifiConfiguration apConfig = new WifiConfiguration();
			//配置热点的名称(可以在名字后面加点随机数什么的)
			apConfig.SSID = networkSSID;

			apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			apConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);




//            if (password != null) {
//                if (password.length() < 8) {
//                    throw new Exception("the length of wifi password must be 8 or longer");
//                }
			// 设置wifi热点密码

//            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedKeyManagement.set(4);
			apConfig.preSharedKey = networkPass;
//            }
//			wifiManager.getWifiApState();

			//通过反射调用设置热点
			Method method = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			//返回热点打开状态
			return (Boolean) method.invoke(wifiManager, apConfig, enabled);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	public static boolean setWifiApEnabled(Context context,boolean enabled,String ssid) {


		String networkSSID = ssid;



		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (enabled) { // disable WiFi in any case
			//wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi

			wifiManager.setWifiEnabled(false);
		}
		try {
			//热点的配置类
			WifiConfiguration apConfig = new WifiConfiguration();
			//配置热点的名称(可以在名字后面加点随机数什么的)
			apConfig.SSID = networkSSID;
            apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


			//通过反射调用设置热点
			Method method = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			//返回热点打开状态
			return (Boolean) method.invoke(wifiManager, apConfig, enabled);
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	public static String getwifiApInfo(Context context){

		if (context != null) {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);



//			Method method = wifi.getClass().getMethod(
//					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
//			//返回热点打开状态
//			return (Boolean) method.invoke(wifiManager, apConfig, enabled);

			return wifi.getConnectionInfo().getSSID();
		}else{
			return "";
		}
	}

	/**判断热点开启状态*/
	public static  boolean isWifiApEnabled(Context context) {
		return getWifiApState(context) == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
	}

	static WIFI_AP_STATE getWifiApState(Context context){
		int tmp;
		try {
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			// Fix for Android 4
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}

	public   enum WIFI_AP_STATE {
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING,  WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}

	public static boolean connectApNoPwd(Context context,String ssid){
		String networkSSID = "\"" + ssid + "\"";


		try {
			DebugLog.log("ssid=" + getWifiSSID(context),context);
			if( networkSSID.equals(getWifiSSID(context))){
				return true;
			}
			DebugLog.log("ssid=" + ssid,context);

			WifiConfiguration conf = new WifiConfiguration();
			conf.allowedAuthAlgorithms.clear();
			conf.allowedGroupCiphers.clear();
			conf.allowedKeyManagement.clear();
			conf.allowedPairwiseCiphers.clear();
			conf.allowedProtocols.clear();

			conf.SSID = networkSSID;   // Please note the quotes. String should contain ssid in quot
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			conf.status = WifiConfiguration.Status.ENABLED;


			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			if(!wifiManager.isWifiEnabled()){  //没有打开wifi ，则打开wifi
				boolean b = wifiManager.setWifiEnabled(true);
				DebugLog.log("open wifi ="+b,context);
			}

			int ret = wifiManager.addNetwork(conf);  // -1 fail
			Toast.makeText(context, " addNetwork ret="+ret, Toast.LENGTH_LONG).show();
			if(ret != -1){
				return wifiManager.enableNetwork(ret, true);   // 直接连接

			}

			//有相同ssid时，networkId 不一样
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			for( WifiConfiguration i : list ) {
				if(i.SSID != null && i.SSID.equals( networkSSID)) {
					wifiManager.enableNetwork(i.networkId, true);
					DebugLog.log("networkId=" + i.networkId,context);

					return true;
					//break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}


	public static boolean connectAp(Context context,String ssid,String pwd){

		if(!AP_NEED_PWD){
			return connectApNoPwd(context,ssid);
		}



		String networkSSID = "\"" + ssid + "\"";
		String networkPass = pwd;

		try {
			DebugLog.log("ssid=" + getWifiSSID(context),context);
			if( networkSSID.equals(getWifiSSID(context))){
				return true;
			}
			DebugLog.log("ssid=" + ssid,context);

			WifiConfiguration conf = new WifiConfiguration();
			conf.SSID = networkSSID;   // Please note the quotes. String should contain ssid in quotes
			//Then, for WEP network you need to do this:


			conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);


			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			//conf.allowedKeyManagement.set(4);
//    	conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			//For WPA network you need to add passphrase like this:

			conf.preSharedKey = "\""+ networkPass +"\"";
			conf.status = WifiConfiguration.Status.ENABLED;
			//For Open network you need to do this:

//    	conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			//Then, you need to add it to Android wifi manager settings:

			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

			if(!wifiManager.isWifiEnabled()){  //没有打开wifi ，则打开wifi

				boolean b = wifiManager.setWifiEnabled(true);
				DebugLog.log("open wifi ="+b,context);

				//关闭热点
				try {
					Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
					method.setAccessible(true);
					WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
					Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
					method2.invoke(wifiManager, config, false);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}


			int ret = wifiManager.addNetwork(conf);  // -1 fail
			if(ret != -1){
				wifiManager.enableNetwork(ret, true);
				return true;
			}
			//And finally, you might need to enable it, so Android connects to it:

			//Toast.makeText(context, "ret="+ret, Toast.LENGTH_LONG).show();

			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			if(list != null) {
				for (WifiConfiguration i : list) {
					if (i.SSID != null && i.SSID.equals(networkSSID)) {
						wifiManager.enableNetwork(i.networkId, true);
						DebugLog.log("networkId=" + i.networkId,context);

						return true;
						//break;
					}
				}
			}else{
				DebugLog.log("getConfiguredNetworks  list  is  null",context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String getApSSID(Context context){
		String ssid = "";
		if(!isWifiApEnabled(context)){
			return ssid;
		}
		try {
			WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			//拿到getWifiApConfiguration()方法
			Method method = manager.getClass().getDeclaredMethod("getWifiApConfiguration");
			//调用getWifiApConfiguration()方法，获取到 热点的WifiConfiguration
			WifiConfiguration configuration = (WifiConfiguration) method.invoke(manager);
			ssid = configuration.SSID;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return ssid;
	}

	public static boolean connectApByKey(Context context,String ssid,String pwd){

		String networkSSID = "\"" + ssid + "\"";
		String networkPass = pwd;

		try {
			DebugLog.log("ssid=" + getWifiSSID(context),context);
			if( networkSSID.equals(getWifiSSID(context))){
				return true;
			}
			DebugLog.log("ssid=" + ssid,context);

			WifiConfiguration conf = new WifiConfiguration();
			conf.SSID = networkSSID;   // Please note the quotes. String should contain ssid in quotes
			//Then, for WEP network you need to do this:


			conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);


			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			//conf.allowedKeyManagement.set(4);
//    	conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			//For WPA network you need to add passphrase like this:

			conf.preSharedKey = "\""+ networkPass +"\"";
			conf.status = WifiConfiguration.Status.ENABLED;
			//For Open network you need to do this:

//    	conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			//Then, you need to add it to Android wifi manager settings:

			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

			if(!wifiManager.isWifiEnabled()){  //没有打开wifi ，则打开wifi
				boolean b = wifiManager.setWifiEnabled(true);
				DebugLog.log("open wifi ="+b,context);
			}


			int ret = wifiManager.addNetwork(conf);  // -1 fail
			if(ret != -1){
				wifiManager.enableNetwork(ret, true);
				return true;
			}
			//And finally, you might need to enable it, so Android connects to it:

			//Toast.makeText(context, "ret="+ret, Toast.LENGTH_LONG).show();

			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			if(list != null) {
				for (WifiConfiguration i : list) {
					if (i.SSID != null && i.SSID.equals(networkSSID)) {
						wifiManager.enableNetwork(i.networkId, true);
						DebugLog.log("networkId=" + i.networkId,context);

						return true;
						//break;
					}
				}
			}else{
				DebugLog.log("getConfiguredNetworks  list  is  null",context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean connectAp(Context context,String ssid){
		String networkSSID = ssid  ; //保存的已经存在引号
		try {
			DebugLog.log("ssid=" + getWifiSSID(context),context);
			if( networkSSID.equals(getWifiSSID(context))){
				return true;
			}
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
			DebugLog.log("wifiList:"+list.size(),context);
			for( WifiConfiguration i : list ) {

				if(i.SSID != null && i.SSID.equals( networkSSID)) {
					wifiManager.disconnect();
					wifiManager.enableNetwork(i.networkId, true);
					DebugLog.log("id_nopwd="+i.networkId,context);
					wifiManager.reconnect();

					return true;
					//break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean disconnectAp(Context context){

		try {
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.disconnect();
			wifiManager.reconnect();
			DebugLog.log("disconnectAp",context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static ScanResult scanAp(Context context,String ssid){

		try {
			DebugLog.log("scanAp start find"+ssid,context);
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			List<ScanResult> list = wifiManager.getScanResults();
			for( ScanResult i : list ) {
				if (i.SSID != null) {
					if (i.SSID.contains(ssid)) {
						DebugLog.log("scanAp has find"+ssid,context);
//						if (i.capabilities.contains("PSK")) {
//							item.key = "*";
//						} else {
//							item.key = "";
//						}
						return i;
					}
				}
			}
			wifiManager.startScan();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}






	//region 系统信息


	public  static final String CPUINFO= "/proc/cpuinfo";
	public  static final String MEMINFO = "/proc/meminfo";
	public static final String STATINFO = "/proc/stat";



	/**
	 * path :  /proc/cpuinfo :cpu硬件信息；/proc/meminfo ：内存信息
	 *        /proc/stat //cpu运行信息
	 * @return
	 *
	 *
	 */
	private static List<String> getProcInfo(String path){

		String line="";
		List<String> list = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr, 8192);
			while((line = br.readLine()) != null){
				list.add(line);
			}
			br.close();



		} catch (IOException e) {

		}
		return list;
	}

	private static String getItemValue(String line,String split){
		String[] strs = line.split(split);
		if(strs.length > 1){
			return strs[1].trim();
		}
		return "";
	}

	private static String getDeviceValue(String path,String itemName,String split){
		List<String> list = getProcInfo(path);
		for(String item : list){
			if(item != null && item.startsWith(itemName)){
				return getItemValue(item,split);
			}
		}
		return "";
	}

	private static String getDeviceValue(List<String> list,String itemName,String split){
		for(String item : list){
			if(item != null && item.startsWith(itemName)){
				return getItemValue(item,split);
			}
		}
		return "";
	}

	//有的手机读不出来
	public static String getCpuNo(){

		return getDeviceValue(CPUINFO,"Serial",":");

	}

	public static int getMemTotal_M(){

		String ret = getDeviceValue(MEMINFO, "MemTotal", ":");

		if(ret.length() ==0){
			return 0;
		}

		try {
			String[] strs = ret.split(" ");
			int kb = Integer.parseInt(strs[0]);
			int m = kb/1024;
			return m;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return 0;

	}

	public static int[] getMemInfo(){

		int[] vals = new int[5];
		String[] val_strs =new String[5];

		List<String > list = getProcInfo(MEMINFO);



		int i = 0;
		val_strs[i++] = getDeviceValue(list, "MemTotal", ":");
		val_strs[i++] = getDeviceValue(list, "MemFree", ":");
		val_strs[i++] = getDeviceValue(list, "SwapCached", ":");

		val_strs[i++] = getDeviceValue(list, "Buffers", ":");
		val_strs[i++] = getDeviceValue(list, "Cached", ":");



		i = 0;
		for(String str :val_strs) {
			try {
				String[] strs = str.split(" ");
				int kb = Integer.parseInt(strs[0]);
				vals[i] = kb / 1024;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			i++;
		}

		return vals;

	}




	public  static String getDeviceNo(Context context){
		String  no = "A2F3"+getSerialNumber();
		String ret = MD5Util.MD5(no);
		return ret;
	}



	public static int[]  getCpuRunInfo(){
		int[] rets =new int[2];
		try {
			String ret = CommandUtils.execCommand_RetLine("top -n 1 -m 2", 1);//读取第一行, -n : 刷新一次
			String[] cpuInfo = ret.split(",");
			for(String str : cpuInfo){
				str = str.trim();
				if(str.startsWith("User")){

					String tmp  = str.replace("User","").trim();
					tmp =tmp.replace("%","");

					rets[0] = Integer.parseInt(tmp);
				}else if(str.startsWith("System")){
					String tmp  = str.replace("System","").trim();
					tmp =tmp.replace("%","");

					rets[1] = Integer.parseInt(tmp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return  rets;
	}

	public static int[]  readSdcardInfo() {
		int[] rets =new int[2];

		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();

			rets[0] = (int)(blockSize*blockCount/1024/1024);
			rets[1] = (int)(blockSize*availCount/1024/1024);
		}

		return rets;
	}


	/*
	------------cpuinfo----------------------------------------
Processor       : ARMv7 Processor rev 5 (v7l)
processor       : 0
BogoMIPS        : 4800.00

Features        : swp half thumb fastmult vfp edsp thumbee neon vfpv3 tls vfpv4
idiva idivt
CPU implementer : 0x41
CPU architecture: 7
CPU variant     : 0x0
CPU part        : 0xc07
CPU revision    : 5

Hardware        : sun8i
Revision        : 0000
Serial          : 601000f10008a8928936


	------------------meminfo-----------------------------------------
MemTotal:         636376 kB
MemFree:          172584 kB
Buffers:           11564 kB
Cached:           179044 kB
SwapCached:            0 kB
Active:           207728 kB
Inactive:         138552 kB
Active(anon):     155692 kB
Inactive(anon):      284 kB
Active(file):      52036 kB
Inactive(file):   138268 kB
Unevictable:           0 kB
Mlocked:               0 kB
HighTotal:         18432 kB
HighFree:            896 kB
LowTotal:         617944 kB
LowFree:          171688 kB
SwapTotal:        262140 kB
SwapFree:         262140 kB
Dirty:                 0 kB
Writeback:             0 kB
AnonPages:        155652 kB
Mapped:            96860 kB
Shmem:               324 kB
Slab:              28164 kB
SReclaimable:      11584 kB
SUnreclaim:        16580 kB
KernelStack:        3768 kB
PageTables:         5516 kB
NFS_Unstable:          0 kB
Bounce:                0 kB
WritebackTmp:          0 kB
CommitLimit:      580328 kB
Committed_AS:   13434280 kB
VmallocTotal:     385024 kB
VmallocUsed:       59944 kB
VmallocChunk:     212992 kB

--------------------------stat-------------------------------------------------
cpu  51949 436 75147 6225906 1085 4 2296 0 0 0
cpu0 33734 177 50047 742622 41 4 2176 0 0 0
intr 3333214 0 0 0 0 0 0 0 0 0 0 0 2 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 943728 0
0 1 1466 0 1 0 0 0 52 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
0 0 0 0 0 0 0 0 3 0 0 0 20305 4564 0 0 0 0 0 0 0 0 0 0 369580 38317 0 0 0 0 0 0
0 0 0 498720 0 0 0 0 0 0 0 0 0 0 0 0 3 444911 0 0 0 0 0 0 0 0 0 0 207821 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
 0 0 0 0 0 0 0 0 0 0
ctxt 6514322
btime 1472888112
processes 50090
procs_running 3
procs_blocked 0
softirq 2586952 2738 784747 112825 8 2734 2734 1052895 231861 184 396226
*/
	//endregion

	public static void startADB(){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {


					CommandUtils.hpcmdExec("setprop service.adb.tcp.port 5555");
					CommandUtils.hpcmdExec("stop adbd ");
					CommandUtils.hpcmdExec("start adbd ");

//					String ps = CommandUtils.hpcmdExec("ps");
//					if(ps != null){
//
//						String[] arr = ps.split("\n");
//						for(int i = 0 ; i < arr.length;i++){
//							if(arr[i].contains("adb")){
//								mMsg+= arr[i] +"\n";
//							}
//						}
//
//					}

				} catch (Exception e) {
					// TODO Auto-generated catch block

					e.printStackTrace();
				}

				super.run();
			}
		}.start();
	}


	static String getSerialNumber(){

		String serial = null;

		try {

			Class<?> c =Class.forName("android.os.SystemProperties");

			Method get =c.getMethod("get", String.class);

			serial = (String)get.invoke(c, "ro.serialno");

		} catch (Exception e) {

			e.printStackTrace();

		}

		return serial;

	}

	public static  String getWifiMac12(Context context) {
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		if (info != null){
			String mac = info.getMacAddress();// 获得本机的MAC地址
			String ssid = info.getSSID();// 获得本机所链接的WIFI名称
			return mac.replace(":","");
		}

		return "123456789012";
	}

	public static  String getWifiSSID(Context context) {
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		if (info != null){
			String ssid = info.getSSID();// 获得本机所链接的WIFI名称
			return ssid;
		}

		return null;
	}
}
