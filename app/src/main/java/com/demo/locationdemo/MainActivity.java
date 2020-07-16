package com.demo.locationdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.demo.locationdemo.clipboard.ClipboardMg;
import com.demo.locationdemo.hook.EvilInstrumentation;
import com.demo.locationdemo.hook.HookHelper;
import com.demo.locationdemo.wakelock.AlarmUtils;
import com.demo.locationdemo.wakelock.WakeBinderHookHelper;
import com.demo.locationdemo.wakelock.WakeLockUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private boolean flag;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
//        WakeBinderHookHelper.hookPowerManager(context);
//        WakeBinderHookHelper.hookAlareManager(context);
//        WakeBinderHookHelper.hookLocationManager(context);
        Log.i(TAG," context.getSystemService(ALARM_SERVICE); = " + context.getSystemService(ALARM_SERVICE).toString());
        Log.i(TAG," this.getSystemService(ALARM_SERVICE); = " + this.getSystemService(ALARM_SERVICE).toString());
        //  hook wakelock
        findViewById(R.id.location1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mHander.postDelayed(mRunnable, 5000);
//                WakeLockUtils.acquireWakeLock(MainActivity.this);
//                startSecondActivity();
//                startSystemActivity();
//                ClipboardMg.clipboard(MainActivity.this);
//                LocationUtils.getGPSLocation(context);
            }
        });
        findViewById(R.id.location2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mHander.postDelayed(mRunnable, 5000);
//                WakeLockUtils.releaseWakeLock(MainActivity.this);
//                startSecondActivity();
//                startSystemActivity();
//                ClipboardMg.clipboard(MainActivity.this);
//                AlarmUtils.getNextAlarmClock(context);
                WakeLockUtils.releaseWakeLock(context);
            }
        });
        findViewById(R.id.location3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mHander.postDelayed(mRunnable, 5000);
//                WakeLockUtils.releaseWakeLock(MainActivity.this);
//                startSecondActivity();
//                startSystemActivity();
//                ClipboardMg.clipboard(MainActivity.this);
                WakeLockUtils.acquireWakeLock(context);
            }
        });
    }

    public void startSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);
    }


    private Handler mHander = new Handler(Looper.myLooper());
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (flag) {
                getNetworkLocation();
                mHander.postDelayed(mRunnable, 5000);
            } else {
                Toast.makeText(context, "no permission", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initPermission();//针对6.0以上版本做权限适配
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
    }

    /**
     * 权限的结果回调函数
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            flag = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * 通过GPS获取定位信息
     */
    public void getGPSLocation() {
        Location gps = LocationUtils.getGPSLocation(this);
        if (gps == null) {
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtils.addLocationListener(context, LocationManager.GPS_PROVIDER, new LocationUtils.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        Toast.makeText(MainActivity.this, "gps onSuccessLocation location:  lat==" + location.getLatitude() + "     lng==" + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "gps location is null", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "gps location: lat==" + gps.getLatitude() + "  lng==" + gps.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 通过网络等获取定位信息
     */
    private void getNetworkLocation() {
        Location net = LocationUtils.getNetWorkLocation(this);
        if (net == null) {
            Toast.makeText(this, "net location is null", Toast.LENGTH_SHORT).show();
            Log.i("location", "net location is null");
        } else {
            Toast.makeText(this, "network location: lat==" + net.getLatitude() + "  lng==" + net.getLongitude(), Toast.LENGTH_SHORT).show();
            Log.i("location", "network location: lat==" + net.getLatitude() + "  lng==" + net.getLongitude());
        }
    }

    /**
     * 采用最好的方式获取定位信息
     */
    private void getBestLocation() {
        Criteria c = new Criteria();//Criteria类是设置定位的标准信息（系统会根据你的要求，匹配最适合你的定位供应商），一个定位的辅助信息的类
        c.setPowerRequirement(Criteria.POWER_LOW);//设置低耗电
        c.setAltitudeRequired(true);//设置需要海拔
        c.setBearingAccuracy(Criteria.ACCURACY_COARSE);//设置COARSE精度标准
        c.setAccuracy(Criteria.ACCURACY_LOW);//设置低精度
        //... Criteria 还有其他属性，就不一一介绍了
        Location best = LocationUtils.getBestLocation(this, c);
        if (best == null) {
            Toast.makeText(this, " best location is null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "best location: lat==" + best.getLatitude() + " lng==" + best.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

}
