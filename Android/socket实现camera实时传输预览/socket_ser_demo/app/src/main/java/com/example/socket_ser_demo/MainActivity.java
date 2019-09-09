package com.example.socket_ser_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Camera.PreviewCallback, View.OnClickListener, MediaRecorder.OnErrorListener {
    private String TAG = "MainActivity";
    public static final int RESOLUTION_360P = 172800;
    public static final int RESOLUTION_480P = 411840;
    public static final int RESOLUTION_720P = 921600;
    public static final int RESOLUTION_1080P = 2073600;
    private SurfaceView surfaceView;
    private SurfaceHolder sfh;
    private Camera camera;
    boolean isPreview = false;        //是否在浏览中
    private Camera.AutoFocusCallback myAutoFocusCallback = null;    //自动对焦
    //================================================
    private SendDataThread sendDataThread;
    MyHandler handler;
    private Button start;
    private Button stop;
    int screenWidth = 300, screenHeight = 300;

    private File videoFolder; // 存放视频的文件夹
    private File videFile; // 视频文件
    private MediaRecorder mRecorder;
    private boolean recording; // 记录是否正在录像,fasle为未录像, true 为正在录像

    //================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckPermission(this).initPermission();//检测权限
        initView();//初始化界面
        initCreateFile();

        /**
         * 读一下手机wifi状态下的ip地址，只有知道它的ip才能连接它嘛
         */
        Toast.makeText(MainActivity.this, getLocalIpAddress(), Toast.LENGTH_SHORT).show();

        /**
         * 启动服务器监听线程
         */
        ServerSocket_thread serversocket_thread = new ServerSocket_thread();
        serversocket_thread.start();

    }

    //====================================================================================
    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.sf_preview);
        start = (Button) findViewById(R.id.luXiang_bt);
        stop = (Button) findViewById(R.id.tingZhi_bt);
