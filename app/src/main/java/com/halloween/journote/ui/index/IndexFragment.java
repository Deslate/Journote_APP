package com.halloween.journote.ui.index;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.UserData;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.halloween.journote.MainActivity;
import com.halloween.journote.R;
import com.halloween.journote.model.DatabaseManager;
import com.halloween.journote.model.Item;

import java.util.Date;
import java.util.List;

import static com.halloween.journote.MainActivity.actionBar;
import static com.halloween.journote.MainActivity.decor;

public class IndexFragment extends Fragment {



    private IndexViewModel indexViewModel;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    ItemListAdapter.OnItemLongClickListener longListener;
    ItemListAdapter.OnItemClickListener listener;
    DatabaseManager database ;
    ItemListAdapter adapter;

    List<Item> list;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        indexViewModel = ViewModelProviders.of(this).get(IndexViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_index, container, false);
        setCustomActionBar();
        setHasOptionsMenu(true);

        database = new DatabaseManager(this.getActivity());

        recyclerView = (RecyclerView) root.findViewById(R.id.index_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
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
        final TextView textView = root.findViewById(R.id.text_index);

        indexViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        listener = new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Item item=list.get(position);
                System.out.println("get position: "+item);
                Intent intent=new Intent("com.halloween.journote.ACTION_START");
                intent.addCategory("com.halloween.journote.EDIT_ACTIVITY");
                intent.putExtra("contentPath", item.getContentPath());
                System.out.println("IndexFragment onClick-----------------getContentPath() ==> "+item.getContentPath());
                startActivityForResult(intent,1);
            }
        };
        longListener = new ItemListAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(root.getContext(), "long click " + position, Toast.LENGTH_SHORT).show();
            }
        };

        //refresh();

        return root;
    }

    public void onStart(){
        super.onStart();
        refresh();
    }
    public  void  onResume(){
        super.onResume();
        refresh();
    }

    public void refresh(){
        list = database.getTop(20);
        adapter = new ItemListAdapter(list);
        adapter.setOnItemClickListener(listener);
        adapter.setOnItemLongClickListener(longListener);
        recyclerView.setAdapter(adapter);
        System.out.println("---------------- List refresh --------------" );
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



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Active Result");
        refresh();
    }


}