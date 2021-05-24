package com.example.speciesrecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leon.lfilepickerlibrary.LFilePicker;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_FROM_ACTIVITY = 1000;
    protected static String levelNow;
    protected static String recordNow;
    private ArrayList<String> recordNames;
    protected static SharedPreferences sharedPreferences;

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
    //ok
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

    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            toHigherLevel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //导入记录时，获得对应文件路径，需删除
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

    //跳转到图片页面， 未写
    public void showImages() {
    }

    //查看所有记录，并可跳转
    public void allRecord() {
        String[] records = TypeConversion.arrayListToStrings(this.recordNames);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("所有记录")
                .setItems(records, (dialog, which) -> {
                    //跳转到对应记录
                    recordNow = records[which];
                    levelNow = recordNow;
                    sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("currentRecord", recordNow);
                    editor.apply();
                    loadList();
                })
                .create().show();
    }

    //新的记录
    //ok
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
            sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
            int i = 0;
            boolean isExist = false;
            String temp = sharedPreferences.getString("record_name_" + i, null);
            while(temp != null) {
                if(temp.equals(newName)) {
                    new AlertDialog.Builder(this)
                        .setMessage("该名称记录已存在")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create().show();
                    isExist = true;
                    break;
                }
                i++;
                temp = sharedPreferences.getString("record_name_" + i, null);
            }
            if(!isExist) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("record_name_" + i, newName);
                editor.putString("currentRecord", newName);
                editor.apply();
                recordNow = newName;
                levelNow = newName;
                alertDialog.cancel();
                dataInit();
                loadList();
            }
        });
    }

    //导入记录，查看文件
    //no need
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
                    sharedPreferences = getSharedPreferences(recordNow, Context.MODE_PRIVATE);
                    if(recordNow.equals(levelNow)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
                        int temp = SharedPreferencesOperation.find("record_name_", levelNow);
                        SharedPreferencesOperation.delete("record_name_", temp);
                        recordNow = "default by jeffrey";
                        levelNow = recordNow;
                    } else {
                        //递归删除，未完成
                        String temp = sharedPreferences.getString(levelNow + "_previous", null);
                        SharedPreferencesOperation.deleteLevel(levelNow);
                        levelNow = temp;
                    }
                    dataInit();
                    loadList();
                }))
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .create().show();
    }

    //返回上一层
    public void toHigherLevel() {
        sharedPreferences = getSharedPreferences(recordNow, Context.MODE_PRIVATE);
        if(levelNow.equals(recordNow)) {
            allRecord();
            return;
        }
        levelNow = sharedPreferences.getString(levelNow + "_previous", levelNow);
        loadList();
    }

    //配置默认记录，及配置
    //ok
    public void addDefaultFile() {
        sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString("currentRecord", null);
        if(temp == null) {
            recordNow = "default by jeffrey";
            levelNow = recordNow;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentRecord", recordNow);
            editor.putString("record_name_0", recordNow);
            editor.apply();
            //未完成
        } else {
            recordNow = temp;
            levelNow = temp;
        }
//        未完成
    }

    //数据初始化 recordNames
    //ok
    public void dataInit() {
        this.recordNames = new ArrayList<>();
        sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
        int i = 0;
        String temp1 = sharedPreferences.getString("record_name_" + i, null);
        while(temp1 != null) {
            this.recordNames.add(temp1);
            i++;
            temp1 = sharedPreferences.getString( "record_name_" + i, null);
        }
    }

    //加载每个层次名
    public void loadList() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(levelNow);
        ArrayList<String> imagePaths = new ArrayList<>();
        ArrayList<String> imageNotes = new ArrayList<>();
        ArrayList<String> tempNames = new ArrayList<>();
        ArrayList<String> tempNotes = new ArrayList<>();
        sharedPreferences = getSharedPreferences(recordNow, Context.MODE_PRIVATE);
        int i = 0;
        String temp;
        if(sharedPreferences.getInt(levelNow, -1) == 6) {
            temp = sharedPreferences.getString(levelNow + "_image_" + i, null);
            while (temp != null) {
                imagePaths.add(temp);
                imageNotes.add(sharedPreferences.getString(levelNow + "_image_note_" + i, null));
                i++;
                temp = sharedPreferences.getString(levelNow + "_image_" + i, null);
            }
        } else {
            temp = sharedPreferences.getString(levelNow + "_next_" + i, null);
            while (temp != null) {
                tempNames.add(temp);
                tempNotes.add(sharedPreferences.getString(temp + "_note", null));
                i++;
                temp = sharedPreferences.getString(levelNow + "_next_" + i, null);
            }
        }

        if (imagePaths.size() > 0) {
            ArrayList<Photo> photos = Photo.getPhotos(imagePaths, imageNotes);
            PhotoAdapter photoAdapter = new PhotoAdapter(MainActivity.this, R.layout.photo_list, photos);
            ListView listView = findViewById(R.id.list_view);
            listView.setAdapter(photoAdapter);
            return;
        }
        ArrayList<Level> levels = Level.getLevels(tempNames, tempNotes);
        LevelAdapter levelAdapter = new LevelAdapter(MainActivity.this, R.layout.level_list, levels);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(levelAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            levelNow = tempNames.get(position);
            loadList();
        });
    }
}