package com.example.speciesrecord;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class NewSpecies extends AppCompatActivity {
    private EditText mKingdomName;
    private EditText mKingdomNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_species);
        setListeners();
    }

    //设置对于输入框的监控
    private void setListeners() {
        mKingdomName = (EditText)findViewById(R.id.kingdomName);
        mKingdomNote = (EditText)findViewById(R.id.kingdomNote);
        mKingdomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Boolean judge = !((String) mKingdomName.getText().toString()).equals("");
                if(judge) {
                    mKingdomNote.setVisibility(View.VISIBLE);
                } else {
                    mKingdomNote.setVisibility(View.GONE);
                }
            }
        });
    }
}