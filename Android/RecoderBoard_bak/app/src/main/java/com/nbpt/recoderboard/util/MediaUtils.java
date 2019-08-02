package com.nbpt.recoderboard.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.nbpt.recoderboard.MainActivity;
import com.nbpt.recoderboard.R;
import com.nbpt.recoderboard.entity.ResolutionMsg;
import com.nbpt.recoderboard.logic.RecordRepeatManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by blueberry on 2017/7/14.
 */

public class MediaUtils implements SurfaceHolder.Callback {
    private static final String TAG = "MediaUtils";
    public static final int MEDIA_VIDEO = 1;
    private Activity activity;
    private Handler handler = new Handler(Looper.getMainLooper());
    private MediaRecorder mMediaRecorder;
    private CamcorderProfile profile;
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private File targetDir;
    private String targetName;
    private File targetFile;
    private int previewWidth, previewHeight;
    private int recorderType;
    private boolean isRecording;
    private long timeSeconds;
    private int or = 0;
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    public static final int RESOLUTION_360P = 172800;
    public static final int RESOLUTION_480P = 411840;
    public static final int RESOLUTION_720P = 921600;
    public static final int RESOLUTION_1080P = 2073600;
    public static int RESOLUTION_VALUE = 0;//分辨率参数 0 720，1 1080

    private static long defaultRecoderDurion = 60 * 1000;
    private long durion = 0;

    private static MediaUtils mInstance = null;

    public static MediaUtils getInstance(Activity context) {
        if (mInstance == null) {
            //如果锁的是类对象的话，尽管new多个实例对象，但他们仍然是属于同一个类依然会被锁住，即线程之间保证同步关系
            //同步代码块 锁住的是该类的类对象
            synchronized (MediaUtils.class) {
                if (mInstance == null) {
                    mInstance = new MediaUtils(context);
                }
            }
        }
        return mInstance;
    }

    private MediaUtils(Activity activity) {
        this.activity = activity;
        // 注册订阅者
        EventBus.getDefault().register(this);
        durion = MediagetReateRecoderDurion(activity);
        //读取数据库中分辨率值
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(activity);
        String recoder_resolution = shp.getString(Constant.prefs_key_recoder_resolution, "");
        if (!TextUtils.isEmpty(recoder_resolution)) {
            int i = Integer.parseInt(recoder_resolution);
            String[] array = activity.getResources().getStringArray(R.array.recoder_resolution);
            Log.d(TAG, "MediaUtils: 读取分辨率recoder_resolution:" + i + durion);
            //set_resolution(i);//设置分辨率 更新 RESOLUTION_VALUE 值
            switch (i) {
                case 0:
                    //720p
                    Log.d(TAG, "onMainEventBus: 设置720P");
                    RESOLUTION_VALUE = 0;
                    break;
                case 1:
                    //1080p
                    Log.d(TAG, "onMainEventBus: 设置1080P");
                    RESOLUTION_VALUE = 1;
                    break;

            }
        }
    }

    public void setRecorderType(int type) {
        this.recorderType = type;
    }

    public void setTargetDir(File file) {
        this.targetDir = file;
    }

    public void setTargetName(String name) {
        this.targetName = name;
    }

    public String getTargetFilePath() {
        return targetFile.getPath();
    }

    public boolean deleteTargetFile() {
        if (targetFile.exists()) {
            return targetFile.delete();
        } else {
            return false;
        }
    }

    //设置预览窗口 初始化预览参数
    public void setSurfaceView(SurfaceView view) {
        this.mSurfaceView = view;
        mSurfaceHolder = mSurfaceView.getHolder();
        Log.d(TAG, "setSurfaceView初始化参数: " + previewWidth + previewHeight);
        //mSurfaceHolder.setFixedSize(previewWidth, previewHeight);//设置预览界面的大小
        mSurfaceHolder.setFixedSize(1920, 1080);//设置预览界面的大小
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);////下面设置surfaceView不维护自己的缓冲区,而是等待屏幕的渲染引擎将内容推送到用户面前
        mSurfaceHolder.addCallback(this);//回调函数设置
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    //录制
    public boolean isRecording() {
        Log.d(TAG, "检测isRecording录制状态: " + isRecording);
        return isRecording;
    }

