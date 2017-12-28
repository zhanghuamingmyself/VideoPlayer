package com.zhanghuaming.videoplayer.serial;

import android.util.Log;


import com.zhanghuaming.videoplayer.utlis.SLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/12/8.
 */
public class UartClient {
    private String TAG = UartClient.class.getSimpleName();
    static UartClient mUartClient;
    boolean isRunning = false;
    private int num = 0;

    public static UartClient getInstance() {
        if (mUartClient == null) {
            mUartClient = new UartClient();
        }
        return mUartClient;
    }

    private UartClient() {
    }


    class UartThread extends Thread {

        public UartThread() {
        }

        @Override
        public void run() {
            SLog.d(TAG, "线程开了");
            try {
                InputStream is = UartUtils.getInstance().getInputStream();
                OutputStream os = UartUtils.getInstance().getOutputStream();
                if (is == null) {
                    isRunning = false;
                    Log.d(TAG, "is == null");
                    return;
                }
                isRunning = true;
                byte[] cache = null;
                byte[] buf = new byte[100];
                int index = 0;
                Log.d(TAG, "进入while前");
                while (isRunning) {
                    num++;
                    Log.d(TAG, "num=" + num);
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "ret=");
                    int ret = is.read(buf);
                    Log.d(TAG, "ret=" + ret);
                    Log.i(TAG, "HEX:" + BufToHex(buf, index, ret));
                    if (ret > 0) {
                        if (cache != null) {
                            byte[] temp = new byte[cache.length + ret];
                            System.arraycopy(cache, 0, temp, 0, cache.length);
                            System.arraycopy(buf, 0, temp, cache.length, ret);
                            cache = temp;
                            Log.i(TAG, "全部数据为---" + BufToHex(cache, 0, cache.length));
                            // Log.i(TAG, "全部数据为---" + BufToHexNoHead(cache, 0, cache.length));
                        } else {
                            cache = new byte[ret];
                            System.arraycopy(buf, 0, cache, 0, ret);
                            Log.i(TAG, "全部数据为---" + BufToHex(cache, 0, cache.length));
                        }
                        String result;
                        if (cache.length >= 2 && IntToHex(cache[0]).equals("0x55") && IntToHex(cache[1]).equals("0xAA")) {
                            if (cache.length == 12) {
                                //逻辑操作
                                Log.i(TAG, "解析中");
                                result = analysis(cache);
                                cache = null;
                            } else if (cache.length > 12) {
                                result = analysis(cache);
                                Log.i(TAG, "解析后有剩余" + (cache.length - 12));
                                byte[] temp = new byte[cache.length - 12];
                                System.arraycopy(cache, 12, temp, 0, cache.length - 12);
                                cache = temp;
                            } else {
                                Log.i(TAG, "解析其他");
                                cache = null;
                            }
                            //result;这个是结果10位的卡号
                        }

                    } else {
                        //不会执行，read会一直阻塞
                        Log.d(TAG, "read from uart empty");
                    }

                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                isRunning = false;
                Log.d(TAG, e.getMessage());

            }
            SLog.f(TAG, "UartKeyBoardClient run end");
        }
    }

    public String analysis(byte[] buf) {
        String result = "";
        for (int i = 7; i < 11; i++) {
            result += IntToHexNoHead(buf[i]);
        }
        result = result.toLowerCase();
        result = Long.toString(Long.parseLong(result, 16));
        if (result.length() < 10) {
            String t = "";
            for (int i = 0; i < 10 - result.length(); i++) {
                t += '0';
            }
            t += result;
            result = t;
        }
        Log.i(TAG, "解析的结果为：" + result);
        return result;
    }

    public void start() {
        SLog.f(TAG, "UartKeyBoardClient start()");
        if (!isRunning) {
            new UartThread().start();
        }
    }


    public static String BufToHex(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHex(buf[i]) + " ";
            }
        }
        return str;
    }


    public static String BufToHexString(byte[] buf) {
        String str = "";
        if (buf != null) {
            for (int i = 0; i < buf.length; i++) {
                str += IntToHex2(buf[i]);
            }
        }
        return str;
    }

    public static String BufToHexString(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHex2(buf[i]);
            }
        }
        return str;
    }

    public static String BufToHexNoHead(byte[] buf, int offset, int count) {
        String str = "";
        if (buf != null) {
            for (int i = offset; i < buf.length && i < offset + count; i++) {
                str += IntToHexNoHead(buf[i]) + " ";
            }
        }
        return str;
    }

    public static String IntToHexNoHead(int n) {
        if (n < 0) {
            n += 256;
        }
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    public static String IntToHex(int n) {
        if (n < 0) {
            n += 256;
        }
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15)
                ch[nIndex] = 'F';
            else if (k == 14)
                ch[nIndex] = 'E';
            else if (k == 13)
                ch[nIndex] = 'D';
            else if (k == 12)
                ch[nIndex] = 'C';
            else if (k == 11)
                ch[nIndex] = 'B';
            else if (k == 10)
                ch[nIndex] = 'A';
            else
                ch[nIndex] = (char) ('0' + k);
            nIndex++;
            if (m == 0)
                break;
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        String strHex = new String("0x");
        strHex += sb.toString();
        return strHex;
    }

    public static String IntToHex2(int n) {
        if (n < 0) {
            n += 256;
        }
        int m = n / 16;
        int k = n % 16;

        String strHex = "" + byteHex(m) + byteHex(k);

        return strHex;
    }

    static char byteHex(int k) {
        char ch;
        if (k == 15)
            ch = 'F';
        else if (k == 14)
            ch = 'E';
        else if (k == 13)
            ch = 'D';
        else if (k == 12)
            ch = 'C';
        else if (k == 11)
            ch = 'B';
        else if (k == 10)
            ch = 'A';
        else
            ch = (char) ('0' + k);
        return ch;
    }

}
