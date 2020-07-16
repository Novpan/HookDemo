package com.demo.locationdemo.wakelock;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.demo.locationdemo.base.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.POWER_SERVICE;

public class WakeBinderHookHelper {

    private static final String TAG = "WakeBinderHookHelper";

    public static void hookPowerManager(final Context context) {
        try {
            // 获取powerManager
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);

            // 获取当前manager里的service
            final Object OrginmService = ReflectUtil.getField(powerManager.getClass(), powerManager, "mService");

            // 动态代理IPowerManager接口
            Class iPM = Class.forName("android.os.IPowerManager");
            Object newPM = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iPM}, new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    if (method.getName().equalsIgnoreCase("acquireWakeLock") ||
                            method.getName().equalsIgnoreCase("releaseWakeLock")) {
                        getBatteryInfo(context);
                    }

                    return method.invoke(OrginmService, objects);

                }
            });

            // 将代理完的service对象放入原理的mService字段
            ReflectUtil.setField(powerManager.getClass(), powerManager, "mService", newPM);

        } catch (Exception e) {
            Log.e("hookPowerManager", e.getMessage());
        }
    }

    public static void hookAlareManager(final Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            final Object orginmService = ReflectUtil.getField(alarmManager.getClass(), alarmManager, "mService");
            Class iAM = Class.forName("android.app.IAlarmManager");
            Object newAM = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iAM}, new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    getBatteryInfo(context);
                    return method.invoke(orginmService, objects);

                }
            });

            ReflectUtil.setField(alarmManager.getClass(), alarmManager, "mService", newAM);

        } catch (Exception e) {

            Log.e("hookAlarmManager", e.getMessage());

        }

    }

    public static void hookLocationManager(final Context context) {
        try {
            // 获取powerManager
            LocationManager alarmManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            // 获取当前manager里的service
            final Object OrginmService = ReflectUtil.getField(alarmManager.getClass(), alarmManager, "mService");

            // 动态代理ILocationManager接口
            Class iPM = Class.forName("android.location.ILocationManager");
            Object newPM = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iPM}, new InvocationHandler() {
                @Override
                public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                    getBatteryInfo(context);
                    return method.invoke(OrginmService, objects);

                }
            });

            // 将代理完的service对象放入原理的mService字段
            ReflectUtil.setField(alarmManager.getClass(), alarmManager, "mService", newPM);

        } catch (Exception e) {
            Log.e("hookLocationManager", e.getMessage());
        }
    }

    private static void getBatteryInfo(Context context) {

        String stacktrace = Log.getStackTraceString(new Throwable());

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;

        BatteryInfo batteryInfo = new BatteryInfo(isCharging, stacktrace, batteryPct);

        Log.i(TAG, "batteryInfo = " + batteryInfo.toString());

    }

}
