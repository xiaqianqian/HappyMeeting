package com.xqq.happymeeting.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xqq.happymeeting.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xqq on 2016/3/1.
 */
public class MyAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private int layout;
    private List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();

    public MyAdapter(Context context, List<Map<String, Object>> files, int layout) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.files = files;
        this.layout = layout;
    }

    public void setFiles(List<Map<String, Object>> files) {
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder mHolder = null;

        if (convertView == null) {
            mHolder = new ViewHolder();
            // 加载布局
            convertView = mInflater.inflate(layout, null);

            mHolder.tvFileName = (TextView) convertView
                    .findViewById(R.id.tv_griditem_fileName);

            mHolder.imgFileFlag = (ImageView) convertView
                    .findViewById(R.id.img_griditem_fileFlag);
            // 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        Map<String, Object> map = files.get(position);
        mHolder.tvFileName.setText((String) map.get("fileName"));
        mHolder.imgFileFlag.setImageResource((int) map.get("PIC"));
        return convertView;
    }

    private static class ViewHolder {
        public TextView tvFileName;
        public ImageView imgFileFlag;
    }
}