    //=============录制============================
    public void record() {
        Log.i(TAG, "开始录制 输出录制状态isRecording:" + isRecording);
        if (isRecording) {

            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                targetFile.delete();
            }
            releaseMediaRecorder();
            mCamera.lock();
            isRecording = false;
        } else {
            startRecordThread();//开启录制线程
        }
    }

    //初始化录制之前  mMediaRecorder参数
    private boolean prepareRecord() {
        try {
            Log.d(TAG, "prepareRecord: 初始化mediarecorder");
            mMediaRecorder = new MediaRecorder();
            if (recorderType == MEDIA_VIDEO) {
                //sc60 预览 慢于 录制，因此此时 预览还未开始，所以此处报错  判断camera是否 null
                mCamera.stopPreview();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                timeSeconds = System.currentTimeMillis();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                String time1 = sdf.format(timeSeconds);
                //Log.d(TAG,"time"+time1);
                //每次重新录制视频的时候 进行此操作
                Log.d(TAG, "mMediaRecorder初始化设置profile.videoFrameRate:" + profile.videoFrameRate + "videoBitRate:" + profile.videoBitRate + "videoFrameWidth" + profile.videoFrameWidth + "videoFrameHeight" + profile.videoFrameHeight);
                mMediaRecorder.setProfile(profile);
            }
            targetFile = new File(targetDir, targetName);
            mMediaRecorder.setOutputFile(targetFile.getPath());

        } catch (Exception e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void stopRecordSave() {
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
                Log.d(TAG, "stopRecordSave: mMediaRecorder.stop");
            } catch (RuntimeException r) {
            } finally {
                Log.d(TAG, "stopRecordSave: 释放 releaseMediaRecorder");
                releaseMediaRecorder();
            }
        } else {
            Log.d(TAG, "stopRecordSave: error");
        }
    }

    public void stopRecordUnSave() {
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException r) {
                if (targetFile.exists()) {
                    //不保存直接删掉
                    targetFile.delete();
                }
            } finally {
                releaseMediaRecorder();
            }
            if (targetFile.exists()) {
                //不保存直接删掉
                targetFile.delete();
            }
        }
    }

    private void startPreView(SurfaceHolder holder) {
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开摄像头
        }
        if (mCamera != null) {
            mCamera.setDisplayOrientation(CameraUtils.getCameraDisplayOrientation(activity.getWindow(), Camera.CameraInfo.CAMERA_FACING_BACK));
            //mCamera.setDisplayOrientation(or);
            try {
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                /*
                //WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);//得到窗口管理器
                // Display display = wm.getDefaultDisplay();//得到当前屏幕
                WindowManager manager = activity.getWindowManager();
                DisplayMetrics outMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(outMetrics);
                int width = outMetrics.widthPixels;
                int height = outMetrics.heightPixels;
                Log.d(TAG, "startPreView: 物理屏幕宽度" + width + "屏幕高度" + height);
                */
                RESOLUTION_VALUE=1;
                //从系统相机所支持的size列表中找到与屏幕长宽比最相近的size
                //Camera.Size previewSize = CameraUtils.getCloselyPreSize(mSurfaceView.getWidth(),mSurfaceView.getHeight(),parameters.getSupportedPreviewSizes());
                Camera.Size previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_720P);//无论参数是1080p 720p 获取出来的 480*864
                /*
                Camera.Size previewSize = null;
                if (1 == RESOLUTION_VALUE) {
                    Log.d(TAG, "startPreView: 1080 ");
                    previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_1080P);
                } else {
                    Log.d(TAG, "startPreView: 720 ");
                    previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_720P);
                }
                */
                previewWidth = previewSize.width;//
                previewHeight = previewSize.height;//更新预览尺寸
                Log.d(TAG, "初始化 get previewSize参数并设置预览参数 parameters， previewWidth:" + previewWidth + "previewHeight:" + previewHeight);
                 parameters.setPreviewSize(previewWidth, previewHeight);//预览尺寸  只要铺满屏幕即可
//=============================================profile==============================================================

                if (1 == RESOLUTION_VALUE) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                    //profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                } else {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                }

                //profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);//
                // 重点，分辨率和比特率
                // 分辨率越大视频大小越大，比特率越大视频越清晰
                // 清晰度由比特率决定，视频尺寸和像素量由分辨率决定
                // 比特率越高越清晰（前提是分辨率保持不变），分辨率越大视频尺寸越大。
                //profile.videoFrameWidth = previewSize.width;
                //profile.videoFrameHeight = previewSize.height;
                Log.d(TAG, "修改前getvideoFrameHeight:" + profile.videoFrameWidth + "videoFrameHeight:" + profile.videoFrameHeight);
                if (1 == RESOLUTION_VALUE) {
                    profile.videoFrameWidth = 1920;
                    profile.videoFrameHeight = 1080;
                } else {
                    profile.videoFrameWidth = 1280;
                    profile.videoFrameHeight = 720;
                }

                // 这样设置 720p的视频 大小在5M , 可根据自己需求调节
                //profile.videoBitRate = 500000;
                profile.videoFrameRate = 30;//1080 15 //
                Log.d(TAG, "修改后videoFrameWidth:" + profile.videoFrameWidth + "videoFrameHeight:" + profile.videoFrameHeight + "profile.videoBitRate:" + profile.videoBitRate);
                //====================================================================================================================================
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        mode.contains("continuous-video");
                        parameters.setFocusMode("continuous-video");
                    }
                }
                //parameters.set("cam_mode",1);//abracadabra!
                mCamera.setParameters(parameters);
                mCamera.startPreview();//开始预览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    //====================surfacevie 三个回调函数==========================================
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        startPreView(holder);
        Log.d(TAG, "surfaceCreated: 预览开始==========并开始录制");

        RecordRepeatManager.getInstance(activity).setRecordDurion(durion).setRecordInterval(500).startRecordAuto();//开始循环录制
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //释放摄像头 停止预览
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            releaseCamera();
        }
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }

    private void startRecordThread() {

        if (prepareRecord()) {
            try {
                mMediaRecorder.start();
                isRecording = true;
            } catch (RuntimeException r) {
                releaseMediaRecorder();
            }
        } else {
            Log.i("xxxx", "设置录制初始化参数失败prepareRecord false");
        }
    }

    /**
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(ResolutionMsg msg) {
        Log.i(TAG, "onMainEventBus...接收到设置分辨率getMsg_durion:" + msg.getMsg_resolution());
        if (!TextUtils.isEmpty(msg.getMsg_resolution())) {
            int i = Integer.parseInt(msg.getMsg_resolution());
            set_resolution(i);//设置分辨率
        }
    }

    //shez
    //更新后需要更新profiel
    private void set_resolution(int resolution_value) {
        switch (resolution_value) {
            case 0:
                //720p
                Log.d(TAG, "onMainEventBus: 设置720P");
                RESOLUTION_VALUE = 0;
                profile.videoFrameWidth = 1280;
                profile.videoFrameHeight = 720;
                break;
            case 1:
                //1080p
                Log.d(TAG, "onMainEventBus: 设置1080P");
                RESOLUTION_VALUE = 1;
                profile.videoFrameWidth = 1920;
                profile.videoFrameHeight = 1080;
                break;

        }

    }
    //======================================================================
//    public void onPreviewFrame(byte[] data, Camera camera) {
//        // At preview mode, the frame data will push to here.
//        FrameLaneData = data;//.clone();
//        FrameCarData = data;//.clone();
//        if (isTakePic) {
//            isTakePic = false;
//            shutterCallback.onShutter();
//            previewSize = mCamera.getParameters().getPreviewSize();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        yunImage = new YuvImage(FrameLaneData, ImageFormat.NV21, previewSize.width, previewSize.height, null);
//                        if (yunImage != null) {
//                            ByteArrayOutputStream strem = new ByteArrayOutputStream();
//                            yunImage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, strem);
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(strem.toByteArray(), 0, strem.size());
//                            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                            String fileName = format.format(new Date());
//                            File file = new File(Tools.getPath() + fileName + ".jpg");
//                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//将图片压缩到流中
//                            bos.flush();//输出
//                            bos.close();//关闭
//                            strem.close();
//                            bitmap.recycle();
//                            //mHandler.sendEmptyMessage(MSG_TAKE_PICTURE);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }
//    }


    //=================================

    public long MediagetReateRecoderDurion(Activity activity) {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(activity);
        String recoder_time = shp.getString(Constant.prefs_key_recoder_time, "");
        if (TextUtils.isEmpty(recoder_time)) {
            return defaultRecoderDurion;
        } else {
            int i = Integer.parseInt(recoder_time);
            switch (i) {
                case 0:
                    return 1 * 60 * 1000;//1分钟
                case 1:
                    return 3 * 60 * 1000;//3分钟
                case 2:
                    return 5 * 60 * 1000;//5分钟
            }
            return defaultRecoderDurion;
        }
    }


}
