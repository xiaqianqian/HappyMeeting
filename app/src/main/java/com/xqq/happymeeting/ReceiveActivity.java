package com.xqq.happymeeting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.xqq.happymeeting.adapter.MyAdapter;
import com.xqq.happymeeting.entity.ActionMessage;
import com.xqq.happymeeting.listener.GridItemClickListener;
import com.xqq.happymeeting.runnable.DownLoadFileRunnable;
import com.xqq.happymeeting.runnable.SyncRunnable;
import com.xqq.happymeeting.utils.Utils;
import com.xqq.happymeeting.widget.FolderDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 点击接待文件之后的界面 ： 接待所需的视频、校歌、文档、图片的等一系列文件
 */
public class ReceiveActivity extends BaseActivity {

    private static WindowManager wm;
    private GridView gridView;
    private ImageView imgMeeting;// 会议流程的图片
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
    private ProgressDialog dialog;
    private Handler myHandler;
    private ImageView img_loading;
    private Animation rotate = null;
    private FolderDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// 执行父类的oncreate方法

        setTitle("接待系统");
        setBtUpdateVisibility(View.VISIBLE);
        findViewById();
        wm = getWindowManager();
        myHandler = new MyHandler();
        initData();
    }

    /**
     * 初始化页面所需要的数据
     */
    private void initData() {
        //1、判断是否有接待系统这个文件目录
        if (Utils.isDelete("接待系统")) {//如果不存在
            //2、如无下载
            new AlertDialog.Builder(this).setTitle("下载")
                    .setMessage("您将要下载接待系统的文件？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    })
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog = new ProgressDialog(ReceiveActivity.this);
                            //开始下载
                            MainActivity.threadPool.execute(
                                    new DownLoadFileRunnable("接待系统", dialog, myHandler));
                            //弹出下载进度条对话框
                            showProgressDialog();
                        }
                    }).show();
        } else {
            //3、如有 则进入
            showView();
        }
    }

    /**
     * 展示界面
     */
    private void showView() {
        files = Utils.getFilesByPath("接待系统");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("PIC", R.mipmap.html);
        map.put("fileName", "学校官网");
        files.add(map);
        initView();
    }

    /**
     * 显示进度条对话框
     */
    private void showProgressDialog() {
        dialog.setTitle("正在下载...");
        dialog.setMax(100);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
    }

    /**
     * 初始界面
     */
    private void initView() {
        // 将会议流程加载到界面上
        String meetingPath = Utils.getMeetingPath("接待系统");
        if (!meetingPath.equals("")) {
            imgMeeting.setImageBitmap(BitmapFactory.decodeFile(meetingPath));
        }
        MyAdapter adapter = new MyAdapter(ReceiveActivity.this, files,
                R.layout.gridview_item_folder);
        gridView.setAdapter(adapter);

        //初始化GroupView监听器
        GridItemClickListener listener = new GridItemClickListener(
                ReceiveActivity.this, files);
        listener.setIsReceived(true);
        listener.setWm(wm);
        gridView.setOnItemClickListener(listener);
        MyOnItemLongClickListener longL = new MyOnItemLongClickListener();

        gridView.setOnItemLongClickListener(longL);
    }

    /**
     * 找到界面上的控件
     */
    private void findViewById() {
        gridView = (GridView) findViewById(R.id.gv_receive);
        imgMeeting = (ImageView) findViewById(R.id.img_receive_flow);
    }

    @Override
    public View setContentView() {
        return View.inflate(ReceiveActivity.this, R.layout.activity_receive,
                null);
    }

    @Override
    public void clearButtonClick() {
    }

    /**
     * 刷新数据
     */
    @Override
    protected void updateButtonClick() {
        new AlertDialog.Builder(this).setTitle("刷新")
                .setMessage("您将要刷新接待系统的文件？")
                .setNegativeButton("取消", null)
                .setPositiveButton("刷新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        progressDialog = new FolderDialog(ReceiveActivity.this,
                                R.layout.update_dialog, R.style.Theme_dialog, wm);
                        img_loading = (ImageView)
                                progressDialog.findViewById(R.id.img_update);
                        //定义这张图片的动画一直旋转
                        rotate = AnimationUtils.
                                loadAnimation(ReceiveActivity.this, R.anim.rotate);
                        //设置匀速旋转，在xml文件中设置会出现卡顿
                        LinearInterpolator interpolator =
                                new LinearInterpolator();
                        rotate.setInterpolator(interpolator);
                        img_loading.startAnimation(rotate);

                        //刷新
                        MainActivity.threadPool.execute(
                                new SyncRunnable("接待系统", myHandler));

                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                    }
                }).show();
    }

    /**
     * 接收子线程传来的信息
     */
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ActionMessage.DOWN_SUCCESS://下载成功
                    showView();
                    dialog.dismiss();
                    Toast.makeText(ReceiveActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                case ActionMessage.UPDATE_SUCCESS://刷新成功
                    showView();
                    img_loading.clearAnimation();
                    progressDialog.dismiss();
                    Toast.makeText(ReceiveActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView,
                                       View view, final int position, long l) {

            new AlertDialog.Builder(ReceiveActivity.this).setTitle("删除")
                    .setMessage("您确定要删除该文件吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File file = (File) files.get(position).get("file");
                            file.delete();
                            showView();
                            Toast.makeText(ReceiveActivity.this, "文件已删除",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            return true;
        }
    }
}