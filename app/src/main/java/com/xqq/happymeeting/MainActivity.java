package com.xqq.happymeeting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主界面 ： 会议、接待两项选择
 */
public class MainActivity extends BaseActivity {
    private boolean mBackKeyPressed = false;// 是否按了返回键
    public static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置title
        setTitle("长沙理工大学会议通");
    }

    public void click(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.img_main_meeting:
                // 进入会议界面
                intent = new Intent(MainActivity.this, MeetingMainActivity.class);
                startActivity(intent);
                break;

            case R.id.img_main_receive:
                // 进入接待界面
                intent = new Intent(MainActivity.this, ReceiveActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public View setContentView() {
        return View.inflate(MainActivity.this, R.layout.activity_main, null);
    }

    @Override
    public void clearButtonClick() {
    }

    @Override
    protected void updateButtonClick() {
    }

    @Override
    public void onBackPressed() {
        if (!mBackKeyPressed) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {// 延时两秒，如果超出则擦错第一次按键记录
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else { // 退出程序
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}