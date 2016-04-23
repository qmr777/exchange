package com.qmr777.exchange.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.adapter.BookListAdapter;
import com.qmr777.exchange.model.MyBook;
import com.qmr777.exchange.model.NearBook;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FavoriteBook extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView recyclerView;
    FloatingActionButton fab;
    NearBook nearBook;
    MyBook myBook;
    MyApplication myApplication;
    BookListAdapter adapter;
    Intent mIntent;

    void initView() {
        actionBar = getSupportActionBar();
        recyclerView = (RecyclerView) findViewById(R.id.rv_book_msg);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        mIntent = getIntent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_book);
        myApplication = (MyApplication) getApplication();
        initView();
        actionBar.setTitle(mIntent.getStringExtra("AtyTitle"));
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView.setLayoutManager(new GridLayoutManager(FavoriteBook.this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetFavoriteBookTask().execute();
            }
        });
        new GetFavoriteBookTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void updateData() {
        adapter = new BookListAdapter(nearBook);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new BookListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("Fragment2", "click");
                Intent intent = new Intent(FavoriteBook.this, BookMsgAty.class);
                Log.d("FavoriteBook", nearBook.getData().get(position).getIsbn13());
                intent.putExtra("isbn", nearBook.getData().get(position).getIsbn13());
                if (myApplication.ifLogin())
                    intent.putExtra("needFavorite", false);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                //长按回收
                cancelPublish(nearBook.getData().get(position).getIsbn13(), nearBook.getData().get(position).getTitle());

            }
        });
    }

    void cancelPublish(String isbn13, String title) {
        String sb = "确认取消对" + title + "的收藏吗?";
        AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteBook.this);
        builder.setTitle("删除收藏")
                .setCancelable(false)
                .setMessage(sb)
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(FavoriteBook.this, "删除", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();

    }

    class GetFavoriteBookTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.hide();
        }

        @Override
        protected String doInBackground(Void... params) {
            String u = "http://" + MyApplication.ServiceIP + ":8080/Exchange/GetFavoriteBook";
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
                    nearBook = gson.fromJson(s, NearBook.class);
                    Log.d("Fragment2", nearBook.getErrcode() + " " + nearBook.getData().size());
                    updateData();
                }
            } else
                Toast.makeText(FavoriteBook.this, "获取不到数据", Toast.LENGTH_SHORT).show();
        }
    }


}
