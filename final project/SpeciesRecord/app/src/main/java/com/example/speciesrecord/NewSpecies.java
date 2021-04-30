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

import java.util.Date;
import java.util.LinkedList;

public class NewSpecies extends AppCompatActivity {
    private String[] mLevelNames;
    private String[] mLevelNotes;
    private Date date;
    private String address;
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
    }

    public void setLevelsListener() {
        EditText[] mLevelEditTexts = new EditText[]{
                (EditText) findViewById(R.id.kingdom_note),
                (EditText) findViewById(R.id.phylum_note),
                (EditText) findViewById(R.id.class_note),
                (EditText) findViewById(R.id.order_note),
                (EditText) findViewById(R.id.family_note),
                (EditText) findViewById(R.id.genus_note)
        };
        EditText[] mLevelListeners = new EditText[]{
                (EditText) findViewById(R.id.kingdom_name),
                (EditText) findViewById(R.id.phylum_name),
                (EditText) findViewById(R.id.class_name),
                (EditText) findViewById(R.id.order_name),
                (EditText) findViewById(R.id.family_name),
                (EditText) findViewById(R.id.genus_name)
        };
        for(int i = 0; i < 6; i++) {
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
        LinearLayout mSpeciesContainer = (LinearLayout) findViewById(R.id.species_container);
        EditText mSpeciesName = (EditText) findViewById(R.id.species_name);
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
                    mSpeciesContainer.setVisibility(View.VISIBLE);
                } else {
                    mSpeciesContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    //查找整个记录中是否已有对应层次
    public boolean isExist(String recordName, String levelName) {
        String recordsPath = getExternalCacheDir().getAbsolutePath() + "/records";
        String currentRecordPath = recordsPath + "/" + recordName;
        return false;
    }

    //确认添加按钮
    public void add_confirm(View view) {
        for(int i = 0; i < 7; i++) {
            if(mLevelNames[i] != null) {
                System.out.println(mLevelNames[i]);
                if(mLevelNotes[i] != null) {
                    System.out.println(mLevelNotes[i]);
                }
            }
        }
        Intent intent = new Intent(NewSpecies.this,MainActivity.class);
        startActivity(intent);
    }
}