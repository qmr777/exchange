package com.qmr777.exchange.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

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
    int position;

    public GetImageTask(ImageView imageView){
        this.imageView = imageView;
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
        }

    }
}
