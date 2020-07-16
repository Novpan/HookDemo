package com.demo.locationdemo.hook;

import android.app.Application;
import android.content.Context;

import com.demo.locationdemo.clipboard.WakeBinderHookHelper;


public class MyApplication extends Application {


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
//            HookHelper.attachContext();
            //  hook 剪切板
            WakeBinderHookHelper.hookClipboardService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
