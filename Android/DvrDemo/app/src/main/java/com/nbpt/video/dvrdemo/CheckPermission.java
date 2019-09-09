package com.nbpt.video.dvrdemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class CheckPermission extends AppCompatActivity {
     private String TAG="CheckPermission";
    //1、首先声明一个数组permissions，将需要的权限都放在里面
    static final String[] permissions  = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;//权限请求码
    private Activity activity;

    public CheckPermission(Activity activity){
        this.activity=activity;
    }

    //权限判断和申请
    public  void initPermission( ) {
        mPermissionList.clear();//清空没有通过的权限
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
                //Log.d(TAG, "initPermission: 遍历未取得的权限");
            }
        }


        //申请权限
        if (mPermissionList.size() > 0) {
            //有权限没有通过，需要申请
            Log.d(TAG, "initPermission: 开始申请权限");
            ActivityCompat.requestPermissions(activity, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去

        }
    }
        //请求权限后回调的方法
        // 参数： requestCode  是我们自己定义的权限请求码
        // 参数： permissions  是我们请求的权限名称数组
        // 参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，
        // 数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            boolean hasPermissionDismiss = false;//有权限没有通过
            if (mRequestCode == requestCode) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        hasPermissionDismiss = true;
                    }
                } //如果有权限没有被允许
                 if (hasPermissionDismiss) {
                     showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
                     }else{
                     //全部权限通过，可以进行下一步操作。。。
                     }
            }
        }

        /**
         * 不再提示权限时的展示对话框
         */
        AlertDialog mPermissionDialog; String mPackName = "com.huawei.liwenzhi.weixinasr";
        private void showPermissionDialog( ) {
            if (mPermissionDialog == null) {
                mPermissionDialog = new AlertDialog.Builder(activity)
                        .setMessage("已禁用权限，请手动授予")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelPermissionDialog();
                                Uri packageURI = Uri.parse("package:" + mPackName);
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                activity.startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //关闭页面或者做其他操作
                                cancelPermissionDialog();
                            } })
                        .create();
            }
            mPermissionDialog.show();
        }
        //关闭对话框
        public void cancelPermissionDialog() {
            mPermissionDialog.cancel();
        }

}
