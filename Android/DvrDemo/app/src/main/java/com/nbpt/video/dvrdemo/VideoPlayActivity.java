package com.nbpt.video.dvrdemo;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.nbpt.video.util.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class VideoPlayActivity extends Activity {
	private String TAG="VideoPlayActivity";
	private VideoView vv_video;
	private MediaController mController;
	private ArrayList allFile;
	private ViewGroup play_start;
	private ViewGroup play_close;
	private String videoname;
	private String path;
	private int position;
	private File file;
	private String vedioTime = "";
	private String videopath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.video_play);
		//==============================================
		vv_video = (VideoView) findViewById(R.id.vv_video);
		mController = new MediaController(this);
		play_start = (ViewGroup) findViewById(R.id.play_start);
		play_close = (ViewGroup) findViewById(R.id.play_close);
		//===========================================================
		videoname = getIntent().getStringExtra("path");

		Log.d(TAG, "onCreate videoname url: "+videoname);
		String[] split = videoname.split("/");
		String string;
		if(videoname.startsWith("http")) {
			 string = split[8];
		}else{
			Log.d(TAG, "onCreate: 播放的是本地视频");
			string = split[7];
		}
		//System.out.println("string = ==  " + string);
//		if (string.startsWith("b")) {
//			vedioTime = string.substring(0, 16);//取jpg文件名
//			vedioTime = vedioTime + ".avi";
//		} else {
//			vedioTime = string.substring(0, 16);
//			vedioTime = vedioTime + ".mp4";//文件名
//		}
		position=getIntent().getIntExtra("position",0);
		allFile = getIntent().getStringArrayListExtra("allfile");//��ȡ�����ļ��б�
		Log.d(TAG, "position:"+ position+"  allFile.size: "+allFile.size());
		//======================================================================================================
//		if(videoname.contains("LockThumbnail")){
//			videopath = FileUtils.getmLockVideoPath();
//			//file = new File(FileUtils.getmLockVideoPath() + vedioTime + ".mp4");
////			file = new File(videopath + vedioTime + ".mp4");
//		}else{
//			videopath = FileUtils.getmVideoPath();
//			//file = new File(FileUtils.getmVideoPath() + vedioTime + ".mp4");
////			file = new File(videopath + vedioTime + ".mp4");
//		}
//		Log.d(TAG, "onCreate videopath: "+videopath);//录像视频文件路径
//		file = new File(videopath + vedioTime);//路径+文件名 获得完整的视频路径
//		Log.d(TAG, "onCreate: getPath "+file.getPath());
//====================================================================================================
		if (string.endsWith(".mp4")) {
			Log.d(TAG, "onCreate: MP4文件格式正确");
			//   设置播放源的路径

			if(videoname.startsWith("http")) {
				vv_video.setVideoPath(MainActivity.proxy.getProxyUrl(videoname));
			}else{
				vv_video.setVideoPath(new File(videoname).getAbsolutePath());
			}

			//vv_video.setVideoPath("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
			//vv_video.setVideoPath("/storage/emulated/0/RecordCamera/CameraVideo/f_20190812095236.mp4");
			// 为  VideoView 指定  MediaController
			vv_video.setMediaController(mController);
			// 为 MediaController 指定控制的 videoView
			mController.setMediaPlayer(vv_video);
			vv_video.requestFocus();
			vv_video.start();
			//  增加监听上一个 和下一个的切换事件  默认两个按钮是不显示
			mController.setPrevNextListeners(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.d(TAG, "position = = : "+position);
					if (position >= allFile.size() - 1) {
						Toast.makeText(VideoPlayActivity.this, "往下已无视频", 0)
								.show();
					} else {
						String next = allFile.get(position + 1).toString();
						Log.d(TAG, "onClick 1111111next file: "+next);
						//System.out.println("next = = "+next);
						String[] split = next.split("/");
						String string;
						if(videoname.startsWith("http")) {
							string = split[8];
						}else{
							Log.d(TAG, "onCreate: 播放的是本地视频");
							string = split[7];
						}
//						if (string.startsWith("b")) {
//							vedioTime = string.substring(0, 16);
//							vedioTime = vedioTime +".avi";
//						} else {
//							vedioTime = string.substring(0, 16);
//							vedioTime = vedioTime +".mp4";
//						}

						if(videoname.startsWith("http")) {
							vv_video.setVideoPath(MainActivity.proxy.getProxyUrl(next));
						}else{
							vv_video.setVideoPath(new File(videoname).getAbsolutePath());
						}
						vv_video.setMediaController(mController);

						vv_video.start();
						position = position + 1;
					}
				}
			}, new OnClickListener() {
				@Override
				public void onClick(View v) {

					Log.d(TAG, "position = = : "+position);
					if (position <= 0) {
						Toast.makeText(VideoPlayActivity.this, "往上已无视频", 0)
								.show();
					} else {
						String up = allFile.get(position - 1).toString();
                        Log.d(TAG, "onClick2222222 up file: "+up);
						String[] split = up.split("/");
						String string;
						if(videoname.startsWith("http")) {
							string = split[8];
						}else{
							Log.d(TAG, "onCreate: 播放的是本地视频");
							string = split[7];
						}
//						if (string.startsWith("b")) {
//							vedioTime = string.substring(0, 16);
//							vedioTime = vedioTime +".avi";
//						} else {
//							vedioTime = string.substring(0, 16);
//							vedioTime = vedioTime +".mp4";
//						}
						//
						if(videoname.startsWith("http")) {
							vv_video.setVideoPath(MainActivity.proxy.getProxyUrl(up));
						}else{
							vv_video.setVideoPath(new File(videoname).getAbsolutePath());
						}

						vv_video.setMediaController(mController);
						vv_video.start();
						position = position - 1;
					}
				}
			});

			vv_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					Toast.makeText(VideoPlayActivity.this, "已经播放结束", 3000)
							.show();

					VideoPlayActivity.this.finish();//退出当前界面
				}
			});

		}
	}

}