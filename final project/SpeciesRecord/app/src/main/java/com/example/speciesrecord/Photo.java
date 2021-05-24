package com.example.speciesrecord;

import java.util.ArrayList;

public class Photo {
    private String path;
    private String note;
    public Photo(String path, String note) {
        this.note = note;
        this.path = path;
    }
    public String getPath() {return this.path;}
    public String getNote() {return this.note;}
    public static ArrayList<Photo> getPhotos(ArrayList<String> paths, ArrayList<String> notes) {
        if(paths == null || notes == null) {
            return null;
        }
        ArrayList<Photo> photos = new ArrayList<>();
        for(int i = 0; i < paths.size(); i++) {
            photos.add(new Photo(paths.get(i), notes.get(i)));
        }
        return photos;
    }
}
