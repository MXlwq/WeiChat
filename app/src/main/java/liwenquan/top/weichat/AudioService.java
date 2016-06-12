package liwenquan.top.weichat;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by LWQ on 2016/6/11.
 */
public class AudioService extends Service
{
    private MediaPlayer mp;
    private String path;
    private MyBinder binder=new MyBinder();
    public class MyBinder extends Binder
    {
        public String getPath(){
            return path;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.path=intent.getStringExtra("audio_name");
        AssetManager am=getApplicationContext().getAssets();
        long length=0;
        try {
            length=am.openFd(path).getLength();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),""+length,Toast.LENGTH_SHORT).show();
        try {
            // 创建MediaPlayer对象
            mp = new MediaPlayer();
            mp = MediaPlayer.create(AudioService.this, Uri.parse(path));
            // mp.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mp.start();
        // 音乐播放完毕的事件处理
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // 循环播放
                try {
                    mp.stop();
                    mp.release();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        // 播放音乐时发生错误的事件处理
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                // 释放资源
                try {
                    mp.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}

