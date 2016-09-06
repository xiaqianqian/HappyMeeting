package com.xqq.happymeeting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xqq.happymeeting.adapter.MyAdapter;
import com.xqq.happymeeting.entity.ActionMessage;
import com.xqq.happymeeting.runnable.DownLoadFileRunnable;
import com.xqq.happymeeting.runnable.GetFileNameRunnable;
import com.xqq.happymeeting.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 这个界面在点击会议之后产生 ： 里面包含所有的会议内容 每次会议内容为一个文件夹 并且在该次会议结束后 该会议内容应该删除 只保留文件夹
 */
public class MeetingMainActivity extends BaseActivity {

    private GridView gridView;
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
    private ProgressDialog dialog;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("会议系统");
        setBtClearVisibility(View.VISIBLE);
        myHandler = new MyHandler();

        findViewById();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        MyAdapter adapter = new MyAdapter(MeetingMainActivity.this,
                files, R.layout.gridview_item_folder);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new MyOnItemClickListener());
    }

    /**
     * 找到界面上的控件
     */
    private void findViewById() {
        gridView = (GridView) findViewById(R.id.gv_meeting_main);
    }

    /**
     * 初始化数据  接收会议系统所有的会议名称
     */
    private void initData() {
        dialog = new ProgressDialog(MeetingMainActivity.this);
        MainActivity.threadPool.execute(new GetFileNameRunnable("会议系统", myHandler));
    }

    @Override
    public View setContentView() {
        return View.inflate(MeetingMainActivity.this, R.layout.activity_meeting_main, null);
    }

    /**
     * 清除该目录下的所有文件
     */
    @Override
    public void clearButtonClick() {
        new AlertDialog.Builder(this).setTitle("清空文件夹")
                .setMessage("您确定要清空会议系统所有的文件？").setNegativeButton("取消", null)
                .setPositiveButton("清空", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Utils.DeleteFile("会议系统");
                        Toast.makeText(MeetingMainActivity.this, "清空完成",
                                Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void updateButtonClick() {
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ActionMessage.MEETING_MAIN://获取目
                    files = Utils.getMapFiles((ArrayList) msg.obj);
                    initView();
                    break;
                case ActionMessage.DOWN_SUCCESS://会议资料下载成功
                    dialog.dismiss();
                    Toast.makeText(MeetingMainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * GridView的点击事件
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                                int position, long l) {

            final String fileName = (String) files.get(position).get("fileName");
            if (!Utils.isExit(fileName)) {
                //1.如果文件夹不存在 则从远程服务器上获取该文件夹的内容
                new AlertDialog.Builder(MeetingMainActivity.this).setTitle("下载")
                        .setMessage("您是否需要下载会议所有文件？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                dialog.setTitle("正在下载...");
                                dialog.setMax(100);
                                dialog.setCanceledOnTouchOutside(false);
                                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                dialog.show();

                                MainActivity.threadPool.execute(new DownLoadFileRunnable("会议系统" +
                                        File.separator + fileName, dialog, myHandler));
                            }
                        }).show();
            } else if (Utils.isDelete("会议系统" + File.separator + fileName))
                //2.如果当前文件夹存在，但是文件内容不存在，则弹出对话框 提示文件已被删除
                Toast.makeText(MeetingMainActivity.this, "对不起，文件存在保密性，已被删除！",
                        Toast.LENGTH_SHORT).show();
            else {
                //3.如果当前文件夹存在且文件夹有内容 则进入会议详细内容
                Intent intent = new Intent(MeetingMainActivity.this, MeetingActivity.class);
                intent.putExtra("title", fileName);
                startActivity(intent);
            }
        }
    }
}