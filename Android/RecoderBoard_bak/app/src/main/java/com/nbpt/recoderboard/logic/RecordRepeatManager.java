package com.nbpt.recoderboard.logic;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nbpt.recoderboard.util.MediaUtils;
import com.nbpt.recoderboard.entity.RecoderMsg;
import com.nbpt.recoderboard.viewmanager.RecoderViewManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by blueberry on 2017/7/17.
 */

public class RecordRepeatManager {
    private static RecordTime_callback recordTime_callbackListener;//// 获取接口对象
    private String TAG = "RecordRepeatManager";
    private static RecordRepeatManager mInstance = null;
    private Activity context;
    private long durion = 60 * 1000, interval = 500;
    private MediaUtils mediaUtils;
    private static final int RECORD_START = 0;
    private static final int RECORD_STOP = 1;
    private static final int RECORD_PAUSE = 2;
    private long timeSeconds;
    private static final int SD_CARD_SIZE = 1000;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //改成 按钮点击拍照
                case RECORD_START:
                    doCallBackMethod(1);
                    //开启一个线程检测 发生一个线程消息
                    //检测SD卡容量  判断剩余控件大小
                    mycheckHandler.sendEmptyMessageDelayed(1, 500);//发送消息 检测SDcard
                    //check_sdcard_fun();

                    timeSeconds = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHHmmss", Locale.getDefault());
                    String time1 = sdf.format(timeSeconds);
                    mediaUtils.setTargetName(time1 + ".mp4");
                    //mediaUtils.setTargetName(UUID.randomUUID() + ".mp4");
                    mediaUtils.record();
                    mHandler.sendEmptyMessageDelayed(RECORD_STOP, durion);
                    break;
                case RECORD_STOP:
                    doCallBackMethod(0);
                    //录制一分钟后 执行此操作 判断是否在录制
                    if (mediaUtils.isRecording()) {
                        mediaUtils.stopRecordSave();//录制结束
                        mHandler.sendEmptyMessage(RECORD_START);
                        //mHandler.sendEmptyMessageDelayed(RECORD_START,600);
                        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                    } else {
                        Log.d(TAG, "handleMessage: 没有在录制 所以保存失败");
                        Toast.makeText(context, "保存失败", Toast.LENGTH_LONG).show();
                        //出现保存失败的情况  释放掉所有 重新开始录制
                        mHandler.sendEmptyMessage(RECORD_START);//重新开始录制
                    }
                    break;
                case RECORD_PAUSE:
                    if (mediaUtils.isRecording()) {
                        mediaUtils.stopRecordSave();//录制结束
                        Toast.makeText(context, "保存成功", Toast.LENGTH_LONG).show();
                        stop();
                    } else {
                        Toast.makeText(context, "保存失败", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    //处理线程消息
    private Handler mycheckHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "handleMessage: sd卡线程消息接收1");
                    CheckSdcardSpace();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    /**
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(RecoderMsg msg) {
        Log.i("xxxx", "onMainEventBus...getMsg_durion:" + msg.getMsg_durion());
        if (!TextUtils.isEmpty(msg.getMsg_durion())) {
            durion = getReateRecoderDurion(msg.getMsg_durion());//界面设置后 重新更新录制时间间隔
        }
    }

    private long getReateRecoderDurion(String recoder_time) {
        int i = Integer.parseInt(recoder_time);
        switch (i) {
            case 0:
                return 1 * 60 * 1000;//1分钟
            case 1:
                return 3 * 60 * 1000;//3分钟
            case 2:
                return 5 * 60 * 1000;//5分钟
        }
        return durion;
    }

    public static RecordRepeatManager getInstance(Activity context) {
        if (mInstance == null) {
            synchronized (RecordRepeatManager.class) {
                if (mInstance == null) {
                    mInstance = new RecordRepeatManager(context);
                }
            }
        }
        return mInstance;
    }

    private RecordRepeatManager(Activity activity) {
        this.context = activity;
        mediaUtils = MediaUtils.getInstance(activity);
        EventBus.getDefault().register(this);
    }

    public RecordRepeatManager setRecordDurion(long recordDurion) {
        this.durion = recordDurion;
        return this;
    }

    /**
     * 目前必须设置为0，才能保证循环录制不漏帧，待确认。
     *
     * @param recordInterval
     * @return
     */
    public RecordRepeatManager setRecordInterval(long recordInterval) {
        this.interval = recordInterval;
        return this;
    }

    public void startRecordAuto() {
        mHandler.sendEmptyMessageDelayed(RECORD_START, 500);
    }

    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
    }

