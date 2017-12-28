package com.zhanghuaming.videoplayer.serial;

import android.util.Log;

import com.zhanghuaming.videoplayer.config.StaticCfg;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2016/11/16.
 */
public class UartUtils {

    private static final int BAUDRATE = StaticCfg.BAUDRATE;
    private static final String TTY_PATH = StaticCfg.TTY_PATH;

    private SerialPort mSerialPort = null;

    static UartUtils mUartUtils;
    private final String TAG = UartUtils.class.getSimpleName();

    public static UartUtils getInstance() {
        if (mUartUtils == null) {
            mUartUtils = new UartUtils();
        }
        return mUartUtils;
    }

    private UartUtils() {
        /* Open the serial port */
        openUart();
        Log.d(TAG, "openUart()");
    }

    void openUart() {
        try {
            mSerialPort = new SerialPort(new File(TTY_PATH), BAUDRATE, 0);
            Log.d(TAG, "open uart " + mSerialPort);
        } catch (Exception e) {
            e.printStackTrace();
            mSerialPort = null;
        }
    }


    public InputStream getInputStream() {
        if (mSerialPort != null) {
            return mSerialPort.getInputStream();
        } else {
            openUart();
        }
        return null;
    }

    public OutputStream getOutputStream() {
        if (mSerialPort != null) {
            return mSerialPort.getOutputStream();
        } else {
            openUart();
        }
        return null;
    }


}
