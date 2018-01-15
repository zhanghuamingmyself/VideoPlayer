package com.zhanghuaming.videoplayer.context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.xys.libzxing.zxing.encoding.EncodingUtils;
import com.zhanghuaming.videoplayer.bean.DownloadInfo;
import com.zhanghuaming.videoplayer.view.FullVideoView;
import com.zhanghuaming.videoplayer.R;
import com.zhanghuaming.videoplayer.http.RetrofixHelper;
import com.zhanghuaming.videoplayer.bean.VideoURLBean;
import com.zhanghuaming.videoplayer.utlis.DownloadUtlis;
import com.zhanghuaming.videoplayer.utlis.SLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.zhanghuaming.videoplayer.config.StaticCfg.videoPath;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FullVideoView videoView = null;
    private List<File> videoList = null;//本地视频列表
    private int playingIndex = 0;//当前播放索引
    private Subscription TimerSubscribe;
  //  private ImageView ivErweima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        videoView = (FullVideoView) this.findViewById(R.id.video_view);
        videoView.setVisibility(View.VISIBLE);
        getLocalVideoList();
        playVideo();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
     //   ivErweima = (ImageView) findViewById(R.id.iv_erweima);
     //   showErweima("hello");
        TimerSubscribe = Observable.interval(0, 60000 * 10, TimeUnit.MILLISECONDS)//延时 ，每间隔，时间单位
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (RetrofixHelper.isNetworkConnected(MainActivity.this)) {
                            refreshFile();
                        }
                    }
                });

    }

