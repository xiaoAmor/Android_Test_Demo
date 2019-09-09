package com.nbpt.video.dvrdemo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.nbpt.video.util.FileUtils;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MainActivity";
    private long exitTime;
    public Boolean isdeviceconnect;
    private WifiInfo mWifiInfo;
    private WifiManager mWifiManager;
    private Button connectdevice;
    private Button appaboutntn;
    private Button appfilebtn;
    public Context context;

    private ArrayList frontListImage;
    private ArrayList frontListVideo;
    private ArrayList frontListVideoPlay;

    private ProgressDialog waitingDialog;

    public static HttpProxyCacheServer proxy;
    private Handler mHandler;


    private Handler mHandler2 = new Handler() {

        public void handlerMessage(Message msg) {
            switch (msg.what) {
                case 20:
                    //updateTitle();
                    Log.d(TAG, "222handlerMessage: 接收按键事件");
                    break;
                case 21:
                    Log.d(TAG, "222handlerMessage: 接收按键事件13");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置全屏显示
        setContentView(R.layout.activity_main);
        initview();
        new CheckPermission(this).initPermission();//检测权限
    }

//    private Handler eventHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == 291) {
//                //Toast.makeText(SplashActivity.this.getBaseContext(), C0250R.string.Lidno_memory_card, 0).show();
//            } else if (msg.what == 1110) {
//                //Toast.makeText(SplashActivity.this.getBaseContext(), C0250R.string.Lidno_update_firmware, 0).show();
//            }
//        }
//    };

    /**
     * 全屏显示
     */
    private void setFullSreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN); // Activity全屏显示，且状态栏被覆盖掉
    }

    private void initview() {

////隐藏标题栏
//        ActionBar actionBar = getSupportActionBar();
////ActionBar actionBar = getActionBar();
//        actionBar.hide();
        context = getApplicationContext();
        connectdevice = findViewById(R.id.connectdevice);
        appfilebtn = findViewById(R.id.appfilebtn);
        appaboutntn = findViewById(R.id.appboutbtn);
        connectdevice.setOnClickListener(this);
        appfilebtn.setOnClickListener(this);
        appaboutntn.setOnClickListener(this);

        proxy = MyAppliaction.getProxy(context);//全局的用于缓存 播放视频 url
        new HandlerThread().start();//启动子线程
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connectdevice:
                //检测wifi有无链接 有没有获取到IP 连接成功就跳转到 连接后的界面
                Log.d(TAG, "onClick: 等待连接DVR");
                showWaitingDialog();
                Message msg = mHandler.obtainMessage();
                msg.what = 0x12;
                mHandler.sendMessage(msg);

//                isdeviceconnect=camisConnected(context);
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "onClick: 线程结束");
//                if(isdeviceconnect)
//                {
//                    Log.d(TAG, "onClick: wif已连接");
//                    //弹出一个等待框  进行获取http的数据 1s 获取一项数据
//                    //连续访问  可能出现某一项 获取失败的情况
//                    frontListImage = FileUtils.getAllHttpImageFile("front");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    frontListVideo = FileUtils.getAllHttpVideoFile("front");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    frontListVideoPlay = FileUtils.getAllHttpVideoFilePlay("front");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    //waitingDialog.dismiss();
//                    if( (frontListImage.size()==0)||(frontListVideo.size()==0)||(frontListVideoPlay.size()==0)   ){
//                        Log.d(TAG, "initview: 没有获取到http文件");
//                    }
//                    //已经连接上热点 并获取到IP 并识别路由ip 43.1
//                    //Log.d(TAG, "onCreate imagefiles======================: " + frontListImage.size() + "file" + frontListImage.toString());
//                    Log.d(TAG, "onCreate imagefiles======================: " + frontListImage.size() );
//                    Log.d(TAG, "onCreate videofiles======================: " + frontListVideo.size() );
//                    Log.d(TAG, "onCreate videofiles======================: " + frontListVideoPlay.size() );
//                    Intent videoIntent = new Intent();
//                    videoIntent.setClass(context, VideoActivity.class);
//                    videoIntent.putStringArrayListExtra("allimagefile", frontListImage);//传递总的文件列表
//                    videoIntent.putStringArrayListExtra("allvideofile", frontListVideo);//传递总的文件列表
//                    videoIntent.putStringArrayListExtra("allvideofileplay", frontListVideoPlay);//传递总的文件列表
//                    startActivity(videoIntent);
////                    MainActivity.this.startActivity(new Intent(MainActivity.this, VideoActivity.class));
////                    MainActivity.this.overridePendingTransition(R.anim.alphain, R.anim.alphaout);
//                }else {
//                    Toast.makeText(context, "请连接正确的热点名称", Toast.LENGTH_SHORT).show();
////连接不上的时候 处理以下流程
//                    //创建提示框
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle(R.string.Lidconnection_failed);
//                    builder.setMessage(R.string.Lidchoose_wifi);
//                    builder.setNegativeButton(R.string.LidCancel, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    });
//                    builder.setPositiveButton(R.string.LidYes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            //确定按钮 跳转到wifi设置界面
//                            MainActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
//                        }
//                    });
//                    builder.create().show();
//                    MainActivity.this.overridePendingTransition(R.anim.alphain, R.anim.alphaout);//两个 Activity 切换时的动画
//                }
                break;

            case R.id.appfilebtn:
                Log.d(TAG, "onClick: 查看本地文件");
                //跳转到本地文件展示界面
                Intent FileIntent = new Intent();
                FileIntent.setClass(context, LocalFileActivity.class);
                startActivity(FileIntent);


                //跳转到本地下载文件 预览界面
                break;
            case R.id.appboutbtn:
                Log.d(TAG, "onClick: 关于APP");
                //showProgressDialog();

                Message msg1 = mHandler.obtainMessage();
                msg1.what = 0x13;
                mHandler.sendMessage(msg1);