    public void pause() {
        mHandler.sendEmptyMessage(RECORD_PAUSE);
    }


    private void check_sdcard_fun() {
        //
        Message message = new Message();
        message.what = 1;
        mycheckHandler.sendMessage(message);
    }

    private void check_sdcard() {
        //开线程初始化数据
        Log.d(TAG, "check_sdcard: 开始检测SD卡容量");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ######check sd##############");
                Message message = new Message();
                message.what = 1;
                mycheckHandler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 循环录像，当内存卡容量少于300M时，自动删除视频列表里面的第一个文件
     */
    private void CheckSdcardSpace() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            Log.d(TAG, "xunhuanluxiang: " + path);
            // 取得sdcard文件路径
            StatFs statfs = new StatFs(path.getPath());//通过 Environment可以获取sdcard的路径
            // 获取block的SIZE
            long blocSize = statfs.getBlockSize();
            // 获取BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            // 己使用的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            // 获取当前可用内存容量，单位：MB
            long sd = availaBlock * blocSize / 1024 / 1024;
            Log.d(TAG, "xunhuanluxiang: 剩余容量" + sd + " MB");
            if (sd < SD_CARD_SIZE) {
                String filepath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/DVRVideos/");
                File file = new File(filepath);
                //Log.d(TAG, "xunhuanluxiang: file path:" + filepath + "file:" + file);
                if (!file.exists()) {
                    Log.d(TAG, "xunhuanluxiang: 目录不存在");
                    //file.mkdirs();//已经在初始化设置中检测并创建
                }
                File[] files = file.listFiles();//列出目录下的所有文件名
                Log.d(TAG, "文件数量: " + files.length);
                /*                for (File f : files){
                    Log.d(TAG, "文件名: "+f);
                }*/
                if (files.length > 0) {
                    //得到文件名的一个map
                    try {
                        //时间格式
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHHmmss");
                        Map<Long, String> map = new HashMap<>();
                        for (File file1 : files) {
                            //提取文件名名称 因为文件名名称根据时间来命名 所以是一个时间戳
                            String time = file1.getName().replace(".mp4", "");//提取每个文件名前面的时间戳
                            Date date = simpleDateFormat.parse(time);//根据时间戳 变成时间
                            //建表  时间戳 ==文件名
                            map.put(date.getTime(), file1.getName()); //<1233282716409,2018-2-1_log.txt>
                        }
                        long lt = Long.valueOf(Collections.min(map.keySet()).toString());
                        File f1 = new File(filepath + map.get(lt));
                        Log.d(TAG, "CheckSdcardSpace: 要删除的文件名:" + f1);
//                        if (f1.getName().endsWith(".MP4"))//判断一下文件名称
//                            Log.d(TAG, "CheckSdcardSpace: ");
                        if (f1.exists())
                            f1.delete();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//
//                    try {
//                        String childFile[] = file.list();
//                        Log.d(TAG, "文件名称集合: " + childFile);
//                        String dele = (filepath + childFile[0]);//不一定按顺序来排序
//                        Log.d(TAG, "xunhuanluxiang: 要删除的文件路径:" + dele);
//                        File file2 = new File(dele);
//                        file2.delete();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        } else if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_REMOVED)) {
            Toast.makeText(context, "请插入内存卡", Toast.LENGTH_SHORT).show();
        }
    }

    //=========================================================================================================
    //接口的调用方法 用来传值
    public void doCallBackMethod(int value) {
        if (recordTime_callbackListener != null) {
            Log.d(TAG, "doCallBackMethod: 接口传输数据");
            recordTime_callbackListener.RecordTimeListener_do(value);//在这里传值
        }
    }

    //用于需要调用的类绑定接口
    public static void setRecordTime_callback(RecordTime_callback recordTime_callback) {
        recordTime_callbackListener = recordTime_callback;
    }

    //申明一个接口 类
    public interface RecordTime_callback {
        void RecordTimeListener_do(int value);//只申明 不实现  在传递数据的位置 实现
    }
//==================================================================================================

}
