package com.example.speciesrecord;

import android.content.SharedPreferences;

public class SharedPreferencesOperation {
    //找到where的下一个插入点
    public static int next(String where, String name) {
        int temp = 0;
        String temp1 = MainActivity.sharedPreferences.getString(where + "_next_" + temp, null);
        while(temp1 != null) {
            if(temp1.equals(name)) {
                break;
            }
            temp++;
            temp1 = MainActivity.sharedPreferences.getString(where + "_next_" + temp, null);
        }
        return temp;
    }

    //找到值为name的键，where+i
    public static int find(String where, String name) {
        int temp = 0;
        String temp1 = MainActivity.sharedPreferences.getString(where + temp, null);
        while(!temp1.equals(name) && temp1 != null) {
            temp++;
            temp1 = MainActivity.sharedPreferences.getString(where + temp, null);
        }
        return temp;
    }

    //删除叫做where+i的记录，i为序号
    public static void delete(String where, int num) {
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        int i = num;
        String temp = MainActivity.sharedPreferences.getString(where + (i + 1), null);
        while(temp != null) {
            editor.putString(where + i, temp);
            i++;
            temp = MainActivity.sharedPreferences.getString(where + (i + 1), null);
        }
        editor.remove(where + i);
        editor.apply();
    }

    //删除叫name的以及其下的所有层次
    public static void deleteLevel(String name) {
        SharedPreferences.Editor editor = MainActivity.sharedPreferences.edit();
        int i = 0;
        String temp = MainActivity.sharedPreferences.getString("name_next_" + i, null);
        while(temp != null) {
            deleteLevel(temp);
            editor.remove("name_next_" + i);
            i++;
            temp = MainActivity.sharedPreferences.getString("name_next_" + i, null);
        }
        temp = MainActivity.sharedPreferences.getString(name + "_previous", null);
        i = find(temp + "_next_", name);
        delete(temp + "_next_", i);
        editor.remove(name);
        editor.remove(name + "_previous");
        editor.apply();
    }
}
