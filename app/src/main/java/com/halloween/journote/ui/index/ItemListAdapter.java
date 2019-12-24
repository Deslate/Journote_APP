package com.halloween.journote.ui.index;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.halloween.journote.R;
import com.halloween.journote.model.Item;
import com.halloween.journote.ui.edit.EditTextController;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private List<Item> items;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton listImage;
        TextView listName;
        TextView listPreview;
        TextView listOrder;
        TextView listOrderSub_I;
        TextView listOrderSub_II;

        public ViewHolder(View view) {
            super(view);
            listImage = (ImageButton) view.findViewById(R.id.list_image);
            listName = (TextView) view.findViewById(R.id.list_name);
            listPreview = (TextView) view.findViewById(R.id.list_preview);
            listOrder = (TextView) view.findViewById(R.id.list_order);
            listOrderSub_I = (TextView) view.findViewById(R.id.list_order_sub1);
            listOrderSub_II = (TextView) view.findViewById(R.id.list_order_sub2);
        }

    }

    public ItemListAdapter(List<Item> items) {
        this.items = items;
    }

    @Override

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final int mposition = position;

        Item item = items.get(position);
        holder.listName.setText(item.getTitle());
        holder.listOrder.setText(new SimpleDateFormat("dd").format(item.getCreateDate()));
        holder.listOrderSub_I.setText(new SimpleDateFormat("EEE", java.util.Locale.US).format(item.getCreateDate()));
        holder.listOrderSub_II.setText(new SimpleDateFormat("HH:mm:ss").format(item.getCreateDate()));

        File JournoteFolder =new File(Environment.getExternalStorageDirectory(),"Journote");
        while(!JournoteFolder.exists()){ JournoteFolder.mkdir(); }
        File Journote_NoteFolder =new File(JournoteFolder,"Notes");
        while(!Journote_NoteFolder.exists()){ Journote_NoteFolder.mkdir(); }
        File file = new File(Journote_NoteFolder+"/"+item.getContentPath());
        StringBuilder result = new StringBuilder();
        String s;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));//构造一个BufferedReader类来读取文件
            s = br.readLine();
        }catch (IOException e) {s = "暂且获取不到文件内容哦";}
        if(null==s) s="空文件，写点什么吧！";
        int n= s.indexOf("\n");
        if(n>=0) s=s.substring(0,s.indexOf("\n"));
        if (s.length()>16) s=s.substring(0,16);
        holder.listPreview.setText(s);




        //点击接口回调
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(mposition);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.onClick(mposition);
                }
                return true;
            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }


    //第一步 定义接口
    public interface OnItemClickListener {
        void onClick(int position);
    }

    private OnItemClickListener listener;

    //第二步， 写一个公共的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }




    public interface OnItemLongClickListener {
        void onClick(int position);
    }

    private OnItemLongClickListener longClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
