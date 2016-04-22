package com.qmr777.exchange.task;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by qmr on 2016/4/21.
 *
 */
public class PushDataToServiceTask extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... params) {
        String data = params[0];
        String u = params[1];
        try {
            URL url = new URL(u);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
            outputStream.close();
            connection.connect();
            connection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
