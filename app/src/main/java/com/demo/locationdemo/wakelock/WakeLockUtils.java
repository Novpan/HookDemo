package com.demo.locationdemo.wakelock;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class WakeLockUtils {

    static PowerManager.WakeLock mWakeLock;

    private static void init(Context activity) {
        final PowerManager pm =
                (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "test:wacklock");
    }

    /**
     * 获取wakelock（播放时使用）
     */
    public static void acquireWakeLock(Context activity) {
        init(activity);
        Log.i("MainActivity", "acquireWakeLock = " + (null != mWakeLock));
        mWakeLock.acquire();
    }

    /**
     * 释放wakelock(退出暂停播放完成时释放)g
     */
    public static void releaseWakeLock(Context activity) {
        init(activity);
        mWakeLock.release();
    }
}
