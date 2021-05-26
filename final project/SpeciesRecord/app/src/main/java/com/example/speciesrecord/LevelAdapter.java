package com.example.speciesrecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class LevelAdapter extends ArrayAdapter<Level> {
    private final int id;
    public LevelAdapter(Context context, int resource, ArrayList<Level> levels) {
        super(context, resource, levels);
        id = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Level level = getItem(position);
        if (convertView == null) {
            View view = LayoutInflater.from(getContext()).inflate(id, parent, false);
            TextView levelListName = view.findViewById(R.id.level_list_name);
            TextView levelListNote = view.findViewById(R.id.level_list_note);
            levelListName.setText(level.getName());
            levelListNote.setText(level.getNote());
            return view;
        }
        TextView levelListName = convertView.findViewById(R.id.level_list_name);
        TextView levelListNote = convertView.findViewById(R.id.level_list_note);
        levelListName.setText(level.getName());
        levelListNote.setText(level.getNote());
        return convertView;
    }
}
