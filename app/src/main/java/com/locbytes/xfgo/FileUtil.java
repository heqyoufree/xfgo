package com.locbytes.xfgo;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private static String filePath = "Android/data/com.locbytes.xfgo/files/";

    public static String saveFileToSDcard(String filename, String info) {
        filename = filePath + filename;
        String result = "false";
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(info.getBytes());
                result = "true";
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getFileDataFromSdcard(String filename) {
        filename = filePath + filename;
        String result = null;
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        File file = new File(Environment.getExternalStorageDirectory(),
                filename);
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = fileInputStream.read(buffer)) != -1) {
                    arrayOutputStream.write(buffer, 0, len);
                }
                fileInputStream.close();
                result = new String(arrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
