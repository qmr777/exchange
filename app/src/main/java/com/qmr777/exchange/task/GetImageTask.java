package com.qmr777.exchange.task;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qmr777.exchange.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qmr777 on 16-4-20.
 * 传入ImageView和图片url
 */
public class GetImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;
    LruCache<Integer,Bitmap> lruCache;
    Context context;
    int position;

    public GetImageTask(Context context, ImageView imageView) {
        this.imageView = imageView;
        this.context = context;
    }

    public GetImageTask(ImageView imageView, LruCache<Integer,Bitmap> lruCache,int position){
        this.imageView = imageView;
        this.lruCache = lruCache;
        this.position = position;
    }
    @Override
    protected Bitmap doInBackground(String... params) {
        Log.d("GetImageTask","GetImage "+params[0]);
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            connection.disconnect();
            return bitmap;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap!=null) {
            imageView.setImageBitmap(bitmap);
            if(lruCache!=null)
                lruCache.put(position,bitmap);
        } else {
            //imageView.setBackground(context.getDrawable(R.drawable.ic_favorite_outline_grey600_36dp));
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_highlight_remove_grey600_36dp);
            imageView.setImageBitmap(b);
        }

    }
}
