package com.example.socket_client_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private String TAG="MainActivity";
    private static final int COMPLETED = 0x111;
    private MyHandler handler;
    private static Bitmap bitmap;
    public static ImageView image;
    ClientThread clientThread;
    public static Context context;

    public static EditText ipaddr;
    public static EditText ipport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new CheckPermission(this).initPermission();//检测权限
        image=(ImageView)findViewById(R.id.imageView1);
        ipaddr=(EditText) findViewById(R.id.addr);
        ipport=(EditText)findViewById(R.id.ipport);

        handler = new MyHandler();
        clientThread=new ClientThread(handler);
        new Thread(clientThread).start();
        context=getApplicationContext();
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
            //else if (msg.what == 0x222)
//            {
//                Toast.makeText(context, "Socket 连接192.168.201.176失败5S 尝试", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 结束Activity&从栈中移除该Activity
        Log.d(TAG, "onDestroy:程序退出 ");

    }

}
