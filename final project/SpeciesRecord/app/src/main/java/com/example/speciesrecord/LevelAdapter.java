package com.example.speciesrecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class LevelAdapter extends ArrayAdapter<Level> {
    private final int id;
    public LevelAdapter(@NonNull Context context, int resource, ArrayList<Level> levels) {
        super(context, resource, levels);
        id = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Level level = getItem(position);
        @SuppressLint("ViewHolder") View view= LayoutInflater.from(getContext()).inflate(id, parent,false);
        TextView levelListName = view.findViewById(R.id.level_list_name);
        TextView levelListNote = view.findViewById(R.id.level_list_note);
        levelListName.setText(level.getName());
        levelListNote.setText(level.getNote());
        return view;
    }
}
