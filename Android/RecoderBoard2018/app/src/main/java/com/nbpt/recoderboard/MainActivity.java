package com.nbpt.recoderboard;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import com.nbpt.recoderboard.fragment.SettingPrefsFragment;
import com.nbpt.recoderboard.logic.GdLocationManager;
import com.nbpt.recoderboard.logic.RecordRepeatManager;
import com.nbpt.recoderboard.util.Constant;
import com.nbpt.recoderboard.util.MediaUtils;
import com.nbpt.recoderboard.viewmanager.MarkViewManager;
import com.nbpt.recoderboard.viewmanager.RecoderViewManager;
import com.nbpt.recoderboard.viewmanager.SettingViewManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by blueberry on 2017/7/13.
 * 主界面
 */

public class MainActivity extends BaseActivity{

    private SurfaceView surfaceView;
    private MediaUtils mediaUtils;
    private SettingPrefsFragment settingPrefsFragment;
    private RecoderViewManager recoderViewManager;
    private SettingViewManager settingViewManager;
    private MarkViewManager markViewManager;
    private static long delayHide = 5*1000;
    private static long defaultRecoderDurion = 25*1000;
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
        mediaUtils.setTargetDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
        timeSeconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHHmmss", Locale.getDefault());
        String time1 = sdf.format(timeSeconds);
        mediaUtils.setTargetName(time1 + ".mp4");
        //mediaUtils.setTargetName(UUID.randomUUID() + ".mp4");
        mediaUtils.setSurfaceView(surfaceView);//设置surfaceView

        long durion = getReateRecoderDurion();
        //Log.i("xxx","durion:"+durion);

        RecordRepeatManager.getInstance(this).setRecordDurion(durion).setRecordInterval(500).startRecordAuto();//开始循环录制
        //RecordRepeatManager.getInstance(this).setRecordDurion(durion).setRecordInterval(500);
        // recoderViewManager.initData().hideDelay(delayHide);//delay秒无操作消失
        //settingViewManager.initData().hideDelay(delayHide);
        recoderViewManager.showContinue();
        recoderViewManager.time_continue();
        markViewManager.initData();//水印部分（车牌号，位置，时间。。。）

        GdLocationManager.getInstance(this).startLocation();

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
                    return 1*60*1000;//1分钟
                case 1:
                    return 3*60*1000;//3分钟
                case 2:
                    return 5*60*1000;//5分钟

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
       return  mediaUtils.isRecording();
    }

    /**
     * 显示设置界面
     */
    public void showSettingLay(){

        if(!settingPrefsFragment.isVisible()) {

            //recoderViewManager.hide();
            settingViewManager.hide();

            if (!settingPrefsFragment.isAdded()) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right)
                        .add(R.id.setting_content, settingPrefsFragment)
                        .addToBackStack(null)
                        .commit();
            }else{
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right)
                        .show(settingPrefsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    /**
     * 隐藏设置界面
     */
    public void hideSettingLay(){

        if(settingPrefsFragment != null && settingPrefsFragment.isVisible()){

            recoderViewManager.showContinue();
            settingViewManager.showContinue();

            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_left,R.animator.slide_out_right)
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
}
