package com.halloween.journote.ui.index;

import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.UserData;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.halloween.journote.MainActivity;
import com.halloween.journote.R;
import com.halloween.journote.model.Item;

import java.util.Date;
import java.util.List;

import static com.halloween.journote.MainActivity.actionBar;
import static com.halloween.journote.MainActivity.decor;
import static com.halloween.journote.MainActivity.items;

public class IndexFragment extends Fragment {



    private IndexViewModel indexViewModel;
    private SwipeRefreshLayout refreshLayout;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        indexViewModel = ViewModelProviders.of(this).get(IndexViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index, container, false);
        setCustomActionBar();
        setHasOptionsMenu(true);

        final ItemListAdapter adapter = new ItemListAdapter(items);
        refreshLayout = root.findViewById(R.id.index_refresh);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                System.out.println("refresh" );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                        refreshLayout.setRefreshing(false);

                    }
                },2000);
            }
        });
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.index_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        final TextView textView = root.findViewById(R.id.text_index);

        indexViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }


    private void setCustomActionBar(){
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this.getActivity()).inflate(R.layout.actionbar_index, null);
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |会导致UI位置错乱
    }
    public void refresh(){
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        System.out.println("refresh" );
    }

}