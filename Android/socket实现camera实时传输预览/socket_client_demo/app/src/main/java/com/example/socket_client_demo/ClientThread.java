package com.example.socket_client_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientThread implements Runnable{
private String TAG="ClientThread";
    private static final int COMPLETED = 0x111;
    public Socket clientsocket;
    //向UI线程发送消息
    private Handler handler;
    private Bitmap bitmap;
    InputStream ins = null;
    public ClientThread(Handler handler){
        this.handler=handler;
    }
    //客户端接收服务端发送的bitmap 传给ui线程更新显示
    @Override
    public void run() {
        int len = 0;
        byte [] buffer = new byte[1024];
        try {
            clientsocket= new Socket("192.168.201.191",9000);
            if(clientsocket!=null)
            {
                Log.d(TAG, "run: 客户端链接成功");
            }
            while (true)
            {
                if(clientsocket!=null)
                {
                    try {
                        ins = clientsocket.getInputStream();//获取客户端的输入流
//                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//                        //从输入流中取数据到buffer
//                        while( (len=ins.read(buffer)) != -1){
//                            outStream.write(buffer, 0, len);//数据转移到outstream
//                        }
//                        Log.d(TAG, "run: "+outStream.size());
//                        ins.close();//关闭输入流
//                        byte data[] = outStream.toByteArray();
//                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//data 转换成 bitmap
//                        outStream.flush();
//                        outStream.close();
                        //=======================================================================
                        DataInputStream dataInput = new DataInputStream(ins);//转换数据输入流
                        int size = dataInput.readInt();//获取数据大小
                        byte[] data = new byte[size];//开辟一个缓存 存放socket数据
                        len = 0;
                        while (len < size) {
                            //每次读取一部分数据  len变成每次增加的偏移量 要读取的数据量 size-len ,实际读取的数据量小于目标读取数据量
                            len += dataInput.read(data, len, size - len);//数据转移到data缓存中
                        }
                        //===================================================

                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//转换成bitmap
                        //ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPut);
                        //=======================================================================
                        //发送到ui线程进行更新显示
                        if(bitmap!=null) {
                            Message msg = handler.obtainMessage();
                            msg.what = COMPLETED;
                            msg.obj = bitmap;
                            handler.sendMessage(msg);
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "run: error1");
                        e.printStackTrace();
                    } finally {
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(!clientsocket.isClosed())
                {
                    Log.d(TAG, "run: 关闭客户端链接");
                    clientsocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
