package com.qmr777.exchange.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.activity.BookMsgAty;
import com.qmr777.exchange.activity.MainActivity;
import com.qmr777.exchange.adapter.BookListAdapter;
import com.qmr777.exchange.model.BookMsg;
import com.qmr777.exchange.model.NearBook;
import com.qmr777.exchange.task.GetDataFromServiceTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment2 extends Fragment {
    RecyclerView recyclerView;
    View view;
    BookListAdapter adapter;
    MyApplication myApplication;
    NearBook nearBook;
    FloatingActionButton fab;


    public Fragment2() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_book_msg);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        Log.d("Fragment2","onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApplication = (MyApplication) getActivity().getApplication();
        //String[] s = {"111","222","333","444","555"};
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(myApplication.getBdLocation()!=null) {
            new GetNearBookTask().execute(myApplication.getBdLocation().getLongitude(), myApplication.getBdLocation().getLatitude());
        }
        else
            new GetNearBookTask().execute(120.0,30.0);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myApplication.getBdLocation()!=null) {
                    new GetNearBookTask().execute(myApplication.getBdLocation().getLongitude(), myApplication.getBdLocation().getLatitude());
                }
                else
                    new GetNearBookTask().execute(120.0,30.0);
            }
        });
        fab.show();
    }//onActivityCreated end

    public void updateData(){
        adapter = new BookListAdapter(nearBook);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new BookListAdapter.onClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("Fragment2","click");
                Intent intent = new Intent(getActivity(),BookMsgAty.class);
                intent.putExtra("isbn",nearBook.getData().get(position).getIsbn13());
                if (myApplication.ifLogin())
                    intent.putExtra("needFavorite", true);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "暂不显示" + nearBook.getData().get(position).getTitle(), Toast.LENGTH_LONG).show();
                nearBook.getData().remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

    //附近的图书Task
    class GetNearBookTask extends AsyncTask<Double,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fab.hide();
        }

        @Override
        protected String doInBackground(Double... params) {
            //String data = params[0];
            double longitude = params[0];
            double latitude = params[1];
            Log.d("Fragment2","GetNearBook");

            String u = "http://"+MyApplication.ServiceIP+":8080/Exchange/GetNearBook";
            try {
                URL url = new URL(u);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                OutputStream outputStream = connection.getOutputStream();
                String data = "longitude="+longitude+"&latitude="+latitude;
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                connection.connect();
                InputStream is = connection.getInputStream();
                String s;
                StringBuilder builder = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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
            fab.show();
            Gson gson = new Gson();
            if(s!=null) {
                if (!s.isEmpty() || !s.equals("null")) {
                    nearBook = gson.fromJson(s, NearBook.class);
                    Log.d("Fragment2", nearBook.getErrcode() + " " + nearBook.getData().size());
                    updateData();
                }
            }
            else
                Toast.makeText(getActivity(),"获取不到数据",Toast.LENGTH_SHORT).show();
        }
    }
}
