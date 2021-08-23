package com.txznet.adapter.base.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess", "unused"})
public class FileUtil {

    private static final String TAG = "FileUtil";

    /*
     * 根据目录删除文件
     * @param path 路径
     * @return 结果
     */
    public static boolean deleteAllFiles(String path) {
        if (TextUtils.isEmpty(path)) {
            Log.d(TAG, "deleteAllFiles : file path is null");
            return false;
        }
        return deleteAllFiles(new File(path));
    }

    /*
     * 删除文件
     * @param deleteFile 文件
     * @return 结果
     */
    public static boolean deleteAllFiles(File deleteFile) {
        try {
            if (deleteFile == null) {
                Log.d(TAG, "deleteAllFiles : deleteFile is null");
                return false;
            }
            // 此File不存在
            if (!deleteFile.exists()) {
                Log.d(TAG, "deleteAllFiles : deleteFile is not exists");
                return false;
            }
            // 是个文件
            if (!deleteFile.isDirectory()) {
                return deleteFile.delete();
            }
            // 是个目录
            else {
                // 遍历删除
                for (File childFile : deleteFile.listFiles()) {
                    // 是文件就删
                    if (childFile.isFile()) {
                        childFile.delete();
                    }
                    // 目录就递归
                    else if (childFile.isDirectory()) {
                        deleteAllFiles(childFile);
                    }
                }
            }
            return deleteFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 复制文件
     * @param sourcePath 源
     * @param aimPath 目标
     * @return 结果
     */
    public static boolean copyFile(String sourcePath, String aimPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(sourcePath);
            File aimFile = createNewFile(aimPath);
            out = new FileOutputStream(aimFile);
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            in = null;
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /*
     * 创建文件，若文件夹不存在则自动创建文件夹，若文件存在则删除旧文件
     *
     * @param dir :待创建文件路径
     */
    public static File createNewFile(String dir) {
        File file = new File(dir);
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /*
     * 把assets目录的指令文件复制到指定目录，连同assets的原本目录
     * @param context c
     * @param fileName 要复制的文件
     * @param saveDir 目标路径
     * @return 结果
     */
    public static boolean saveAssetsFileToPath(Context context, String fileName, String saveDir) {
        if (context == null || fileName == null || saveDir == null) {
            Log.d(TAG, "saveAssetsFileToPath : are you kidding me ?");
            return false;
        }
        if (saveDir.endsWith(fileName)) {
            saveDir.replace(fileName, "");
        }
        try {
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            InputStream is = context.getAssets().open(fileName);
            File newFile = createNewFile(saveDir + "/" + fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[2048];
            int byteCount;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            fos.close();
            is.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
