package com.example.speciesrecord;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leon.lfilepickerlibrary.LFilePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //文件夹选择
    private final int REQUEST_CODE_FROM_ACTIVITY = 1000;
    private int level;
    private String appPath;
    private String tempPath;
    private String recordsPath;
    private String recordNow;
    private ArrayList<String> recordNames;
    private ArrayList<String> tempNames;
    private ArrayList<String> tempNotes;
    private ArrayList<String> imageNotes;
    private int imageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        FileOperation.verifyStoragePermissions(this);//申请读写权限
        addDefaultFile();//配置默认记录
        setPage();//绑定初始化toolbar和FAB
        dataInit();//初始化数据
        loadList();//加载每个层次序列
    }

    //绑定初始化toolbar和FAB
    public void setPage() {
        Toolbar toolbar = findViewById(R.id.record_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton add_fab = findViewById(R.id.add_fab);
        add_fab.setOnClickListener(view -> {
            if (recordNow.equals("default by jeffrey")) {
                new AlertDialog.Builder(this)
                        .setMessage("不得更改0.0")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create().show();
            } else {
                //跳转到增加物种页面
                Intent intent = new Intent(MainActivity.this, NewSpecies.class);
                startActivity(intent);
            }
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
        switch (item.getItemId()) {
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
            case android.R.id.home:
                toHigherLevel();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    //跳转到图片页面
    public void showImages() {
    }

    //查看所有记录，并可跳转
    public void allRecord() {
        String[] records = TypeConversion.arrayListToStrings(recordNames);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("所有记录")
                .setItems(records, (dialog, which) -> {
                    //跳转到对应记录
                    recordNow = records[which];
                    tempPath = recordsPath + "/" + recordNow;
                    FileOperation.writeFile(recordsPath + "/" + "currentRecord.txt", recordNow);
                    loadList();
                })
                .create().show();
    }

    //新的记录
    public void newRecord() {
        View view = View.inflate(MainActivity.this, R.layout.new_record_toast, null);
        final Button btn = view.findViewById(R.id.btn_confirm_new);
        EditText editText = view.findViewById(R.id.new_name);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("新的记录")
                .setView(view)
                .create();
        alertDialog.show();
        btn.setOnClickListener(v -> {
            //初始化相应文件，跳转到新页面
            String newName = editText.getText().toString();
            recordNow = newName;
            tempPath = recordsPath + "/" + newName;
            File newRecord = new File(tempPath);
            if (newRecord.isDirectory()) {
                new AlertDialog.Builder(this)
                        .setMessage("该名称记录已存在")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create().show();
            } else {
                newRecord.mkdir();
                FileOperation.writeFile(recordsPath + "/" + "currentRecord.txt", recordNow);
                dataInit();
                loadList();
                alertDialog.cancel();
            }
        });
    }

    //导入记录，查看文件
    public void importRecord() {
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUEST_CODE_FROM_ACTIVITY)
                .withTitle("导入记录")
                .withChooseMode(true)
                .withIsGreater(true)
                .withFileSize(-1)
                .start();
    }

    //删除
    public void deleteThis() {
        if (recordNow.equals("default by jeffrey")) {
            new AlertDialog.Builder(this)
                    .setMessage("这个是删不掉的(别硬删，app直接就崩了0~0)")
                    .setPositiveButton("确定", (dialog, which) -> {
                    })
                    .create().show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("你确定要删除此分类及其下所有的记录吗")
                .setPositiveButton("确定", ((dialog, which) -> {
                    FileOperation.delFolder(tempPath);
                    for (int i = tempPath.length() - 1; i >= 0; i--) {
                        if (String.valueOf(tempPath.charAt(i)).equals("/")) {
                            if (tempPath.substring(i + 1).equals(recordNow)) {
                                recordNow = "default by jeffrey";
                                tempPath = recordsPath + "/" + "default by jeffrey";
                                dataInit();
                                break;
                            }
                            tempPath = tempPath.substring(0, i);
                            break;
                        }
                    }
                    loadList();
                }))
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .create().show();
    }

    //返回上一层
    public void toHigherLevel() {
        for (int i = tempPath.length() - 1; i >= 0; i--) {
            if (String.valueOf(tempPath.charAt(i)).equals("/")) {
                if (tempPath.substring(i + 1).equals(recordNow)) {
                    allRecord();
                    break;
                }
                tempPath = tempPath.substring(0, i);
                break;
            }
        }
        loadList();
    }

    //配置默认记录，及配置
    public void addDefaultFile() {
        appPath = getExternalCacheDir().getAbsolutePath();
        recordsPath = appPath + "/records";
        File recordsDir = new File(recordsPath);
        if (recordsDir.exists()) {
            recordNow = FileOperation.readFile(recordsPath + "/currentRecord.txt");
            tempPath = recordsPath + "/" + recordNow;
            return;
        }
        recordNow = "default by jeffrey";
        tempPath = recordsPath + "/" + recordNow;
        File defaultRecord = new File(tempPath);
        recordsDir.mkdir();
        defaultRecord.mkdir();
        FileOperation.writeFile(recordsPath + "/currentRecord.txt", recordNow);

        //未完成
    }

    //数据初始化
    public void dataInit() {
        level = -1;
        recordNames = new ArrayList<>();
        File[] recordFiles = new File(recordsPath).listFiles();
        assert recordFiles != null;
        for (File file : recordFiles) {
            if (file.isDirectory()) {
                recordNames.add(file.getName());
            }
        }
    }

    //加载每个层次名
    public void loadList() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(FileOperation.getFileName(tempPath));
        File[] levelFiles = new File(tempPath).listFiles();
        if (levelFiles == null) {
            return;
        }
        imageNum = 0;
        imageNotes = new ArrayList<>();
        tempNames = new ArrayList<>();
        tempNotes = new ArrayList<>();
        for (File file : levelFiles) {
            if (file.isDirectory()) {
                tempNames.add(file.getName());
                File[] nextFiles = file.listFiles();
                if (nextFiles == null) {
                    return;
                }
                if (nextFiles.length == 0) {
                    tempNotes.add(null);
                    continue;
                }
                String temp = null;
                for (File nextFile : nextFiles) {
                    if (nextFile.getName().equals("note.txt")) {
                        temp = FileOperation.readFile(nextFile.getAbsolutePath());
                        break;
                    }
                }
                tempNotes.add(temp);
            } else {
                switch (Objects.requireNonNull(FileOperation.getFileType(file.toString()))) {
                    case (".txt"):
                        if (!FileOperation.getExactName(file.toString()).equals(FileOperation.getFileName(tempPath))) {
                            imageNotes.add(FileOperation.readFile(file.toString()));
                        }
                        break;
                    case (".jpg"):
                    case (".pdf"):
                    case (".png"):
                    case (".jpeg"):
                        imageNum++;
                }
            }
        }
        if (imageNum > 0) {
            return;
        }
        ArrayList<Level> levels = Level.getLevels(tempNames, tempNotes);
        LevelAdapter levelAdapter = new LevelAdapter(MainActivity.this, R.layout.level_list, levels);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(levelAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            tempPath = tempPath + "/" + tempNames.get(position);
            loadList();
        });
    }
}