package com.halloween.journote.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager {

    Context context;
    private DatabaseOpenHelper databaseOpenHelper;
    private SQLiteDatabase database;
    Item tempItem;
    //TODO 构造方法只需要传入 Context
    public DatabaseManager(Context context){
        this.context = context;
        databaseOpenHelper=new DatabaseOpenHelper(context);
        database = databaseOpenHelper.getWritableDatabase();
        tempItem = new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",getCurrentRecordCount()+1);
    }

    //TODO 增添一个 Item 记录第一条 Record
    public void addItem(Item newItem){
        String title = newItem.getTitle();
        String contentPath = newItem.getContentPath();
        String labels = newItem.getLabelListAsString();
        String itemAdditions = newItem.getItemAdditionListAsString();
        ContentValues values =new ContentValues();
        values.put("title",title);
        values.put("contentPath",contentPath);
        values.put("labels",labels);
        values.put("itemAdditions",itemAdditions);
        database.insert("Item", null, values);
        values.clear();
        addRecord(newItem.getHistory().getInitRecord());
        Toast.makeText(context,"added"+contentPath,Toast.LENGTH_LONG).show();
    }

    //TODO 删除某个 Item 并记录 Record
    public void deleteItem(Item itemToDelete){

    }

    //TODO 修改某个 Item 并记录 Record
    public void updateItem(Item itemUpdated){
        List<Cursor> cursorList = searchItemWithContentPath(itemUpdated.getContentPath());
        if(cursorList.size()==1){
            String title = itemUpdated.getTitle();
            String contentPath = itemUpdated.getContentPath();
            String labels = itemUpdated.getLabelListAsString();
            String itemAdditions = itemUpdated.getItemAdditionListAsString();
            ContentValues values =new ContentValues();
            values.put("title",title);
            values.put("labels",labels);
            values.put("itemAdditions",itemAdditions);
            database.update("Item", values, "contentPath=?", new String[]{contentPath});
            values.clear();
            addRecord(itemUpdated.getHistory().getLatestRecord());
        }
    }

    //TODO 根据路径ID获取 Item 对象
    public Item getItem(String contentPath){
        Item item = null;
        History history;
        List<Label> labels = new ArrayList<Label>();
        List<ItemAddition> itemAdditions = new ArrayList<ItemAddition>();

        List<Cursor> itemCursorList = searchItemWithContentPath(contentPath);
        if(itemCursorList.size()==1){
            Cursor itemCursor = itemCursorList.get(0);
            String title = itemCursor.getString(itemCursor.getColumnIndex("title"));
            String labelsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
            String itemAdditionsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
            List<Cursor> recordCursorList = searchItemWithContentPath(contentPath);
            int size = recordCursorList.size();
            List<Record> recordList = new ArrayList<Record>();
            for(int i=0;i<size;i++){
                Cursor cursor = recordCursorList.get(i);
                long recordNumber = cursor.getLong(cursor.getColumnIndex("recordNumber"));
                String editDateString = cursor.getString(cursor.getColumnIndex("editDate"));
                String editorUserId = cursor.getString(cursor.getColumnIndex("editorUserId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                Date editDate = new Date();
                try {
                    DateFormat format1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);//"Fri Feb 22 20:22:00 CST 2019"字符串的转换
                    editDate = format1.parse(editDateString);
                }catch (ParseException e){ e.printStackTrace(); }
                Record record = new Record(editDate,editorUserId,description,contentPath,recordNumber);
                recordList.add(record);
                record = null;
            }
            history = new History(recordList);
            //处理 List<Label>
            String[] labelStr = labelsString.substring(1,labelsString.length()-1).split(",");
            for(int i=0;i<=labelStr.length-1;i++){labels.add(new Label(labelStr[i]));}
            //处理 List<ItemAddition>
            String[] additionStr = itemAdditionsString.substring(1,itemAdditionsString.length()-1).split(",");
            for(int i=0;i<=additionStr.length-1;i++){itemAdditions.add(new ItemAddition(additionStr[i]));}

            item = new Item (title,contentPath,labels,history,itemAdditions);
        }
        return item;
    }





    //TODO 获取当前记录总数
    public long getCurrentRecordCount(){
        Cursor cursor= database.rawQuery("select count(1) from Record",null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }

    //TODO 新增一个标签 记录
    public void addLabel(Label newLabel){

    }

    //TODO 删除一个标签 记录
    public void deleteLabel(Label labelToDelete){

    }

    //TODO 修改一个标签 记录
    public void updateLabel(Label labelUpdated){

    }

    //TODO 更新一条设置
    public void updateSettings(String setting,String value){

    }

    //TODO 获取最新的可用路径名ID
    public String getNewContentPathId() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        int numberInSec = 1;
        String contentPathId = format.format(date);
        Boolean notEnd = true;
        while (notEnd) {
            notEnd = false;
            Cursor cursor = database.query("Item", null, "contentPath=?", new String[]{contentPathId + numberInSec}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    notEnd = true;
                    numberInSec += 1;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return contentPathId+numberInSec;
    }

    private void addRecord(Record record){
        long recordNumber = record.getRecordNumber();
        String editDate = record.getEditDate().toString();
        String editorUserId = record.getEditorUserId();
        String description = record.getDescription();
        ContentValues values =new ContentValues();
        values.put("recordNumber",recordNumber);
        values.put("editDate",editDate);
        values.put("editorUserId",editorUserId);
        values.put("description",description);
        database.insert("Record", null, values);
        values.clear();
    }

    private List<Cursor> searchItemWithContentPath(String contentPath){
        List<Cursor> cursorList = new ArrayList<Cursor>();
        Cursor cursor=database.query("Item",null, "contentPath=?", new String[]{contentPath}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                cursorList.add(cursor);
            }while(cursor.moveToNext());
        }
        return cursorList;
    }

    private List<ItemString> searchRecordWithContentPath(String contentPath){
        List<ItemString> itemStringList = new ArrayList<ItemString>();
        Cursor cursor=database.query("Record",null, "contentPath=?", new String[]{contentPath}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                new ItemString();

                cursor.close();
            }while(cursor.moveToNext());
        }
        return itemStringList;
    }

    private class ItemString{

    }

}
