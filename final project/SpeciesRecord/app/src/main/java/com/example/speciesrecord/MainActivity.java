package com.example.speciesrecord;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    protected static String levelNow;
    protected static String recordNow;
    private boolean photoMode = false;
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

    //右上菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.show_images) {
            showImages(item);
            return true;
        } else if (item.getItemId() == R.id.all_record) {
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

    //跳转到图片页面， 未写
    public void showImages(MenuItem item) {
        if(item.getTitle().toString().equals("图片模式")) {
            item.setTitle("初始页面");
            return;
        }
        item.setTitle("图片模式");
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
            while (temp != null) {
                if (temp.equals(newName)) {
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
            if (!isExist) {
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
                    if (recordNow.equals(levelNow)) {
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
        if (levelNow.equals(recordNow)) {
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
        if (temp == null) {
            recordNow = "default by jeffrey";
            levelNow = recordNow;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("currentRecord", recordNow);
            editor.putString("record_name_0", recordNow);
            editor.apply();
            String imgPath = getExternalFilesDir("").getAbsolutePath() + "/images";
            File imgFile = new File(imgPath);
            if (!imgFile.mkdirs()) {
                return;
            }
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
            sharedPreferences = getSharedPreferences("default by jeffrey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putString("default by jeffrey_next_0", "蛱蝶科");
            editor1.putString("default by jeffrey_next_1", "凤蝶科");
            editor1.putString("default by jeffrey_next_2", "灰蝶科");
            editor1.putString("default by jeffrey_next_3", "粉蝶科");
            editor1.putInt("蛱蝶科", 4);
            editor1.putInt("凤蝶科", 4);
            editor1.putInt("灰蝶科", 4);
            editor1.putInt("粉蝶科", 4);
            editor1.putString("蛱蝶科_note", "前足退化");
            editor1.putString("凤蝶科_note", "常具尾突");
            editor1.putString("灰蝶科_note", "体型较小");
            editor1.putString("粉蝶科_note", "色调较淡");
            editor1.putString("蛱蝶科_previous", "default by jeffrey");
            editor1.putString("凤蝶科_previous", "default by jeffrey");
            editor1.putString("灰蝶科_previous", "default by jeffrey");
            editor1.putString("粉蝶科_previous", "default by jeffrey");
            editor1.putString("蛱蝶科_next_0", "明窗蛱蝶");
            editor1.putString("蛱蝶科_next_1", "枯叶蛱蝶");
            editor1.putString("蛱蝶科_next_2", "银豹蛱蝶");
            editor1.putString("蛱蝶科_next_3", "拟斑脉蛱蝶");
            editor1.putString("凤蝶科_next_0", "柑橘凤蝶");
            editor1.putString("凤蝶科_next_1", "绿带翠凤蝶");
            editor1.putString("灰蝶科_next_0", "东亚燕灰蝶");
            editor1.putString("灰蝶科_next_1", "亮灰蝶");
            editor1.putString("粉蝶科_next_0", "Y纹绢粉蝶");
            editor1.putString("粉蝶科_next_1", "西村绢粉蝶");
            editor1.putInt("明窗蛱蝶", 6);
            editor1.putInt("枯叶蛱蝶", 6);
            editor1.putInt("银豹蛱蝶", 6);
            editor1.putInt("拟斑脉蛱蝶", 6);
            editor1.putInt("柑橘凤蝶", 6);
            editor1.putInt("绿带翠凤蝶", 6);
            editor1.putInt("东亚燕灰蝶", 6);
            editor1.putInt("亮灰蝶", 6);
            editor1.putInt("Y纹绢粉蝶", 6);
            editor1.putInt("西村绢粉蝶", 6);
            editor1.putString("明窗蛱蝶_previous", "蛱蝶科");
            editor1.putString("枯叶蛱蝶_previous", "蛱蝶科");
            editor1.putString("银豹蛱蝶_previous", "蛱蝶科");
            editor1.putString("拟斑脉蛱蝶_previous", "蛱蝶科");
            editor1.putString("柑橘凤蝶_previous", "凤蝶科");
            editor1.putString("绿带翠凤蝶_previous", "凤蝶科");
            editor1.putString("东亚燕灰蝶_previous", "灰蝶科");
            editor1.putString("亮灰蝶_previous", "灰蝶科");
            editor1.putString("Y纹绢粉蝶_previous", "粉蝶科");
            editor1.putString("西村绢粉蝶_previous", "粉蝶科");
            String[] temp1 = new String[]{
                    "明窗蛱蝶_image_0", "明窗蛱蝶_image_1", "明窗蛱蝶_image_2",
                    "枯叶蛱蝶_image_0", "银豹蛱蝶_image_0", "拟斑脉蛱蝶_image_0",
                    "柑橘凤蝶_image_0", "柑橘凤蝶_image_1", "绿带翠凤蝶_image_0",
                    "绿带翠凤蝶_image_1", "东亚燕灰蝶_image_0", "东亚燕灰蝶_image_1",
                    "亮灰蝶_image_0", "Y纹绢粉蝶_image_0", "西村绢粉蝶_image_0"
            };
            String[] temp2 = new String[]{
                    "明窗蛱蝶_image_note_0", "明窗蛱蝶_image_note_1", "明窗蛱蝶_image_note_2",
                    "枯叶蛱蝶_image_note_0", "银豹蛱蝶_image_note_0", "拟斑脉蛱蝶_image_note_0",
                    "柑橘凤蝶_image_note_0", "柑橘凤蝶_image_note_1", "绿带翠凤蝶_image_note_0",
                    "绿带翠凤蝶_image_note_1", "东亚燕灰蝶_image_note_0", "东亚燕灰蝶_image_note_1",
                    "亮灰蝶_image_note_0", "Y纹绢粉蝶_image_note_0", "西村绢粉蝶_image_note_0"
            };
            String[] temp3 = new String[]{
                    "生态照\n2021/4/14 天津蓟州\n为正在吸水的雄蝶",
                    "2021/4/14\n天津蓟州\n海拔100m\n雄",
                    "2021/5/6\n天津蓟州\n海拔100m\n雌",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "东北绿带",
                    "南方型绿带",
                    "生态照\n2021/5/6 天津蓟州",
                    "2021/5/7\n天津蓟州\n海拔100m\n雄",
                    "",
                    "2020/6/24\n四川\n海拔1800m\n雄",
                    "2020/6/24\n四川\n海拔2000m\n雄",
            };
            for (int i = 0; i < 15; i++) {
                editor1.putString(temp1[i], imgPath + "/" + names[i]);
                editor1.putString(temp2[i], temp3[i]);
            }
            editor1.apply();
        } else {
            recordNow = temp;
            levelNow = temp;
        }
    }

    //数据初始化 recordNames
    //ok
    public void dataInit() {
        this.recordNames = new ArrayList<>();
        sharedPreferences = getSharedPreferences("SpeciesRecord", Context.MODE_PRIVATE);
        int i = 0;
        String temp1 = sharedPreferences.getString("record_name_" + i, null);
        while (temp1 != null) {
            this.recordNames.add(temp1);
            i++;
            temp1 = sharedPreferences.getString("record_name_" + i, null);
        }
    }

    //加载每个层次名
    //ok
    public void loadList() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(levelNow);
        ArrayList<String> imagePaths = new ArrayList<>();
        ArrayList<String> imageNotes = new ArrayList<>();
        ArrayList<String> tempNames = new ArrayList<>();
        ArrayList<String> tempNotes = new ArrayList<>();
        sharedPreferences = getSharedPreferences(recordNow, Context.MODE_PRIVATE);
        int i = 0;
        String temp;
        if (sharedPreferences.getInt(levelNow, -1) == 6) {
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
            listView.setOnItemClickListener((parent, view, position, id) -> {
            });
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