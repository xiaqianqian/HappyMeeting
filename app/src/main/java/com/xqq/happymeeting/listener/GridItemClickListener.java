package com.xqq.happymeeting.listener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.xqq.happymeeting.R;
import com.xqq.happymeeting.utils.GridViewInit;
import com.xqq.happymeeting.utils.OpenFile;
import com.xqq.happymeeting.widget.FolderDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xqq on 2015/11/21.
 */
public class GridItemClickListener implements AdapterView.OnItemClickListener {
    private Context context;
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
    private boolean isReceived = false;
    private WindowManager wm;

    public GridItemClickListener(Context context,
                                 List<Map<String, Object>> files) {
        super();
        this.context = context;
        this.files = files;
    }

    public void setWm(WindowManager wm) {
        this.wm = wm;
    }

    public void setIsReceived(boolean isReceived) {
        this.isReceived = isReceived;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long arg3) {
        if ((position == (files.size() - 1)) && isReceived) {
            // 访问学校官网
            Uri uri = Uri.parse("http://www.csust.edu.cn/pub/cslgdx/index.htm");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } else {
            // 获取当前文件夹
            File file = (File) files.get(position).get("file");
            if (file.isDirectory()) {// 如果当前为文件夹 则弹出对话框显示该文件夹下的文件
                FolderDialog dialog = new FolderDialog(context, 0, 0,
                        R.layout.activity_dialog, R.style.Theme_dialog, wm);
                // 设置对话框的标题
                GridView gridView = (GridView) dialog
                        .findViewById(R.id.gv_dialog);
                TextView tvTitle = (TextView) dialog
                        .findViewById(R.id.tv_fileName_dialog);
                tvTitle.setText(file.getName());

                // 初始化gridView
                GridViewInit init = new GridViewInit("接待系统" + File.separator +
                        file.getName(), context, gridView);
                init.initView(R.layout.gridview_item_dialog);
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);
            } else {// 如果为文件怎调用安装程序打开该文件
                // 调用手机中的软件打开文件
                OpenFile openFile = new OpenFile(context);
                openFile.open(file);
            }
        }
    }
}
