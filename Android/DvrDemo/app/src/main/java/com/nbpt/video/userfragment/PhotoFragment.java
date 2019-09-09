package com.nbpt.video.userfragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.nbpt.video.dvrdemo.ImageActivity;
import com.nbpt.video.dvrdemo.R;
import com.nbpt.video.util.FileUtils;
import com.nbpt.video.util.ImageSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ArrayList imagefiles;
    private String TAG = "PhotoFragment";

    private GridView main_girdview;
    private FrontGridViewAdapter frontAdapter;

    public static ProgressBar download_progress;
    private Button btn_edit;
    private Button btn_checkall;
    private Button btn_cancel;
    private Button btn_delete;
    private Button btn_back;
    private Button btn_frontcamera;
    private Button btn_backcamera;
    private boolean isFront = false;//默认
    static int POSION;
    //static int prosess=0;

    /**
     * 用户选择的图片，存储为图片的完整路径
     **/
    public static List<String> mSelectedImage = new LinkedList<String>();
    private boolean selectall = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case 11:
                    //此处可以使用定时http请求下载 下载完成一个 更新一个进度
                    if(mSelectedImage.size()!=0) {
                        download_progress.setVisibility(View.VISIBLE);
                        String path=mSelectedImage.get(0);
                        //Log.d(TAG, "handleMessage 开始下载: "+path);
                        //FileUtils.HttpDownload(path+"/?type=download");
                        FileUtils.HttpDownload(path,mHandler);

                    }else{
                        Log.d(TAG, "handleMessage: 全部下载完成");
                        mSelectedImage.clear();
                        download_progress.setProgress(0);
                        download_progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"下载完成",Toast.LENGTH_SHORT).show();
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;
                case 12:
                    //检测有没有下载完成
                    //下载完成后 再执行 11 下载下一个文件
                    Log.d(TAG, "handleMessage: 下载完成单个文件，开始下一个");
                    if(mSelectedImage.size()!=0) {
                        mSelectedImage.remove(0);
                    }
                    mHandler.sendEmptyMessage(11);
                    //mHandler.sendEmptyMessageDelayed(11,500);

                    break;

            }
        }
    };

    public PhotoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFragment newInstance(String param1, Serializable list) {
        PhotoFragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putSerializable(ARG_PARAM2, (Serializable) list);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            imagefiles = (ArrayList) getArguments().getSerializable(ARG_PARAM2);
        }
        //获取所有的图片文件路径
        Log.d(TAG, "onCreate imagefiles======================: " + imagefiles.size() + "file" + imagefiles.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        initview(view);
        return view;
    }

    private void initview(View view) {
        download_progress=view.findViewById(R.id.progress_bar_download);
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
            return imagefiles.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return imagefiles.get(position);
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
            holder = new Holder();
            //==============================================================
            holder.img = (ImageView) convertView.findViewById(R.id.grid_img);
            holder.tv = (TextView) convertView.findViewById(R.id.grid_text);
            holder.image_button = (ImageButton) convertView.findViewById(R.id.id_item_select);
            holder.playImg = (ImageView) convertView.findViewById(R.id.grid_play_img);
            holder.playImg.setVisibility(View.GONE);//图片布局的时候  播放图标隐藏显示
            //============================================================================
            imagePath = (String) imagefiles.get(position);//url 列表中存储的是每个图片的完整访问路径  存储的http的路径
            String[] split = imagePath.split("/");
            String string = split[8];//文件名称
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

            //holder.img.setImageBitmap(BitmapFactory.decodeFile(imagefiles.get(position).toString()));
            //Log.d(TAG, "getView: " + imagefiles.get(position).toString());
            //holder.img.setImageBitmap(getImage(imagefiles.get(position).toString()+"/?type=download"));
            //   holder.img.setImageBitmap( BitmapFactory.decodeFile("/storage/emulated/0/RecordCamera/CameraImage/f_20190805135425.jpg"));
            //直接http获取图片文件转换成bmp文件 需要异步下载 使用glide
            //Glide.with(getContext()).load(imagefiles.get(position).toString()+"/?type=download").into(holder.img);
            Glide.with(getContext()).load(imagefiles.get(position).toString()).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, "onClick: " + imagefiles.get(position).toString());//点击每个图片
                    if (btn_edit.getVisibility() == View.VISIBLE) {
                        ImageActivity.POSION = position;
                        System.out.println("POSION == =" + ImageActivity.POSION);
                        ImageSource.getImageName(imagefiles);
                        Intent intent = new Intent();
                        intent.setClass(context, ImageActivity.class);
                        startActivity(intent);
                    } else if (btn_edit.getVisibility() == View.GONE) {

                        //判断 mSelectedImage 列表内是否有  当前选择的这个文件  如果有的话  取消现在  没有的话  加进去
                        if (mSelectedImage.contains(imagefiles.get(position).toString())) {
                            mSelectedImage.remove(imagefiles.get(position).toString());

                            holder.image_button.setVisibility(View.GONE);//选择图标 隐藏
                            holder.image_button.setImageResource(R.drawable.picture_unselected);
                            holder.img.setColorFilter(null);
                            Log.d(TAG, "onClick: 取消选中");
                        } else {
                            mSelectedImage.add(imagefiles.get(position).toString());//添加选择
                            holder.image_button.setVisibility(View.VISIBLE);
                            holder.image_button.setImageResource(R.drawable.pictures_selected);
                            holder.img.setColorFilter(Color.parseColor("#77000000"));
                            Log.d(TAG, "onClick: 选中");
                        }
                    }

                }
            });

            //每次全选或者取消的时候 返回的时候 都会刷新item
            if (mSelectedImage.contains(imagefiles.get(position).toString())) {
                holder.image_button.setVisibility(View.VISIBLE);
                holder.image_button
                        .setImageResource(R.drawable.pictures_selected);
                holder.img.setColorFilter(Color.parseColor("#77000000"));
            } else {
                holder.image_button.setVisibility(View.GONE);
                holder.image_button
                        .setImageResource(R.drawable.picture_unselected);
                holder.img.setColorFilter(null);
            }
