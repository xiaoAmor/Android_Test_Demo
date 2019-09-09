package com.nbpt.video.fileserver;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.nbpt.video.fileserver.util.FileUtils;

import java.io.IOException;

public class MyService extends Service {
    private String TAG="MyService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: "+Environment.getExternalStorageDirectory());
        FileUtils.setmExtsdPath(Environment.getExternalStorageDirectory().toString());//设置存储路径
        FileServer myServer = new FileServer(8000);
        try {
            myServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
