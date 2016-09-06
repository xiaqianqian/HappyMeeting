package com.xqq.happymeeting.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.xqq.happymeeting.ReceiveActivity;

/**
 * Created by xqq on 2015/11/22.
 */
public class FolderDialog extends Dialog {

    private static int default_width = ViewGroup.LayoutParams.MATCH_PARENT; // 默认宽度
    private static int default_height = ViewGroup.LayoutParams.WRAP_CONTENT;// 默认高度

    public FolderDialog(Context context, int width, int height, int layout,
                        int style, WindowManager wm) {
        super(context, style);
        // set content
        setContentView(layout);
        // set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        Display display = wm.getDefaultDisplay();
        // 设置对话框的宽高
        params.width = (int) (display.getWidth() * 0.7);
        params.height = (int) (display.getHeight() * 0.3);
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
    }

    public FolderDialog(Context context, int layout, int style, WindowManager wm) {
        super(context, style);
        // set content
        setContentView(layout);
        // set window params
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        Display display = wm.getDefaultDisplay();
        // 设置对话框的宽高
        params.width = (int) (display.getWidth() * 0.1);
        params.height = (int) (display.getHeight() * 0.18);
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
    }
}
