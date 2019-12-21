package com.halloween.journote.model;

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

    public Item (String title,String contentPath,List<Label> labels,Date createDate,String editorUserId,List<ItemAddition> itemAdditions){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = labels;
        this.history = new History(new ArrayList<Record>(Arrays.asList(new Record(0,createDate,editorUserId))));
        this.itemAdditions = itemAdditions;
        //实例化Record，构成Record数组，放入History，赋值
    }
    public Item (String title,String contentPath,Date createDate,String editorUserId){
        this.title = title;
        this.contentPath = contentPath;
        this.labels = new ArrayList<Label>();//没有标签时，标签数组长度为零
        this.history = new History(new ArrayList<Record>(Arrays.asList(new Record(0,createDate,editorUserId))));
        this.itemAdditions = new ArrayList<ItemAddition>();//没有附加信息，附加信息数组长度为零
    }

    public String getTitle(){
        return title;
    }
    public String getContentPath(){
        return contentPath;
    }
    private List<Label> getLabelList(){
        return labels;
    }
    private History getHistory(){
        return history;
    }
    public Date getCreateDate(){
        return history.getInitRecord().getEditDate();
    }
    public void addLable(Label newLabel){
        labels.add(newLabel);
    }
    public void addItemAddition(ItemAddition newItemAddition){
        itemAdditions.add(newItemAddition);
    }
}
