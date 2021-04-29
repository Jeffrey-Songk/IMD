package com.example.speciesrecord;

import java.sql.Time;
import java.util.ArrayList;

public class Level {
    private String name;
    private String note;
    protected ArrayList<Level> next;
    protected Level previous;

    public Level() {
        this.next = new ArrayList<>();
        this.previous = null;
    }
    public Level(String name, String note) {
        this.name = name;
        this.note = note;
        this.next = new ArrayList<>();
        this.previous = null;
    }
}

class species extends Level {
    private Time time;
    private String address;
    private int imagesNum;
    private String[] imagesNote;

    public species(Time time, String address, int imagesNum, String[] imagesNote) {
        this.time = time;
        this.address = address;
        this.imagesNum = imagesNum;
        this.imagesNote = imagesNote;
    }
}