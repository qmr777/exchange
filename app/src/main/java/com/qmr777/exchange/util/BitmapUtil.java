package com.qmr777.exchange.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InputMismatchException;

/**
 * Created by qmr on 2016/4/25.
 * 处理Bitmap 单例
 * 从网络获取图片、异步获取、压缩
 */
public class BitmapUtil {
    private static BitmapUtil bitmapUtil;

    private LruCache<String, Bitmap> bitmapCache;

    private BitmapUtil() {
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        bitmapCache = new LruCache<>(cacheSize);
    }

    public static BitmapUtil getInstance() {
        if (bitmapUtil == null)
            bitmapUtil = new BitmapUtil();
        return bitmapUtil;
    }

    public interface afterGetImage {
        void loadImageSuccess(Bitmap bitmap);

        void loadImageFailed();
    }

    /**
     * 从网络异步加载图片(AsyncTask)并保存到内存缓存中
     *
     * @param url           网络图片地址;
     * @param afterGetImage 加载成功/失败的回调方法;
     */
    public void GetHttpBitmap(String url, afterGetImage afterGetImage) {
        if (bitmapCache.get(MD5Util.MD5(url)) != null) {
            if (afterGetImage != null)
                afterGetImage.loadImageSuccess(bitmapCache.get(MD5Util.MD5(url)));
        } else
            new GetHttpImage(afterGetImage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);//允许并发执行
    }

    /**
     * 获取资源文件中的Bitmap
     *
     * @param context    传入一个context
     * @param resourceID 图片的jd
     * @return Bitmap
     */
    public Bitmap GetResourceBitmap(Context context, int resourceID) {
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceID);
        return bitmap;
    }

    class GetHttpImage extends AsyncTask<String, Void, Bitmap> {
        private afterGetImage afterGetImage;
        String MD5;

        public GetHttpImage(afterGetImage afterGetImage) {
            this.afterGetImage = afterGetImage;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            MD5 = MD5Util.MD5(params[0]);
            return BitmapUtil.getInstance().GetHttpBitmap(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //afterGetImage(bitmap);
            bitmapCache.put(MD5, bitmap);
            if (afterGetImage != null) {
                if (bitmap != null)
                    afterGetImage.loadImageSuccess(bitmap);
                else
                    afterGetImage.loadImageFailed();
            }

        }
    }

    /**
     * 本地图片缩略图，指定最大宽/高
     *
     * @param src    本地图片路径
     * @param width  最大宽度
     * @param height 最大高度
     * @return 压缩后的bitmap
     */

    public Bitmap GetThumbnail(String src, int width, int height) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, options);
        options.inJustDecodeBounds = false;
        float bw = options.outWidth;
        float bh = options.outHeight;
        float ratew = width / bw;
        float rateh = height / bh;
        float rate = (ratew < rateh) ? ratew : rateh;
        options.inSampleSize = (int) rate;
        bitmap = BitmapFactory.decodeFile(src, options);
        return bitmap;
    }

    /**
     * 资源图片缩略图
     *
     * @param res        context.getResource()
     * @param resourceID 图片资源id
     * @param width      最大宽度
     * @param height     最大高度
     * @return 压缩后的bitmap
     */
    public Bitmap GetThumbnail(Resources res, int resourceID, int width, int height) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceID, options);
        options.inJustDecodeBounds = false;
        float bw = options.outWidth;
        float bh = options.outHeight;
        float ratew = bw / width;
        float rateh = bh / height;
        float rate = (ratew > rateh) ? ratew : rateh;
        options.inSampleSize = (int) rate;
        bitmap = BitmapFactory.decodeResource(res, resourceID, options);
        return bitmap;
    }

    /**
     * 网络加载图片(非异步)
     *
     * @param url 网络图片url
     * @return Bitmap可能为null
     */
    public Bitmap GetHttpBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
