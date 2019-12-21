package com.halloween.journote.model;

import java.util.Date;

public class Record {

    private int recordNumber;
    private Date editDate;//java.util.date e.g.< Tue Jul 31 11:46:06 GMT+08:00 2018 >
    private String editorUserId;//use Email userId to identify a user

    public Record(int recordNumber,Date editDate,String editorUserId){
        this.editDate = editDate;
        this.editorUserId = editorUserId;
    }
    public int getRecordNumber(){
        return recordNumber;
    }
    public Date getEditDate(){
        return editDate;
    }
    public String getEditorUserId(){
        return editorUserId;
    }
    public void setRecordNumber(int newRecordNumber){
        this.recordNumber = newRecordNumber;
    }

}
