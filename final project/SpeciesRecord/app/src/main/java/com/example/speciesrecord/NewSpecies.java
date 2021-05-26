package com.example.speciesrecord;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.ArrayList;

public class NewSpecies extends AppCompatActivity {
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
        setLevelsListener();
        setNameListener();
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
        MainActivity.sharedPreferences = getSharedPreferences(MainActivity.recordNow, Context.MODE_PRIVATE);
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
                        int where = MainActivity.sharedPreferences.getInt(mLevelNames[finalI], -1);
                        if(where != -1) {
                            mLevelEditTexts[finalI].setText(MainActivity.sharedPreferences.getString(mLevelNames[finalI] + "_note", null));
                            String previous = MainActivity.sharedPreferences.getString(mLevelNames[finalI] + "_previous", null);
                            int where1 = MainActivity.sharedPreferences.getInt(previous, -1);
                            if(where1 != -1) {
                                mLevelListeners[where1].setText(previous);
                            }
                        }
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
        int level = MainActivity.sharedPreferences.getInt(MainActivity.levelNow, -1);
        if(level != -1) {
            mLevelListeners[level].setText(MainActivity.levelNow);
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
            String[] filePathColumns = {android.provider.MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            if(imageNotes.size() < imagePaths.size()) imageNotes.add(null);
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
        MainActivity.sharedPreferences = getSharedPreferences(MainActivity.recordNow, Context.MODE_PRIVATE);
        boolean rationality = true;
        String temp = MainActivity.recordNow;
        for(int i = 0; i < 7; i++) {
            if(mLevelNames[i] != null) {
                int temp1 = MainActivity.sharedPreferences.getInt(mLevelNames[i], -1);
                if(temp1 == -1) {
                    SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
                    editor.putInt(mLevelNames[i], i);
                    editor.putString(temp + "_next_" + SharedPreferencesOperation.next(temp, mLevelNames[i]), mLevelNames[i]);
                    editor.putString(mLevelNames[i] + "_previous", temp);
                    editor.apply();
                } else if(temp1 != i) {
                    rationality = false;
                    break;
                } else {
                    String temp2 = MainActivity.sharedPreferences.getString(mLevelNames[i] + "_previous", null);
                    if(MainActivity.sharedPreferences.getInt(temp2, -1)
                            >= MainActivity.sharedPreferences.getInt(temp, -1)) {
                        SharedPreferences.Editor editor2 = MainActivity.sharedPreferences.edit();
                        editor2.putString(mLevelNames[i] + "_previous", temp2);
                        editor2.putString(temp2 + "_next_" + SharedPreferencesOperation.next(temp2, mLevelNames[i]), mLevelNames[i]);
                        editor2.apply();
                    } else {
                        SharedPreferences.Editor editor3 = MainActivity.sharedPreferences.edit();
                        editor3.putString(mLevelNames[i] + "_previous", temp);
                        editor3.putString(temp + "_next_" + SharedPreferencesOperation.next(temp, mLevelNames[i]), mLevelNames[i]);
                        editor3.apply();
                        int temp3 = SharedPreferencesOperation.find(temp2 + "_next_", mLevelNames[i]);
                        SharedPreferencesOperation.delete(temp2 + "_next_", temp3);
                    }
                }
                if(mLevelNotes[i] != null){
                    SharedPreferences.Editor editor1 = MainActivity.sharedPreferences.edit();
                    editor1.putString(mLevelNames[i] + "_note", mLevelNotes[i]);
                    editor1.apply();
                }
                if(i == 6) {
                    SharedPreferences.Editor editor1 = MainActivity.sharedPreferences.edit();
                    for(int j = 0; j < imagePaths.size(); j++) {
                        editor1.putString(mLevelNames[i] + "_image_" + j, imagePaths.get(j));
                        if(mLevelNotes[j] != null) {
                            editor1.putString(mLevelNames[i] + "_image_note_" + j, imageNotes.get(j));
                        }
                    }
                    editor1.apply();
                }
                temp = mLevelNames[i];
            }
        }
        if(!rationality) {
            Toast.makeText(this, "写入数据与存储数据冲突", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(NewSpecies.this, MainActivity.class);
        startActivity(intent);
    }
}