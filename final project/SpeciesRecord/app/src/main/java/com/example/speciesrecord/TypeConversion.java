package com.example.speciesrecord;

import java.util.ArrayList;

public class TypeConversion {
    public static String[] arrayListToStrings(ArrayList<String> arrayList) {
        int length = arrayList.size();
        String[] strings = new String[length];
        for(int i = 0; i < length; i++) {
            strings[i] = arrayList.get(i);
        }
        return strings;
    }

    public static String stringsToString(String[] strings, String split) {
        if(strings != null) {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0; i < strings.length; i++) {
                stringBuilder.append(strings[i]);
                if(i < strings.length - 1)
                    stringBuilder.append(split);
            }
            return stringBuilder.toString();
        }
        return null;
    }
}
