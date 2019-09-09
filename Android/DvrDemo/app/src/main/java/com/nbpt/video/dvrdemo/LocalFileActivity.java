package com.nbpt.video.dvrdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.nbpt.video.userfragment.LImageFragment;
import com.nbpt.video.userfragment.LVideoFragment;

import java.util.ArrayList;
import java.util.List;

public class LocalFileActivity extends AppCompatActivity {
    private TabLayout mtabLayout;
    private ViewPager mviewPager;
    //private ArrayList<String> titleList;
    private List<String> titleList ;
    List<Fragment> mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_file);
        init_view();
    }

    private void init_view() {
        mtabLayout = findViewById(R.id.dvrfiletab_layout);
        mviewPager=findViewById(R.id.dvrfileview_pager);
        titleList  = new ArrayList<>();

        titleList .add("本地视频");
        titleList .add("本地图片");

        mFragment = new ArrayList<>();
        mFragment.add( LVideoFragment.newInstance("video","1"));
        mFragment.add( LImageFragment.newInstance("image","2"));
        //mFragment.add();

        mtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //添加选中Tab的逻辑
                //添加选中Tab的逻辑
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                添加未选中Tab的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                再次选中tab的逻辑
            }
        });
//给ViewPager创建适配器，将Title和Fragment添加进ViewPager中
        mviewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
            //此方法可以不写  直接在初始化的时候 指定标题即可
            @Override
            public CharSequence getPageTitle(int position) {
                return titleList.get(position);
            }
        });
        mtabLayout.setupWithViewPager(mviewPager);

    }
}
