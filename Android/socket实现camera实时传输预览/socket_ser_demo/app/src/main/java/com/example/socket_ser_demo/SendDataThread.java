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

    //static OutputStream os = null;
    //private static ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
    //private static ByteArrayOutputStream byteArrayOutputStream;
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
                    if( TheadServer.client.isConnected()&&(TheadServer.client != null))
                    //if 客户端socket存在 则发送消息
                    //if (TheadServer.client != null)
                    {
                        //Log.d("SendDataThread", "handleMessage: 客户端存在");
                        try {
                            //os = TheadServer.client.getOutputStream();//获取sockect的输出数据流 用于向socket中写入 帧数据
                            DataOutputStream dos = new DataOutputStream(TheadServer.client.getOutputStream());//向dos直接写数据到client
                            //====================数据转换 到data中=================================================================
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();//输出数据流
                            //图片数据转换成  数据流
                            image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, byteArrayOutputStream);
                            //image.compressToJpeg(new Rect(0, 0, 1280, 720), 50, byteArrayOutputStream);
                            byte[]data=byteArrayOutputStream.toByteArray();
                            //==================不需要转换 直接byteArrayOutputStream 到byte=================================================
                            //byteArrayOutputStream 转换到 ByteArrayInputStream
                           // ByteArrayInputStream inputstream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                           // byte[] data = new byte[byteArrayOutputStream.toByteArray().length];
                           // inputstream.read(data);//从inputstream中 取出数据  官方说明 read方法每次从数据源中读取和缓冲区大小相同的数据并存储在缓冲区中
                            //Log.d("SendDataThread", "handleMessage: "+byteArrayOutputStream.toByteArray().length);
                            //======================写入数据============================================================
                            //dos.writeInt(byteArrayOutputStream.toByteArray().length);//接收的时候 先读取一个长度
                            dos.writeInt(data.length);//接收的时候 先读取一个长度
                            dos.write(data);
                            dos.flush();
                            //dos.close();
                            //=======================测试单张图片传输效果============================================
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
