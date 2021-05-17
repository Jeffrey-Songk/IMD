package com.example.speciesrecord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leon.lfilepickerlibrary.LFilePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    //文件夹选择
    private final int REQUEST_CODE_FROM_ACTIVITY = 1000;
    private String tempPath;
    private String recordsPath;
    private String recordNow;
    private ArrayList<String> recordNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        setPage();//绑定初始化toolbar和FAB
        verifyStoragePermissions(this);//申请读写权限
        addDefaultFile();//配置默认记录
        dataInit();
    }
    //绑定初始化toolbar和FAB
    public void setPage() {
        Toolbar toolbar = findViewById(R.id.record_toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton add_fab = findViewById(R.id.add_fab);
        add_fab.setOnClickListener(view -> {
            //跳转到增加物种页面
            Intent intent = new Intent(MainActivity.this, NewSpecies.class);
            startActivity(intent);
        });
    }
    //导入记录时，获得对应文件路径，未完
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_FROM_ACTIVITY) {
                String path = data.getStringExtra("path");
                Toast.makeText(getApplicationContext(), "选中的路径为" + path, Toast.LENGTH_SHORT).show();
            }
        }
    }
    //右上菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_images:
                showImages();
                return true;
            case R.id.all_record:
                allRecord();
                return true;
            case R.id.new_record:
                newRecord();
                return true;
            case R.id.delete:
                deleteThis();
                return true;
            case R.id.import_record:
                importRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //跳转到图片页面
    public void showImages() {}
    //查看所有记录
    public void allRecord() {
        String[] records = TypeConversion.stringsToArrayList(recordNames);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("所有记录")
                .setItems(records, (dialog, which) -> {
                    //跳转到对应记录
                })
                .create().show();
    }

    //新的记录
    public void newRecord() {
        View view = View.inflate(MainActivity.this, R.layout.new_record_toast, null);
        final Button btn = view.findViewById(R.id.btn_confirm_new);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("新的记录")
                .setView(view)
                .create().show();
        btn.setOnClickListener(v -> {
            //初始化相应文件，跳转到新页面
        });
    }

    //导入记录，查看文件
    public void importRecord() {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUEST_CODE_FROM_ACTIVITY)
                .withTitle("导入记录")
                .withChooseMode(false)
                .withIsGreater(true)
                .withFileSize(-1)
                .start();
    }

    public void deleteThis() {

    }

    //申请读写权限
    public static void verifyStoragePermissions(Activity activity) {
        //权限
        final int REQUEST_EXTERNAL_STORAGE = 1;
        final String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE" };
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //配置默认记录，及配置
    public void addDefaultFile() {
        tempPath = getExternalCacheDir().getAbsolutePath();
        recordsPath = tempPath + "/records";
        recordNames = new ArrayList<>();
        File recordsDir = new File(recordsPath);
        if (recordsDir.exists()) {
            recordNow = FileOperation.readFile(recordsPath + "/currentRecord.txt");
            return;
        }
        recordNow = "default by jeffrey";
        File defaultRecord = new File(recordsPath + "/" + recordNow);
        recordsDir.mkdir();
        defaultRecord.mkdir();
        FileOperation.writeFile(recordsPath + "/currentRecord.txt", recordNow);

        //未完成
    }

    public void dataInit() {
        File[] recordFiles = new File(recordsPath).listFiles();
        assert recordFiles != null;
        for(File file: recordFiles) {
            if(file.isDirectory()) {
                recordNames.add(file.getName());
            }
        }
    }
}