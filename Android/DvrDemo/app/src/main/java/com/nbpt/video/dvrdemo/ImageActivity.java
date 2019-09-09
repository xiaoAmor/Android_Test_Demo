package com.nbpt.video.dvrdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.nbpt.video.dvrdemo.GalleryView.TransitionEffect;
import com.nbpt.video.tools.OutlineContainer;
import com.nbpt.video.util.FileUtils;
import com.nbpt.video.util.ImageSource;

import java.io.IOException;

public class ImageActivity extends Activity {
    private String TAG = "ImageActivity";
    private GalleryView mJazzy;
    public static int POSION;
    private TextView filetv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_activity);
        setupJazziness(TransitionEffect.Tablet);
    }

    private void setupJazziness(TransitionEffect effect) {
        mJazzy = (GalleryView) findViewById(R.id.jazzy_pager);
        filetv = (TextView) findViewById(R.id.filenametv);
        mJazzy.setTransitionEffect(effect);
        mJazzy.setAdapter(new MainAdapter());
        mJazzy.setPageMargin(100);
        System.out.println("RecordPictureActivity.POSION = = " + POSION);
        mJazzy.setCurrentItem(POSION);
    }

    private class MainAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(ImageActivity.this);
            imageView.setMaxHeight(getWindowManager().getDefaultDisplay()
                    .getWidth());
            imageView.setMaxWidth(getWindowManager().getDefaultDisplay()
                    .getHeight());

            String string = ImageSource.c.get(position).get(String.valueOf(position));
            Log.d(TAG, "=========instantiateItem: file url:" + string);
            if (string.startsWith("http")) {
                Glide.with(getApplicationContext()).load(string + "/?type=download").into(imageView);
            } else {
                Bitmap d = null;
                try {
                    d = getLoacalpng(string);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                imageView.setImageBitmap(d);
            }
            //String filename = FileUtils.getmPicturePath() + string;
            //System.out.println("filename = ="+filename);

            filetv.setText(string);

            container.addView(imageView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mJazzy.setObjectForPosition(imageView, position);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object obj) { // setCurrentItem(GridViewActivity.POSION)
            // ;
            container.removeView(mJazzy.findViewFromObject(position));
        }

        @Override
        public int getCount() {
            return ImageSource.GESHU;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            if (view instanceof OutlineContainer) {
                return view == obj;
            } else {
                return view == obj;
            }
        }

        public void setCurrentItem(int item) {
            setCurrentItem(item);

        }
    }

    public static Bitmap getLoacalpng(String url) throws IOException {
        return BitmapFactory.decodeFile(url);
    }
}