package com.example.dai.oicq_android;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.dai.oicq_android.util.ApplicationUtil;
import com.example.dai.oicq_android.util.PropertiesUtil;
import com.example.dai.oicq_android.view.MyDialog;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author dailiwen
 * @date 2018/03/31
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Intent intent;
    private IntentFilter intentFilter;
    private MyDialog myDialog;
    private NetworkChangeReceiver networkChangeReceiver;
    private static Properties PROPERTIES = PropertiesUtil.getProperties();
    public static String SERVER_URL = "http://" + PROPERTIES.getProperty("serverUrl") + ":" + PROPERTIES.getProperty("serverPort") + "/IcodeFramework_be";

    /**
     * 取消HTTP请求tag
     */
    private long tag;

    private final int RC_CAMERA_AND_LOCATION = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //网络状态改变广播监听
        intentFilter = new IntentFilter();
        networkChangeReceiver = new NetworkChangeReceiver();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    /**
     * 初始化控件
     */
    public abstract void initData();

    /**
     * 初始化控件
     */
    public abstract void initView();

    /**
     * 设置监听
     */
    public abstract void initListener();

    /**
     * 简化Toast
     *
     * @param msg
     */
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 标题栏隐藏
     *
     * @param hideTitle
     */
    public void hideTitle(boolean hideTitle) {
        if (hideTitle) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    /**
     * 是否设置沉浸状态栏
     */
    public void setStatusBar(boolean isSetStatusBar) {
        if (isSetStatusBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 透明导航栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
    }

    /**
     * 页面跳转
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }

    public void startActivity(Class<?> clz, Bundle bundle) {
        if (intent == null) {
            intent = new Intent();
        }
        if (bundle == null) {
            startActivity(clz);
            return;
        } else {
            intent.setClass(BaseActivity.this, clz);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    /**
     * 获取权限方法
     * AfterPermissionGranted 注解
     * 如果所有的请求权限都已经被授权，任何一个通过正确的requestCode注解的方法都将执行。在所有权限都被授权后，通过注解描述的该方法，将会自动执行。
     * 当然，你也可以不用AfterPermissionGranted 注解，你可以将该方法放入onPermissionsGranted 这个回调方法里面来执行。
     */
    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    public void requestCodeQRCodePermissions() {
        //获取相机权限
        //String perms = Manifest.permission.CAMERA;
        //获取相机和读写权限
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        //看是否获取到权限，未获取进行获取
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和闪光灯的权限", RC_CAMERA_AND_LOCATION, perms);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    public void showProgressDialog(long flag) {
        tag = flag;
        if (myDialog == null) {
            myDialog = new MyDialog(this, R.style.MyDialog);
            myDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    myDialog.dismiss();
                }
            });
            myDialog.show();
        } else if (!myDialog.isShowing()) {
            myDialog.show();
        }
    }

    public void dismissShowProgress() {
        myDialog.dismiss();
    }

    /**
     * 网络状态改变监听
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {

            } else {
                showToast("网络异常，请检查您的网络");
            }
        }
    }
}
