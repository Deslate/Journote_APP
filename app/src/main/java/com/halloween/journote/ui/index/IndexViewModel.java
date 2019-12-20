package com.halloween.journote.ui.index;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.halloween.journote.MainActivity;
import com.halloween.journote.R;

public class IndexViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IndexViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("当前还没有任何笔记哦");
    }

    public LiveData<String> getText() {
        return mText;
    }
}