//
            if (selectall == true) {
                //全选后 每个item构建的时候 都会走此流程 显示
                holder.image_button.setVisibility(View.VISIBLE);
                holder.image_button.setImageResource(R.drawable.pictures_selected);
                holder.img.setColorFilter(Color.parseColor("#77000000"));
            }

            holder.img.setTag(position);

            return convertView;
        }

        class Holder {
            ImageView img;
            ImageButton image_button;
            ImageView playImg;
            TextView tv;
        }
    }

    public static Bitmap getImage(String path) {

        try {
            Log.d("getImage", "getImage: " + path);
            HttpURLConnection conn = (HttpURLConnection) new URL(path).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            //设置读取超时时间（毫秒）
            conn.setReadTimeout(5000);

            System.out.println("tdw1");
            //if(conn.getResponseCode() == 200){
            //得到数据流
            InputStream inputStream = conn.getInputStream();
            //读取输入流
            //==============================================
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            Log.d("getget", "run: " + result.toString());
            //====================================================================
            //解析得到图片
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            //inputStream.close();
            return bitmap;
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
                    for (int i = 0; i < imagefiles.size(); i++) {
                        //全部添加到
                        mSelectedImage.add(imagefiles.get(i).toString());
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();//重新刷新
                    break;
                case R.id.btn_cancel:
                    download_progress.setProgress(0);
                    download_progress.setVisibility(View.GONE);
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                        //清除所有的选中
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_back:
                    download_progress.setProgress(0);
                    download_progress.setVisibility(View.GONE);
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
                        //Log.d(TAG, "onClick: 下载  新开启线程 每个开始下载  mSelectedImage传递到线程中 进行下载  没下载完一个 清除一个 刷新一下页面 选中图标消失一个");
                        //或者下方放置一个长进度条 下载一个 走一个进度

                        //download_progress.setMax(mSelectedImage.size());
                        //download_progress.setProgress(0);
                        //新开启一个线程 进行下载
                        //发送消息给mHander
                        mHandler.sendEmptyMessage(11);//通知线程下载
                    }else{
                        Log.d(TAG, "onClick: 未选中需要下载的文件");
                    }

                   // main_girdview.setAdapter(frontAdapter);
                   // frontAdapter.notifyDataSetChanged();
                default:
                    break;

            }
        }

    }
}
