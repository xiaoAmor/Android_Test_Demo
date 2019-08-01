package com.example.socket_ser_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    public static final int RESOLUTION_360P = 172800;
    public static final int RESOLUTION_480P = 411840;
    public static final int RESOLUTION_720P = 921600;
    public static final int RESOLUTION_1080P = 2073600;
    private SurfaceView surfaceView;
    private SurfaceHolder sfh;
    private Camera camera;
    boolean isPreview = false;        //是否在浏览中

    //================================================
    private SendDataThread sendDataThread;
    MyHandler handler;
    private Button start;
    private Button stop;
    int screenWidth = 300, screenHeight = 300;

    //================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckPermission(this).initPermission();//检测权限
        initView();//初始化界面

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
//        start = (Button) findViewById(R.id.start);
//        stop = (Button) findViewById(R.id.stop);
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
    }
    //====================================================================================
    private void initCamera() {
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

                Camera.Size previewSize = CameraUtils.getCameraVideoSize(camera, RESOLUTION_720P);//获取最接近分辨率的预览尺寸
                screenWidth = previewSize.width;//此处是最接近需要预览的尺寸
                screenHeight = previewSize.height;
                Log.d(TAG, "获取预览尺寸最接近设置分辨率的尺寸previewWidth:" + screenWidth + "<>previewHeight:" + screenHeight);
                Camera.Size size2 = camera.getParameters().getPreviewSize();//尺寸修改为 720p预览尺寸
                Log.d(TAG, "获取size2分辨率的尺寸previewWidth:" + size2.width + "<>previewHeight:" + size2.height);
                parameters.setPreviewSize(screenWidth, screenHeight);// 设置照片的大小

                /* 每秒从摄像头捕获5帧画面， */
                parameters.setPreviewFrameRate(10);
                parameters.setPictureFormat(ImageFormat.NV21);           // 设置图片格式
                //=======================================================================
                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    //实时截取每一帧视频流数据
                    //每一帧的图像数据均会通过这个callback返回，在这里面我们可以处理返回的字节数组，转换为bitmap，然后显示出来
                    //注意的是返回的数组图像格式为YUV，并不支持直接BitmapFactory.decodeByteArray（）方法，
                    // 需要通过格式转换。但这也导致运行计算所化时间较长
                    @Override
                    public void onPreviewFrame(byte[] data, Camera c) {

                        //传递进来的数据 是原生YUV420SP数据
                        // TODO Auto-generated method stub
                        Camera.Size size = camera.getParameters().getPreviewSize();//尺寸修改为 720p预览尺寸 自动根据 上面设置的尺寸
                        try {
                            //调用image.compressToJpeg（）将YUV格式图像数据data转为jpg格式
                            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);//压缩每一帧数据
                            //YuvImage image = new YuvImage(data, ImageFormat.NV21, screenWidth, screenHeight, null);//压缩每一帧数据
                            if (image != null) {
                                //使用线程 将数据通过服务端 发送给 客户端 检查client是否连接
                                //每一帧数据
                                Message msg = sendDataThread.revHandler.obtainMessage();
                                msg.what = 0x111;
                                msg.obj = image;//传递的对象
                                sendDataThread.revHandler.sendMessage(msg);
//// //================================================================================================================================
//                                ByteArrayOutputStream   outstream = new ByteArrayOutputStream();
//                                    image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, outstream);
//                                    outstream.flush();
//                                    new Thread(clientThread).start();
                            }
                        } catch (Exception ex) {
                            Log.e("Sys", "Error:" + ex.getMessage());
                        }
                    }
                });
                // 自动对焦
                List<String> focusModes = parameters.getSupportedFocusModes();// 设置聚焦模式
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        mode.contains("continuous-video");
                        parameters.setFocusMode("continuous-video");
                    }
                }
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
}
