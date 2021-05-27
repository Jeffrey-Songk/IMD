package com.example.speciesrecord;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = findViewById(R.id.record_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton add_fab = findViewById(R.id.add_fab);
        add_fab.setOnClickListener(view -> {
            if (recordNow.equals("Initial Record by Jeffrey")) {
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

    //右上菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.all_record) {
            allRecord();
            return true;
        } else if (item.getItemId() == R.id.new_record) {
            newRecord();
            return true;
        } else if (item.getItemId() == R.id.delete) {
            deleteThis();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            toHigherLevel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                            getExternalFilesDir("").getAbsolutePath()
                                    + "/databases/SpeciesRecord.db", null);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("is_now", 0);
                    db.update("records", contentValues, "is_now = 1", null);
                    contentValues = new ContentValues();
                    contentValues.put("is_now", 1);
                    db.update("records", contentValues, "record = ?", new String[]{recordNow});
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
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    getExternalFilesDir("").getAbsolutePath()
                            + "/databases/SpeciesRecord.db", null);
            Cursor cursor = db.rawQuery("select record from records where record = ?", new String[]{newName});
            if(cursor.getCount() > 0) {
                new AlertDialog.Builder(this)
                        .setMessage("该名称记录已存在")
                        .setPositiveButton("确定", (dialog, which) -> {
                        })
                        .create().show();
            } else {
                SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(
                        getExternalFilesDir("").getAbsolutePath()
                                + "/databases/" + newName + ".db", null);
                String SQL = "create table levels(name varchar(30), level integer, note varchar(127), previous varchar(30))";
                db1.execSQL(SQL);
                ContentValues contentValues = new ContentValues();
                contentValues.put("is_now", 0);
                db.update("records", contentValues, "is_now = 1", null);
                contentValues = new ContentValues();
                contentValues.put("record", newName);
                contentValues.put("is_now", 1);
                db.insert("records", null, contentValues);
                recordNow = newName;
                levelNow = newName;
                alertDialog.cancel();
                dataInit();
                loadList();
            }
            cursor.close();
        });
    }

    //删除
    public void deleteThis() {
        if (recordNow.equals("Initial Record by Jeffrey")) {
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
                    SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                            getExternalFilesDir("").getAbsolutePath()
                                    + "/databases/SpeciesRecord.db", null);
                    if(recordNow.equals(levelNow)) {
                        File file = new File(getExternalFilesDir("").getAbsolutePath() + "/databases/" + recordNow + ".db");
                        if(!file.delete()) {
                            System.out.println("fail");
                            return;
                        }
                        db.delete("records", "record = ?", new String[]{recordNow});
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("is_now", 1);
                        db.update("records", contentValues, "record = ?", new String[]{"Initial Record by Jeffrey"});
                        levelNow = "Initial Record by Jeffrey";
                        recordNow = levelNow;
                    } else {
//                        //递归删除，未完成
//                        String temp = sharedPreferences.getString(levelNow + "_previous", null);
//                        SharedPreferencesOperation.deleteLevel(levelNow);
//                        levelNow = temp;
                        db = SQLiteDatabase.openOrCreateDatabase(
                                getExternalFilesDir("").getAbsolutePath()
                                        + "/databases/" + recordNow + ".db", null);
                        Cursor cursor = db.rawQuery("select previous from levels where name = ?", new String[]{levelNow});
                        cursor.moveToFirst();
                        levelNow = cursor.getString(0);
                        cursor.close();
                        DatabaseOperation.deleteLevel(db, levelNow);

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
        if (levelNow.equals(recordNow)) {
            allRecord();
            return;
        }
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                getExternalFilesDir("").getAbsolutePath()
                        + "/databases/" + recordNow + ".db", null);
        Cursor cursor = db.rawQuery("select previous from levels where name = ?", new String[]{levelNow});
        cursor.moveToFirst();
        levelNow = cursor.getString(0);
        cursor.close();
        loadList();
    }

    //配置默认记录，及配置
    //ok
    public void addDefaultFile() {
        File databaseFile = new File(getExternalFilesDir("").getAbsolutePath() + "/databases");
        if(databaseFile.mkdirs()) {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    getExternalFilesDir("").getAbsolutePath()
                            + "/databases/SpeciesRecord.db", null);
            String SQL = "create table records(record varchar(30), is_now boolean)";
            db.execSQL(SQL);
            SQL = "insert into records(record, is_now) values('Initial Record by Jeffrey', 1)";
            db.execSQL(SQL);
            recordNow = "Initial Record by Jeffrey";
            levelNow = "Initial Record by Jeffrey";
            String imgPath = getExternalFilesDir("").getAbsolutePath() + "/images";
            File imgFile = new File(imgPath);
            if (!imgFile.mkdirs()) {
                return;
            }
            db = SQLiteDatabase.openOrCreateDatabase(
                    getExternalFilesDir("").getAbsolutePath()
                            + "/databases/Initial Record by Jeffrey.db", null);
            SQL = "create table levels(name varchar(30), level integer, note varchar(127), previous varchar(30))";
            db.execSQL(SQL);

            DatabaseOperation.insertToLevel(db, "蛱蝶科", 4, "前足退化", "Initial Record by Jeffrey");
            DatabaseOperation.insertToLevel(db, "凤蝶科", 4, "常具尾突", "Initial Record by Jeffrey");
            DatabaseOperation.insertToLevel(db, "灰蝶科", 4, "体型较小", "Initial Record by Jeffrey");
            DatabaseOperation.insertToLevel(db, "粉蝶科", 4, "色调较淡", "Initial Record by Jeffrey");

            InputStream[] inputStreams = new InputStream[]{
                    getResources().openRawResource(+R.drawable.default_1),
                    getResources().openRawResource(+R.drawable.default_2),
                    getResources().openRawResource(+R.drawable.default_3),
                    getResources().openRawResource(+R.drawable.default_4),
                    getResources().openRawResource(+R.drawable.default_5),
                    getResources().openRawResource(+R.drawable.default_6),
                    getResources().openRawResource(+R.drawable.default_7),
                    getResources().openRawResource(+R.drawable.default_8),
                    getResources().openRawResource(+R.drawable.default_9),
                    getResources().openRawResource(+R.drawable.default_10),
                    getResources().openRawResource(+R.drawable.default_11),
                    getResources().openRawResource(+R.drawable.default_12),
                    getResources().openRawResource(+R.drawable.default_13),
                    getResources().openRawResource(+R.drawable.default_14),
                    getResources().openRawResource(+R.drawable.default_15)
            };
            String[] names = new String[]{
                    "明窗蛱蝶_1.jpg", "明窗蛱蝶_2.jpg", "明窗蛱蝶_3.jpg",
                    "枯叶蛱蝶.jpg", "银豹蛱蝶.jpg", "拟斑脉蛱蝶.jpg",
                    "柑橘凤蝶_1.jpg", "柑橘凤蝶_2.jpg", "绿带翠凤蝶_1.jpg",
                    "绿带翠凤蝶_2.jpg", "东亚燕灰蝶_1.jpg", "东亚燕灰蝶_2.jpg",
                    "亮灰蝶.jpg", "Y纹绢粉蝶.jpg", "西村绢粉蝶.jpg"
            };
            for (int i = 0; i < 15; i++) {
                try {
                    String LogoFilePath = imgPath + "/" + names[i];
                    FileOutputStream fos = new FileOutputStream(LogoFilePath);
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = inputStreams[i].read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                    }
                    fos.close();
                    inputStreams[i].close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            DatabaseOperation.insertToLevel(db, "明窗蛱蝶", 6, "一年一季，出现在春天", "蛱蝶科");
            DatabaseOperation.insertToLevel(db, "枯叶蛱蝶", 6, "", "蛱蝶科");
            DatabaseOperation.insertToLevel(db, "银豹蛱蝶", 6, "", "蛱蝶科");
            DatabaseOperation.insertToLevel(db, "拟斑脉蛱蝶", 6, "", "蛱蝶科");
            DatabaseOperation.insertToLevel(db, "柑橘凤蝶", 6, "广布的凤蝶", "凤蝶科");
            DatabaseOperation.insertToLevel(db, "绿带翠凤蝶", 6, "", "凤蝶科");
            DatabaseOperation.insertToLevel(db, "东亚燕灰蝶", 6, "", "灰蝶科");
            DatabaseOperation.insertToLevel(db, "亮灰蝶", 6, "", "灰蝶科");
            DatabaseOperation.insertToLevel(db, "Y纹绢粉蝶", 6, "高海拔绢粉蝶", "粉蝶科");
            DatabaseOperation.insertToLevel(db, "西村绢粉蝶", 6, "高海拔绢粉蝶", "粉蝶科");
            DatabaseOperation.createSpeciesImagesTable(db, "明窗蛱蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "枯叶蛱蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "银豹蛱蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "拟斑脉蛱蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "柑橘凤蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "绿带翠凤蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "东亚燕灰蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "亮灰蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "Y纹绢粉蝶");
            DatabaseOperation.createSpeciesImagesTable(db, "西村绢粉蝶");
            DatabaseOperation.insertImagetoTable(db, "明窗蛱蝶", imgPath + "/" + names[0], "生态照");
            DatabaseOperation.insertImagetoTable(db, "明窗蛱蝶", imgPath + "/" + names[1], "雄");
            DatabaseOperation.insertImagetoTable(db, "明窗蛱蝶", imgPath + "/" + names[2], "雌");
            DatabaseOperation.insertImagetoTable(db, "枯叶蛱蝶", imgPath + "/" + names[3], "");
            DatabaseOperation.insertImagetoTable(db, "银豹蛱蝶", imgPath + "/" + names[4], "");
            DatabaseOperation.insertImagetoTable(db, "拟斑脉蛱蝶", imgPath + "/" + names[5], "");
            DatabaseOperation.insertImagetoTable(db, "柑橘凤蝶", imgPath + "/" + names[6], "");
            DatabaseOperation.insertImagetoTable(db, "柑橘凤蝶", imgPath + "/" + names[7], "");
            DatabaseOperation.insertImagetoTable(db, "绿带翠凤蝶", imgPath + "/" + names[8], "东北绿带翠");
            DatabaseOperation.insertImagetoTable(db, "绿带翠凤蝶", imgPath + "/" + names[9], "南方型绿带翠");
            DatabaseOperation.insertImagetoTable(db, "东亚燕灰蝶", imgPath + "/" + names[10], "");
            DatabaseOperation.insertImagetoTable(db, "东亚燕灰蝶", imgPath + "/" + names[11], "");
            DatabaseOperation.insertImagetoTable(db, "亮灰蝶", imgPath + "/" + names[12], "");
            DatabaseOperation.insertImagetoTable(db, "Y纹绢粉蝶", imgPath + "/" + names[13], "");
            DatabaseOperation.insertImagetoTable(db, "西村绢粉蝶", imgPath + "/" + names[14], "");
        } else {
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                    getExternalFilesDir("").getAbsolutePath()
                            + "/databases/SpeciesRecord.db", null);
            Cursor cursor = db.rawQuery("select record from records where is_now = 1", null);
            cursor.moveToFirst();
            recordNow = cursor.getString(0);
            levelNow = recordNow;
            cursor.close();
        }
    }

    //数据初始化 recordNames
    //ok
    public void dataInit() {
        this.recordNames = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                getExternalFilesDir("").getAbsolutePath()
                        + "/databases/SpeciesRecord.db", null);
        Cursor cursor = db.rawQuery("select record from records", null);
        cursor.moveToFirst();
        this.recordNames.add(cursor.getString(0));
        while(cursor.moveToNext()) {
            this.recordNames.add(cursor.getString(0));
        }
        cursor.close();
    }

    //加载每个层次名
    //ok
    public void loadList() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(levelNow);
        ArrayList<String> imagePaths = new ArrayList<>();
        ArrayList<String> imageNotes = new ArrayList<>();
        ArrayList<String> tempNames = new ArrayList<>();
        ArrayList<String> tempNotes = new ArrayList<>();

        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                getExternalFilesDir("").getAbsolutePath()
                        + "/databases/" + recordNow + ".db", null);
        Cursor cursor = db.rawQuery("select level from levels where name = ?", new String[]{levelNow});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            if(cursor.getInt(0) == 6) {
                Cursor cursor1 = db.rawQuery("select * from " + levelNow, null);
                if(cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    imagePaths.add(cursor1.getString(0));
                    if(cursor1.getString(1).equals(""))
                        imageNotes.add(null);
                    else
                        imageNotes.add(cursor1.getString(1));
                    while (cursor1.moveToNext()) {
                        imagePaths.add(cursor1.getString(0));
                        if(cursor1.getString(1).equals(""))
                            imageNotes.add(null);
                        else
                            imageNotes.add(cursor1.getString(1));
                    }
                }
                cursor1.close();
                ArrayList<Photo> photos = Photo.getPhotos(imagePaths, imageNotes);
                PhotoAdapter photoAdapter = new PhotoAdapter(MainActivity.this, R.layout.photo_list, photos);
                ListView listView = findViewById(R.id.list_view);
                listView.setAdapter(photoAdapter);
                return;
            }
        }
        cursor.close();
        cursor = db.rawQuery("select name, note from levels where previous = ?", new String[]{levelNow});
        if(cursor.getCount() > 0) {
            cursor.moveToFirst();
            tempNames.add(cursor.getString(0));
            if(cursor.getString(1).equals(""))
                tempNotes.add(null);
            else
                tempNotes.add(cursor.getString(1));
            while (cursor.moveToNext()) {
                tempNames.add(cursor.getString(0));
                if(cursor.getString(1).equals(""))
                    tempNotes.add(null);
                else
                    tempNotes.add(cursor.getString(1));
            }
        }
        cursor.close();
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