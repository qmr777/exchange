package com.qmr777.exchange.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.activity.BookMsgAty;
import com.qmr777.exchange.activity.FavoriteBook;
import com.qmr777.exchange.activity.MyBookAty;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment4 extends Fragment {
    View view;
    CircleImageView circleImageView;
    TextView tv_username;
    ListView listView;


    public Fragment4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment4, container, false);
        circleImageView = (CircleImageView) view.findViewById(R.id.iv_avatar);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        listView = (ListView) view.findViewById(R.id.lv_my_status);
        Log.d("Fragment3","onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tv_username.setText(((MyApplication)getActivity().getApplication()).getUsername());
        String[] strings = {"在换书籍", "添加书籍", "换出书籍", "换入书籍", "我的收藏"};

        listView.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,strings));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(getActivity(), MyBookAty.class);
                        intent.putExtra("AtyTitle", "我的闲置");
                        startActivity(intent);
                        break;
                    case 1:
                        Toast.makeText(getActivity(),"扫描条形码以确定书籍",Toast.LENGTH_LONG).show();
                        intent = new Intent(getActivity(), CaptureActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case 4:
                        intent = new Intent(getActivity(), FavoriteBook.class);
                        intent.putExtra("AtyTitle", "我的收藏");
                        startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode == Activity.RESULT_OK){
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    Log.d("result",result);
                    Intent intent = new Intent(getActivity(), BookMsgAty.class);
                    intent.putExtra("needCheck",true);
                    intent.putExtra("isbn",result);
                    startActivity(intent);
                }
        }
    }
}
