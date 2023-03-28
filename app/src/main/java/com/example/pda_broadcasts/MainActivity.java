package com.example.pda_broadcasts;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pda_broadcasts.utils.ToastUtil;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Intent yffService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToastUtil.getInstance().init(this);
        startYFFService();
    }

    public void startYFFService() {
        yffService = new Intent(MainActivity.this, BroadcastService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                ToastUtil.toast("请找到 <源发发> 并开启悬浮权限");
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            } else {
                if (!isServiceRunning(this, yffService.getComponent().getClassName())) {
                    startService(yffService);
                    moveTaskToBack(true);
                } else {
                    ToastUtil.toast("已经开启悬浮窗");
                }
            }
        } else {
            stopService(yffService);
            startService(yffService);
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtil.toast("授权失败,请打开权限设置手动授权");
                } else {
                    ToastUtil.toast("授权成功");
                    startService(yffService);
                    moveTaskToBack(true);
                }
            }
        }
    }

    /**
     * 判断服务是否开启
     */
    public boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(ServiceName)) {
                return true;
            }
        }
        return false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}