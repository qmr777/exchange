package com.qmr777.exchange.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.adapter.BookListAdapter;
import com.qmr777.exchange.adapter.MyBookAdapter;
import com.qmr777.exchange.model.MyBook;
import com.qmr777.exchange.model.NearBook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyBookAty extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    MyBook myBook;
    MyApplication myApplication;
    MyBookAdapter adapter;

    void initView() {
        actionBar = getSupportActionBar();
        recyclerView = (RecyclerView) findViewById(R.id.rv_book_msg);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        myApplication = (MyApplication) getApplication();
        actionBar.setTitle("我的闲置");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_book);
        initView();
        recyclerView.setLayoutManager(new GridLayoutManager(MyBookAty.this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onStart() {
        super.onStart();
        new GetFavoriteBookTask().execute();
    }

    void updateData() {
        adapter = new MyBookAdapter(myBook);
        recyclerView.setAdapter(adapter);
        adapter.setOnClick(new MyBookAdapter.onClick() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MyBookAty.this, BookMsgAty.class);
                intent.putExtra("isbn", myBook.getData().get(position).getIsbn13());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    class GetFavoriteBookTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.hide();
        }

        @Override
        protected String doInBackground(Void... params) {
            String u = "http://" + MyApplication.ServiceIP + ":8080/Exchange/InExchange";
            try {
                URL url = new URL(u);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("POST");
                OutputStream outputStream = connection.getOutputStream();
                String data = "id=" + myApplication.getUser_id();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                connection.connect();
                InputStream is = connection.getInputStream();
                String s;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while ((s = reader.readLine()) != null)
                    builder.append(s);
                reader.close();
                is.close();
                connection.disconnect();
                return builder.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            fab.show();
            Gson gson = new Gson();
            if (s != null) {
                if (!s.isEmpty() || !s.equals("null")) {
                    myBook = gson.fromJson(s, MyBook.class);
                    Log.d("MyBookAty", myBook.getErrcode() + " " + myBook.getData().size());
                    updateData();
                }
            } else
                Toast.makeText(MyBookAty.this, "获取不到数据", Toast.LENGTH_SHORT).show();
        }
    }
}
