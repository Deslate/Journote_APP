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
    private TextView mKeyboardTopViewFirstTxt;
    private TextView mKeyboardTopViewSecondTxt;
    private TextView mKeyboardTopViewThirdTxt;
    private TextView mKeyboardTopViewFourthTxt;
    private View mKeyboardTopViewTipContainer;
    private boolean mInputViewIsNull = true;

    private SeekBar mKeyboardTopViewSeekBar;
    private ValueAnimator mExtendSeekBarAnimator;
    private ValueAnimator mShrinkSeekBarAnimator;
    private boolean mIsCanMoveCursor = false;
    private int mLastSeekBarProgress = 25;

    private PopupWindow mSoftKeyboardTopPopupWindow;
    private boolean mIsSoftKeyBoardShowing = false;


    private int keyboardHeight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);




        mContainer = findViewById(R.id.container);
        editTitle = (EditText) findViewById(R.id.edit_title);
        editContent = (EditText) findViewById(R.id.edit_content);
        //mEditText.addTextChangedListener(this);

        //设置软键盘收放监听事件
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardOnGlobalChangeListener());


        //设置EditText控制
        new EditTextController(this,editContent);

    }

    /*@Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //Toast.makeText(this,"您输入的数据为："+s.toString(),Toast.LENGTH_SHORT).show();
        //System.out.println("您输入的数据为："+s.toString());

    }

    @Override
    public void afterTextChanged(Editable s) {
        //String text = s.toString();
    }

     */
    @Override
    public void onClick(View v) {
        String txt = "";
        switch (v.getId()) {
            case R.id.keyboard_top_view_first_txt:
                txt = mKeyboardTopViewFirstTxt.getText().toString();
                break;
            case R.id.keyboard_top_view_second_txt:
                txt = mKeyboardTopViewSecondTxt.getText().toString();
                break;
            case R.id.keyboard_top_view_third_txt:
                txt = mKeyboardTopViewThirdTxt.getText().toString();
                break;
            case R.id.keyboard_top_view_fourth_txt:
                txt = mKeyboardTopViewFourthTxt.getText().toString();
                break;

        }
        EditTextController.insert(txt);
        //System.out.println("您输入的数据为："+txt);
    }
    private int getScreenHeight() {
        return  ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getHeight();
    }

    private int getScreenWidth() {
        return ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getWidth();
    }


    //自定义类来实现ViewTreeObserver.OnGlobalLayoutListener接口
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
                editContent.setEnabled(true);
                editTitle.setEnabled(true);
                showKeyboardTopPopupWindow(getScreenWidth() / 2, keyboardHeight+getNavigationBarHeight(EditActivity.this));//显示popup工具栏
                getSupportActionBar().hide();


            } else {

                //进入浏览模式
                //editTitle.setCursorVisible(false);
                //editTitle.setCursorVisible(false);
                getSupportActionBar().show();
                if (preShowing) {
                    closePopupWindow();//隐藏popup工具栏
                }
                mIsSoftKeyBoardShowing = false;
            }
        }
    }




    private void showKeyboardTopPopupWindow(int x, int y) {
        System.out.println("private void showKeyboardTopPopupWindow(int x, int y)");
        if (mSoftKeyboardTopPopupWindow != null && mSoftKeyboardTopPopupWindow.isShowing()) {
            updateKeyboardTopPopupWindow(x, y); //可能是输入法切换了输入模式，高度会变化（比如切换为语音输入）
            return;
        }

        View popupView = getLayoutInflater().inflate(R.layout.tool_bar, null);
        System.out.println("View popupView = getLayoutInflater().inflate(R.layout.tool_bar, null);");

        mKeyboardTopViewFirstTxt = (TextView) popupView.findViewById(R.id.keyboard_top_view_first_txt);
        mKeyboardTopViewSecondTxt = (TextView) popupView.findViewById(R.id.keyboard_top_view_second_txt);
        mKeyboardTopViewThirdTxt = (TextView) popupView.findViewById(R.id.keyboard_top_view_third_txt);
        mKeyboardTopViewFourthTxt = (TextView) popupView.findViewById(R.id.keyboard_top_view_fourth_txt);
        mKeyboardTopViewSeekBar = (SeekBar) popupView.findViewById(R.id.keyboard_top_view_seek_bar);
        mKeyboardTopViewTipContainer = popupView.findViewById(R.id.keyboard_top_view_tip_container);

        System.out.println("findViewById");

        mKeyboardTopViewFirstTxt.setOnClickListener(this);
        mKeyboardTopViewSecondTxt.setOnClickListener(this);
        mKeyboardTopViewThirdTxt.setOnClickListener(this);
        mKeyboardTopViewFourthTxt.setOnClickListener(this);
        //mKeyboardTopViewSeekBar.setOnSeekBarChangeListener(this);

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
        updateKeyboardTopViewTips(TextUtils.isEmpty(editTitle.getText()));
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
            mKeyboardTopViewFirstTxt = null;
            mKeyboardTopViewSecondTxt = null;
            mKeyboardTopViewThirdTxt = null;
            mKeyboardTopViewFourthTxt = null;
            mKeyboardTopViewSeekBar = null;
            mInputViewIsNull = true;
        }
    }
    private void updateKeyboardTopViewTips(boolean isNull) {
        if (mInputViewIsNull == isNull) {
            return;
        }

        if (isNull) {
            if (mKeyboardTopViewFirstTxt != null) {
                mKeyboardTopViewFirstTxt.setText("emm");
            }
            if (mKeyboardTopViewSecondTxt != null) {
                mKeyboardTopViewSecondTxt.setText("emm");
            }
            if (mKeyboardTopViewThirdTxt != null) {
                mKeyboardTopViewThirdTxt.setText("emm");
            }
            if (mKeyboardTopViewFourthTxt != null) {
                mKeyboardTopViewFourthTxt.setText("emm");
            }
            mInputViewIsNull = true;
        } else {
            if (mKeyboardTopViewFirstTxt != null) {
                mKeyboardTopViewFirstTxt.setText("emm");
            }
            if (mKeyboardTopViewSecondTxt != null) {
                mKeyboardTopViewSecondTxt.setText("emm");
            }
            if (mKeyboardTopViewThirdTxt != null) {
                mKeyboardTopViewThirdTxt.setText("emm");
            }
            if (mKeyboardTopViewFourthTxt != null) {
                mKeyboardTopViewFourthTxt.setText("emm");
            }
            mInputViewIsNull = false;
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


}
