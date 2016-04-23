package com.qmr777.exchange.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmr777.exchange.R;
import com.qmr777.exchange.model.NearBook;
import com.qmr777.exchange.task.GetImageTask;

import java.util.HashMap;

/**
 * Created by qmr777 on 16-4-20.
 *
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.MyViewHolder> {

    private onClickListener listener;
    private NearBook nearBook;
    private String[] strings;
    LruCache<Integer,Bitmap> lruCache = new LruCache<>(30);

    public interface onClickListener{
        void onClick(View view,int position);
        void onLongClick(View view, int position);
    }

    public BookListAdapter(NearBook nearBook){
        this.nearBook = nearBook;
    }

    public BookListAdapter(String[] strings){
        this.strings = strings;
    }

    public void setListener(onClickListener listener){
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_book_item,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if(strings != null) {
            holder.textView.setText(strings[position]);
        }
        else {
            holder.textView.setText(nearBook.getData().get(position).getTitle());

            if(lruCache.get(position)!=null)
                holder.imageView.setImageBitmap(lruCache.get(position));
            else {
                new GetImageTask(holder.imageView,lruCache,position).execute(nearBook.getData().get(position).getMedium_img());
                //lruCache.put(position,image);
            }
        }


        if(listener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Fragment2","onClick");
                    listener.onClick(holder.itemView,holder.getAdapterPosition());
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClick(holder.itemView,holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(strings!=null)
            return strings.length;
        else
            return nearBook.getData().size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_book_cover);
            textView = (TextView) itemView.findViewById(R.id.tv_book_name);

        }
    }
}
