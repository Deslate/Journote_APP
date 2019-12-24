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

    List<Item> tempList;
    long[] tempScan;

    //TODO 构造方法只需要传入 Context
    public DatabaseManager(Context context){
        this.context = context;
        databaseOpenHelper=new DatabaseOpenHelper(context);
        database = databaseOpenHelper.getWritableDatabase();
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
        System.out.println("******************Item added****************");
        Toast.makeText(context,"added"+contentPath,Toast.LENGTH_LONG).show();
    }

    //TODO 删除某个 Item 并记录 Record
    public void deleteItem(Item itemToDelete){
        database.delete("Item", "contentPath=?", new String[]{""+itemToDelete.getContentPath()});
    }

    //TODO 修改某个 Item 并记录 Record
    public void updateItem(Item itemUpdated){
        List<ItemString> itemStringList = searchItemWithContentPath(itemUpdated.getContentPath());
        if(itemStringList.size()==1){
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

            Record record = itemUpdated.getHistory().getLatestRecord();
            System.out.println(record.toString());
            if(null==record) {
                record = new Record(new Date(),"unknown","no-record-fix",itemUpdated.getContentPath(),getCurrentRecordCount()+1);
            }
            addRecord(record);
        }
    }

    //TODO 根据路径ID获取 Item 对象
    public Item getItem(String contentPath){
        Item item = null;
        History history;
        List<Label> labels = new ArrayList<Label>();
        List<ItemAddition> itemAdditions = new ArrayList<ItemAddition>();

        List<ItemString> itemStringList = searchItemWithContentPath(contentPath);
        if(itemStringList.size()>=1){
            ItemString itemString = itemStringList.get(0);
            String title = itemString.getTitle();
            //处理 List<Record>
            List<Record> recordList = searchRecordWithContentPath(contentPath);
            history = new History(recordList);
            //处理 List<Label>
            String labelString = itemString.getLabelsString();
            String[] labelStr = labelString.substring(1,labelString.length()-1).split(",");
            for(int i=0;i<=labelStr.length-1;i++){labels.add(new Label(labelStr[i]));}
            //处理 List<ItemAddition>
            String itemAdditionString = itemString.getItemAdditionsString();
            String[] additionStr = itemAdditionString.substring(1,itemAdditionString.length()-1).split(",");
            for(int i=0;i<=additionStr.length-1;i++){itemAdditions.add(new ItemAddition(additionStr[i]));}

            item = new Item (title,contentPath,labels,history,itemAdditions);
            System.out.println("Build:"+item.toString());
        }
        return item;
    }

    //TODO 获取最新的 N 条数据
    public List<Item> getTop(int request){
        List<Item> response = new ArrayList<Item>();
        Boolean have = true ;
        long recordNumber = getCurrentItemCount();
        if(recordNumber<request){request=(int)recordNumber;};
        for(int i=0;i<request;i++){
            System.out.println("searchItemWithId"+(recordNumber-i-1)+ " when CurrentItemCount "+(int)getCurrentItemCount());
            List<ItemString> strings = searchItemWithId(recordNumber-i);
            response.add(getItem(strings.get(0).getContentPath()));
        }
        return response;
    }





    //TODO 获取当前记录总数
    public long getCurrentRecordCount(){
        Cursor cursor= database.rawQuery("select count(1) from Record",null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }
    public long getCurrentItemCount(){
        Cursor cursor= database.rawQuery("select count(1) from Item",null);
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
        String contentPath = record.getContentPath();
        //bug啊，千招万招终于找到你！这里还要添加contentPath嘞！
        ContentValues values =new ContentValues();
        values.put("recordNumber",recordNumber);
        values.put("editDate",editDate);
        values.put("editorUserId",editorUserId);
        values.put("description",description);
        values.put("contentPath",contentPath);
        System.out.println("saved: record ==> ID:"+recordNumber+"");
        database.insert("Record", null, values);
        values.clear();
    }

    private List<ItemString> searchItemWithContentPath(String contentPath){
        List<ItemString> itemStringList = new ArrayList<ItemString>();
        ItemString itemString;
        Cursor itemCursor=database.query("Item",null, "contentPath=?", new String[]{contentPath}, null, null, null);
        System.out.println("start To Search: contentPath="+ contentPath);
        if(itemCursor.moveToFirst()){
            do{
                String title = itemCursor.getString(itemCursor.getColumnIndex("title"));
                String labelsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
                String itemAdditionsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
                itemString = new ItemString(title,contentPath,labelsString,itemAdditionsString);
                itemStringList.add(itemString);
                System.out.println("find item  where contrntPath="+contentPath);
            }while(itemCursor.moveToNext());
        }
        itemCursor.close();
        itemString = null;
        return itemStringList;
    }
    private List<ItemString> searchItemWithId(long id){
        List<ItemString> itemStringList = new ArrayList<ItemString>();
        ItemString itemString;
        Cursor itemCursor=database.query("Item",null, "id=?", new String[]{""+id}, null, null, null);
        System.out.println("start To Search: id="+ id);
        if(itemCursor.moveToFirst()){
            do{
                String title = itemCursor.getString(itemCursor.getColumnIndex("title"));
                String labelsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
                String itemAdditionsString = itemCursor.getString(itemCursor.getColumnIndex("labels"));
                String contentPath = itemCursor.getString(itemCursor.getColumnIndex("contentPath"));
                System.out.println(contentPath);
                itemString = new ItemString(title,contentPath,labelsString,itemAdditionsString);
                itemStringList.add(itemString);
                System.out.println("find item where Id="+id);
            }while(itemCursor.moveToNext());
        }
        itemCursor.close();
        itemString = null;
        return itemStringList;
    }

    private List<Record> searchRecordWithContentPath(String contentPath){
        //List<ItemString> itemStringList = new ArrayList<ItemString>();
        List<Record> recordList = new ArrayList<Record>();
        Record record;
        Cursor cursor=database.query("Record",null, "contentPath=?", new String[]{contentPath}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                long recordNumber = cursor.getLong(cursor.getColumnIndex("recordNumber"));
                String editDateString = cursor.getString(cursor.getColumnIndex("editDate"));
                String editorUserId = cursor.getString(cursor.getColumnIndex("editorUserId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                Date editDate = new Date();
                try {
                    DateFormat format1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);//"Fri Feb 22 20:22:00 CST 2019"字符串的转换
                    editDate = format1.parse(editDateString);
                }catch (ParseException e){ e.printStackTrace(); }
                record = new Record(editDate,editorUserId,description,contentPath,recordNumber);
                recordList.add(record);
            }while(cursor.moveToNext());
        }
        cursor.close();
        record = null;
        return recordList;
    }

    private List<Record> searchRecordWithNumber(Long recordNumber){
        //List<ItemString> itemStringList = new ArrayList<ItemString>();
        List<Record> recordList = new ArrayList<Record>();
        Record record;
        Cursor cursor=database.query("Record",null, "recordNumber=?", new String[]{""+recordNumber}, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String contentPath = cursor.getString(cursor.getColumnIndex("contentPath"));
                String editDateString = cursor.getString(cursor.getColumnIndex("editDate"));
                String editorUserId = cursor.getString(cursor.getColumnIndex("editorUserId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                Date editDate = new Date();
                System.out.println("Searched: record ==> ID:"+recordNumber+" content path: "+contentPath);
                try {
                    DateFormat format1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);//"Fri Feb 22 20:22:00 CST 2019"字符串的转换
                    editDate = format1.parse(editDateString);
                }catch (ParseException e){ e.printStackTrace(); }
                record = new Record(editDate,editorUserId,description,contentPath,recordNumber);
                recordList.add(record);
            }while(cursor.moveToNext());
        }
        cursor.close();
        record = null;
        return recordList;
    }


    private class ItemString{
        private String title;
        private String contentPath;
        private String labelsString;
        private String itemAdditionsString;
        public ItemString (String title,String contentPath,String labelsString,String itemAdditionsString){
            this.title = title;
            this.labelsString = labelsString;
            this.itemAdditionsString = itemAdditionsString;
            this.contentPath = contentPath;
        }

        public String getTitle() {
            return title;
        }

        public String getLabelsString() {
            return labelsString;
        }

        public String getItemAdditionsString() {
            return itemAdditionsString;
        }

        public String getContentPath() {
            return contentPath;
        }
    }

}
