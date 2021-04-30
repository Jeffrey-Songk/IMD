package com.example.speciesrecord;

import java.util.ArrayList;
import java.util.Date;

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

class Species extends Level {
    private Date date;
    private String address;
    private int imagesNum;
    private String[] imagesNote;

    public Species(Date date, String address, int imagesNum, String[] imagesNote) {
        this.date = date;
        this.address = address;
        this.imagesNum = imagesNum;
        this.imagesNote = imagesNote;
    }
}