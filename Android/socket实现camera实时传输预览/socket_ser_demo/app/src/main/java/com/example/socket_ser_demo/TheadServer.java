package com.example.socket_ser_demo;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//import java.awt.Image;
//import java.awt.image.BufferedImage;

//import javax.imageio.ImageIO;

public class TheadServer implements Runnable {
    //public static ByteArrayOutputStream outputStream;
    private String TAG = "TheadServer";
    public static Socket client = null;

    //public static OutputStream os = null;
    //private BufferedImage bufferedImage;
    public InputStream ins;
    private BufferedReader in = null;
    private PrintWriter printWriter = null;
    public   static OutputStream outputStream;

    public TheadServer(ServerSocket ss) throws IOException {
        this.client = ss.accept();//获取连接的客户端
        this.outputStream = client.getOutputStream();
        Log.d(TAG, "TheadServer: 接收到客户端:" + client.getInetAddress());
        //===========================================================
//        try {
//            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF-8")), true);
//            in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
//            printWriter.println("成功连接服务器" + "（服务器发送）");
//            System.out.println("成功连接服务器");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //=======================================================================
    }

    @Override
    public void run() {
        try {
            Log.d(TAG, "run: 服务线程启动运行。。。");
            while (true) {
                // 3.拿到输入流（客户端发送的信息就在这里）
//                InputStream is = client.getInputStream();
//                // 4.解析数据
//                InputStreamReader reader = new InputStreamReader(is);
//                BufferedReader bufReader = new BufferedReader(reader);
//                String s = null;
//                StringBuffer sb = new StringBuffer();//存储 客户端发送的数据包
//                while ((s = bufReader.readLine()) != null) {
//                    sb.append(s);
//                }
//                Log.d(TAG, "run: "+ sb.toString());
//                //System.out.println("服务器：" + sb.toString());
//                // 关闭输入流
//                client.shutdownInput();
//
//                OutputStream os = client.getOutputStream();
//                os.write(("我是服务端,客户端发给我的数据就是：" + sb.toString()).getBytes());
//                os.flush();//刷新
//                // 关闭输出流
//                client.shutdownOutput();
//                os.close();
//
//                // 关闭IO资源
//                bufReader.close();
//                reader.close();
//                is.close();

//             //可以正常发送数据
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
//                bw.write("1111111111111222222222222222223333333333333");
//                bw.flush();//刷新
//                bw.close();//关闭流

                try {
                    //outputStream.write("1234aeadadada".getBytes());
                   // outputStream.flush();//刷新
                    //=================================================================================
//                    if(TheadServer.client!=null) {
//                        Log.d(TAG, "run: send bitmap...");
//                        DataOutputStream dos = new DataOutputStream(TheadServer.client.getOutputStream());
//                        FileInputStream fis = new FileInputStream("/sdcard/DCIM/Camera/IMG_20190409_155019.jpg");
//                        int size = fis.available();
//                        byte[] data = new byte[size];
//                        fis.read(data);
//                        dos.writeInt(size);
//                        dos.write(data);
//                        dos.flush();
//                        fis.close();
//                    }

                    Thread.sleep(5000);//每隔1s执行一次
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

//            while (true) {                                   //循环接收、读取 Client 端发送过来的信息
//                OutputStream oos = client.getOutputStream();//获取客户端的数据流
//                ImageIO.write(bufferedImage, "JPG", oos);
//                oos.write("\n".getBytes());
//                oos.flush();
//                oos.close();
//            }


//            ins = client.getInputStream();//获取客户端的数据流
//            bufferedImage = ImageIO.read(ins);
//            ins.close();
//            ThreadClient tc = new ThreadClient(bufferedImage);
//            new Thread(tc).start();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "run: error1 ");

        } finally {
            try {
                if (!client.isClosed()) {
                    Log.d(TAG, "run: 关闭client");
                    client.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "run: error2");
            }
        }
    }
}