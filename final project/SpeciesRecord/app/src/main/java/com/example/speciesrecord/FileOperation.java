package com.example.speciesrecord;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileOperation {
    //在对应path的文件下写入content
    public static void writeFile(String path, String content) {
        File file = new File(path);
        try {
            if (!file.isFile()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String path) {
        try {
            StringBuilder content = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            content.append(bufferedReader.readLine());
            String line;
            while((line = bufferedReader.readLine()) != null) {
                content.append("\n").append(line);
            }
            return content.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //删除文件
    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();

        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();
        }

    }

    //删除文件夹
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);  //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete();  //删除空文件夹

        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();

        }

    }

    //删除文件夹里面的所有文件
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
            }
        }
    }

    //复制单个文件
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void copyFile(String oldPath, String newPath) {
        try {
            Files.copy(Paths.get(oldPath), Paths.get(newPath));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    //复制整个文件夹内容
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs();  //如果文件夹不存在  则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    //移动文件到指定目录
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);

    }

    //移动文件到指定目录
    public static void moveFolder(String oldPath, String newPath) {
        if(oldPath.equals(newPath)) {
            return;
        }
        copyFolder(oldPath, newPath);
        delFolder(oldPath);
    }

    //申请读写权限
    public static void verifyStoragePermissions(Activity activity) {
        //权限
        final int REQUEST_EXTERNAL_STORAGE = 1;
        final String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //通过文件路径获取文件类型，含.
    public static String getFileType(String path) {
        for(int i = path.length() - 1; i >= 0; i--) {
            if(String.valueOf(path.charAt(i)).equals(".")) {
                return path.substring(i);
            }
        }
        return null;
    }

    //通过文件路径获取文件名
    public static String getFileName(String path) {
        for(int i = path.length() - 1; i >= 0; i--) {
            if(String.valueOf(path.charAt(i)).equals("/")) {
                return path.substring(i + 1);
            }
        }
        return null;
    }

    //通过文件路径获取文件名(无文件类型)
    public static String getExactName(String path) {
        for(int i = path.length() - 1; i >= 0; i--) {
            if(String.valueOf(path.charAt(i)).equals(".")) {
                for(int j = i - 1; j >= 0; j--) {
                    if(String.valueOf(path.charAt(j)).equals("/")) {
                        System.out.println(path.substring(j + 1, i));
                        return path.substring(j + 1, i);
                    }
                }
                return null;
            }
        }
        return null;
    }
}
