package com.nbpt.video.dvrdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

//实现Runnalbe接口，重载Runnalbe接口中的run()方法。然后可以分配该类的实例，在创建 Thread 时作为一个参数来传递并启动
public class ClientThread implements Runnable {
    private String TAG = "ClientThread";
    private static final int COMPLETED = 0x111;
    public static  Socket clientsocket;
    //向UI线程发送消息
    private Handler handler;
    private Bitmap bitmap;

    //InputStream ins = null;
    public ClientThread(Handler handler) {
        this.handler = handler;
    }
    //客户端接收服务端发送的bitmap 传给ui线程更新显示
    @Override
    public void run() {
        int readlen = 0;
        while (VideoActivity.threadrunning) {
            //=====================================================================================================================
            try {
                Log.d(TAG, "run: socket client connect server");
                clientsocket = new Socket("192.168.43.1", 9000);//5S重连一次
//                clientsocket = new Socket();
//                SocketAddress socketAddress = new InetSocketAddress("192.168.43.1", 9000);
//                clientsocket.connect(socketAddress, 3000);
                if (clientsocket.isOutputShutdown()) {
                    Log.e("!!!!output is down!!!!", "down");
                }
                if (clientsocket != null) {
                    Log.d(TAG, "run: 客户端链接成功");
                    //一旦连接成功后 持续接收服务端的消息
                    while (true) {
                        if (clientsocket != null) {
                            try {
                                InputStream ins = clientsocket.getInputStream();//获取客户端的输入流
                                //=======================================================================
                                DataInputStream dataInput = new DataInputStream(ins);//转换数据输入流
                                int size = dataInput.readInt();//获取数据大小 必须和发送顺序一样  发送的时候先发送一个长度
                                if (size > 0) {
                                    byte[] data = new byte[size];//开辟一个缓存 存放socket数据
                                    readlen = 0;
                                    //Log.d(TAG, "run:size "+size);
                                    while (readlen < size) {
                                        //每次读取一部分数据  len变成每次增加的偏移量 要读取的数据量 size-len ,实际读取的数据量小于目标读取数据量
                                        readlen += dataInput.read(data, readlen, size - readlen);//数据转移到data缓存中
                                        //Log.d(TAG, "run: !!!!!!!!"+len);
                                    }
                                    //===================================================
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//转换成bitmap
                                    //ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outPut);
                                    //=======================================================================
                                    //发送到ui线程进行更新显示
                                    if (bitmap != null) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = COMPLETED;
                                        msg.obj = bitmap;
                                        handler.sendMessage(msg);
                                    }
                                } else {
                                    Log.d(TAG, "run: 读取图片大小失败");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Log.d(TAG, "run: cannot recv data;error1,break while recv");
                                break;
                            } catch (Exception e) {
                                Log.d(TAG, "run: error2");
                                e.printStackTrace();
                            } 
                        } else {
                            Log.d(TAG, "run: client null 空连接 break while recv data ");
                            break;
                        }

                    }//end whie
                } else {
                    Log.d(TAG, "run: socket client connect failed!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //关闭socket
                Log.d(TAG, "run: while threadrunning final,break while connect socket server");
                close_socket();
            }
            //=====================================================================================================================
//            try {
//                Log.d(TAG, "run: sleep 5S try!");
//                Thread.sleep(5000);//延时1s
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }//end while
        //
        Log.d(TAG, "run: 线程未运行");
        close_socket();
    }

    public void close_socket() {
        if ( (clientsocket != null) && (!clientsocket.isClosed())){
                Log.d(TAG, "run: 关闭客户端链接");
                try {
                    VideoActivity.playImg.setVisibility(View.VISIBLE);
                    clientsocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
