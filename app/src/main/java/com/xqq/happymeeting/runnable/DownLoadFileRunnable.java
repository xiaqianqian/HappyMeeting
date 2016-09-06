package com.xqq.happymeeting.runnable;

import android.app.ProgressDialog;
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
 * Created by xqq on 2016/2/25.
 * 从远程服务器上下载文件
 */
public class DownLoadFileRunnable implements Runnable {
    private static final String smbDir = "smb://final:Dzbapp195@10.255.195.10/开心会议通/";
    private String url;
    private String localDir = Utils.getBootPath();
    private double sumDown = 0;//已经下载文件的大小
    private double sum = 0;//总文件的大小
    private ProgressDialog dialog;
    private Handler myHandler;

    public DownLoadFileRunnable(String url, ProgressDialog dialog, Handler myHandler) {
        this.url = smbDir + url + File.separator;
        this.localDir = localDir + File.separator + url;
        this.dialog = dialog;
        this.myHandler = myHandler;
    }

    @Override
    public void run() {
        try {
            SmbFile file = new SmbFile(url);
            SmbFile[] smbFiles = file.listFiles();
            getSumLength(smbFiles);//获取总的文件大小
            downFolder(smbFiles, localDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Message msg = new Message();
            msg.what = ActionMessage.DOWN_SUCCESS;
            myHandler.sendMessage(msg);
        }
    }

    /**
     * 获取该文件目录下所有文件的大小
     */
    private void getSumLength(SmbFile[] smbFiles) {
        try {
            for (SmbFile smbFile : smbFiles) {
                if (smbFile.isFile()) {
                    sum += smbFile.length();
                } else {
                    getSumLength(smbFile.listFiles());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件夹的下载
     * @param smbFiles
     */
    private void downFolder(SmbFile[] smbFiles, String localPath) {
        try {
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (SmbFile smbFile : smbFiles) {
                if (smbFile.isDirectory()) {
                    downFolder(smbFile.listFiles(), localPath +
                            File.separator + smbFile.getName().replace("/", ""));
                } else {
                    downFile(smbFile, localPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件从远程服务器上
     * @param smbFile
     */
    private void downFile(SmbFile smbFile, String localPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            File localFile = new File(localPath + File.separator + smbFile.getName());
            in = new BufferedInputStream(new SmbFileInputStream(smbFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte buffer[] = new byte[1024];
            int len = -1;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
                sumDown += len;
                dialog.setProgress((int) ((sumDown / sum) * 100));
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
