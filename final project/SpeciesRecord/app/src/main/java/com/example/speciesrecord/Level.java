package com.example.speciesrecord;

import java.util.ArrayList;
import java.util.Date;

public class Level {
    private String name;
    private String note;

    public Level(String name, String note) {
        this.name = name;
        this.note = note;
    }

    public String getName() {return name;}
    public String getNote() {return note;}
    public static ArrayList<Level> getLevels(ArrayList<String> names, ArrayList<String> notes) {
        if(names == null || notes == null) {
            return null;
        }
        ArrayList<Level> levels = new ArrayList<>();
        for(int i = 0; i < names.size(); i++) {
            levels.add(new Level(names.get(i), notes.get(i)));
        }
        return levels;
    }
}
