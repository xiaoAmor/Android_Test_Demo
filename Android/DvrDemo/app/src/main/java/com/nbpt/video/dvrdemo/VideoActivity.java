package com.nbpt.video.dvrdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.nbpt.video.userfragment.PhotoFragment;
import com.nbpt.video.userfragment.VideoFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VideoActivity extends AppCompatActivity {
    private String TAG = "VideoActivity";
    private static final int COMPLETED = 0x111;
    private static Bitmap bitmap;
    private ClientHandler handler;
    private ClientThread clientThread;

    private static Thread thread1;
    public static boolean threadrunning = false;

    private TabLayout mtabLayout;
    private ViewPager mviewPager;

    public static ImageView clientimagev;
    public static ImageView playImg;
    private List<String> titleList;
    List<Fragment> mFragment;

    private ArrayList allimagefile;
    private ArrayList allvideofile;
    private ArrayList allvideofileplay;

    //用于接收 socket客户端的消息  连接失败消息  服务断开消息 刷新显示消息
    static class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case COMPLETED:
                    //可以计时 超时2s还未收到此消息 判断客服端连接被断开 可以重新连接
                    //Log.d("MyHandler", "handleMessage: ");
                    //刷新ui显示
                    bitmap = (Bitmap) msg.obj;
                    clientimagev.setImageBitmap(bitmap);
                    //super.handleMessage(msg);
                    break;

                case 222:
                    //断开连接了  显示播放按钮 并提示
                    playImg.setVisibility(View.VISIBLE);
                    break;

                case 333:

                    //连接成功 关闭等待进度条
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init_view();

    }

    private void init_view() {
        playImg = findViewById(R.id.play_img_btn);
        clientimagev = findViewById(R.id.client_imageView);
        mtabLayout = findViewById(R.id.dvrfiletab_layout);
        mviewPager = findViewById(R.id.dvrfileview_pager);

        allimagefile = getIntent().getStringArrayListExtra("allimagefile");
        allvideofile = getIntent().getStringArrayListExtra("allvideofile");
        allvideofileplay = getIntent().getStringArrayListExtra("allvideofileplay");

        handler = new ClientHandler();//接收clientThread传递出来的消息 用于接收图片消息
        clientThread = new ClientThread(handler);//将handle传递进去 用于接收消息

        titleList = new ArrayList<>();

        titleList.add("视频");
        titleList.add("图片");

        mFragment = new ArrayList<>();
        mFragment.add(VideoFragment.newInstance(allvideofileplay, allvideofile));
        mFragment.add(PhotoFragment.newInstance("allimagefile", allimagefile));

//        mtabLayout.addTab(mtabLayout.newTab().setText("视频").setIcon(R.mipmap.ic_launcher));
//        mtabLayout.addTab(mtabLayout.newTab().setText("图片").setIcon(R.mipmap.ic_launcher));

        mtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //添加选中Tab的逻辑
                //添加选中Tab的逻辑
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                添加未选中Tab的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                再次选中tab的逻辑
            }
        });
//给ViewPager创建适配器，将Title和Fragment添加进ViewPager中
        mviewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

            //此方法可以不写  直接在初始化的时候 指定标题即可
            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        mtabLayout.setupWithViewPager(mviewPager);


        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 播放按钮点击，打开预览");
                threadrunning = true;
                thread1 = new Thread(clientThread);//开启子线程 进行连接服务器进行预览
                thread1.start();
                playImg.setVisibility(View.GONE);//隐藏播放按钮
            }
        });
        if ((ClientThread.clientsocket != null) && (!ClientThread.clientsocket.isClosed())) {
            Log.d(TAG, "run: 关闭客户端链接");
            try {
                ClientThread.clientsocket.close();
                ClientThread.clientsocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
        stop_client_priview();
        //Log.d(TAG, "onDestroy: 关闭当前页面");
    }

    public static void stop_client_priview() {
        if (thread1 != null && thread1.isAlive()) {
            Log.d("stop_client_priview", "onDestroy: 关闭子线程");
            threadrunning = false;

            playImg.setVisibility(View.VISIBLE);//隐藏播放按钮
            thread1.interrupt();
            thread1 = null;
        }
    }

}
