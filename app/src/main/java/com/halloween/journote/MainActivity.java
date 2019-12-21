package com.halloween.journote;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.halloween.journote.model.Item;
import com.halloween.journote.model.ItemAddition;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.halloween.journote.model.ItemAddition.Mood.SAD;


public class MainActivity extends AppCompatActivity {

    public static ActionBar actionBar;
    public static View decor;
    public static List<Item> items = new ArrayList<>();
    public static LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        //new ItemAddition(SAD);
        layoutManager = new LinearLayoutManager(this);

        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com"));

        actionBar=getSupportActionBar();
        decor = this.getWindow().getDecorView();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_index, R.id.navigation_calendar, R.id.navigation_all, R.id.navigation_more)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
