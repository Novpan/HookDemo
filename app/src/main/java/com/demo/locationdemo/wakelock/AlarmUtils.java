package com.demo.locationdemo.wakelock;

import android.app.AlarmManager;
import android.content.Context;
import android.util.Log;

public class AlarmUtils {

    static AlarmManager mAlarmManager;

    private static void init(Context context) {
        mAlarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static void setTime(Context activity) {
        if (mAlarmManager == null) {
            init(activity);
        }
        Log.i("MainActivity", "setTime ");
        mAlarmManager.setTime(1000);
    }

    public static void getNextAlarmClock(Context activity) {
        if (mAlarmManager == null) {
            init(activity);
        }
        mAlarmManager.getNextAlarmClock();
    }
}
