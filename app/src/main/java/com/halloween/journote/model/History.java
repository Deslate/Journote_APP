package com.halloween.journote.model;

import java.util.List;

public class History {

    private List<Record> editRecords;

    public History (List<Record> editRecords){
        this.editRecords = editRecords;
    }

    public List<Record> getAllRecord (){
        return editRecords;
    }

    public Record getLatestRecord (){
        int size = editRecords.size();
        return editRecords.get(size-1);
    }

    public Record getInitRecord (){
        return editRecords.get(0);
    }

}
