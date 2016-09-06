package com.xqq.happymeeting;

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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xqq.happymeeting.listener.GridItemClickListener;
import com.xqq.happymeeting.runnable.SyncRunnable;
import com.xqq.happymeeting.utils.Utils;
import com.xqq.happymeeting.widget.FolderDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 点击会议文件之后的界面 ： 会议所需的所有文件以及照片
 */
public class MeetingActivity extends BaseActivity {

    private GridView gridView;
    private ImageView imgMeeting;// 会议流程的图片
    private String title;
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
    private ImageView img_loading;//正在刷新的图片
    private Animation rotate;//旋转动画
    private Handler myHandler;//j接收消息
    private WindowManager wm;
    private FolderDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置title
        title = getIntent().getStringExtra("title");
        setTitle(title);
        setBtClearVisibility(View.VISIBLE);
        setBtUpdateVisibility(View.VISIBLE);
        myHandler = new MyHandler();

        wm = getWindowManager();
        showView();
    }

    /**
     * 展示界面
     */
    private void showView() {
        //初始化界面
        initView();

        //初始化数据
        initData();

        //设置适配器
        SimpleAdapter adapter = new SimpleAdapter(MeetingActivity.this, files,
                R.layout.gridview_item_folder, new String[]{"PIC", "fileName"},
                new int[]{R.id.img_griditem_fileFlag, R.id.tv_griditem_fileName});
        gridView.setAdapter(adapter);
        GridItemClickListener listener = new
                GridItemClickListener(MeetingActivity.this, files);
        listener.setWm(wm);
        //设置点击事件
        gridView.setOnItemClickListener(listener);
        //设置长按事件
        gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        files = Utils.getFilesByPath("会议系统" + File.separator + title);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        findViewById();
        // 将会议流程加载到界面上
        String meetingPath = Utils.getMeetingPath("会议系统" + File.separator + title);
        if (!meetingPath.equals("")) {
            imgMeeting.setImageBitmap(BitmapFactory.decodeFile(meetingPath));
        }
    }

    /**
     * 找到界面上的控件
     */
    private void findViewById() {
        gridView = (GridView) findViewById(R.id.gv_meeting);
        imgMeeting = (ImageView) findViewById(R.id.img_meeting_flow);
    }

    @Override
    public View setContentView() {
        return View.inflate(MeetingActivity.this, R.layout.activity_meeting,
                null);
    }

    @Override
    public void clearButtonClick() {
        //清空此次会议所有文件
        new AlertDialog.Builder(this).setTitle("删除会议内容")
                .setMessage("您确定要此次会议所有的文件？").setNegativeButton("取消", null)
                .setPositiveButton("清空", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        Utils.DeleteFileNoBoot("会议系统" + File.separator + title);

                        Toast.makeText(MeetingActivity.this, "删除完成",
                                Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).show();
    }

    @Override
    protected void updateButtonClick() {
        new AlertDialog.Builder(this).setTitle("刷新")
                .setMessage("您将要刷新接待系统的文件？")
                .setNegativeButton("取消", null)
                .setPositiveButton("刷新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        progressDialog = new FolderDialog(MeetingActivity.this,
                                R.layout.update_dialog, R.style.Theme_dialog, wm);
                        img_loading = (ImageView) progressDialog.findViewById(R.id.img_update);
                        //定义这张图片的动画一直旋转
                        rotate = AnimationUtils.loadAnimation(MeetingActivity.this, R.anim.rotate);
                        LinearInterpolator interpolator = new LinearInterpolator();  //设置匀速旋转，在xml文件中设置会出现卡顿
                        rotate.setInterpolator(interpolator);
                        img_loading.startAnimation(rotate);
                        //刷新
                        MainActivity.threadPool.execute(
                                new SyncRunnable("会议系统" + File.separator + title,
                                        myHandler));

                        progressDialog.show();
                        progressDialog.setCanceledOnTouchOutside(false);
                    }
                }).show();
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView,
                                       View view, final int position, long l) {

            new AlertDialog.Builder(MeetingActivity.this).setTitle("删除")
                    .setMessage("您确定要删除该文件吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File file = (File) files.get(position).get("file");
                            file.delete();
                            showView();
                            Toast.makeText(MeetingActivity.this, "文件已删除",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            return true;
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            showView();
            img_loading.clearAnimation();
            progressDialog.dismiss();
            Toast.makeText(MeetingActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
        }
    }
}