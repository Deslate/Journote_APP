package com.halloween.journote;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.halloween.journote.model.DatabaseOpenHelper;
import com.halloween.journote.model.Item;
import com.halloween.journote.model.ItemAddition;
import com.halloween.journote.model.Label;
import com.halloween.journote.ui.index.ItemListAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.halloween.journote.model.ItemAddition.Mood.SAD;


public class MainActivity extends AppCompatActivity {

    public static ActionBar actionBar;
    public static View decor;
    public static List<Item> items = new ArrayList<>();
    //



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);


        DatabaseOpenHelper databaseOpenHelper=new DatabaseOpenHelper(this);
        SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

        Item example = new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this);

        example.addItemAddition(new ItemAddition(SAD));
        example.addLable(new Label("emm","emmmm"));
        DateFormat format1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);
        String str ="SUN DEC 22 21:35:31 GMT+08:00 2019";
        Date date = new Date();
        try{ date = format1.parse(str);}catch (Exception e){System.out.println("fail");}
        //Toast.makeText(this,""+date.toString(),Toast.LENGTH_LONG).show();


        //new ItemAddition(SAD);

        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("今日随笔","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想法","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("无标题","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("测试","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("实例","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("使用说明","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));
        items.add(new Item("想不出其他名字了","Journote/item2019122201231670",new Date(),"deslate@outlook.com",this));

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
