package com.example.socket_client_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final int COMPLETED = 0x111;
    private MyHandler handler;
    private static Bitmap bitmap;
    public static ImageView image;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckPermission(this).initPermission();//检测权限
        image=(ImageView)findViewById(R.id.imageView1);
        handler = new MyHandler();
        clientThread=new ClientThread(handler);
        new Thread(clientThread).start();
    }

    static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            if (msg.what == COMPLETED) {
                //Log.d("MyHandler", "handleMessage: ");
                //刷新ui显示
                bitmap = (Bitmap)msg.obj;
                image.setImageBitmap(bitmap);
                super.handleMessage(msg);
            }
        }
    }
}
