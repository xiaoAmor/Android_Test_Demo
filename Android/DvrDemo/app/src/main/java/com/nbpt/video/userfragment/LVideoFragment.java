package com.nbpt.video.userfragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nbpt.video.dvrdemo.R;
import com.nbpt.video.dvrdemo.VideoPlayActivity;
import com.nbpt.video.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LVideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "LVideoFragment";

    private ArrayList frontList;

    private GridView main_girdview;
    private FrontGridViewAdapter frontAdapter;
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


    public LVideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LVideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LVideoFragment newInstance(String param1, String param2) {
        LVideoFragment fragment = new LVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        frontList = FileUtils.getDownLoadVideoFile("front");//获取所有的文件列表
        Log.d(TAG, "onCreate前置文件列表: " + frontList.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lvideo, container, false);
        initview(view);
        return view;
    }

    private void initview(View view) {
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

        private LayoutInflater inflater;
        String vedioTime;
        private Context context;
        String vedioPath;

        public FrontGridViewAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return frontList.size();//返回GridView的数量
        }

        @Override
        public Object getItem(int position) {
            return frontList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
            //每个item的数据展现
            final Holder holder;
//			if (convertView == null) {
            convertView = inflater.inflate(R.layout.show_gridview_item,
                    null);//关联xml 使用每个item的布局文件
            holder = new Holder();
            //===========================================
            holder.img = (ImageView) convertView
                    .findViewById(R.id.grid_img);
            holder.tv = (TextView) convertView.findViewById(R.id.grid_text);
            holder.image_button = (ImageButton) convertView
                    .findViewById(R.id.id_item_select);
            holder.playImg = (ImageView) convertView
                    .findViewById(R.id.grid_play_img);
            //=============================================
            vedioPath = (String) frontList.get(position);//每个文件都是带绝对路径名称的
            //Log.d(TAG, "getView vedioPath: "+vedioPath);// /storage/emulated/0/RecordCamera/Thumbnail/f_20190812095236.jpg
            String[] split = vedioPath.split("/");//分隔文件名称
            //Log.d(TAG, "getView: "+split[7]);
            String string = split[7];//取出文件名称
            if (string.startsWith("b")) {
                vedioTime = string.substring(2, 16);
            } else {
                //f_20190807072102.jpg
                vedioTime = string.substring(2, 16);
            }
            //提取时间按格式显示
            holder.tv.setText((new StringBuilder())
                    .append(vedioTime.substring(0, 4)).append("/")
                    .append(vedioTime.substring(4, 6)).append("/")
                    .append(vedioTime.substring(6, 8)).append(" ")
                    .append(vedioTime.substring(8, 10)).append(":")
                    .append(vedioTime.substring(10, 12)).append(":")
                    .append(vedioTime.substring(12, 14)));
            //根据文件名提取图片数据作为背景
            Glide.with(getContext()).load(frontList.get(position).toString()).into(holder.img);

            holder.img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //编辑按钮可见的时候
                    if (btn_edit.getVisibility() == View.VISIBLE) {
                        Log.d(TAG, "onClick 选中的序号: " + position);
                        File file = new File("/mnt/sdcard/camerarecord");
                        if (file.exists()) {
                            Log.d(TAG, "onClick: StopRecordDialog");
                            StopRecordDialog();
                        } else {
                            //跳转到视频播放页面
                            Intent playIntent = new Intent();
                            playIntent.setClass(context, VideoPlayActivity.class);
                            String pathString = frontList.get(position).toString();
                            Log.d(TAG, "onClick: 跳转到播放界面 开始播放文件:" + pathString);
                            playIntent.putExtra("path", pathString);//传递当前文件的路径
                            //传递当前的序号
                            playIntent.putExtra("position", position);
                            playIntent.putStringArrayListExtra("allfile", frontList);//传递总的文件列表
                            startActivity(playIntent);
                        }
                    } else if (btn_edit.getVisibility() == View.GONE) {

                        //Log.d(TAG, "onClick: 编辑按钮不可见的时候");
                        //已经添加到  mSelectedImage 中 就取消选中
                        if (mSelectedImage.contains(frontList.get(position).toString())) {
                            mSelectedImage.remove(frontList.get(position).toString());
                            holder.image_button.setVisibility(View.GONE);
                            holder.image_button.setImageResource(R.drawable.picture_unselected);
                            holder.img.setColorFilter(null);
                        } else {
                            mSelectedImage.add(frontList.get(position).toString());
                            holder.image_button.setVisibility(View.VISIBLE);
                            holder.image_button.setImageResource(R.drawable.pictures_selected);
                            holder.img.setColorFilter(Color.parseColor("#77000000"));
                        }
                    }

                }
            });

            if (mSelectedImage.contains(frontList.get(position).toString())) {
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

            if (selectall == true) {
                holder.image_button.setVisibility(View.VISIBLE);
                holder.image_button
                        .setImageResource(R.drawable.pictures_selected);
                holder.img.setColorFilter(Color.parseColor("#77000000"));
            }

            holder.img.setTag(position);
            return convertView;
        }

        private class Holder {
            ImageView img;//缩略图
            ImageButton image_button;
            private ImageView playImg;//播放按钮
            TextView tv;//视频标签
        }
    }

    public void StopRecordDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("标题")
                .setMessage("请先停止录制再播放视频")
                .setPositiveButton("确定", null)
                .show();
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
                    for (int i = 0; i < frontList.size(); i++) {
                        //全部添加到
                        mSelectedImage.add(frontList.get(i).toString());
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();//重新刷新
                    break;
                case R.id.btn_cancel:
                    selectall = false;
                    if (mSelectedImage.size() != 0) {
                        mSelectedImage.clear();
                        //清除所有的选中
                    }
                    main_girdview.setAdapter(frontAdapter);
                    frontAdapter.notifyDataSetChanged();
                    break;
                case R.id.btn_back:
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
                    if (isFront) {
                        Log.d(TAG, "onClick: 前置删除文件");
                        for (int i = 0; i < mSelectedImage.size(); i++) {
                            String string = mSelectedImage.get(i).toString();
                            File mThumbnailFile = new File(string);
                            if (mThumbnailFile.exists()) {
                                mThumbnailFile.delete();
                            }
                            File mCameraVideo = new File(FileUtils.getmVideoPath()
                                    + string.substring(string.lastIndexOf('/') + 1)
                                    .replace("jpg", "avi"));
                            if (mCameraVideo.exists()) {
                                mCameraVideo.delete();
                            }
                        }
                        frontList = FileUtils.getAllFile("back");
//                        main_girdview.setAdapter(frontAdapter);
//                        frontAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "onClick: 后置删除文件" + mSelectedImage.size());
                        for (int i = 0; i < mSelectedImage.size(); i++) {
                            String string = mSelectedImage.get(i).toString();
                            Log.d(TAG, "要删除的文件缩略图名全称: " + string);
                            //delete_sdcard_videofile(string);//删除外部SD卡文件
                            File File = new File(string);
                            if (File.exists()) {
                                //Log.d(TAG, "onClick: 文件存在则删除");
                                File.delete();
                            }
                        }

                        //selectall = false;
                        if (mSelectedImage.size() != 0) {
                            mSelectedImage.clear();
                        }
                        //
                        frontList = FileUtils.getDownLoadVideoFile("front");
//                        main_girdview.setAdapter(frontAdapter);
//                        frontAdapter.notifyDataSetChanged();
                    }
                     main_girdview.setAdapter(frontAdapter);
                     frontAdapter.notifyDataSetChanged();
                default:
                    break;

            }
        }
    }



    public Bitmap getVideoThumbNail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

}
