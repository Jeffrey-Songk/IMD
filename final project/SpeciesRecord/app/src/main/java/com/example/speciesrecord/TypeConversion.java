package com.example.speciesrecord;

import java.util.ArrayList;

public class TypeConversion {
    public static String[] stringsToArrayList(ArrayList<String> arrayList) {
        int length = arrayList.size();
        String[] strings = new String[length];
        for(int i = 0; i < length; i++) {
            strings[i] = arrayList.get(i);
        }
        return strings;
    }
}
