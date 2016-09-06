package com.xqq.happymeeting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 基类Title文件
 */
public abstract class BaseActivity extends Activity {

    private TextView tvTitle;// 标题
    private Button btClear;// 清空按钮
    private Button btUpdate;//刷新按钮
    private LinearLayout llBase;// 子孩子布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);

        findViewById();
        setOnClickListener();

        // 设置清空按钮的默认为不可看见的
        btClear.setVisibility(View.GONE);
        btUpdate.setVisibility(View.GONE);

        View child = setContentView();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        // 添加孩子布局文件
        llBase.addView(child, params);
    }

    /**
     * 点击事件
     */
    private void setOnClickListener() {// 点击清除所有文件
        btClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                clearButtonClick();
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButtonClick();
            }
        });
    }

    /**
     * 找到界面上的控件
     */
    private void findViewById() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btClear = (Button) findViewById(R.id.btn_clear);
        llBase = (LinearLayout) findViewById(R.id.base_ll);
        btUpdate = (Button) findViewById(R.id.btn_update);
    }

    /**
     * 设置title的文本内容
     */
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * 设置ClearButton的可看见性
     */
    public void setBtClearVisibility(int visibility) {
        btClear.setVisibility(visibility);
    }
    public void setBtUpdateVisibility(int visibility) {
        btUpdate.setVisibility(visibility);
    }

    /**
     * 设置孩子布局 由孩子实现
     */
    public abstract View setContentView();

    /**
     * 清空按钮的点击事件 由孩子去实现
     */
    public abstract void clearButtonClick();

    protected abstract void updateButtonClick();
}
