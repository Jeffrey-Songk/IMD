package com.example.speciesrecord;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String[] records;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.record_toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton add_fab = findViewById(R.id.add_fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //jump to add page.
            }
        });
        findRecords();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.show_images:
                //跳转到图片页面
                return true;
            case R.id.other_record:
                otherRecord();
                return true;
            case R.id.new_record:
                newRecord();
                return true;
            case R.id.import_record:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    //初始化Records[]
    public void findRecords() {
        records = new String[]{"列表项1", "列表项2", "列表项3"};
    }
    public void otherRecord() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("其他记录")
                .setItems(records, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转到对应记录
                    }
                })
                .create().show();
    }
    public void newRecord() {
        View view = View.inflate(MainActivity.this, R.layout.new_record, null);
        final EditText new_name = (EditText) view.findViewById(R.id.new_name);
        final Button btn = (Button) view.findViewById(R.id.btn_comfirm_new);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_icon)
                .setTitle("新增记录")
                .setView(view)
                .create().show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}