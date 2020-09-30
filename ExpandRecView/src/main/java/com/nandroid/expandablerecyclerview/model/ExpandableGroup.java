package com.nandroid.expandablerecyclerview.model;

import java.util.ArrayList;

public class ExpandableGroup<T,U> {

    public T groupItem;
    public ArrayList<U> childItems;
    public boolean needsOpen = false;

    public ExpandableGroup(T groupItem, ArrayList<U> childItems){
        this.groupItem = groupItem;
        this.childItems = childItems;
    }

    public ExpandableGroup(T groupItem, ArrayList<U> childItems,boolean needsOpen){
        this.groupItem = groupItem;
        this.childItems = childItems;
        this.needsOpen = needsOpen;
    }

}
