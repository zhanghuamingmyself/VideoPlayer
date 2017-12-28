package com.zhanghuaming.videoplayer.utlis;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

/**
 * Created by zhang on 2017/12/14.
 *
 */

public class DownloadUtlis {
    private static final String TAG = DownloadUtlis.class.getSimpleName();
    public static boolean isSaving = false;
    public static boolean writeResponseBodyToDisk(ResponseBody body,String path,String name) {
        try {
            isSaving = true;
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(path+ File.separator+name);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096*2];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                Log.d(TAG, "开始写入磁盘 ");
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                 //   Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();
                Log.d(TAG, "写入磁盘结束  大小"+fileSizeDownloaded);
                isSaving =false;
                return true;
            } catch (IOException e) {
                boolean b = futureStudioIconFile.delete();
                SLog.e(TAG,"写入文件"+futureStudioIconFile.getPath()+"失败"+e.getMessage()+"删除"+b);
                isSaving = false;
                return false;
            } finally {
                isSaving = false;
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            isSaving =false;
            return false;
        }
    }


}
