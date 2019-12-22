package com.halloween.journote.ui.index;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.halloween.journote.R;
import com.halloween.journote.model.Item;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {

    private List<Item> items;

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton listImage;
        TextView listName;

        public ViewHolder(View view) {
            super(view);
            listImage = (ImageButton) view.findViewById(R.id.list_image);
            listName = (TextView) view.findViewById(R.id.list_name);
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
        //holder.fruitImage.setImageResource(fruit.getImageId());
        holder.listName.setText(item.getTitle());

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
