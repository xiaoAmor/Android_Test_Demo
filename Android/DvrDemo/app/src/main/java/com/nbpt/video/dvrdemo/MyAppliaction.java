package com.nbpt.video.dvrdemo;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;

import java.io.File;

public class MyAppliaction extends Application {

    private HttpProxyCacheServer proxy;
    //public static HttpProxyCacheServer proxy = MyAppliaction.getProxy(MainActivity.context);//全局的
    public static HttpProxyCacheServer getProxy(Context context) {
        MyAppliaction app = (MyAppliaction) context.getApplicationContext();
        if(app.proxy == null)
        {
            Log.d("MyAppliaction", "getProxy:!!!!null!!!!!!!!!!!!! ");
        }

        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this).cacheDirectory(new File(getCacheDirectory()))
                //最大缓存200M
                .maxCacheFilesCount(3)
                .build();
        //return new HttpProxyCacheServer(this);
    }
    private String getCacheDirectory(){
        String sdpath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/RecordCamera/cache/";
        File file = new File(sdpath);
        //Log.d(TAG, "getAllFile file: "+file);
        if (!file.exists()) {
            file.mkdirs();
        }

        return sdpath;
    }

}
