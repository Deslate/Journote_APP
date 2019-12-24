package com.halloween.journote.model;

import java.util.List;

public class History {

    private List<Record> editRecords;

    public History (List<Record> editRecords){
        this.editRecords = editRecords;
    }

    public void addRecord(Record record){editRecords.add(record); }

    public List<Record> getAllRecord (){
        return editRecords;
    }

    public Record getLatestRecord (){
        int size = editRecords.size();
        if(size>=1) {
            return editRecords.get(size-1);
        }else{return null;}
    }

    public Record getInitRecord (){
        return editRecords.get(0);
    }

    @Override
    public String toString(){
        String output = "";
        int size = editRecords.size();
        for(int i=0;i<size;i++){//遍历List，循环调用 Record 的 toString 方法
            output = "{"+output+editRecords.get(i).toString()+"}";
        }
        return output;
    }

}
