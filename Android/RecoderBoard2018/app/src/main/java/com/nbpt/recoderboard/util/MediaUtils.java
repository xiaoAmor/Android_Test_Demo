package com.nbpt.recoderboard.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nbpt.recoderboard.R;
import com.nbpt.recoderboard.entity.ResolutionMsg;

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
    private Handler handler= new Handler(Looper.getMainLooper());
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
    public static  int RESOLUTION_VALUE=0;

    private static MediaUtils mInstance = null;

    public static MediaUtils getInstance(Activity context) {
        if (mInstance == null) {
            synchronized (MediaUtils.class) {
                if (mInstance == null) {
                    mInstance = new MediaUtils(context);
                }
            }
        }
        return mInstance;
    }
    private  MediaUtils(Activity activity) {
        this.activity = activity;
        // 注册订阅者
        EventBus.getDefault().register(this);
        //读取数据库中分辨率值
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(activity);
        String recoder_resolution = shp.getString(Constant.prefs_key_recoder_resolution,"");
        if(!TextUtils.isEmpty(recoder_resolution)) {
            int i = Integer.parseInt(recoder_resolution);
            String[] array = activity.getResources().getStringArray(R.array.recoder_resolution);
            Log.d(TAG, "MediaUtils: 读取分辨率recoder_resolution:"+i);
            set_resolution(i);//设置分辨率
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


    public void setSurfaceView(SurfaceView view) {
        this.mSurfaceView = view;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(previewWidth, previewHeight);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
    }
    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void record() {
        Log.i(TAG,"isRecording:"+isRecording);
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
            startRecordThread();
        }
    }

    private boolean prepareRecord() {
        try {
            //Log.d(TAG, "prepareRecord: 初始化mediarecorder");
            mMediaRecorder = new MediaRecorder();
            if (recorderType == MEDIA_VIDEO) {
                mCamera.stopPreview();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//                timeSeconds = System.currentTimeMillis();
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                String time1 = sdf.format(timeSeconds);
                //Log.d(TAG,"time"+time1);
               Log.d(TAG,"初始化设置profile.videoFrameRate:"+profile.videoFrameRate+"profile.videoBitRate:"+profile.videoBitRate+"profile.videoFrameWidth"+ profile.videoFrameWidth+"profile.videoFrameHeight"+profile.videoFrameHeight);
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
            } catch (RuntimeException r) {
            } finally {
                releaseMediaRecorder();
            }
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
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (mCamera != null) {
            mCamera.setDisplayOrientation(CameraUtils.getCameraDisplayOrientation(activity.getWindow(), Camera.CameraInfo.CAMERA_FACING_BACK));
            //mCamera.setDisplayOrientation(or);
            try {
                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                //从系统相机所支持的size列表中找到与屏幕长宽比最相近的size
                //Camera.Size previewSize = CameraUtils.getCloselyPreSize(mSurfaceView.getWidth(),mSurfaceView.getHeight(),parameters.getSupportedPreviewSizes());
                //Camera.Size previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_1080P);
                Camera.Size previewSize=null;
                if(1==RESOLUTION_VALUE){
                     previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_1080P);
                }else{
                     previewSize = CameraUtils.getCameraPreviewSize(mCamera, RESOLUTION_720P);
                }
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;

                Log.d(TAG,"初始化 profile previewWidth:"+previewWidth+"previewHeight:"+previewHeight);
                parameters.setPreviewSize(previewWidth, previewHeight);
                //profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                if(1==RESOLUTION_VALUE) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                }else{
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                }
                // 重点，分辨率和比特率
                // 分辨率越大视频大小越大，比特率越大视频越清晰
                // 清晰度由比特率决定，视频尺寸和像素量由分辨率决定
                // 比特率越高越清晰（前提是分辨率保持不变），分辨率越大视频尺寸越大。
                profile.videoFrameWidth = previewSize.width;
                profile.videoFrameHeight = previewSize.height;
//                profile.videoFrameWidth = 1280;
//                profile.videoFrameHeight =720;
                Log.d(TAG,"videoFrameHeight:"+profile.videoFrameWidth+"videoFrameHeight:"+profile.videoFrameHeight);
                // 这样设置 720p的视频 大小在5M , 可根据自己需求调节
                profile.videoBitRate = 2*previewSize.width * previewSize.height;
                profile.videoFrameRate=20;
                Log.d(TAG,"修改后videoFrameWidth:"+profile.videoFrameWidth+"videoFrameHeight:"+profile.videoFrameHeight+"profile.videoBitRate:"+profile.videoBitRate);
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        mode.contains("continuous-video");
                        parameters.setFocusMode("continuous-video");
                    }
                }
                mCamera.setParameters(parameters);
                mCamera.startPreview();
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        startPreView(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

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
        }else{
            Log.i("xxxx","prepareRecord false");
        }
    }

    /**
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(ResolutionMsg msg) {
        Log.i(TAG,"onMainEventBus...接收到设置分辨率getMsg_durion:"+msg.getMsg_resolution());
        if(!TextUtils.isEmpty(msg.getMsg_resolution())) {
            int i = Integer.parseInt(msg.getMsg_resolution());
            set_resolution(i);//设置分辨率
//            switch (i) {
//                case 0:
//                    //720p
//                    Log.d(TAG, "onMainEventBus: 设置720P");
//                    break;
//                case 1:
//                    //1080p
//                    Log.d(TAG, "onMainEventBus: 设置1080P");
//                    break;
//            }
        }
    }
    private void set_resolution(int resolution_value){
        switch (resolution_value) {
            case 0:
                //720p
                Log.d(TAG, "onMainEventBus: 设置720P");
                RESOLUTION_VALUE=0;
                break;
            case 1:
                //1080p
                Log.d(TAG, "onMainEventBus: 设置1080P");
                RESOLUTION_VALUE=1;
                break;

        }

    }
    //======================================================================

}