//=============================================================
        handler = new MyHandler();

        sendDataThread = new SendDataThread(handler);//新建一个线程
        new Thread(sendDataThread).start();//启动该线程

        DisplayMetrics dm = new DisplayMetrics();//可以获取屏幕大小
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        screenHeight = dm.heightPixels;

        sfh = surfaceView.getHolder();//
        sfh.setFixedSize(screenWidth, screenHeight / 4 * 3);

        start.setOnClickListener(this);
        stop.setOnClickListener(this);


        sfh.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d(TAG, "surfaceCreated: ");
                initCamera();//开始预览
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d(TAG, "surfaceChanged: ");
                initCamera();//开始预览
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if (camera != null) {
                    if (isPreview)
                        camera.stopPreview();
                    camera.setPreviewCallback(null);//必须有 否则出错
                    camera.release();
                    camera = null;
                }
            }
        });

        //自动聚焦变量回调
        myAutoFocusCallback = new Camera.AutoFocusCallback() {
            public void onAutoFocus(boolean success, Camera camera) {
                if (success)//success表示对焦成功
                {
                    Log.i(TAG, "onAutoFocus succeed...");
                } else {
                    Log.i(TAG, "onAutoFocus failed...");
                }
            }
        };

    }

    //====================================================================================
    private void initCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        if (camera == null) {
            Log.d(TAG, "startPreView: 打开摄像头");
            try {
                int cametacount = Camera.getNumberOfCameras();//获取设备摄像头数量
                Log.d(TAG, "initCamera: " + cametacount);
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开后置摄像头
                //ClientThread.size = camera.getParameters().getPreviewSize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (camera != null) {
            try {
                Log.d(TAG, "initCamera: 刷新画面");
                //设置旋转
                camera.setDisplayOrientation(CameraUtils.getCameraDisplayOrientation(this.getWindow(), Camera.CameraInfo.CAMERA_FACING_BACK));
                camera.setPreviewDisplay(sfh);                 // 通过SurfaceView显示取景画面
                Camera.Parameters parameters = camera.getParameters();//获取相机参数
                // ================================================================================
//                List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
//                int length = pictureSizes.size();
//                for (int i = 0; i < length; i++) {
//                    Log.d(TAG, "initCamera pictureSizes: " + pictureSizes.get(i).width + "-----" + pictureSizes.get(i).height);
//                }
//
//                //List<Size> previewSizes = mCameraDevice.getCamera().getParameters().getSupportedPreviewSizes();
//                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
//                length = previewSizes.size();
//                for (int i = 0; i < length; i++) {
//                    Log.d(TAG, "initCamera previewSizes: " + previewSizes.get(i).width + "=====" + previewSizes.get(i).height);
//
//                }
//======================================================================================================
                Camera.Size previewSize = CameraUtils.getCameraVideoSize(camera, RESOLUTION_1080P);//获取最接近分辨率的预览尺寸
                screenWidth = previewSize.width;//此处是最接近需要预览的尺寸
                screenHeight = previewSize.height;
                Log.d(TAG, "获取预览尺寸最接近设置分辨率的尺寸previewWidth:" + screenWidth + "<>previewHeight:" + screenHeight);
                parameters.setPreviewSize(screenWidth, screenHeight);// //获得摄像区域的大小
                /* 每秒从摄像头捕获5帧画面， */
                parameters.setPreviewFrameRate(9);
                parameters.setPictureFormat(ImageFormat.NV21);           // 设置图片格式
//=====================================================================================================================================
                Camera.Size size2 = camera.getParameters().getPreviewSize();//尺寸修改为 720p预览尺寸
                Log.d(TAG, "获取size2分辨率的尺寸previewWidth:" + size2.width + "<>previewHeight:" + size2.height);
                int bufferSize = (((size2.width | 0xf) + 1) * size2.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())) / 8;
                //int bufferSize = (((screenWidth | 0xf) + 1) * screenHeight * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())) / 8;
                camera.addCallbackBuffer(new byte[bufferSize]);
                camera.setPreviewCallbackWithBuffer(this);
                //=======================================================================
//                camera.setPreviewCallback(new Camera.PreviewCallback() {
//                    //实时截取每一帧视频流数据
//                    //每一帧的图像数据均会通过这个callback返回，在这里面我们可以处理返回的字节数组，转换为bitmap，然后显示出来
//                    //注意的是返回的数组图像格式为YUV，并不支持直接BitmapFactory.decodeByteArray（）方法，
//                    // 需要通过格式转换。但这也导致运行计算所化时间较长
//                    @Override
//                    public void onPreviewFrame(byte[] data, Camera c) {
//
//                        //传递进来的数据 是原生YUV420SP数据
//                        // TODO Auto-generated method stub
//                        Camera.Size size = camera.getParameters().getPreviewSize();//尺寸修改为 720p预览尺寸 自动根据 上面设置的尺寸
//                        try {
//                            //调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
//                            //YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);//压缩每一帧数据
//                            YuvImage image = new YuvImage(data, ImageFormat.NV21, screenWidth, screenHeight, null);//压缩每一帧数据
//                            //YuvImage image = new YuvImage(data, ImageFormat.NV21, 640, 480, null);//压缩每一帧数据
//                            if (image != null) {
//                                //使用线程 将数据通过服务端 发送给 客户端 检查client是否连接
//                                //每一帧数据
//                                Message msg = sendDataThread.revHandler.obtainMessage();
//                                msg.what = 0x111;
//                                msg.obj = image;//传递的对象
//                                sendDataThread.revHandler.sendMessage(msg);
////// //================================================================================================================================
////                                ByteArrayOutputStream   outstream = new ByteArrayOutputStream();
////                                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
////                                    outstream.flush();
////                                    new Thread(clientThread).start();
//                            }
//                        } catch (Exception ex) {
//                            Log.e("Sys", "Error:" + ex.getMessage());
//                        }
//                    }
//                });
                // 自动对焦
//                List<String> focusModes = parameters.getSupportedFocusModes();// 设置聚焦模式
//                if (focusModes != null) {
//                    for (String mode : focusModes) {
//                        mode.contains("continuous-video");
//                        parameters.setFocusMode("continuous-video");
//                    }
//                }

                //camera.autoFocus(myAutoFocusCallback);
                //==================================================================================
                camera.setParameters(parameters);// 设置相机参数
                camera.startPreview();                                   // 开始预览
                //camera.autoFocus(null);                                  // 自动对焦
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreview = true;
        } else {
            Log.d(TAG, "initCamera: 打开后摄像头失败");
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (data == null) {
            Camera.Parameters params = camera.getParameters();
            Camera.Size size = params.getPreviewSize();
            int bufferSize = (((size.width | 0x1f) + 1) * size.height * ImageFormat.getBitsPerPixel(params.getPreviewFormat())) / 8;
            camera.addCallbackBuffer(new byte[bufferSize]);
        } else {
            try {
                //调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                //YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);//压缩每一帧数据
                YuvImage image = new YuvImage(data, ImageFormat.NV21, screenWidth, screenHeight, null);//压缩每一帧数据
                //YuvImage image = new YuvImage(data, ImageFormat.NV21, 640, 480, null);//压缩每一帧数据
                if (image != null) {
                    //使用线程 将数据通过服务端 发送给 客户端 检查client是否连接
                    //每一帧数据
                    Message msg = sendDataThread.revHandler.obtainMessage();
                    msg.what = 0x111;
                    msg.obj = image;//传递的对象
                    sendDataThread.revHandler.sendMessage(msg);
                }
            } catch (Exception ex) {
                Log.e("Sys", "Error:" + ex.getMessage());
            }
//addCallbackBuffer的地方有两个，一个是在startPreview之前，
// 一个是在onPreviewFrame中，这两个都需要调用，如果在onPreviewFrame中不调用，
// 那么，就无法继续回调到onPreviewFrame中了
            camera.addCallbackBuffer(data);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.luXiang_bt: // 录像点击事件
                Log.d(TAG, "onClick: 111111111111111");
                if (!recording) {
                    try {
                        // 获取当前时间,作为视频文件的文件名
                        String path=Environment.getExternalStorageDirectory().getAbsolutePath();
                        Log.d(TAG, "onClick: "+path);
                        String nowTime = java.text.MessageFormat.format("{0,date,yyyyMMdd_HHmmss}", new Object[]{new java.sql.Date(System.currentTimeMillis())});
                        Log.d(TAG, "onClick====================: "+nowTime);
                        String path1=videoFolder.getAbsoluteFile() + File.separator + "video" + nowTime + ".mp4";
                        Log.d(TAG, "onClick: "+path1);
                        // 声明视频文件对象
                        videFile = new File(videoFolder.getAbsoluteFile() + File.separator + "video" + nowTime + ".mp4");
                        Log.d(TAG, "onClick path: "+videFile.getPath());
                        // 关闭预览并释放资源
                        camera.unlock();


                        if (mRecorder == null) {
                            mRecorder = new MediaRecorder();
                            mRecorder.setOnErrorListener(this);
                        } else {
                            mRecorder.reset();
                        }


                        mRecorder.setCamera(camera);
                        // 创建此视频文件
                        //videFile.createNewFile();
                        mRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface()); // 预览
                        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); // 视频源
                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 录音源为麦克风
                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 输出格式为mp4
                        /**
                         *引用android.util.DisplayMetrics 获取分辨率
                         */
                        // DisplayMetrics dm = new DisplayMetrics();
                        // getWindowManager().getDefaultDisplay().getMetrics(dm);
                        mRecorder.setVideoSize(800, 480); // 视频尺寸
                        //mRecorder.setVideoEncodingBitRate(2 * 1280 * 720); //设置视频编码帧率
                       // mRecorder.setVideoFrameRate(30); // 视频帧频率
                        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP); // 视频编码
                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // 音频编码
                        mRecorder.setMaxDuration(1800000); // 设置最大录制时间
                        //mRecorder.setOutputFile(videFile.getAbsolutePath()); // 设置录制文件源
                        mRecorder.setOutputFile(path1); // 设置录制文件源
                        try {
                            mRecorder.prepare(); // 准备录像
                        } catch (IOException e) {
                            Log.d(TAG, "onClick: error55555555555555555555");
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            Log.d(TAG, "onClick: error6666666666666666666666666");
                            e.printStackTrace();
                        }
//                        mRecorder.setOnErrorListener((MediaRecorder.OnErrorListener) this);
//                        mRecorder.setOnInfoListener((MediaRecorder.OnInfoListener) this);                      mRecorder.setOnErrorListener((MediaRecorder.OnErrorListener) this);
//                        mRecorder.setOnInfoListener((MediaRecorder.OnInfoListener) this);

                        mRecorder.start(); // 开始录像
                        //time_tv.setVisibility(View.VISIBLE); // 设置文本框可见
                        //handler.post(timeRun); // 调用Runable
                        recording = true; // 改变录制状态为正在录制
                        //setAutofocus();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(MainActivity.this, "视频正在录制中...",
                            Toast.LENGTH_LONG).show();
                break;
            case R.id.tingZhi_bt: // 停止点击事件
                Log.d(TAG, "onClick: 22222222222");
                if (recording) {
                    Log.d(TAG, "onClick: 停止录像");

                    try {
                        if(mRecorder!=null) {
                            Log.d(TAG, "stopRecordSave: mMediaRecorder.stop");
                            mRecorder.setOnErrorListener(null);
                            mRecorder.setOnInfoListener(null);
                            mRecorder.setPreviewDisplay(null);

                            mRecorder.stop();
                        }

                    } catch (IllegalStateException e) {
                        Log.d(TAG, "stopRecordSave: error 11111111111111111111");
                        e.printStackTrace();
                    }
                    catch (RuntimeException e) {
                        Log.d(TAG, "stopRecordSave: error 2222222222222222");
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        Log.d(TAG, "stopRecordSave: error 333333333333333333333");
                        e.printStackTrace();
                    }
                    finally {
                        Log.d(TAG, "stopRecordSave: 释放 releaseMediaRecorder");
                        releaseMediaRecorder();
                    }

//                    mRecorder.stop();
//                    mRecorder.release();
                   // handler.removeCallbacks(timeRun);
                    //time_tv.setVisibility(View.GONE);
                   // int videoTimeLength = time;
                   // time = 0;
                    recording = false;
//                    Toast.makeText(
//                            MainActivity.this,
//                            videFile.getAbsolutePath() + " " + videoTimeLength
//                                    + "秒", Toast.LENGTH_LONG).show();
                }
                // 开启相机
                if (camera == null) {
                    camera = Camera.open();
                    try {
                        camera.setPreviewDisplay(sfh);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                camera.startPreview(); // 开启预览
                break;


        }

    }

    private void releaseMediaRecorder() {
        if (mRecorder != null) {
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }

    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {

    }

    //====================================================================================
    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x222) {
                //返回信息显示代码
                Log.d("MainActivity", "handleMessage: ###################");
            }
            if (msg.what == 0x111) {
                //返回信息显示代码
                //Log.d("1122", "handleMessage: ");
            }
        }
    }
