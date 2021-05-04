package com.example.speciesrecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

public class NewSpecies extends AppCompatActivity {
    private String currentRecord;
    private String[] mLevelNames;
    private String[] mLevelNotes;
    private String mDate;
    private String mAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_species);

        init();

    }

    public void init() {
        dataInit();
        //写入相应分类层次的名称后，出现对应层次的备注。如果是种，则还有日期和地点。
        setLevelsListener();
        setNameListener();
    }

    public void dataInit() {
        mLevelNames = new String[]{null, null, null, null, null, null, null};
        mLevelNotes = new String[]{null, null, null, null, null, null, null};
        String recordsPath = getExternalCacheDir().getAbsolutePath() + "/records/currentRecord.txt";
        File fileName = new File(recordsPath);
        if (fileName.isFile()) {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(recordsPath);
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
        EditText mSpeciesDate = findViewById(R.id.species_date);
        EditText mSpeciesAddress = findViewById(R.id.species_address);
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
        mSpeciesDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mSpeciesDate.getText().toString().equals("")) {
                    mDate = mSpeciesDate.getText().toString();
                } else {
                    mDate = null;
                }
            }
        });
        mSpeciesAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mSpeciesAddress.getText().toString().equals("")) {
                    mAddress = mSpeciesAddress.getText().toString();
                } else {
                    mAddress = null;
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
                    mDate = null;
                }
            }
        });
    }

    //递归查找是否已有对应文件名的文件
    public File isExist(String levelName, File fileName, File result) {
        if (result != null) {
            return result;
        }
        File[] files = fileName.listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                if (file.getName().equals(levelName)) {
                    return file;
                }
                return isExist(levelName, file, null);
            }
        }
        return null;
    }

    //查找整个记录中是否已有对应层次
    public File recordExist(String recordName, String levelName) {
        String recordsPath = getExternalCacheDir().getAbsolutePath() + "/records";
        String currentRecordPath = recordsPath + "/" + recordName;
        File fileName = new File(currentRecordPath);
        return isExist(levelName, fileName, null);
    }

    //确认添加按钮
    public void add_confirm(View view) {
        StringBuilder path = new StringBuilder(getExternalCacheDir().getAbsolutePath() + "/records/" + currentRecord);
        for (int i = 0; i < 7; i++) {
            if (mLevelNames[i] != null) {
                File findLevel = recordExist(currentRecord, mLevelNames[i]);
                if (findLevel == null) {
                    File newLevel = new File(path + "/" + mLevelNames[i]);
                    newLevel.mkdir();
                } else {
                    File newLevel = new File(path + "/" + mLevelNames[i]);
                    newLevel.mkdir();
                    FileOperation.moveFolder(findLevel.toString(), newLevel.toString());
                }
                path.append("/").append(mLevelNames[i]);
                if (mLevelNotes[i] != null) {
                    FileOperation.writeFile(path + "/note.txt", mLevelNotes[i]);
                }
                if (i == 6) {
                    if (mDate != null) {
                        FileOperation.writeFile(path + "/date.txt", mDate);
                    }
                    if (mAddress != null) {
                        FileOperation.writeFile(path + "/address.txt", mAddress);
                    }
                }
            }
        }
        Intent intent = new Intent(NewSpecies.this, MainActivity.class);
        startActivity(intent);
    }
}