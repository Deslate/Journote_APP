package com.halloween.journote.ui.index;


import android.os.Bundle;
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

import com.halloween.journote.MainActivity;
import com.halloween.journote.R;

import static com.halloween.journote.MainActivity.actionBar;
import static com.halloween.journote.MainActivity.decor;
import static com.halloween.journote.MainActivity.items;
import static com.halloween.journote.MainActivity.layoutManager;

public class IndexFragment extends Fragment {



    private IndexViewModel indexViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        indexViewModel = ViewModelProviders.of(this).get(IndexViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index, container, false);
        setCustomActionBar();
        setHasOptionsMenu(true);

        /*RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.index_recycler_view);

        recyclerView.setLayoutManager(layoutManager);
        ItemListAdapter adapter = new ItemListAdapter(items);
        recyclerView.setAdapter(adapter);

         */

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

        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}