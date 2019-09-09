package com.nbpt.video.userfragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nbpt.video.dvrdemo.MainActivity;
import com.nbpt.video.dvrdemo.R;
import com.nbpt.video.dvrdemo.VideoPlayActivity;
import com.nbpt.video.util.FileUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "VideoFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList videofiles;
    private ArrayList videofilesplay;


    private GridView main_girdview;
    private FrontGridViewAdapter frontAdapter;

    private   TextView tvMsg;
    public static ProgressBar download_progress;
    private Button btn_edit;
    private Button btn_checkall;
    private Button btn_cancel;
    private Button btn_delete;
    private Button btn_back;
    private Button btn_frontcamera;
    private Button btn_backcamera;
    private boolean isFront = false;//默认

    /**
     * 用户选择的图片，存储为图片的完整路径
     **/
    public static List<String> mSelectedImage = new LinkedList<String>();
    private boolean selectall = false;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 11:
                    //此处可以使用定时http请求下载 下载完成一个 更新一个进度
                    if (mSelectedImage.size() != 0) {
                        show_progress();
                        String path = mSelectedImage.get(0);
                        //Log.d(TAG, "handleMessage 开始下载: "+path);
                        //取一下文件名 匹配一下 下载目录  如果有同名文件提示已经下载过  跳过该文件
                        //FileUtils.HttpDownload(path+"/?type=download");
                        FileUtils.HttpDownload(path, mHandler);//需要等待 下载完成 再执行下一个下载
                    } else {
                        Log.d(TAG, "handleMessage: 全部下载完成");
                        btn_delete.setEnabled(true);
                        mSelectedImage.clear();
                        download_progress.setProgress(0);
                            hide_progress();
                        Toast.makeText(getContext(), "下载完成", Toast.LENGTH_SHORT).show();
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;
                case 12:
                    //检测有没有下载完成
                    //下载完成后 再执行 11 下载下一个文件
                    Log.d(TAG, "handleMessage: 下载完成单个文件，开始下一个");
                    //开启一个线程 获取当前下载文件的缩略图  删除视频的同时 删除缩略图
                    //或者下载完成后 直接下载服务器的缩略图


                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.remove(0);
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    //mHandler.sendEmptyMessage(11);
                    mHandler.sendEmptyMessageDelayed(11, 1000);
                    break;
                case 13:
                    tvMsg.setText((int)(msg.arg1*1.0/msg.arg2*100)+"%");  //设置显示的进度，只显示整数

                    break;
            }
        }
    };

    private void show_progress() {
        download_progress.setVisibility(View.VISIBLE);
        tvMsg.setVisibility(View.VISIBLE);
    }
    private void hide_progress() {
        download_progress.setProgress(0);
        download_progress.setVisibility(View.GONE);
        tvMsg.setVisibility(View.GONE);
    }


    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(Serializable list2, Serializable list) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, list2);
        args.putSerializable(ARG_PARAM2, list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            videofilesplay = (ArrayList) getArguments().getSerializable(ARG_PARAM1);
            videofiles = (ArrayList) getArguments().getSerializable(ARG_PARAM2);
        }
        //获取所有的图片文件路径
        Log.d(TAG, "onCreate videofiles======================: " + videofiles.size() + "file" + videofiles.toString());
        Log.d(TAG, "onCreate videofilesplay======================: " + videofilesplay.size() + "file" + videofilesplay.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //共用一个布局文件
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        initview(view);
        return view;
    }

    private void initview(View view) {
        tvMsg = view.findViewById(R.id.tvMsg);
        download_progress = view.findViewById(R.id.progress_bar_download);
        btn_edit = (Button) view.findViewById(R.id.btn_edit);
        btn_checkall = (Button) view.findViewById(R.id.btn_checkall);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        btn_back = (Button) view.findViewById(R.id.btn_back);
        btn_frontcamera = (Button) view.findViewById(R.id.btn_frontcamera);
        btn_backcamera = (Button) view.findViewById(R.id.btn_backcamera);

        main_girdview = (GridView) view.findViewById(R.id.main_girdview);
        frontAdapter = new FrontGridViewAdapter(getContext());
        main_girdview.setAdapter(frontAdapter);
        frontAdapter.notifyDataSetChanged();

        btn_checkall.setEnabled(false);//全选默认没点击
        btn_cancel.setEnabled(false);//默认没点击
        btn_delete.setEnabled(false);//默认没点击
        btn_edit.setOnClickListener(new ClickListener());
        btn_checkall.setOnClickListener(new ClickListener());
        btn_cancel.setOnClickListener(new ClickListener());
        btn_delete.setOnClickListener(new ClickListener());
        btn_back.setOnClickListener(new ClickListener());
        btn_frontcamera.setOnClickListener(new ClickListener());
        btn_backcamera.setOnClickListener(new ClickListener());

    }

    private class FrontGridViewAdapter extends BaseAdapter {
        // private ArrayList list;
        private LayoutInflater inflater;

        private Context context;
        String imagePath;
        String vedioTime;

        public FrontGridViewAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return videofiles.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return videofiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final Holder holder;
//			if (convertView == null) {
            convertView = inflater.inflate(R.layout.show_gridview_item, null);//单个itme 布局
            holder = new FrontGridViewAdapter.Holder();
            holder.img = (ImageView) convertView.findViewById(R.id.grid_img);
            holder.tv = (TextView) convertView.findViewById(R.id.grid_text);
            holder.image_button = (ImageButton) convertView.findViewById(R.id.id_item_select);
            holder.playImg = (ImageView) convertView.findViewById(R.id.grid_play_img);
            //holder.playImg.setVisibility(View.GONE);//图片布局的时候  播放图标隐藏显示
            imagePath = (String) videofiles.get(position);//url 列表中存储的是每个图片的完整访问路径
            String[] split = imagePath.split("/");
            String string = split[8];
            //Log.d(TAG, "split[8] : "+string);
            if (string.startsWith("b")) {
                vedioTime = string.substring(2, 16);
            } else {
                vedioTime = string.substring(2, 16);
            }
            //每个ITME上显示文件名称
            holder.tv.setText((new StringBuilder())
                    .append(vedioTime.substring(0, 4)).append("/")
                    .append(vedioTime.substring(4, 6)).append("/")
                    .append(vedioTime.substring(6, 8)).append(" ")
                    .append(vedioTime.substring(8, 10)).append(":")
                    .append(vedioTime.substring(10, 12)).append(":")
                    .append(vedioTime.substring(12, 14)));

            //直接http获取图片文件转换成bmp文件 需要异步下载 使用glide
            //Glide.with(getContext()).load(videofiles.get(position).toString() + "/?type=download").into(holder.img);
            //Glide.with(getContext()).load(videofiles.get(position).toString()).into(holder.img);
            Glide.with(getContext()).load(videofiles.get(position).toString()).into(holder.img);
            //String path1="http://192.168.43.1:8000/storage/emulated/0/RecordCamera/CameraVideo/f_20190821160300.mp4";
            //Glide.with(getContext()).load( MainActivity.proxy.getProxyUrl(videofilesplay.get(position).toString())).into(holder.img);
            //holder.img.setImageBitmap(getVideoThumbnail(videofilesplay.get(position).toString()));//卡顿
            holder.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (btn_edit.getVisibility() == View.VISIBLE) {
                        Intent playIntent = new Intent();
                        playIntent.setClass(context, VideoPlayActivity.class);
                        //playIntent.setClass(context, TestPlayActivity.class);
                        //直接根据position对应关系 可能出现不对应的关系
                        String pathString = videofilesplay.get(position).toString();//根据图片的句柄 在播放url列表中选择url
                        Log.d(TAG, "onClick: 跳转到播放界面 开始播放文件:" + pathString);
                        playIntent.putExtra("path", pathString);//传递当前文件的路径
                        playIntent.putExtra("position", position);
                        playIntent.putStringArrayListExtra("allfile", videofilesplay);//传递总的文件列表
                        startActivity(playIntent);
                        //跳转到播放页面
                    } else if (btn_edit.getVisibility() == View.GONE) {
                        if (mSelectedImage.contains(videofilesplay.get(position).toString())) {
                            mSelectedImage.remove(videofilesplay.get(position).toString());
                            holder.image_button.setVisibility(View.GONE);
                            holder.image_button
                                    .setImageResource(R.drawable.picture_unselected);
                            holder.img.setColorFilter(null);
                        } else {
                            mSelectedImage.add(videofilesplay.get(position).toString());
                            holder.image_button.setVisibility(View.VISIBLE);
                            holder.image_button.setImageResource(R.drawable.pictures_selected);
                            holder.img.setColorFilter(Color.parseColor("#77000000"));
                        }
                    }

                }
            });
            if (mSelectedImage.contains(videofilesplay.get(position).toString())) {
                holder.image_button.setVisibility(View.VISIBLE);
                holder.image_button.setImageResource(R.drawable.pictures_selected);
                holder.img.setColorFilter(Color.parseColor("#77000000"));
            } else {
                holder.image_button.setVisibility(View.GONE);
                holder.image_button.setImageResource(R.drawable.picture_unselected);
                holder.img.setColorFilter(null);
            }
            if (selectall == true) {
                holder.image_button.setVisibility(View.VISIBLE);
                holder.image_button.setImageResource(R.drawable.pictures_selected);
                holder.img.setColorFilter(Color.parseColor("#77000000"));
            }

            holder.img.setTag(position);

            return convertView;
        }

        private class Holder {
            ImageView img;
            ImageButton image_button;
            private ImageView playImg;
            TextView tv;
        }
    }

    public Bitmap getVideoThumbnail(String url) {
        Bitmap bitmap = null;
//MediaMetadataRetriever 是android中定义好的一个类，提供了统一
//的接口，用于从输入的媒体文件中取得帧和元数据；
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //（）根据文件路径获取缩略图
//retriever.setDataSource(filePath);
            retriever.setDataSource(url, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        Log.v("bitmap!!!", "bitmap=" + bitmap);
        return bitmap;
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_edit:
                    //编辑按钮
                    btn_edit.setVisibility(View.GONE);//隐藏编辑按钮
                    btn_back.setVisibility(View.VISIBLE);//返回按钮显示
                    //全选  取消 删除按钮 都可以操作
                    btn_checkall.setEnabled(true);
                    btn_cancel.setEnabled(true);
                    btn_delete.setEnabled(true);

                    break;
                case R.id.btn_checkall:
                    selectall = true;
                    for (int i = 0; i < videofilesplay.size(); i++) {
                        //全部添加到
                        mSelectedImage.add(videofilesplay.get(i).toString());
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();//重新刷新
                    break;
                case R.id.btn_cancel:

                   hide_progress();
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                        //清除所有的选中
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_back:
                    hide_progress();
                    btn_edit.setVisibility(View.VISIBLE);//编辑按钮显示
                    btn_back.setVisibility(View.GONE);//返回按钮隐藏
                    //其他3个按钮 禁止点击
                    btn_checkall.setEnabled(false);
                    btn_cancel.setEnabled(false);
                    btn_delete.setEnabled(false);
                    //执行和取消相同操作 取消选中 刷新界面
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;

                case R.id.btn_frontcamera:
                    isFront = true;
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                    }
                    btn_frontcamera.setVisibility(View.GONE);
                    btn_backcamera.setVisibility(View.VISIBLE);

                    //重新获取一下 后置的文件
                    //imagefiles = FileUtils.getAllImageFile("back");


                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;

                case R.id.btn_backcamera:
                    isFront = false;
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                    }
                    btn_backcamera.setVisibility(View.GONE);
                    btn_frontcamera.setVisibility(View.VISIBLE);
                    //重新获取一下
                    //imagefiles = FileUtils.getAllImageFile("front");

                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;

                case R.id.btn_delete:
                    selectall = false;

                    //每个item上放置一个进度条
                    if (mSelectedImage.size() != 0) {
                        btn_delete.setEnabled(false);
                        Log.d(TAG, "onClick: 下载  新开启线程 每个开始下载  mSelectedImage传递到线程中 进行下载  没下载完一个 清除一个 刷新一下页面 选中图标消失一个");
                        //或者下方放置一个长进度条 下载一个 走一个进度

                        //新开启一个线程 进行下载
                        //发送消息给mHander
                        mHandler.sendEmptyMessage(11);
                    } else {
                        Log.d(TAG, "onClick: 未选中需要下载的文件");
                    }

                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                default:
                    break;

            }
        }
    }
}
