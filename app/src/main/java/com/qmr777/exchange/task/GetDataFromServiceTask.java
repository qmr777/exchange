package com.qmr777.exchange.task;

import android.os.AsyncTask;

import com.qmr777.exchange.MyApplication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by qmr on 2016/4/21.
 * 无参请求
 */
public class GetDataFromServiceTask extends AsyncTask<String,Void,String> {

    afterGetData afterGetData;

    public GetDataFromServiceTask(afterGetData afterGetData){
        this.afterGetData = afterGetData;
    }

    public interface afterGetData{
        void doSth(String s);
    }

    @Override
    protected String doInBackground(String... params) {
        String u = "http://"+MyApplication.ServiceIP+":8080/Exchange/GetFavoriteBook";
        String username = params[0];
        String id = params[1];
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            //OutputStream outputStream = connection.getOutputStream();
            //String data = "username="+username+"&&password="+id;
            //outputStream.write(data.getBytes());
            InputStream is = connection.getInputStream();
            String s;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while ((s = reader.readLine())!=null)
                builder.append(s);
            is.close();
            reader.close();
            connection.disconnect();
            return builder.toString();

        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        afterGetData.doSth(s);
    }
}
