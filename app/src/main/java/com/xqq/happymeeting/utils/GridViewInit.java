package com.xqq.happymeeting.utils;

/**
 * Created by xqq on 2015/11/22.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.xqq.happymeeting.R;
import com.xqq.happymeeting.adapter.MyAdapter;
import com.xqq.happymeeting.listener.GridItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GridView的初始化
 */
public class GridViewInit {

    private Context context;
    private GridView gridView;
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
    private String path;

    public GridViewInit(String path, Context context, GridView gridView) {
        this.context = context;
        this.gridView = gridView;
        this.path = path;
    }

    /**
     * 界面初始化
     */
    public void initView(int layout) {
        // 加载布局
        if (files != null) {
            showView();
        }
    }

    private void showView() {
        // 获得当前所在文件下的文件
        files = Utils.getFilesByPath(path);
        MyAdapter adapter = new MyAdapter(context, files, R.layout.gridview_item_dialog);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new GridItemClickListener(context,
                files));
        gridView.setOnItemLongClickListener(new MyOnItemLongClickListener());
    }

    private class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView,
                                       View view, final int position, long l) {

            new AlertDialog.Builder(context).setTitle("删除")
                    .setMessage("您确定要删除该文件吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            File file = (File) files.get(position).get("file");
                            file.delete();
                            showView();
                            Toast.makeText(context, "文件已删除",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).show();
            return true;
        }
    }
}