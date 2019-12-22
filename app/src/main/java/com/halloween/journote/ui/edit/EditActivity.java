package com.halloween.journote.ui.edit;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.halloween.journote.R;

public class EditActivity extends AppCompatActivity implements View.OnClickListener{


    private View mContainer;
    private EditText editTitle;
    private EditText editContent;


    private View mKeyboardTopViewTipContainer;
    private Button commitBtn;
    private Button imgInsertBtn;
    private Button fontMenuBtn;
    private Button undoBtn;

    private PopupWindow mSoftKeyboardTopPopupWindow;
    private boolean mIsSoftKeyBoardShowing = false;


    private int keyboardHeight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        mContainer = findViewById(R.id.container);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);

        //设置软键盘收放监听事件
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());

        //设置EditText控制
        new EditTextController(this,editContent);

    }


    @Override
    public void onClick(View v) {
        String txt = "";
        switch (v.getId()) {
            case R.id.undo_btn:
                break;
            case R.id.img_insert_btn:
                break;
            case R.id.font_menu_btn:
                break;
            case R.id.commit_btn:
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);
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


}
