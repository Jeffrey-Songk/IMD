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

import java.util.LinkedList;

public class NewSpecies extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_species);

        init();
    }

    public void init() {
        //写入相应分类层次的名称后，出现对应层次的备注。如果是种，则还有日期和地点。
        setLevelsListener();
        setNameListener();
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
        EditText[] mLevelNames = new EditText[]{
                (EditText) findViewById(R.id.kingdom_name),
                (EditText) findViewById(R.id.phylum_name),
                (EditText) findViewById(R.id.class_name),
                (EditText) findViewById(R.id.order_name),
                (EditText) findViewById(R.id.family_name),
                (EditText) findViewById(R.id.genus_name)
        };
        for(int i = 0; i < 6; i++) {
            int finalI = i;
            mLevelNames[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!mLevelNames[finalI].getText().toString().equals("")) {
                        mLevelEditTexts[finalI].setVisibility(View.VISIBLE);
                    } else {
                        mLevelEditTexts[finalI].setVisibility(View.GONE);
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

    public void add_confirm(View view) {
        Intent intent = new Intent(NewSpecies.this,MainActivity.class);
        startActivity(intent);
    }
}