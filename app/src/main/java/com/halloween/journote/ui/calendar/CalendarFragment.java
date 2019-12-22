package com.halloween.journote.ui.calendar;

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

import com.haibin.calendarview.CalendarView;
import com.halloween.journote.R;
import com.halloween.journote.ui.index.ItemListAdapter;

import static com.halloween.journote.MainActivity.actionBar;
import static com.halloween.journote.MainActivity.decor;
import static com.halloween.journote.MainActivity.items;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                ViewModelProviders.of(this).get(CalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        setCustomActionBar();
        final TextView textView = root.findViewById(R.id.text_calendar);
        final CalendarView mCalendarView = root.findViewById(R.id.calendarView ) ;

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ItemListAdapter adapter = new ItemListAdapter(items);
        recyclerView.setAdapter(adapter);

        calendarViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mCalendarView.setWeekView(MeiZuWeekView.class) ;

        //public void setOnViewChangeListener(OnViewChangeListener listener);

        return root;
    }
    private void setCustomActionBar(){
        ActionBar.LayoutParams lp =new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        View mActionBarView = LayoutInflater.from(this.getActivity()).inflate(R.layout.actionbar_calendar, null);
        actionBar.setCustomView(mActionBarView, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        decor.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}