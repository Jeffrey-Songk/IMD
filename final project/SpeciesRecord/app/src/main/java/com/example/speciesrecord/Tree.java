package com.example.speciesrecord;

import java.util.ArrayList;

public class Tree {
    protected Level root;
    public Tree() {
        root = new Level();
    }
    //设置从属关系
    public void setSubordination(Level higher, Level lower) {
        if(higher.previous == null) {
            higher.previous = this.root;
            this.root.next.add(higher);
        }
        higher.next.add(lower);
        lower.previous = higher;
    }
}

//class TreeHandle {
//    Tree tree;
////    private String[] classification;
////    //为classification传值
////    public void setClassification() {
////
////    }
//    //设置从属关系
//    public void setSubordination(Level higher, Level lower) {
//        if(higher.previous == null) {
//            higher.previous = this.tree.root;
//            this.tree.root.next.add(higher);
//        }
//        higher.next.add(lower);
//        lower.previous = higher;
//    }
//    public void addNewSpecies() {
//
//    }
//}