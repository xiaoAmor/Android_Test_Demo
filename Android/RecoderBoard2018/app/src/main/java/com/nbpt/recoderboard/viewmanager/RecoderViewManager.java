package com.nbpt.recoderboard.viewmanager;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.nbpt.recoderboard.MainActivity;
import com.nbpt.recoderboard.R;
import com.nbpt.recoderboard.util.Utils;

/**
 * Created by blueberry on 2017/7/14.
 * 预留录像按钮，主要做点击事件，延长delay秒消失及其他
 */

public class RecoderViewManager implements IViewManager,View.OnClickListener{

    private MainActivity mainActivity;
    private Button btn_recoder;
    private TextView tv_recoder_time;
    private Handler handler = new Handler();
    private long delay;

    private long timer = 0;
    private String timeStr = "";

    private boolean isRecodering = false;
    private  long recordDurion;

    public RecoderViewManager(MainActivity context)
    {
        this.mainActivity = context;
    }

    @Override
    public RecoderViewManager initView() {
        btn_recoder = (Button) mainActivity.findViewById(R.id.btn_recoder);
        tv_recoder_time = (TextView) mainActivity.findViewById(R.id.tv_recoder_time);
        btn_recoder.setOnClickListener(this);
        recordDurion =mainActivity.getReateRecoderDurion();//每次开始录制时获取参数 重新配置
        return this;
    }

    public RecoderViewManager initData()
    {
        return this;
    }
    public  void  shou_recoder_time(){
        this.show();
    }
    @Override
    public void show() {
        btn_recoder.setVisibility(View.VISIBLE);
        tv_recoder_time.setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        btn_recoder.setVisibility(View.INVISIBLE);
        tv_recoder_time.setVisibility(View.INVISIBLE);
    }

    @Override
    public void hideDelay(long delay) {
        this.delay = delay;
        handler.postDelayed(hideR,delay);
    }

    @Override
    public void showContinue() {
        handler.removeCallbacks(hideR);
        show();
        //hideDelay(delay);
    }

    @Override
    public void stop() {
        isRecodering = false;
        handler.removeCallbacks(timeR);
        handler.removeCallbacks(hideR);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_recoder:
                if(isRecodering){
                    timer=0;
                    isRecodering = false;
                    if(mainActivity.isRecording()){
                        mainActivity.stopRecordSave();//录制结束
                        Toast.makeText(mainActivity,"保存成功",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mainActivity,"保存失败",Toast.LENGTH_LONG).show();
                    }
                    tv_recoder_time.setVisibility(View.INVISIBLE);
                    btn_recoder.setBackgroundResource(R.drawable.recoder);
                    handler.removeCallbacks(timeR);

                }else {
                    btn_recoder.setBackgroundResource(R.drawable.recoder_stop);
                    mainActivity.record();//开始录制
                    timer=0;
                    isRecodering = true;
                    tv_recoder_time.setVisibility(View.VISIBLE);
                    handler.postDelayed(timeR,1*1000);
                    Toast.makeText(mainActivity,"开始录制",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * 录制时间刷新
     */
    private Runnable timeR = new Runnable() {
        @Override
        public void run() {
            timer += 1000;

            if(recordDurion>0){
                if(timer>=recordDurion){
                    timer=0;
                }
            }else{
                recordDurion =mainActivity.getReateRecoderDurion();//每次开始录制时获取参数 重新配置
                if(timer>=(60*1000)){
                    timer=0;
                }
            }
            timeStr = Utils.getFormatTime(timer);
            tv_recoder_time.setText(timeStr);
            handler.postDelayed(timeR, 1000);
        }
    };

    private Runnable hideR = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public void time_continue(){
    handler.postDelayed(timeR,1*1000);
}



}
