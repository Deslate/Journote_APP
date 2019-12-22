package com.halloween.journote.ui.edit;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EditTextController implements TextWatcher, View.OnFocusChangeListener {

    private Context context;
    private static EditText editText;

    public int selectionStart;


    public EditTextController (Context context , EditText editText){
        System.out.println("set EditTextController");
        this.context = context;
        this.editText = editText;
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(this);
    }

    //TODO 公开方法：将String插入至Editable -public
    public static void insert(String insertString) {//insertString：将要插入的文字
        if (TextUtils.isEmpty(insertString)) return;
        int start = editText.getSelectionStart();//获取光标位置
        int end = editText.getSelectionEnd();
        Editable editable = editText.getEditableText();//获取EditText的文字
        if (start < 0 || start >= editable.length()) {
            editable.append(insertString);//选择不存在或在文末
        } else {
            editable.replace(start, end, insertString);//光标所在位置插入文字
        }
    }

    //TODO 公开方法：根据地址插入图片 -public
    public void insertImage(String path){

    }

    //TODO 重写 TextChangeListener
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
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
    private List<keyword> scanKeyword(Editable editable){return new ArrayList<keyword>();}

    //TODO 核心：替换指定关键字为Spannable
    private Editable convertKeyword(Editable editable,List<keyword> keywords){return editable;};

    //TODO 核心类：关键字
    private class keyword{}

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
}
