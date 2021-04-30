package com.example.speciesrecord;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LevelDescriptionAdapter extends RecyclerView.Adapter<LevelDescriptionAdapter.ViewHolder> {
    private String[] mLevelDescriptions;

    public LevelDescriptionAdapter() {
        mLevelDescriptions = new String[]{"界", "门", "纲", "目", "科", "属"};
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout levelContainer;
        TextView levelDescription;
        EditText levelName;
        EditText levelNote;

        public ViewHolder(View view) {
            super(view);
            levelContainer = (LinearLayout) view.findViewById(R.id.level_container);
            levelDescription = (TextView) view.findViewById(R.id.level_description);
            levelName = (EditText) view.findViewById(R.id.level_name);
            levelNote = (EditText) view.findViewById(R.id.level_note);
            //不为空时，出现备注文本框
            levelName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!levelName.getText().toString().equals("")) {
                        levelNote.setVisibility(View.VISIBLE);
                    } else {
                        levelNote.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.levelDescription.setText(mLevelDescriptions[position]);
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
