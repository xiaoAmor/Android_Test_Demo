package com.example.socket_ser_demo;


import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Size;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SendDataThread implements Runnable {
    private String TAG = "SendDataThread";
    //接受UI线程消息
    public MySendHandler revHandler;//接收主界面的消息
    //向UI线程发送消息
    private Handler handler;

    static OutputStream os = null;
    //private static ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
    private static ByteArrayOutputStream byteArrayOutputStream;
    private static byte byteBuffer[] = new byte[1024];

    public SendDataThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        Log.d(TAG, "run: 接收主界面消息线程 启动。。。");
        handler.sendEmptyMessage(222);
        Looper.prepare();
        //接受UI发来的信息
        revHandler = new MySendHandler(); //新建一个线程用来接收主界面的消息
        Looper.loop();
    }

    public static class MySendHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x111) {
                // Log.d("SendDataThread", "handleMessage: 收到主界面消息");
                //收到主界面的消息
                //主要处理主界面每一帧数据都会发送到此线程进行处理  通过socket发送出来 到client
                try {
                    //35ms一帧
                    YuvImage image = (YuvImage) msg.obj;//接收的数据对象
                    //作为服务端 将每一帧数据传给客户端
                    //==================================================================================
                    //###################################################################################
                    //使用socket进行传输
                    //if 客户端socket存在 则发送消息
                    if (TheadServer.client != null) {
                        //Log.d("SendDataThread", "handleMessage: 客户端存在");
                        try {
                            os = TheadServer.client.getOutputStream();//获取sockect的输出数据流 用于向socket中写入 帧数据
                            DataOutputStream dos = new DataOutputStream(os);
                            //====================数据转换 到data中=================================================================
                            byteArrayOutputStream = new ByteArrayOutputStream();//输出数据流
                            //图片数据转换成  数据流
                            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 80, byteArrayOutputStream);
                            ByteArrayInputStream inputstream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                            //Log.d("MySendHandler", "handleMessage: "+byteArrayOutputStream.toByteArray().length);
                            //Log.d("MySendHandler", "handleMessagesize: "+byteArrayOutputStream.size());
                            byte[] data = new byte[byteArrayOutputStream.toByteArray().length];
                            inputstream.read(data);
                            //======================写入数据============================================================
                            dos.writeInt(byteArrayOutputStream.toByteArray().length);
                            dos.write(data);
                            dos.flush();

//                            DataOutputStream dos = new DataOutputStream(TheadServer.client.getOutputStream());
//                            FileInputStream fis = new FileInputStream("/sdcard/DCIM/Camera/IMG_20190409_155019.jpg");
//                            int size = fis.available();
//                            byte[] data = new byte[size];
//                            fis.read(data);
//                            dos.writeInt(size);
//                            dos.write(data);
//                            dos.flush();
//                            fis.close();
                        } catch (IOException e) {
                            Log.d("SendDataThread", "handleMessage: error1");
                            e.printStackTrace();
                        } finally {
                        }
                    }
                    //==================================================================================
                    //###################################################################################
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
