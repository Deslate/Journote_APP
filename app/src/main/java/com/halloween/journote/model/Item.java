package com.halloween.journote.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Item {

    private String title;
    private String contentPath;
    private List<Label> labels;
    private History history;
    private List<ItemAddition> itemAdditions;

    public Item (String title,String contentPath,List<Label> labels,History history,List<ItemAddition> itemAdditions){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = labels;
        this.history = history;
        this.itemAdditions = itemAdditions;
    }

    public Item (String title, String contentPath, List<Label> labels, Date createDate, String editorUserId, List<ItemAddition> itemAdditions, Context context){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = labels;
        this.history = new History(new ArrayList<Record>(Arrays.asList(new Record(createDate,editorUserId,"Create",contentPath,context))));
        this.itemAdditions = itemAdditions;
        //实例化Record，构成Record数组，放入History，赋值
    }
    public Item (String title, String contentPath, Date createDate, String editorUserId, Context context){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = new ArrayList<Label>();//没有标签时，标签数组长度为零
        this.history = new History(new ArrayList<Record>(Arrays.asList(new Record(createDate,editorUserId,"Create",contentPath,context))));
        this.itemAdditions = new ArrayList<ItemAddition>();//没有附加信息，附加信息数组长度为零
    }
    public Item (String title, String contentPath, Date createDate, String editorUserId, long recordNumber){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = new ArrayList<Label>();//没有标签时，标签数组长度为零
        this.history = new History(new ArrayList<Record>(Arrays.asList(new Record(createDate,editorUserId,"Create",contentPath,recordNumber))));
        this.itemAdditions = new ArrayList<ItemAddition>();//没有附加信息，附加信息数组长度为零
    }

    public String getTitle(){
        return title;
    }
    public String getContentPath(){
        return contentPath;
    }
    public List<Label> getLabelList(){
        return labels;
    }
    public History getHistory(){
        return history;
    }
    public List<ItemAddition> getItemAdditionList(){return itemAdditions;}
    public Date getCreateDate(){
        return history.getInitRecord().getEditDate();
    }
    public void addLable(Label newLabel){
        labels.add(newLabel);
    }
    public void addItemAddition(ItemAddition newItemAddition){
        itemAdditions.add(newItemAddition);
    }
    public String getItemAdditionListAsString(){
        String output = "[";
        int size = itemAdditions.size();
        for(int i=0;i<size;i++){
            output = output+itemAdditions.get(i).toString();
            if(i==size-1){ output = output+"]";}else{output = output+",";}
        }
        return output;
    }
    public String getLabelListAsString(){
        String output = "[";
        int size = labels.size();
        for(int i=0;i<size;i++){
            output = output+labels.get(i).toString();
            if(i!=size-1){ output = output+",";}
        }
        output = output+"]";
        return output;
    }
}
