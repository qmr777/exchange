package com.qmr777.exchange.activity;

import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.model.BookMsg;
import com.qmr777.exchange.task.GetImageTask;
import com.qmr777.exchange.task.PushDataToServiceTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//需要intent包含一个isbn参数

public class BookMsgAty extends AppCompatActivity {
    ImageView imageView;
    TextView title,author,translator,isbn,rating,author_summary,book_summary,book_catalog;
    BookMsg bookMsg;
    ActionBar actionBar;
    FloatingActionButton fab;
    MyApplication myApplication;
    ScrollView scrollView;
    Boolean needCheck;//需要确认书Fragment4

    void initView(){
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        myApplication = (MyApplication) getApplication();
        imageView = (ImageView) findViewById(R.id.iv_book_cover);
        title = (TextView) findViewById(R.id.tv_title);
        author = (TextView) findViewById(R.id.tv_author);
        translator = (TextView) findViewById(R.id.tv_translator);
        isbn = (TextView) findViewById(R.id.tv_isbn);
        rating = (TextView) findViewById(R.id.tv_rating);
        author_summary = (TextView) findViewById(R.id.tv_author_summary);
        book_summary = (TextView) findViewById(R.id.tv_book_summary);
        book_catalog = (TextView) findViewById(R.id.tv_book_catelog);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_msg_aty);
        initView();

        actionBar.setDisplayHomeAsUpEnabled(true);
        if(!getIntent().getBooleanExtra("needCheck",false))
            fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String date = format.format(new Date());
                //Toast.makeText(BookMsgAty.this,"FAB",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username",myApplication.getUsername());
                    jsonObject.put("user_id",myApplication.getUser_id());
                    jsonObject.put("longitude",myApplication.getBdLocation().getLongitude());
                    jsonObject.put("latitude",myApplication.getBdLocation().getLatitude());
                    jsonObject.put("dateTime",date);
                    jsonObject.put("isbn10",bookMsg.getIsbn10());
                    jsonObject.put("isbn13",bookMsg.getIsbn13());
                    jsonObject.put("title",bookMsg.getTitle());
                    jsonObject.put("price",bookMsg.getPrice());
                    jsonObject.put("large_img",bookMsg.getImages().getLarge());
                    jsonObject.put("medium_img",bookMsg.getImages().getMedium());
                    Log.d("BookMsgAty",jsonObject.toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
                String urlS = "http://"+MyApplication.ServiceIP + ":8080/Exchange/User_publish";
                String data = jsonObject.toString();
                new PushDataToServiceTask().execute(data,urlS);
                finish();
            }
        });
        new GetBookMsgTask().execute(getIntent().getStringExtra("isbn"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    class GetBookMsgTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            String u = "http://api.douban.com/v2/book/isbn/"+params[0];
            Log.d("BookMsgAty",u);
            try {
                URL url = new URL(u);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String s;
                while ((s = reader.readLine())!=null)
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
            //Log.d("result",s);
             if(s!=null){
                Gson gson = new Gson();
                bookMsg = gson.fromJson(s,BookMsg.class);
                Log.d("result",bookMsg.getIsbn10());
                Log.d("result",bookMsg.getIsbn13());
                if(bookMsg.getIsbn10().isEmpty()&&bookMsg.getIsbn13().isEmpty())
                    Toast.makeText(BookMsgAty.this,"获取不到图书信息",Toast.LENGTH_SHORT).show();
                else {
                    Log.d("BookMsgAty", bookMsg.getOrigin_title());
                    //Toast.makeText(BookMsgAty.this,bookMsg.getAlt_title(),Toast.LENGTH_SHORT).show();
                    title.setText(bookMsg.getTitle());
                    actionBar.setTitle(bookMsg.getTitle());
                    new GetImageTask(imageView).execute(bookMsg.getImages().getLarge());
                    author.setText("作者 " + bookMsg.getAuthor().toString());
                    Log.d("BookMsgAty", bookMsg.getTranslator().size()+"");
                    if (bookMsg.getTranslator().size() == 0)
                        translator.setText("翻译 " + bookMsg.getTranslator().toString());
                    isbn.setText("isbn " + bookMsg.getIsbn13());
                    rating.setText("评分 " + bookMsg.getRating().getAverage());
                    author_summary.setText("作者简介\r\n" + bookMsg.getAuthor_intro());
                    book_summary.setText("本书介绍\r\n" + bookMsg.getSummary());
                    book_catalog.setText("本书目录\r\n" + bookMsg.getCatalog());
                }
            }
            else {
                 Toast.makeText(BookMsgAty.this, "获取不到图书信息", Toast.LENGTH_SHORT).show();
                 finish();
             }
        }
    }
}