//====================================================================================

    /**
     * 服务器监听线程 开机自动启动
     */
    class ServerSocket_thread extends Thread {
        public void run()//重写Thread的run方法
        {
            try {
                ServerSocket serverSocket = new ServerSocket(9000);//监听8080端口，这个程序的通信端口就是8080了
                Log.d("SocketServiceM", "main: 服务器已启动,开始监听...");

                while (true) {
                    Log.d("SocketServiceM", "等待客户端连接waiting for...");
                    //client = ss.accept();
                    ////步骤二，每接受到一个新Socket连接请求，就会新建一个Thread去处理与其之间的通信
                    new Thread(new TheadServer(serverSocket)).start();// 只是启动了 server  得到 client句柄
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
//====================================================================================

    /**
     * 获取WIFI下ip地址
     */
    private String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }
//====================================================================================

    /**
     * 文件的创建
     */
    private void initCreateFile() {
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 设定存放视频的文件夹的路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "VideoFolder" + File.separator;
            Log.d(TAG, "initCreateFile: " + path);
            // 声明存放视频的文件夹的File对象
            videoFolder = new File(path);
            // 如果不存在此文件夹,则创建
            if (!videoFolder.exists()) {
                videoFolder.mkdirs();
            }
            // 设置surfaceView不管理的缓冲区
            surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            // 设置surfaceView分辨率
            //surfaceView.getHolder().setFixedSize(1000, 500);

        } else
            Toast.makeText(this, "未找到sdCard!", Toast.LENGTH_LONG).show();
    }

    /**
     * 将Camera和mediaRecoder释放
     */
    @Override
    protected void onDestroy() {
        //handler.removeCallbacks(timeRun);
        if (mRecorder != null) {
            mRecorder.release();
        }
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        super.onDestroy();
    }

}