//                Thread   newThread1=new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //在子线程给handler发送数据
//                                System.out.println("#################Thread is running  0000000000000");
//                                // myhandler.sendEmptyMessage( 0 ) ;
//                                Message message = new Message();
//                                message.what =21;
//                                mHandler2.sendMessage(message);
//                mHandler2.sendEmptyMessage(20);
//
//                    }
//                }) ;
//                newThread1.start();

                break;
        }

    }

    //===========================================================================================
    /* access modifiers changed from: private */
    public boolean camisConnected(Context context) {
        this.mWifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        String camseverip = intToIp(this.mWifiManager.getDhcpInfo().serverAddress);
        Log.d(TAG, "camisConnected: camseverip:" + camseverip);
        boolean isconnected = isWifiEnabled(context);
        //NetworkInfo ni = ((ConnectivityManager) context.getSystemService("connectivity")).getNetworkInfo(1);
        if (!isconnected || (!camseverip.equals("192.168.43.1"))) {
            return false;
        }
        return true;
    }

    private String intToIp(int i) {
        return (i & 255) + "." + ((i >> 8) & 255) + "." + ((i >> 16) & 255) + "." + ((i >> 24) & 255);
    }

    public static boolean isWifiEnabled(Context myContext) {
        if (myContext == null) {
            throw new NullPointerException("Global context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        } else {
            return false;
        }
    }

    //===============================================================================================
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        if (System.currentTimeMillis() - this.exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            this.exitTime = System.currentTimeMillis();
        } else {
//            if (this.NetworkId != 99999) {
//                disconnectWifi(this.NetworkId);
//            }
            finish();
            System.exit(0);
        }
        return true;
    }

    private void showWaitingDialog() {
        /* 等待Dialog具有屏蔽其他控件的交互能力
         * @setCancelable 为使屏幕不可点击，设置为不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该Dialog
         */
        waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setTitle("please waiting,connecting...");
        waitingDialog.setMessage("waiting...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);//触摸任意点 关闭，false则一直存在
        waitingDialog.show();
    }

    private void showProgressDialog() {
        /* @setProgress 设置初始进度
         * @setProgressStyle 设置样式（水平进度条）
         * @setMax 设置进度最大值
         */
        final int MAX_PROGRESS = 100;
        final ProgressDialog progressDialog =
                new ProgressDialog(MainActivity.this);
        progressDialog.setProgress(0);
        progressDialog.setTitle("我是一个进度条Dialog");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//长条形状
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setMax(MAX_PROGRESS);
        progressDialog.show();
        /* 模拟进度增加的过程
         * 新开一个线程，每个100ms，进度增加1
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress < MAX_PROGRESS) {
                    try {
                        Thread.sleep(100);//100ms
                        progress++;
                        progressDialog.setProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 进度达到最大值后，窗口消失
                progressDialog.cancel();
            }
        }).start();
    }


    class HandlerThread extends Thread {
        @Override
        public void run() {
            //开始建立消息循环
            Looper.prepare();//初始化Looper
            mHandler = new Handler() {//默认绑定本线程的Looper
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0x12:
                            //updateTitle();
                            Log.d(TAG, "handlerMessage: 接收按键事件");
                            isdeviceconnect = camisConnected(context);
                            if (isdeviceconnect) {
                                Log.d(TAG, "onClick: wif已连接");
                                //弹出一个等待框  进行获取http的数据 1s 获取一项数据
                                //连续访问  可能出现某一项 获取失败的情况
                                frontListVideoPlay = FileUtils.getAllHttpVideoFilePlay("front");
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                frontListVideo = FileUtils.getAllHttpVideoFile("front");
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                frontListImage = FileUtils.getAllHttpImageFile("front");
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                waitingDialog.dismiss();
                                try {
                                    if ((frontListImage.size() == 0) || (frontListVideo.size() == 0) || (frontListVideoPlay.size() == 0)) {
                                        Log.d(TAG, "initview: 没有正确获取到http文件");
                                        Toast.makeText(context, "未正确获取到文件列表，请重试!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //已经连接上热点 并获取到IP 并识别路由ip 43.1
                                        //Log.d(TAG, "onCreate imagefiles======================: " + frontListImage.size() + "file" + frontListImage.toString());
                                        Log.d(TAG, "onCreate imagefiles==================: " + frontListImage.size());
                                        Log.d(TAG, "onCreate videofiles==================: " + frontListVideo.size());
                                        Log.d(TAG, "onCreate videofiles==================: " + frontListVideoPlay.size());
                                        Intent videoIntent = new Intent();
                                        videoIntent.setClass(context, VideoActivity.class);
                                        videoIntent.putStringArrayListExtra("allimagefile", frontListImage);//传递总的文件列表
                                        videoIntent.putStringArrayListExtra("allvideofile", frontListVideo);//传递总的文件列表
                                        videoIntent.putStringArrayListExtra("allvideofileplay", frontListVideoPlay);//传递总的文件列表
                                        startActivity(videoIntent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                waitingDialog.dismiss();
                                Log.d(TAG, "handleMessage: 没有正确连接热点");

                                Toast.makeText(context, "请连接正确的热点名称", Toast.LENGTH_SHORT).show();
//连接不上的时候 处理以下流程
                                //创建提示框
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle(R.string.Lidconnection_failed);
                                builder.setMessage(R.string.Lidchoose_wifi);
                                builder.setNegativeButton(R.string.LidCancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.setPositiveButton(R.string.LidYes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //确定按钮 跳转到wifi设置界面
                                        MainActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
                                    }
                                });
                                builder.create().show();
                                MainActivity.this.overridePendingTransition(R.anim.alphain, R.anim.alphaout);//两个 Activity 切换时的动画
                            }

                            break;
                        case 0x13:
                            Log.d(TAG, "handlerMessage: 接收按键事件0x13");
                            break;
                    }
                }
            };
            Looper.loop();//启动消息循环
        }
    }

}
