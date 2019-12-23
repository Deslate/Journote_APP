package com.halloween.journote.ui.edit;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.halloween.journote.R;
import com.halloween.journote.model.DatabaseManager;
import com.halloween.journote.model.DatabaseOpenHelper;
import com.halloween.journote.model.Item;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.halloween.journote.MainActivity.actionBar;
import static com.halloween.journote.MainActivity.decor;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{


    private View mContainer;
    private EditText editTitle;
    private EditText editContent;

    private static final int IMAGE_CODE = 1;
    private static final int TAKE_PHOTO = 2;
    private static final int EDIT_PHOTO = 3;

    private EditTextController editTextController;


    private View mKeyboardTopViewTipContainer;
    private Button commitBtn;
    private Button imgInsertBtn;
    private Button fontMenuBtn;
    private Button undoBtn;
    private Button backBtn;
    private Button mainMenuBtn;

    private PopupWindow mSoftKeyboardTopPopupWindow;
    private boolean mIsSoftKeyBoardShowing = false;


    private int keyboardHeight;

    private Item item;

    private DatabaseManager database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setCustomActionBar();

        mContainer = findViewById(R.id.container);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);
        backBtn = (Button) findViewById(R.id.back_btn);

        //初始化数据库控制器
        database = new DatabaseManager(this);

        //初始化Item对象
        String contentPath = performInit();

        //设置EditText跟踪
        editTextController = new EditTextController(this,editContent,contentPath);

        //设置软键盘收放监听事件
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());


        //设置菜单栏（ActionBar）控件监听
        backBtn.setOnClickListener(this);

    }

    //TODO 设置自定义 Actionbar layout
    private void setCustomActionBar(){
        ActionBar.LayoutParams layoutParams =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View previewActionBar = LayoutInflater.from(this).inflate(R.layout.actionbar_edit, null);
        getSupportActionBar().setCustomView(previewActionBar, layoutParams);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //TODO 设置状态栏
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //TODO 初始化 Actionbar 内部控件
        mainMenuBtn = findViewById(R.id.menu_btn);
        mainMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this,view);
        popupMenu.inflate(R.menu.edit_actbar_main_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    //TODO 删除按钮 显示删除确认弹窗
                    case R.id.action_1:
                        DeleteConfirm deleteConfirm = new DeleteConfirm(EditActivity.this, R.style.dialog, "确认删除？", new DeleteConfirm.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                dialog.dismiss();
                                performDelete();
                                Toast.makeText(EditActivity.this,"已将此项删除",Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteConfirm.setTitle("提示").show();
                        Toast.makeText(EditActivity.this,"Option 1",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_2:
                        Toast.makeText(EditActivity.this,"Option 2",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_3:
                        Toast.makeText(EditActivity.this,"Option 3",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        //do nothing
                }

                return false;
            }
        });
        popupMenu.show();
    }


    @Override
    public void onClick(View v) {
        String txt = "";
        switch (v.getId()) {
            case R.id.undo_btn:
                break;
            case R.id.img_insert_btn:
                Intent getAlbum = new Intent(Intent.ACTION_PICK);//////////////////////////
                getAlbum.setType("image/*");
                startActivityForResult(getAlbum,IMAGE_CODE);
                break;
            case R.id.font_menu_btn:
                break;
            case R.id.commit_btn:
                // 点击commit，【26】【+】
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
                break;
            case R.id.back_btn:
                performQuit();
                break;
        }
    }



    //自定义类来实现 ViewTreeObserver.OnGlobalLayoutListener 接口 ⌨️

    private class KeyboardOnGlobalChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            // 获取当前页面窗口的显示范围
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int screenHeight = getScreenHeight();
            keyboardHeight = screenHeight - rect.bottom; // 输入法的高度
            boolean preShowing = mIsSoftKeyBoardShowing;
            if (Math.abs(keyboardHeight) > screenHeight / 5) {
                mIsSoftKeyBoardShowing = true; // 超过屏幕五分之一则表示弹出了输入法

                //进入编辑模式
                editTitle.setCursorVisible(true);
                editContent.setCursorVisible(true);
                showKeyboardTopPopupWindow(getScreenWidth() / 2, keyboardHeight+getNavigationBarHeight(EditActivity.this));//显示popup工具栏
                getSupportActionBar().hide();
            } else {

                //进入浏览模式
                editTitle.setCursorVisible(false);
                editContent.setCursorVisible(false);
                getSupportActionBar().show();
                //setCustomActionBar();
                if (preShowing) {
                    closePopupWindow();//隐藏popup工具栏
                }
                mIsSoftKeyBoardShowing = false;
            }
        }
    }

    //初始化 🔧 popup 工具栏
    private void showKeyboardTopPopupWindow(int x, int y) {
        System.out.println("private void showKeyboardTopPopupWindow(int x, int y)");
        if (mSoftKeyboardTopPopupWindow != null && mSoftKeyboardTopPopupWindow.isShowing()) {
            updateKeyboardTopPopupWindow(x, y); //可能是输入法切换了输入模式，高度会变化（比如切换为语音输入）
            return;
        }

        View popupView = getLayoutInflater().inflate(R.layout.tool_bar, null);
        System.out.println("View popupView = getLayoutInflater().inflate(R.layout.tool_bar, null);");

        undoBtn = (Button) popupView.findViewById(R.id.undo_btn);
        imgInsertBtn = (Button) popupView.findViewById(R.id.img_insert_btn);
        fontMenuBtn = (Button) popupView.findViewById(R.id.font_menu_btn);
        commitBtn = (Button) popupView.findViewById(R.id.commit_btn);

        mKeyboardTopViewTipContainer = popupView.findViewById(R.id.keyboard_top_view_tip_container);

        System.out.println("findViewById");

        undoBtn.setOnClickListener(this);
        imgInsertBtn.setOnClickListener(this);
        fontMenuBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

        System.out.println("setOnClickListener");

        mSoftKeyboardTopPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        System.out.println("new PopupWindow");
        mSoftKeyboardTopPopupWindow.setTouchable(true);
        mSoftKeyboardTopPopupWindow.setOutsideTouchable(false);
        mSoftKeyboardTopPopupWindow.setFocusable(false);
        mSoftKeyboardTopPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED); //解决遮盖输入法
        System.out.println(""+mSoftKeyboardTopPopupWindow+mContainer);
        mSoftKeyboardTopPopupWindow.showAtLocation(mContainer, Gravity.BOTTOM, x, y);//显示popup,注意虚拟键的处理
        System.out.println("mSoftKeyboardTopPopupWindow = new PopupWindow");
    }

    private void updateKeyboardTopPopupWindow(int x, int y) {
        if (mSoftKeyboardTopPopupWindow != null && mSoftKeyboardTopPopupWindow.isShowing()) {
            mSoftKeyboardTopPopupWindow.update(x, y, mSoftKeyboardTopPopupWindow.getWidth(), mSoftKeyboardTopPopupWindow.getHeight());
        }
    }
    private void closePopupWindow() {
        if (mSoftKeyboardTopPopupWindow != null && mSoftKeyboardTopPopupWindow.isShowing()) {
            mSoftKeyboardTopPopupWindow.dismiss();
            mSoftKeyboardTopPopupWindow = null;
            undoBtn = null;
            imgInsertBtn = null;
            fontMenuBtn = null;
            commitBtn = null;
        }
    }

    //TODO onActivityResult 回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,"emmm",Toast.LENGTH_LONG).show();
            return;
        }
        if(requestCode == IMAGE_CODE){
            try{
                //Toast.makeText(this,"haha",Toast.LENGTH_LONG).show();
                Uri originalUri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = EditActivity.this.getContentResolver().query(originalUri,proj,null,null,null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                editTextController.insertImage(path,path);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(EditActivity.this,"对不起，插入图片失败",Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == TAKE_PHOTO){
            if (resultCode==RESULT_OK){
                /*try{
                    Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    // ���ﻹҪ�洢ͼƬ��Ϣ�����ݿ⣬����bitmap��processBitmapAndInsert�������룡����
                    File Butler =new File(Environment.getExternalStorageDirectory(),"Butler");
                    if(!Butler.exists()){
                        Butler.mkdir();
                        if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "����Butler��"+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();}
                    }
                    File Gallery=new File(Butler,"Gallery");
                    if(!Gallery.exists()){
                        Gallery.mkdir();
                        if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "����Gallery��"+Butler, Toast.LENGTH_SHORT).show();}
                    }
                    java.util.Calendar now =java.util.Calendar.getInstance();
                    int Pday=now.get(java.util.Calendar.DAY_OF_MONTH);
                    int Pmonth=now.get(java.util.Calendar.MONTH)+1;
                    int Pyear=now.get(java.util.Calendar.YEAR);
                    int Phour=now.get(java.util.Calendar.HOUR_OF_DAY);
                    int Pminute=now.get(java.util.Calendar.MINUTE);
                    int Psecond=now.get(java.util.Calendar.SECOND);
                    String User=com.des.butler.MainActivity.getUserName(ItemEdit.this,com.des.butler.MainActivity.userId);
                    String fileName ="photo:"+User+"-"+Pyear+"-"+Pmonth+"-"+Pday+"-"+Phour+"-"+Pminute+"-"+Psecond+".jpg";
                    if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, fileName, Toast.LENGTH_SHORT).show();}
                    File file=new File(Gallery,fileName);
                    try{
                        FileOutputStream fos =new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                        if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "�ļ�δ�����ɹ�", Toast.LENGTH_SHORT).show();}
                    }catch(IOException e){
                        e.printStackTrace();
                        if(com.des.butler.MainActivity.DeveloperState==true){Toast.makeText(ItemEdit.this, "IO����쳣", Toast.LENGTH_SHORT).show();}
                    }
                    String path = file.getPath();
                    insertImg(path);

                }catch(FileNotFoundException e){
                    e.printStackTrace();

                }

                 */

            }
        }

    }

    public static int getNavigationBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height","dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private int getScreenHeight() {
        return  ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
    }

    private int getScreenWidth() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }

    private void performDelete(){
        database.deleteItem(item);
        item = null;
    }
    private void performQuit(){
        database.updateItem(item);
        finish();
    }
    //TODO 初始化Item对象
    private String performInit(){
        String contentPath = new DatabaseManager(this).getNewContentPathId()+".journote";
        //String contentPath = "haha";
        item = new Item("Untitled",contentPath,new Date(),"Default",this);
        database.addItem(item);
        return contentPath;
    }

}
