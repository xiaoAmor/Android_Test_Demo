package com.nbpt.recoderboard;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.nbpt.recoderboard.fragment.SettingPrefsFragment;
import com.nbpt.recoderboard.logic.GdLocationManager;
import com.nbpt.recoderboard.logic.RecordRepeatManager;
import com.nbpt.recoderboard.util.Constant;
import com.nbpt.recoderboard.util.MediaUtils;
import com.nbpt.recoderboard.viewmanager.MarkViewManager;
import com.nbpt.recoderboard.viewmanager.RecoderViewManager;
import com.nbpt.recoderboard.viewmanager.SettingViewManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by blueberry on 2017/7/13.
 * 主界面
 */

public class MainActivity extends BaseActivity {
    private String TAG = "MainActivity";
    private SurfaceView surfaceView;
    private MediaUtils mediaUtils;
    private SettingPrefsFragment settingPrefsFragment;
    private RecoderViewManager recoderViewManager;
    private SettingViewManager settingViewManager;
    private MarkViewManager markViewManager;
    private static long delayHide = 5 * 1000;
    private static long defaultRecoderDurion = 60 * 1000;
    private long timeSeconds;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.sf_preview);
        settingPrefsFragment = SettingPrefsFragment.newInstance();
        recoderViewManager = new RecoderViewManager(this).initView();
        settingViewManager = new SettingViewManager(this).initView();
        markViewManager = new MarkViewManager(this).initView();
    }

    @Override
    protected void initData() {
        //mediaUtils = new MediaUtils(this);
        mediaUtils = MediaUtils.getInstance(this);
        mediaUtils.setRecorderType(MediaUtils.MEDIA_VIDEO);
        String filepath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/DVRVideos/");
        File file = new File(filepath);
        if (!file.exists()) {
            Log.d(TAG, "initData: 目录不存在");
            file.mkdirs();
        }
        //mediaUtils.setTargetDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        mediaUtils.setTargetDir(file);
        timeSeconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHHmmss", Locale.getDefault());
        String time1 = sdf.format(timeSeconds);
        mediaUtils.setTargetName(time1 + ".mp4");
        //mediaUtils.setTargetName(UUID.randomUUID() + ".mp4");
        mediaUtils.setSurfaceView(surfaceView);//设置surfaceView

        //long durion = getReateRecoderDurion();
        //Log.i("xxx","durion:"+durion);

        //应该在预览启动后 开始录制
        //RecordRepeatManager.getInstance(this).setRecordDurion(durion).setRecordInterval(500).startRecordAuto();//开始循环录制
        //RecordRepeatManager.getInstance(this).setRecordDurion(durion).setRecordInterval(500);
        // recoderViewManager.initData().hideDelay(delayHide);//delay秒无操作消失
        //settingViewManager.initData().hideDelay(delayHide);
        recoderViewManager.showContinue();
        //recoderViewManager.time_continue();//开始计时
        markViewManager.initData();//水印部分（车牌号，位置，时间。。。）

        GdLocationManager.getInstance(this).startLocation();

        //新建线程 输出 系统支持的分辨率列表
        Runner1_debug_camera_sizes runner1 = new Runner1_debug_camera_sizes();
        Thread thread1 = new Thread(runner1);
        //thread1.start();
        thread1.run();

    }
    public long getReateRecoderDurion() {
        SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(this);
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

    public void record() {
        //mediaUtils.record();
        long durion = getReateRecoderDurion();
        RecordRepeatManager.getInstance(this).setRecordDurion(durion).setRecordInterval(500).startRecordAuto();//开始循环录制
    }

    public void stopRecordSave() {
        RecordRepeatManager.getInstance(this).pause();
        //mediaUtils.stopRecordSave();
    }

    public boolean isRecording() {
        return mediaUtils.isRecording();
    }

    /**
     * 显示设置界面
     */
    public void showSettingLay() {

        if (!settingPrefsFragment.isVisible()) {

            //recoderViewManager.hide();
            settingViewManager.hide();

            if (!settingPrefsFragment.isAdded()) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                        .add(R.id.setting_content, settingPrefsFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                        .show(settingPrefsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    /**
     * 隐藏设置界面
     */
    public void hideSettingLay() {

        if (settingPrefsFragment != null && settingPrefsFragment.isVisible()) {

            recoderViewManager.showContinue();
            settingViewManager.showContinue();

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right)
                    .hide(settingPrefsFragment)
                    .commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        recoderViewManager.stop();
        settingViewManager.stop();
        GdLocationManager.getInstance(this).stopLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GdLocationManager.getInstance(this).destroyLocation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        settingViewManager.showContinue();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideSettingLay();//触摸预览界面隐藏设置界面
            settingViewManager.showContinue();//界面有操作再延长delay秒消失
            recoderViewManager.showContinue();

        } else if (event.getAction() == MotionEvent.ACTION_UP) {

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            showSettingLay();
        }
        return false;
    }
    //===============================================================================

    public void print_size(List<Camera.Size> sizes) {
        for (Camera.Size size : sizes) {
            Log.d(TAG, ">>>print_size:width " + size.width + ";height:" + size.height + ">>>");
        }
    }

    private void get_screen_args() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        Log.d("TAG", width + " , " + height);
    }

    class Runner1_debug_camera_sizes implements Runnable { // 实现了Runnable接口，jdk就知道这个类是一个线程
        public void run() {
            Log.d(TAG, "run: 开启线程运行");
//            ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//            int heapSize = manager.getMemoryClass();
//            Log.d(TAG, "run: heapSize:"+heapSize);
            get_screen_args();
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int cameraId = 0; cameraId < Camera.getNumberOfCameras(); cameraId++) {
                Camera.getCameraInfo(cameraId, cameraInfo);
                Camera camera = Camera.open(cameraId);
                Camera.Parameters params = camera.getParameters();
                List<Camera.Size> previewSIzes = params.getSupportedVideoSizes();
                Log.d(TAG, "====run:cameraId: " + cameraId);
                //print_size(previewSIzes);
                //===========================================
                //  List<Camera.Size> previewSIzes = params.getSupportedVideoSizes();//获取系统相机支持的分辨率列表 getSupportedPreviewSizes
                //Log.d(TAG, "===========+++++++==startPreView:getSupportedVideoSizes "+cameraSizeToSting( previewSIzes));
                List<Camera.Size> supportedPictureSizes = params.getSupportedPictureSizes();
                Log.d(TAG, "====run: supportedPictureSizes");
                //print_size(supportedPictureSizes);
                List<Camera.Size> supportedPreviewSizes = params.getSupportedPreviewSizes();
                Log.d(TAG, "====run: supportedPreviewSizes");
                //print_size(supportedPreviewSizes);
                //=========================================
                // Log.d(TAG, "initData: Video supported sizes: " + );
                if (camera != null) {
                    camera.release();
                    camera = null;
                }
            }


        }
    }


}
