package com.example.speciesrecord;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;

public class PhotoAdapter extends ArrayAdapter<Photo> {
    private final int id;
    public PhotoAdapter(Context context, int resource, ArrayList<Photo> photos) {
        super(context, resource, photos);
        id = resource;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Photo photo = getItem(position);
        if(convertView == null) {
            View view = LayoutInflater.from(getContext()).inflate(id, parent, false);
            ImageView imageView = view.findViewById(R.id.photo_list_photo);
            TextView imageListNote = view.findViewById(R.id.photo_list_note);
            imageView.setImageURI(Uri.fromFile(new File(photo.getPath())));
            imageListNote.setText(photo.getNote());
            return view;
        }
        ImageView imageView = convertView.findViewById(R.id.photo_list_photo);
        TextView imageListNote = convertView.findViewById(R.id.photo_list_note);
        imageView.setImageURI(Uri.fromFile(new File(photo.getPath())));
        imageListNote.setText(photo.getNote());
        return convertView;
    }
}
