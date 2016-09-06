package com.xqq.happymeeting.utils;

import android.os.Environment;

import com.xqq.happymeeting.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xqq on 2015/11/21.
 */
public class Utils {
    /**
     * 递归删除文件和文件夹
     */
    public static void DeleteFile(String path) {
        File file = new File(getBootPath() + path);
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f.getPath());
                }
                file.delete();
            }
        }
    }

    /**
     * 递归删除文件和文件夹 不要删除的根目录
     */
    public static void DeleteFileNoBoot(String path) {
        File file = new File(getBootPath() + path);
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                for (File f : childFile) {
                    deleteFile(f.getPath());
                }
            }
        }
    }

    private static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f.getPath());
                }
                file.delete();
            }
        }
    }

    /**
     * 判断文件是存储在自身内存还是sd中
     *
     * @return
     */
    public static String getBootPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getPath() + "/happymeeting/";
        } else {
            return Environment.getDataDirectory().getPath() + "/happymeeting/";
        }
    }

    /**
     * 返回会议流程图片的路径
     */
    public static String getMeetingPath(String path) {
        File f = new File(getBootPath() + path);
        if (f.exists()) {
            File[] fileArray = f.listFiles();
            for (File file : fileArray) {
                if (file.getName().contains("会议流程")) {// 文件夹
                    return file.getPath();
                }
            }
        }
        return "";
    }

    /**
     * 获取所有文件map集合
     */
    public static List<Map<String, Object>> getMapFiles(List<String> files) {
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        for (String fileName : files) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (!fileName.contains(".")) {// 文件夹
                map.put("PIC", R.mipmap.ex_folder);
            } else if (fileName.contains(".doc")) {
                map.put("PIC", R.mipmap.doc);
            } else if (fileName.contains(".xls")) {
                map.put("PIC", R.mipmap.xls);
            } else if (fileName.contains(".pdf")) {
                map.put("PIC", R.mipmap.pdf);
            } else if (fileName.contains(".ppt")) {
                map.put("PIC", R.mipmap.ppt);
            } else if (fileName.contains(".txt")) {
                map.put("PIC", R.mipmap.txt);
            } else if (fileName.contains(".png")
                    || fileName.contains(".jpg")) {
                map.put("PIC", R.mipmap.picture);
            } else if (fileName.contains(".mp3")) {
                map.put("PIC", R.mipmap.music);
            } else {
                map.put("PIC", R.mipmap.video);
            }
            map.put("fileName", fileName);
            contents.add(map);
        }
        return contents;
    }

    /**
     * 判断该文件夹是否存在于手机中
     */
    public static boolean isDelete(String path) {
        File file = new File(getBootPath() + path);
        if (file.exists() && file.listFiles().length > 0)
            return false;
        else
            return true;
    }

    /**
     * 判断该文件夹是否存在于手机中
     */
    public static boolean isExit(String path) {
        File file = new File(getBootPath() + "会议系统/" + path);
        if (file.exists())
            return true;
        else
            return false;
    }

    /**
     * 获取手机中某路径下的所有文件
     */
    public static List<Map<String, Object>> getFilesByPath(String path) {
        List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
        File dirFile = new File(getBootPath() + path);
        if (dirFile.exists())
            for (File file : dirFile.listFiles()) {
                String fileName = file.getName();
                Map<String, Object> map = new HashMap<String, Object>();
                if (!fileName.contains("会议流程")) {
                    if (!fileName.contains(".")) {// 文件夹
                        map.put("PIC", R.mipmap.ex_folder);
                    } else if (fileName.contains(".doc")) {
                        map.put("PIC", R.mipmap.doc);
                    } else if (fileName.contains(".xls")) {
                        map.put("PIC", R.mipmap.xls);
                    } else if (fileName.contains(".pdf")) {
                        map.put("PIC", R.mipmap.pdf);
                    } else if (fileName.contains(".ppt")) {
                        map.put("PIC", R.mipmap.ppt);
                    } else if (fileName.contains(".txt")) {
                        map.put("PIC", R.mipmap.txt);
                    } else if (fileName.contains(".png")
                            || fileName.contains(".jpg")) {
                        map.put("PIC", R.mipmap.picture);
                    } else if (fileName.contains(".mp3")) {
                        map.put("PIC", R.mipmap.music);
                    } else {
                        map.put("PIC", R.mipmap.video);
                    }
                    map.put("fileName", fileName);
                    map.put("file", file);
                    contents.add(map);
                }
            }
        return contents;
    }
}
