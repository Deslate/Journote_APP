package com.halloween.journote.ui.edit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditTextController implements TextWatcher, View.OnFocusChangeListener {

    private Context context;
    private static EditText editText;
    private static Editable editable;
    private String contentPath;

    private enum KeywordType{IMAGE,SEPARATOR,TITLE}


    public EditTextController (Context context , EditText editText,String contentPath){
        this.context = context;
        this.editText = editText;
        this.contentPath = contentPath;
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);
        editable = editText.getEditableText();//获取EditText的文字
    }

    //TODO 公开方法：将String插入至Editable -public
    public static void insert(String insertString) {//insertString：将要插入的文字
        if (TextUtils.isEmpty(insertString)) return;
        int start = editText.getSelectionStart();//获取光标位置
        int end = editText.getSelectionEnd();
        if (start < 0 || start >= editable.length()) {
            editable.append(insertString);//选择不存在或在文末
        } else {
            editable.replace(start, end, insertString);//光标所在位置插入文字
        }
    }

    //TODO 公开方法：根据地址插入图片 -public
    public void insertImage(String text,String path){
        Toast.makeText(context,"text："+text+"  path："+path,Toast.LENGTH_LONG).show();
        System.out.println(path);
        text = "!["+text+"]("+path+")";
        Bitmap originalBitmap= BitmapFactory.decodeFile(path);
        //System.out.println("path is : "+path);
        if(originalBitmap != null){
            SpannableString ss = new SpannableString(text);
            if 	(originalBitmap==null){
                originalBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888);
                originalBitmap.eraseColor(Color.parseColor("#EEEEEE")); // �����ɫ
                Canvas canvas = new Canvas(originalBitmap);
                Paint paint = new Paint();
                paint.setTextSize(50);
                paint.setColor(Color.WHITE);
                paint.setFlags(100);
                paint.setStyle(Paint.Style.FILL); //��������������������
                canvas.drawText("图片丢失",100,175,paint);

            }
            int w = originalBitmap.getWidth();
            int h = originalBitmap.getHeight();
            int vw = editText.getWidth();
            System.out.println("width: "+vw);
            Bitmap bitmap;//float k=0.1f;
            float r;
            if (w!=vw){
                r=(float)vw/(float)w;//r=r*k;
                if(r>0){
                    Matrix matrix=new Matrix();
                    matrix.postScale(r,r);
                    bitmap=Bitmap.createBitmap(originalBitmap,0,0,w,h,matrix,false);
                }else{
                    bitmap=null;
                }
            }else{ bitmap=originalBitmap; }
            CenterImageSpan imageSpan = new CenterImageSpan(context, bitmap);
            mClickableSpan clickablespan=new mClickableSpan(path){
                @Override
                public void onClick(View v){
                    //Intent intent=new Intent("com.des.butler.ACTION_START");
                    //intent.addCategory("com.des.butler.PICTURE");
                    //intent.putExtra("ID", AApicId);
                    //context.startActivity(intent);
                    //saveToDataBase();
                    //finish();
                }
            };
            ss.setSpan(clickablespan,0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(imageSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            System.out.println("ss is : "+ss);
            editable.append("\n");
            if (TextUtils.isEmpty(ss)) return;
            int start = editText.getSelectionStart();//获取光标位置
            int end = editText.getSelectionEnd();
            if (start < 0 || start >= editable.length()) {
                editable.append(ss);//选择不存在或在文末
            } else {
                editable.replace(start, end, ss);//光标所在位置插入文字
            }

            editable.append("\n");
            editable.append("\n");
            Toast.makeText(context,ss,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context,"BitmapFactory.decodeFile(path) ==> null",Toast.LENGTH_LONG).show();
            System.out.println("is null ");
        }
    }

    //TODO 重写 TextChangeListener
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        System.out.println("onTextChange");
        saveAll();
        if(before==0&&count>=1){
            //添加内容的情况
            getInserted(s,start,count);

        }else if(before>=1&&count==0){
            //删除部分内容的情况
        }else if(before>=1&&count>=1){
            //替换部分内容的情况
        }
    }
    @Override
    public void afterTextChanged(Editable s) {
    }



    //TODO Insert 的辅助方法，插入内容而不引起监听
    private void insertWithoutListening(String txt){
        editText.removeTextChangedListener(this);
        insert(txt);
        editText.addTextChangedListener(this);
    }

    //TODO onTextChanged-获取插入的内容
    public String getInserted(CharSequence s, int start,int count){
        String inserted = null;
        String afterChange = s.toString();
        inserted = afterChange.substring(start,start+count);
        return inserted;
    }

    //TODO 核心：将指定内容进行关键字转换

    //TODO 核心：扫描指定内容中的关键字
    private List<Keyword> scanKeyword(Editable editable){

        List<Keyword> keywordList = new ArrayList<Keyword>();
        Pattern pattern;
        Matcher matcher;

        //TODO 使用正则表达式检索关键字组 ![……](……)
        //正则表达式： "
        String ImageRegularExpression = "!\\[[^!\\[]*\\]\\([^!\\[]*\\)";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(ImageRegularExpression);
        matcher = pattern.matcher(editable); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.IMAGE)); }

        //TODO 使用正则表达式检索关键字组 [……](……)
        //正则表达式： "
        String LinkRegularExpression = "(?<!!)\\[[^\\[]*\\]\\([^\\[]*\\)";
        pattern = Pattern.compile(ImageRegularExpression);
        matcher = pattern.matcher(editable); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.IMAGE)); }

        //TODO 使用正则表达式检索关键字组 ---
        String separatorRegularExpression = "\\n----*\\n";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(ImageRegularExpression);
        matcher = pattern.matcher(editable); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.SEPARATOR)); }

        //TODO 使用正则表达式检索关键字组 #
        String titleOneRegularExpression = "\\n{1,6}.+";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(ImageRegularExpression);
        matcher = pattern.matcher(editable); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.TITLE)); }

        return keywordList;
    }

    //TODO 核心：替换指定关键字为Spannable
    private Editable convertKeyword(Editable editable,List<Keyword> keywords){return editable;};

    //TODO 核心类：关键字
    private class Keyword{
        private int indexStart;
        private int indexEnd;
        private String originalText;
        private KeywordType keywordType;
        public Keyword(int indexStart,int indexEnd,String originalText,KeywordType keywordType){
            this.indexStart = indexStart;
            this.indexEnd = indexEnd;
            this.originalText = originalText;
            this.keywordType = keywordType;
        }
        public int getIndexStart() { return indexStart; }
        public int getIndexEnd() { return indexEnd; }
        public String getOriginalText() { return originalText; }
        public KeywordType getKeywordType() { return keywordType; }
    }

    //TODO 私有方法：将 Spannable String 插入至 Editable
    public void insert(SpannableString insertSpannable) {//insertString：将要插入的Spannable
        if (TextUtils.isEmpty(insertSpannable)) return;
        int start = editText.getSelectionStart();//获取光标位置
        int end = editText.getSelectionEnd();
        Editable editable = editText.getEditableText();//获取 EditText 的 Editable
        if (start < 0 || start >= editable.length()) {
            editable.append(insertSpannable);//光标不存在或在文末时，直接插入至文末
        } else {
            editable.replace(start, end, insertSpannable);//光标选中了范围时，在光标所在位置插入文字
        }
    }

    //一个有趣的移动光标方法
    private void moveEditViewCursor(boolean isMoveLeft) {
        int index = editText.getSelectionStart();
        if (isMoveLeft) {
            if (index <= 0) return;
            editText.setSelection(index - 1);
        } else {
            Editable edit = editText.getEditableText();//获取EditText的文字
            if (index >= edit.length()) return;
            editText.setSelection(index + 1);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            // 获得焦点
        } else {
            // 失去焦点
        }
    }
    public class mClickableSpan extends ClickableSpan {
        private String path;
        public mClickableSpan(String path){
            this.path = path;
        }
        @Override
        public void onClick(View widget) {
            // TODO 设置点击事件
        }

    }

    //TODO SaveAll-全部保存
    public void saveAll(){
        System.out.println("saveAll: new SaveTask");
        new SaveTask(editable,contentPath).execute();
    }

    //TODO 开启线程存储内容
    class SaveTask extends AsyncTask<Void,Integer,Boolean> {
        private String saveContentPath;
        private Editable saveEditable;
        public SaveTask(Editable saveEditable,String saveContentPath){
            this.saveContentPath = contentPath;
            this.saveEditable = editable;
        }

        @Override
        protected void onPreExecute(){
            System.out.println("SaveTask: onPreExecute");
            //downloadStatus.setText("开始下载 ...");
            //progress.setProgress(0);
        }
        @Override
        protected Boolean doInBackground(Void... params){

            System.out.println("save in background" );

            File JournoteFolder =new File(Environment.getExternalStorageDirectory(),"Journote");
            while(!JournoteFolder.exists()){ JournoteFolder.mkdir(); }
            File Journote_NoteFolder =new File(JournoteFolder,"Notes");
            while(!Journote_NoteFolder.exists()){ Journote_NoteFolder.mkdir(); }

            String allData = editable.toString();

            FileOutputStream out = null;
            BufferedWriter writer = null;
            try{
                out = new FileOutputStream(Journote_NoteFolder+"/data.journote");
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(allData);
            }catch (IOException e){
                e.printStackTrace();
            }finally{
                try{
                    if(writer != null){
                        writer.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            System.out.println("finish save "+allData);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            //downloadStatus.setText("下载进度："+values[0]+"%");
            //progress.setProgress(values[0]) ;
        }
        @Override
        protected void onPostExecute(Boolean result){
            System.out.println("SaveTask: onPostExecute");
            //Toast.makeText(MainActivity.this,"Pose Execute",Toast.LENGTH_SHORT).show();
        }

    }
}
