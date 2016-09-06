package com.xqq.happymeeting.runnable;

import android.os.Handler;
import android.os.Message;

import com.xqq.happymeeting.entity.ActionMessage;
import java.io.File;
import java.util.ArrayList;
import jcifs.smb.SmbFile;

/**
 * Created by xqq on 2016/2/25.
 * 用于获取远程服务器上 选定目录下的所有文件名
 */
public class GetFileNameRunnable implements Runnable {
    private static final String dir = "smb://final:Dzbapp195@10.255.195.10/开心会议通/";
    private String url = "";//请求服务器的路径
    private Handler myHandler;

    public GetFileNameRunnable(String path, Handler myHandler) {
        this.url = this.dir + path + File.separator;
        this.myHandler = myHandler;
    }

    @Override
    public void run() {
        ArrayList<String> files = new ArrayList<String>();
        try {
            SmbFile file = new SmbFile(url);
            SmbFile[] smbFiles = file.listFiles();
            for (SmbFile smbFile : smbFiles) {
                files.add(smbFile.getName().replace("/", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = new Message();
        msg.what = ActionMessage.MEETING_MAIN;
        msg.obj = files;
        myHandler.sendMessage(msg);
    }
}
