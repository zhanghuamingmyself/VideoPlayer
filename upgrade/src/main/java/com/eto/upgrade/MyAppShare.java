package com.eto.upgrade;

import android.content.Context;



/**
 * Created by Administrator on 2016/10/17.
 */
public class MyAppShare extends ShareUtils {
    static final String INNERTOOLMD5="innertool_md5";


    public static void setInnertoolmd5(Context context, String value) {

        saveText(context, INNERTOOLMD5, value);
    }

    public static String getInnertoolmd5(Context context) {
        return getText(context,INNERTOOLMD5);
    }



}
