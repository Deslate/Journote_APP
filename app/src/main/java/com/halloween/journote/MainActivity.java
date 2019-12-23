package com.halloween.journote;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.halloween.journote.model.DatabaseOpenHelper;
import com.halloween.journote.model.Item;
import com.halloween.journote.model.ItemAddition;
import com.halloween.journote.model.Label;
import com.halloween.journote.ui.edit.EditTextController;
import com.halloween.journote.ui.index.ItemListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.InputStream;
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

    public void test(){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap originalBitmap= BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20191223_115457.jpg",options);
        if(originalBitmap == null){
            Toast.makeText(this,"shit ", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"success!", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(this,originalBitmap.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }

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


        test();



    }

}