//    public void showErweima(String url) {
//
//        if (url.equals("")) {
//            Toast.makeText(this, "无法生成空的二维码", Toast.LENGTH_SHORT).show();
//            SLog.e(TAG, "无法生成空的二维码");
//        } else {
//            Bitmap bitmap = EncodingUtils.createQRCode(url, 500, 500, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
//            ivErweima.setImageBitmap(bitmap);
//        }
//
//    }

    void refreshFile() {
        Observable<List<VideoURLBean>> back = RetrofixHelper.getVideoURL();
        back.flatMap(new Func1<List<VideoURLBean>, Observable<String>>() {
            @Override
            public Observable<String> call(List<VideoURLBean> videoURLBeenList) {
                if (videoURLBeenList != null && videoURLBeenList.size() > 0) {
                    List<String> local;
                    List<String> remote;
                    local = getLocalFileName();
                    remote = new ArrayList<>();
                    for (int i = 0; i < videoURLBeenList.size(); i++) {
                        SLog.i(TAG, "网络视频文件有" + videoURLBeenList.get(i).name + "--" + videoURLBeenList.get(i).md5);
                        remote.add(videoURLBeenList.get(i).name);
                    }
                    final List<String> needDownload = compare(remote, local);
                    return Observable.create(new Observable.OnSubscribe<String>() {
                        @Override
                        public void call(Subscriber<? super String> subscriber) {
                            for(String n:needDownload) {
                                subscriber.onNext(n);
                            }
                        }
                    });
                }
                return Observable.create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        subscriber.onCompleted();
                    }
                });
            }
        }).flatMap(new Func1<String, Observable<DownloadInfo>>() {
            @Override
            public Observable<DownloadInfo> call(final String url) {
                SLog.i(TAG,"开始下载"+url+"文件");
                return RetrofixHelper.downloadVideo(url);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<DownloadInfo>() {
            @Override
            public void onCompleted() {
                SLog.i(TAG, "更新任务完成");
            }

            @Override
            public void onError(Throwable e) {
                SLog.e(TAG, "视频文件下载错误" + e.getMessage());
//                if (needDownload != null && needDownload.size() > 0) {
//                    boolean delState = new File(videoPath + needDownload.get(0)).delete();
//                    if (delState) {
//                        SLog.e(TAG, "删除错误视频文件" + needDownload.get(0) + "成功");
//                    } else {
//                        SLog.e(TAG, "删除错误视频文件" + needDownload.get(0) + "失败");
//                    }
//                    getLocalVideoList();
//                }
            }

            @Override
            public void onNext(final DownloadInfo downloadInfo) {
                SLog.i(TAG, "正在保存" + downloadInfo.fileName);
                DownloadUtlis.writeResponseBodyToDisk(downloadInfo.responseBody, videoPath, downloadInfo.fileName);
                downloadInfo.responseBody.close();
                getLocalVideoList();
                if (!videoView.isPlaying()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playVideo();
                        }
                    });
                }
            }
        });
    }


    //获取本地视频列表
    void getLocalVideoList() {
        File d = new File(videoPath);
        SLog.i(TAG, "视频文件夹路径---" + videoPath);
        if (!d.exists() || !d.isDirectory()) {
            d.mkdirs();
        }
        File[] fList = d.listFiles();
        if (fList != null && fList.length != 0) {
            videoList = Arrays.asList(fList);
            SLog.i(TAG, "视频文件夹有" + videoList.size() + "个文件");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "空列表", Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    //获取下一个视频本地路径
    String getPath() {
        String path;
        if (videoList != null && playingIndex < videoList.size()) {
            path = videoList.get(playingIndex).getPath();
            playingIndex++;
        } else if (videoList != null && playingIndex == videoList.size() && videoList.size() != 0) {
            playingIndex = 0;
            path = videoList.get(playingIndex).getPath();
        } else {
            playingIndex = 0;
            path = null;
        }
        return path;
    }

    //播放视频
    void playVideo() {
        String videoUrl = getPath();
        if (videoUrl != null) {
            Uri uri = Uri.parse(videoUrl);
            //videoView.setMediaController(new MediaController(this));
            SLog.i(TAG, "播放视频---" + videoUrl);
            videoView.setVideoURI(uri);
            videoView.start();
            videoView.requestFocus();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playVideo();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    playVideo();
                    Toast.makeText(MainActivity.this, "视频播放异常", Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }
    }


    //获取本地相对路径和视频名字
    List<String> getLocalFileName() {
        List<String> filePath = new ArrayList<>();
        if (videoList != null && videoList.size() != 0) {
            for (int i = 0; i < videoList.size(); i++) {
                filePath.add(videoList.get(i).getPath().replace(videoPath, ""));
                SLog.i(TAG, "本地文件有" + filePath.get(i));
            }
        }
        return filePath;
    }


    //筛选需要下载或删除的文件
    private List<String> compare(List<String> netList, List<String> localList) {
        List<String> needDownload = new ArrayList<>();//需要下载的文件没有网络路径
        List<String> needDel = new ArrayList<>();//需要删除的文件+本地路径
        if (needDownload == null) {
            needDownload = new ArrayList<>();
        }
        if (needDel == null) {
            needDel = new ArrayList<>();
        }

        if (localList != null && localList.size() > 0) {
            for (String n : netList) {
                if (localList.indexOf(n) == -1) {
                    needDownload.add(n);
                    SLog.i(TAG, "需要下载的文件" + n);
                }
            }
        } else {
            needDownload = netList;
        }

        if (netList != null && netList.size() > 0) {
            for (String l : localList) {
                if (netList.indexOf(l) == -1) {
                    needDel.add(videoPath + l);
                    SLog.i(TAG, "需要删除的文件" + videoPath + l);
                }
            }
        } else {
            for (String s : localList) {
                needDel.add(videoPath + s);
            }
        }

        boolean delState;
        for (String p : needDel) {
            File f = new File(p);
            delState = f.delete();
            if (delState) {
                SLog.i(TAG, "正在删除" + p);
            } else {
                SLog.i(TAG, "删除" + p + "失败");
            }
        }

        getLocalVideoList();
        return needDownload;
    }

    @Override
    protected void onDestroy() {
        if (!TimerSubscribe.isUnsubscribed()) {
            TimerSubscribe.unsubscribe();
        }
        videoView.stopPlayback();
        videoView = null;
        super.onDestroy();
    }
}
