package com.demo.locationdemo.clipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ClipboardMg {

    private static final String TAG = "hook/ClipboardMg";

    public static void clipboard(Context context) {
        final ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        Log.i(TAG,"cm.hasPrimaryClip() = " + cm.hasPrimaryClip());
        //  ClipData 里保存了一个ArryList 的 Item 序列， 可以用 getItemCount() 来获取个数
        ClipData.Item item = data.getItemAt(0);
        String text = item.getText().toString();// 注意 item.getText 可能为空
        Log.i(TAG,"text = " + text);
    }
}
