package com.qmr777.exchange.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.IntegerRes;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmr777.exchange.R;
import com.qmr777.exchange.model.MyBook;
import com.qmr777.exchange.task.GetImageTask;

/**
 * Created by qmr on 2016/4/24.
 *
 */
public class MyBookAdapter extends RecyclerView.Adapter<MyBookAdapter.MyViewHolder> {
    ImageView imageView;
    LruCache<Integer, Bitmap> lruCache = new LruCache<>(30);
    onClick onClick;
    MyBook myBook;

    public MyBookAdapter(MyBook myBook) {
        this.myBook = myBook;
    }

    public void setOnClick(onClick onClick) {
        this.onClick = onClick;
    }

    public interface onClick {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_book_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Log.d("MyBookAty", "bindViewHolder");
        holder.textView.setText(myBook.getData().get(position).getTitle());
        if (lruCache.get(position) == null)
            new GetImageTask(holder.imageView, lruCache, position).execute(myBook.getData().get(position).getMedium_img());
        else
            imageView.setImageBitmap(lruCache.get(position));

        if (onClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClick(v, holder.getAdapterPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onClick.onItemLongClick(v, holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return myBook.getData().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_book_cover);
            textView = (TextView) itemView.findViewById(R.id.tv_book_name);
        }
    }
}
