package com.halloween.journote.ui.edit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
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

    private enum KeywordType{IMAGE,SEPARATOR,TITLE,LINK}

    static private String TEXT;

    private int AsyncTaskStart;
    private int AsyncTaskEnd;


    public EditTextController (Context context , EditText editText){
        this.context = context;
        this.editText = editText;
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);
        editable = editText.getEditableText();//获取EditText的文字
    }

    //TODO 公开方法：开始同步 contentPath 与 EditText
    public void startControl(String contentPath){
        System.out.println("EditTextController:Start For "+contentPath+" on "+editText);
        this.contentPath = contentPath;
        readToEditable(contentPath);
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
            StringBuilder builder = new StringBuilder(TEXT);
            builder.replace(start, end, insertString);

        }
        System.out.println("TEXT:"+TEXT);
    }


    //TODO 公开方法：根据地址插入图片 -public
    public void insertImage(String text,String path){
        int start = editText.getSelectionStart();//获取光标位置
        int end = editText.getSelectionEnd();
        insertImageWithStartAndEndWithoutListening(text,path,start,end,true);//封装，以便替换关键字时调用
    }

    //TODO 源码模式
    public void setRaw(){
        editText.removeTextChangedListener(this);
        editText.setText(TEXT);
    }

    //TODO 显示模式
    public void setDisplay(){
        convertKeywords(scanKeyword(TEXT));
    }



    //TODO 重写 TextChangeListener
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Toast.makeText(context,s+"  "+start+"   "+count,Toast.LENGTH_LONG).show();
        //System.out.println(s+"  "+start+"   "+count);
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //changeTEXT(s.toString().substring(start,start+count),start,start+before);
        TEXT=s.toString();
        //System.out.println(s+"  "+start+"   "+count+"   "+before);
    }
    @Override
    public void afterTextChanged(Editable s) {
    }

    //TODO changeTEXT：将 onTextChange 的参数解释为 TEXT 的变化
    private void changeTEXT(String changeString,int start,int end){
        if(start==0&&end==TEXT.length()){
            TEXT = changeString;
        }else if(start==0&&end!=TEXT.length()){
            TEXT = changeString+TEXT.substring(end-1);
        }else if(start!=0&&end==TEXT.length()){
            TEXT = TEXT.substring(0,start)+changeString;
        }else if(start!=0&&start<TEXT.length()&&end!=0){
            TEXT = TEXT.substring(0,start)+changeString+TEXT.substring(end);
        }else if(start>=TEXT.length()){
            TEXT = TEXT + changeString;
        }
        System.out.println(TEXT);
    }



    //TODO Insert 的辅助方法，插入内容而不引起监听
    private void insertWithoutListening(String txt){
        editText.removeTextChangedListener(this);
        insert(txt);
        editText.addTextChangedListener(this);
    }

    private void setTextWithoutListening(String txt){
        editText.removeTextChangedListener(this);
        editText.setText(txt);
        editText.addTextChangedListener(this);
    }

    //TODO InsertImage 的实际方法，根据起始和终止位置插入图片
    private void insertImageWithStartAndEnd(String discripthon,String path,int start,int end,boolean insert){
        editable = editText.getEditableText();//获取EditText的文字

        String text = "!["+discripthon+"]("+path+")";
        System.out.println("insertImageWithStartAndEnd:----Satrt:"+start+" end:"+end +" editable length: "+ editable.length());
        System.out.println("dscp:"+discripthon);
        System.out.println("path:"+path);
        //Toast.makeText(context, "]insert\npath:"+path+"\ndscp:"+discripthon, Toast.LENGTH_LONG).show();
        new BitmapWorkerTask(path,text,start,end,insert).execute();
        // 线程结束后运行 afterget
    }

    private void afterget(Bitmap originalBitmap,String path,String text,int start,int end,boolean insert){
        SpannableString ss = new SpannableString(text);
        if (originalBitmap != null) {

            if (originalBitmap == null) {
                originalBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
                originalBitmap.eraseColor(Color.parseColor("#EEEEEE")); // �����ɫ
                Canvas canvas = new Canvas(originalBitmap);
                Paint paint = new Paint();
                paint.setTextSize(50);
                paint.setColor(Color.WHITE);
                paint.setFlags(100);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawText("图片丢失", 100, 175, paint);

            }
            int vw = editText.getWidth();
            int w = originalBitmap.getWidth();
            int h = originalBitmap.getHeight();
            //System.out.println("width: " + vw);
            Bitmap bitmap;//float k=0.1f;
            float r;
            if (w != vw) {
                r = (float) vw / (float) w;//r=r*k;
                if (r > 0) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(r, r);
                    bitmap = Bitmap.createBitmap(originalBitmap, 0, 0, w, h, matrix, false);
                } else {
                    bitmap = null;
                }
            } else {
                bitmap = originalBitmap;
            }
            CenterImageSpan imageSpan = new CenterImageSpan(context, bitmap);
            mClickableSpan clickablespan = new mClickableSpan(path) {
                @Override
                public void onClick(View v) {
                }
            };
            ss.setSpan(clickablespan,0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(imageSpan, 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //System.out.println("Spannable String : " + ss);
            //Toast.makeText(context,ss,Toast.LENGTH_LONG).show();



        } else {
            System.out.println("fail to get image");
            Toast.makeText(context,"fail to get image",Toast.LENGTH_LONG).show();
        }
        if (ss != null) {
            if (start < 0 ||start >= editable.length()) {
                editable.append(ss);//选择不存在或在文末
                //System.out.println(editable.toString());
                if(insert){
                    editable.insert(end+text.length(),"\n\n");
                    editText.setSelection(end+text.length()+1);
                    System.out.println("try to add endl");
                }
            }
            else{
                editable.replace(start, end, ss);//光标所在位置插入
            }

        }
    }

    //TODO 图片加载线程
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private String path;
        private String text;
        private int start;
        private int end;
        private boolean insert;

        public BitmapWorkerTask(String path,  String text,int start,int end,boolean insert) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            this.path = path;
            this.start = start;
            this.end = end;
            this.text = text;
            this.insert = insert;
        }
        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            System.out.println("------------AsyncTask: decode in background----------");
            //data = params[0];
            Bitmap originalBitmap = BitmapFactory.decodeFile(path);
            return originalBitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap originalBitmap) {
            afterget(originalBitmap,path,text,start,end,insert);
        }
    }

    //TODO InsertImageWithStartAndEnd 的无监听版本
    private void insertImageWithStartAndEndWithoutListening(String text,String path,int start,int end,boolean insert){
        editText.removeTextChangedListener(this);
        insertImageWithStartAndEnd(text,path,start,end,insert);
        editText.addTextChangedListener(this);
    }

    //TODO onTextChanged-获取插入的内容


    //TODO 核心：扫描指定内容中的关键字
    private List<Keyword> scanKeyword(String string){

        System.out.println("EditTextController.scanKeyword---------start scan keyword");
        List<Keyword> keywordList = new ArrayList<Keyword>();
        Pattern pattern;
        Matcher matcher;
        int[] count = new int[4];

        System.out.println("To scan: length:"+string.length());

        //TODO 使用正则表达式检索关键字组 ![……](……)
        //正则表达式： "
        String ImageRegularExpression = "!\\[[^\\[]*\\]\\([^!\\[]*\\)";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(ImageRegularExpression);
        matcher = pattern.matcher(string); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.IMAGE));
            //Toast.makeText(context,matcher.group(),Toast.LENGTH_LONG).show();count[0]++;
        }

        //TODO 使用正则表达式检索关键字组 [……](……)
        //正则表达式： "
        String LinkRegularExpression = "(?<!!)\\[[^\\[]*\\]\\([^\\[]*\\)";
        pattern = Pattern.compile(LinkRegularExpression);
        matcher = pattern.matcher(string); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.LINK));count[1]++;}


        //TODO 使用正则表达式检索关键字组 ---
        String separatorRegularExpression = "\\n----*\\n";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(separatorRegularExpression);
        matcher = pattern.matcher(string); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.SEPARATOR));count[2]++;}

        //TODO 使用正则表达式检索关键字组 #
        String titleOneRegularExpression = "\\n#{1,6}.+";
        //注意： ![image for "![……](……)"](img633619.jpg)  将返回   ![……](……)"](img633619.jpg)
        pattern = Pattern.compile(titleOneRegularExpression);
        matcher = pattern.matcher(string); // 获取 matcher 对象
        while(matcher.find()) { keywordList.add(new Keyword(matcher.start(),matcher.end(),matcher.group(),KeywordType.TITLE));count[3]++;}


        System.out.println("scan get: "+keywordList.size()+"kaywords,image:"+count[0]+" link:"+count[1]+" separator:"+count[2]+" title:"+count[3]);
        System.out.println("finish scan keyword");
        return keywordList;
    }

    //TODO 核心：进行关键字转换
    private void convertKeywords(List<Keyword> keywordList){
        System.out.println("EditTextController.convertKeywords-------------start to convert Keyword");
        for (Keyword keyword : keywordList) { //使用foreach遍历List
            String string = keyword.getOriginalText();
            System.out.println("convert: index"+keyword.getIndexStart()+"~"+keyword.getIndexEnd()+", type:"+keyword.keywordType);
            //System.out.println("origional"+keyword.getOriginalText());
            switch (keyword.keywordType){
                case IMAGE:


                    if (string != null) {
                        insertImageWithStartAndEndWithoutListening(
                                string.substring(string.indexOf("![") + 2, string.indexOf("](")),
                                string.substring(string.indexOf("](") + 2, string.lastIndexOf(")")),
                                keyword.getIndexStart(),
                                keyword.getIndexEnd(),false);
                    } else {
                        Toast.makeText(context, "String==null", Toast.LENGTH_LONG).show();
                    }


                    break;
                case TITLE:
                    break;
                case SEPARATOR:
                    //insertImageWithStartAndEnd(
                    //"---",
                    //"separator",
                    //keyword.getIndexStart(),
                    //keyword.getIndexEnd());
                    break;
                case LINK:
                    break;
            }
            //Toast.makeText(context,editText.getText(),Toast.LENGTH_LONG).show(); 可以显示图片
        }
        System.out.println("finish converting Keyword");
    }

    //TODO 核心：替换指定关键字为Spannable
    private void convertKeyword(Keyword keyword){};

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
        editText.removeTextChangedListener(this);
        if (start < 0 || start >= editable.length()) {
            editable.append(insertSpannable);//光标不存在或在文末时，直接插入至文末
        } else {
            editable.replace(start, end, insertSpannable);//光标选中了范围时，在光标所在位置插入文字
        }
        editText.addTextChangedListener(this);
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

    //TODO 公开方法：SaveAll-全部保存,在此开启线程
    public void saveAll(){
        System.out.println("saveAll: new SaveTask to save "+ TEXT);
        new SaveTask(TEXT,contentPath).execute();
    }

    //TODO 线程：存储内容
    class SaveTask extends AsyncTask<Void,Integer,Boolean> {
        private String saveContentPath;
        private String content;
        public SaveTask(String content,String saveContentPath){
            this.saveContentPath = saveContentPath;
            this.content = content;
        }

        @Override
        protected void onPreExecute(){
            //System.out.println("SaveTask: onPreExecute");
        }
        @Override
        protected Boolean doInBackground(Void... params){

            System.out.println("save in background" );

            File JournoteFolder =new File(Environment.getExternalStorageDirectory(),"Journote");
            while(!JournoteFolder.exists()){ JournoteFolder.mkdir(); }
            File Journote_NoteFolder =new File(JournoteFolder,"Notes");
            while(!Journote_NoteFolder.exists()){ Journote_NoteFolder.mkdir(); }

            FileOutputStream out = null;
            BufferedWriter writer = null;
            try{
                out = new FileOutputStream(Journote_NoteFolder+"/"+contentPath);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                writer.write(content.replaceAll("\\n", ";n"));
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
            System.out.println("-----------finish save-----------");
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            //downloadStatus.setText("下载进度："+values[0]+"%");
            //progress.setProgress(values[0]) ;
        }
        @Override
        protected void onPostExecute(Boolean result){
            //System.out.println("SaveTask: onPostExecute");
            //Toast.makeText(MainActivity.this,"Pose Execute",Toast.LENGTH_SHORT).show();
        }

    }

    //TODO 从 文件路径 读取至 editable
    private void readToEditable(String contentPath){
        System.out.println("read: new ReadTask");
        new ReadTask(contentPath).execute();
    }

    //TODO 线程：读取内容
    class ReadTask extends AsyncTask<Void,Integer,Boolean> {
        private String saveContentPath;
        private String stringGet;
        public ReadTask(String saveContentPath){
            this.saveContentPath = saveContentPath;
        }

        @Override
        protected void onPreExecute(){
            System.out.println("ReadTask: onPreExecute");
            //downloadStatus.setText("开始下载 ...");
            //progress.setProgress(0);
        }
        @Override
        protected Boolean doInBackground(Void... params){

            System.out.println("----------------read in background---------------------" );

            File JournoteFolder =new File(Environment.getExternalStorageDirectory(),"Journote");
            while(!JournoteFolder.exists()){ JournoteFolder.mkdir(); }
            File Journote_NoteFolder =new File(JournoteFolder,"Notes");
            while(!Journote_NoteFolder.exists()){ Journote_NoteFolder.mkdir(); }

            FileInputStream in = null;
            BufferedReader reader = null;
            StringBuilder content = new StringBuilder();
            try{
                in = new FileInputStream(Journote_NoteFolder+"/"+saveContentPath);
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine())!=null){
                    content.append(line);
                    //content.append("\\n");
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if (reader!=null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            System.out.println(stringGet);
            stringGet = content.toString().replaceAll(";n", "\n");
            System.out.println(stringGet);
            if(stringGet!=null) TEXT = stringGet;
            publishProgress(1);
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values){
            setTextWithoutListening(TEXT);
            System.out.println("read into TEXT: "+TEXT);

        }
        @Override
        protected void onPostExecute(Boolean result){
            convertKeywords(scanKeyword(TEXT));
            //Toast.makeText(context,editText.getText(),Toast.LENGTH_LONG).show();
            System.out.println("--------------------finish read----------------------");

        }

    }
}
