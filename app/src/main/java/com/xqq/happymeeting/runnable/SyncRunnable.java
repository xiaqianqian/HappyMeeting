package com.xqq.happymeeting.runnable;

import android.os.Handler;
import android.os.Message;

import com.xqq.happymeeting.entity.ActionMessage;
import com.xqq.happymeeting.utils.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * Created by xqq on 2016/2/27.
 * 刷新接待系统
 */
public class SyncRunnable implements Runnable {
    private String url = "smb://final:Dzbapp195@10.255.195.10/开心会议通/";

    private String localPath = Utils.getBootPath();
    private Handler myHandler;

    public SyncRunnable(String path, Handler myHandler) {
        this.localPath = localPath  + path;
        this.url = url + path + File.separator;
        this.myHandler = myHandler;
    }

    @Override
    public void run() {
        try {
            SmbFile file = new SmbFile(url);
            SmbFile[] smbFiles = file.listFiles();
            checkFolders(smbFiles, localPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Message msg = new Message();
            msg.what = ActionMessage.UPDATE_SUCCESS;
            myHandler.sendMessage(msg);
        }
    }

    private void checkFolders(SmbFile[] smbFiles, String path) throws Exception {
        for (SmbFile smbFile : smbFiles) {
            if (smbFile.isFile())
                checkFiles(smbFile, path);
            else
                checkFolders(smbFile.listFiles(), path + File.separator + smbFile.getName());
        }
    }

    private void checkFiles(SmbFile smbFile, String path) throws Exception {
        File localFile = new File(path + File.separator +smbFile.getName().replace("/", ""));
        //判断文件是否改变
        boolean isChange = localFile.exists() && (localFile.length() != smbFile.length());
        //判断文件是否不存在
        boolean notExit = !localFile.exists();
        if (isChange || notExit) {
            if (!notExit) {//如果文件存在
                localFile.delete();//删除原来的   重新下载新的
                localFile = new File(path + File.separator + smbFile.getName().replace("/", ""));
            }
            //下载新的
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new BufferedInputStream(new SmbFileInputStream(smbFile));
                out = new BufferedOutputStream(new FileOutputStream(localFile));
                byte buffer[] = new byte[1024];
                int len = -1;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
