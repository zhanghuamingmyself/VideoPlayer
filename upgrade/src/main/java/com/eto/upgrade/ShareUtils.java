package com.eto.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtils {

	private static final String PREFERENCE_NAME = "appset";
	
	
	
	
	protected static void  saveText(Context context,String itemName, String val){
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCE_NAME, 0).edit();
		editor.putString(itemName, val);
		editor.commit();
		
	}
	
	protected static String getText(Context context,String itemName) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		String val = preferences.getString(itemName, "");
		return val;
	}
	
	//////////////////////////////
	
	protected static void  saveInt(Context context,String itemName, int val){
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCE_NAME, 0).edit();
		editor.putInt(itemName, val);
		editor.commit();
		
	}
	
	protected static int getInt(Context context,String itemName) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		int val = preferences.getInt(itemName, 0);
		return val;
	}
	
	
	/////////////////////
	
	protected static void  saveBoolean(Context context,String itemName, boolean val){
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCE_NAME, 0).edit();
		editor.putBoolean(itemName, val);
		editor.commit();
		
	}
	
	protected static Boolean getBoolean(Context context,String itemName) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		Boolean val = preferences.getBoolean(itemName, false);
		return val;
	}
	
	
	/////////////////////
	
	protected static void  saveFloat(Context context,String itemName, float val){
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCE_NAME, 0).edit();
		editor.putFloat(itemName, val);
		editor.commit();
		
	}
	
	protected static float getFloat(Context context,String itemName) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		float val = preferences.getFloat(itemName, 0);
		return val;
		
	
	}
	
	
	/////////////////////
	
	
	protected static void  saveLong(Context context,String itemName, long val){
		SharedPreferences.Editor editor = context.getSharedPreferences(
				PREFERENCE_NAME, 0).edit();
		editor.putLong(itemName, val);
		editor.commit();
		
	}
	
	protected static long getLong(Context context,String itemName) {

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_NAME, Context.MODE_PRIVATE);
		long val = preferences.getLong(itemName, 0);
		return val;
		
	
	}
	
	
	/////////////////////
	
	
//	public static void setUserName(Context context, String value) {
//		
//		saveText(context, "setUserName", value);
//	}
//	
//	public static String getUserName(Context context) {
//		return getText(context, "setUserName");
//	}
	//----------------
	


	
	
}
