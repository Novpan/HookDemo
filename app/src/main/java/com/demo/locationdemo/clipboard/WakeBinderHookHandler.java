package com.demo.locationdemo.clipboard;

import android.content.ClipData;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 伪造剪切版服务对象
 */

public class WakeBinderHookHandler implements InvocationHandler {

    private static final String TAG = "hook/WakeBinderHookHandler";

    // 原始的Service对象 (IInterface)
    Object base;

    public WakeBinderHookHandler(IBinder base, Class<?> stubClass) {
        try {
            Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);
            // IClipboard.Stub.asInterface(base);
            this.base = asInterfaceMethod.invoke(null, base);
        } catch (Exception e) {
            throw new RuntimeException("hooked failed!");
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if ("acquireWakeLock".equals(method.getName()) || "releaseWakeLock".equals(method.getName())) {
            Log.d(TAG, "WakeBinderHookHandler " + Log.getStackTraceString(new Throwable(">><<")));
        }
        return method.invoke(base, args);
    }
}
