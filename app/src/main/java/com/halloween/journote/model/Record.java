package com.halloween.journote.model;

import android.content.Context;

import java.util.Date;

public class Record {

    private long recordNumber;
    private Date editDate;//java.util.date e.g.< Tue Jul 31 11:46:06 GMT+08:00 2018 >
    private String editorUserId;//use Email userId to identify a user
    private String contentPath;
    private String description;

    public Record(Date editDate, String editorUserId, String discription,String contentPath, Context context){
        this.recordNumber = new DatabaseManager(context).getCurrentRecordCount()+1;
        this.editDate = editDate;
        this.editorUserId = editorUserId;
        this.contentPath = contentPath;
        this.description = discription;
    }

    public Record(Date editDate, String editorUserId, String discription,String contentPath, long recordNumber){
        this.recordNumber = recordNumber;
        this.editDate = editDate;
        this.editorUserId = editorUserId;
        this.contentPath = contentPath;
        this.description = discription;
    }

    public long getRecordNumber(){
        return recordNumber;
    }
    public Date getEditDate(){
        return editDate;
    }
    public String getEditorUserId(){
        return editorUserId;
    }
    public String getContentPath() {return contentPath;}
    public String getDescription(){ return description; }
    public void setRecordNumber(int newRecordNumber){
        this.recordNumber = newRecordNumber;
    }

    @Override
    public String toString(){
        return recordNumber+"|"+editDate.toString()+"|"+editorUserId+"|"+contentPath+"|"+description;
    }

}
