package com.example.speciesrecord;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class NewSpecies extends AppCompatActivity {
    private String currentRecord;
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
        dataInit();
        //写入相应分类层次的名称后，出现对应层次的备注。如果是种，则还有日期和地点。
        setLevelsListener();
        setNameListener();
    }

    public void dataInit() {
        imagePaths = new ArrayList<>();
        imageNotes = new ArrayList<>();
        mLevelNames = new String[]{null, null, null, null, null, null, null};
        mLevelNotes = new String[]{null, null, null, null, null, null, null};
        String recordsPath = getExternalCacheDir().getAbsolutePath() + "/records/currentRecord.txt";
        File fileName = new File(recordsPath);
        if (fileName.isFile()) {
            try {
                FileReader fileReader = new FileReader(recordsPath);
                BufferedReader bufferReader = new BufferedReader(fileReader);
                currentRecord = bufferReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        //need
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {android.provider.MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePaths.add(c.getString(columnIndex));
            c.close();

            View view = View.inflate(NewSpecies.this, R.layout.new_record_toast, null);
            final Button btn = view.findViewById(R.id.btn_confirm_new);
            EditText editText = view.findViewById(R.id.new_name);
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
            if(imageNotes.size() < imagePaths.size()) imageNotes.add(null);
        }
    }

    //递归查找是否已有对应文件名的文件
    public File isExist(String levelName, File fileName, File result) {
        if (result != null) {
            return result;
        }
        File[] files = fileName.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        File temp;
        for (File file: files) {
            if (file.isDirectory()) {
                if (file.getName().equals(levelName)) {
                    return file;
                }
                temp = isExist(levelName, file, null);
                if(temp != null) {
                    return temp;
                }
            }
        }
        return null;
    }

    //查找整个记录中是否已有对应层次
    public File recordExist(String levelName) {
        String currentRecordPath = getExternalCacheDir().getAbsolutePath() + "/records/" + currentRecord;
        File fileName = new File(currentRecordPath);
        return isExist(levelName, fileName, null);
    }

    //确认添加按钮
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addConfirm(View view) {
        StringBuilder path = new StringBuilder(getExternalCacheDir().getAbsolutePath() + "/records/" + currentRecord);
        for (int i = 0; i < 7; i++) {
            if (mLevelNames[i] != null) {
                File findLevel = recordExist(mLevelNames[i]);
                File newLevel = new File(path + "/" + mLevelNames[i]);
                newLevel.mkdir();
                if (findLevel != null) {
                    FileOperation.moveFolder(findLevel.toString(), newLevel.toString());
                }
                path.append("/").append(mLevelNames[i]);
                if (mLevelNotes[i] != null) {
                    FileOperation.writeFile(path + "/note.txt", mLevelNotes[i]);
                }
                if (i == 6 && imagePaths.size() > 0) {
                    for(int j = 0; j < imagePaths.size(); j++) {
                        FileOperation.copyFile(imagePaths.get(j), path + "/" + (j + 1) + FileOperation.getFileType(imagePaths.get(j)));
                    }
                }
            }
        }
        Intent intent = new Intent(NewSpecies.this, MainActivity.class);
        startActivity(intent);
    }
}