package com.example.speciesrecord;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewSpecies extends AppCompatActivity {
    private String startPath;
    private String[] mLevelNames;
    private String[] mLevelNotes;
    private ArrayList<String> imagePaths;
    private ArrayList<String> imageNotes;
    private final int IMAGE_REQUEST_CODE = 903;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_species);

        init();
    }

    public void init() {
        FileOperation.verifyStoragePermissions(this);
        judgeToMain();
        setPage();
        dataInit();
        setLevelsListener();
        setNameListener();
    }

    public void judgeToMain() {
        startPath = getExternalFilesDir("").getAbsolutePath();
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if(new File(startPath + "/temp").exists()) {
                FileOperation.delFolder(startPath + "/temp");
            }
            if (uri != null) {
                String path = uri.getPath();
                String name = FileOperation.getExactName(path);
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(startPath + "/databases/SpeciesRecord.db", null);
                Cursor cursor = db.rawQuery("select record from records where record = ?", new String[]{name});
                if(cursor.getCount() > 0) {
                    Toast.makeText(this, "已存在对应记录名称，打开失败", Toast.LENGTH_LONG).show();
                    return;
                }
                cursor.close();
                for(int i = 1; i < path.length(); i++) {
                    if(String.valueOf(path.charAt(i)).equals("/")) {
                        path = path.substring(i + 1);
                        break;
                    }
                }
                FileOperation.unZip(new File(path), startPath + "/temp");
                FileOperation.moveFile(startPath + "/temp/" + name + "/" + name + ".db",
                        startPath + "/databases");
                FileOperation.moveFolder(startPath + "/temp/" + name,
                        startPath + "/images");
                ContentValues contentValues = new ContentValues();
                contentValues.put("is_now", 0);
                db.update("records", contentValues, "is_now = 1", null);
                contentValues = new ContentValues();
                contentValues.put("record", name);
                contentValues.put("is_now", 1);
                db.insert("records", null, contentValues);
                db = SQLiteDatabase.openOrCreateDatabase(
                        startPath
                                + "/databases/" + name + ".db", null);
                Cursor cursor1 = db.rawQuery("select name from levels where level = 6", null);
                if(cursor1.getCount() > 0) {
                    cursor1.moveToFirst();
                    String species = cursor1.getString(0);
                    Cursor cursor2 = db.rawQuery("select * from " + species, null);
                    if(cursor2.getCount() > 0) {
                        cursor2.moveToFirst();
                        contentValues = new ContentValues();
                        contentValues.put("path", startPath + "/images/" + name + "/" + species + "_" + cursor2.getInt(2) + ".jpg");
                        db.update(species, contentValues, "_id = ?", new String[]{String.valueOf(cursor2.getInt(2))});
                        while (cursor2.moveToNext()) {
                            contentValues = new ContentValues();
                            contentValues.put("path", startPath + "/images/" + name + "/" + species + "_" + cursor2.getInt(2) + ".jpg");
                            db.update(species, contentValues, "_id = ?", new String[]{String.valueOf(cursor2.getInt(2))});
                        }
                    }
                    cursor2.close();
                    while (cursor1.moveToNext()) {
                        species = cursor1.getString(0);
                        cursor2 = db.rawQuery("select * from " + species, null);
                        if(cursor2.getCount() > 0) {
                            cursor2.moveToFirst();
                            contentValues = new ContentValues();
                            contentValues.put("path", startPath + "/images/" + name + "/" + species + "_" + cursor2.getInt(2) + ".jpg");
                            db.update(species, contentValues, "_id = ?", new String[]{String.valueOf(cursor2.getInt(2))});
                            while (cursor2.moveToNext()) {
                                contentValues = new ContentValues();
                                contentValues.put("path", startPath + "/images/" + name + "/" + species + "_" + cursor2.getInt(2) + ".jpg");
                                db.update(species, contentValues, "_id = ?", new String[]{String.valueOf(cursor2.getInt(2))});
                            }
                        }
                        cursor2.close();
                    }
                }
                cursor1.close();
            }

            Intent intent1 = new Intent(NewSpecies.this, MainActivity.class);
            startActivity(intent1);
        }
    }

    public void setPage() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Toolbar toolbar = findViewById(R.id.new_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void dataInit() {
        this.imagePaths = new ArrayList<>();
        this.imageNotes = new ArrayList<>();
        this.mLevelNames = new String[]{null, null, null, null, null, null, null};
        this.mLevelNotes = new String[]{null, null, null, null, null, null, null};
    }

    public void setLevelsListener() {
        EditText[] mLevelEditTexts = new EditText[]{
                findViewById(R.id.kingdom_note),
                findViewById(R.id.phylum_note),
                findViewById(R.id.class_note),
                findViewById(R.id.order_note),
                findViewById(R.id.family_note),
                findViewById(R.id.genus_note)
        };
        EditText[] mLevelListeners = new EditText[]{
                findViewById(R.id.kingdom_name),
                findViewById(R.id.phylum_name),
                findViewById(R.id.class_name),
                findViewById(R.id.order_name),
                findViewById(R.id.family_name),
                findViewById(R.id.genus_name)
        };
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                startPath
                        + "/databases/" + MainActivity.recordNow + ".db", null);
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            mLevelListeners[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!mLevelListeners[finalI].getText().toString().equals("")) {
                        mLevelEditTexts[finalI].setVisibility(View.VISIBLE);
                        mLevelNames[finalI] = mLevelListeners[finalI].getText().toString();
                        Cursor cursor = db.rawQuery("select note, previous from levels where name = ?", new String[]{mLevelNames[finalI]});
                        if(cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            mLevelEditTexts[finalI].setText((cursor.getString(0)));
                            if(!cursor.getString(1).equals(MainActivity.recordNow)) {
                                Cursor cursor1 = db.rawQuery("select level from levels where name = ?", new String[]{cursor.getString(1)});
                                if(cursor1.getCount() > 0) {
                                    cursor1.moveToFirst();
                                    mLevelListeners[cursor1.getInt(0)].setText(cursor.getString(1));
                                }
                                cursor1.close();
                            }
                        }
                        cursor.close();
                    } else {
                        mLevelEditTexts[finalI].setVisibility(View.GONE);
                        mLevelNames[finalI] = null;
                    }
                }
            });
            mLevelEditTexts[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!mLevelEditTexts[finalI].getText().toString().equals("")) {
                        mLevelNotes[finalI] = mLevelEditTexts[finalI].getText().toString();
                    } else {
                        mLevelNotes[finalI] = null;
                    }
                }
            });
        }
        if(!MainActivity.levelNow.equals(MainActivity.recordNow)) {
            Cursor cursor = db.rawQuery("select level from levels where name = ?", new String[]{MainActivity.levelNow});
            cursor.moveToFirst();
            mLevelListeners[cursor.getInt(0)].setText(MainActivity.levelNow);
            cursor.close();
        }
    }

    public void setNameListener() {
        LinearLayout mSpeciesContainer = findViewById(R.id.species_container);
        EditText mSpeciesName = findViewById(R.id.species_name);
        EditText mSpeciesNote = findViewById(R.id.species_note);
        mSpeciesName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mSpeciesName.getText().toString().equals("")) {
                    mLevelNames[6] = mSpeciesName.getText().toString();
                    mSpeciesContainer.setVisibility(View.VISIBLE);
                } else {
                    mSpeciesContainer.setVisibility(View.GONE);
                    mLevelNames[6] = null;
                }
            }
        });

        mSpeciesNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mSpeciesName.getText().toString().equals("")) {
                    mLevelNotes[6] = mSpeciesNote.getText().toString();
                } else {
                    mLevelNotes[6] = null;
                }
            }
        });
    }

    public void addPhotos(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            if(imageNotes.size() < imagePaths.size()) {
                imageNotes.add(null);
            }
            imagePaths.add(c.getString(columnIndex));
            c.close();
            View view = View.inflate(NewSpecies.this, R.layout.new_record_toast, null);
            final Button btn = view.findViewById(R.id.btn_confirm_new);
            EditText editText = view.findViewById(R.id.new_name);
            editText.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);
            editText.setSingleLine(false);
            editText.setMaxLines(5);
            editText.setHint("请输入对该图片的描述");
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("图片备注")
                    .setIcon(R.drawable.ic_icon)
                    .setView(view)
                    .create();
            alertDialog.show();
            btn.setOnClickListener(v -> {
                imageNotes.add(editText.getText().toString());
                alertDialog.cancel();
            });
            Button button = findViewById(R.id.add_photo);
            String temp = "添加图片(已选" + imagePaths.size() + "张)";
            button.setText(temp);
        }
    }

    //确认添加按钮
    public void addConfirm(View view) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(
                startPath
                        + "/databases/" + MainActivity.recordNow + ".db", null);
        Cursor cursor, cursor1, cursor2;
        int rationality = -1;
        String temp = MainActivity.recordNow;
        for(int i = 0; i < 7; i++) {
            if(mLevelNames[i] != null) {
                if(mLevelNames[i].equals(MainActivity.recordNow)) {
                    rationality = 2;
                    break;
                } else {
                    String regex = "[\u4e00-\u9fa5]";
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(mLevelNames[i].charAt(0) + "");
                    if(!m.matches()) {
                        int temp2 = mLevelNames[i].charAt(0);
                        if(!((temp2>=65&&temp2<=90)||(temp2>=97&&temp2<=122))) {
                            rationality = 3;
                            break;
                        }
                    }
                }
                cursor = db.rawQuery("select * from levels where name = ?", new String[]{mLevelNames[i]});
                if (cursor.getCount() == 0) {
                    DatabaseOperation.insertToLevel(db, mLevelNames[i], i, mLevelNotes[i], temp);
                } else {
                    cursor.moveToFirst();
                    if (cursor.getInt(1) != i) {
                        rationality = 1;
                        break;
                    } else if (!temp.equals(cursor.getString(3))) {
                        cursor1 = db.rawQuery("select level from levels where name = ?", new String[]{cursor.getString(3)});
                        cursor2 = db.rawQuery("select level from levels where name = ?", new String[]{temp});
                        ContentValues contentValues = new ContentValues();
                        if (cursor1.getCount() == 0) {
                            contentValues.put("previous", temp);
                            db.update("levels", contentValues, "name = ?", new String[]{mLevelNames[i]});
                        } else if (cursor2.getCount() == 0) {
                            contentValues.put("previous", cursor.getString(3));
                            db.update("levels", contentValues, "name = ?", new String[]{mLevelNames[i]});
                        } else {
                            cursor1.moveToFirst();
                            cursor2.moveToFirst();
                            String temp1 = (cursor1.getInt(0) > cursor2.getInt(0) ? cursor.getString(3) : temp);
                            contentValues.put("previous", temp1);
                            db.update("levels", contentValues, "name = ?", new String[]{mLevelNames[i]});
                        }
                        cursor1.close();
                        cursor2.close();
                    }
                }
                cursor.close();
                if(i == 6) {
                    DatabaseOperation.createSpeciesImagesTable(db, mLevelNames[i]);
                    if(imagePaths.size() > 0) {
                        if(MainActivity.isLightweight) {
                            for (int j = 0; j < imagePaths.size(); j++) {
                                DatabaseOperation.insertImagetoTable(db, mLevelNames[i], imagePaths.get(j), imageNotes.get(j));
                            }
                        } else {
                            for (int j = 0; j < imagePaths.size(); j++) {
                                DatabaseOperation.insertImagetoTable(
                                        db,
                                        mLevelNames[i],
                                        FileOperation.getSaveImage(
                                                imagePaths.get(j),
                                                startPath + "/images/" + MainActivity.recordNow,
                                                mLevelNames[i],
                                                -1),
                                        imageNotes.get(j));
                            }
                        }
                    }
                }
                temp = mLevelNames[i];
            }
        }
        if(rationality == 1) {
            Toast.makeText(this, "写入数据与存储数据冲突", Toast.LENGTH_LONG).show();
            return;
        } else if (rationality == 2) {
            Toast.makeText(this, "不得记录与记录名相同的层次", Toast.LENGTH_LONG).show();
            return;
        } else if(rationality == 3) {
            Toast.makeText(this, "各名称请以中文或字母开头", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(NewSpecies.this, MainActivity.class);
        startActivity(intent);
    }
}