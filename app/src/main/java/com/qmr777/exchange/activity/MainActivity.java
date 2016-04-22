package com.qmr777.exchange.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.fragment.Fragment1;
import com.qmr777.exchange.fragment.Fragment2;
import com.qmr777.exchange.fragment.Fragment3;
import com.qmr777.exchange.fragment.Fragment4;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements View.OnClickListener, activityAct {
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView tv1, tv2, tv3;
    Fragment fragment1, fragment2, fragment3,fragment4;
    MyApplication application;
    List<TextView> textViewList;
    ActionBar actionBar;

    LocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = (TextView) findViewById(R.id.a);
        tv2 = (TextView) findViewById(R.id.b);
        tv3 = (TextView) findViewById(R.id.c);
        application = (MyApplication) getApplication();
        actionBar = getSupportActionBar();
        textViewList = new ArrayList<>();
        textViewList.add(tv1);
        textViewList.add(tv2);
        textViewList.add(tv3);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();
        frameLayout = (FrameLayout) findViewById(R.id.fl_show);
        //new Thread(getLocation).start();
        //Intent intent = new Intent(MainActivity.this, CaptureActivity.class);


        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if(!bdLocation.getAddrStr().isEmpty()&&bdLocation.getAddrStr()!=null)
                    actionBar.setTitle(bdLocation.getAddrStr());
                ((MyApplication)getApplication()).setBdLocation(bdLocation);
                SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                editor.putFloat("longitude", Float.parseFloat(bdLocation.getLongitude()+""));
                editor.putFloat("latitude", Float.parseFloat(bdLocation.getLatitude()+""));
                editor.putString("addrStr",bdLocation.getAddrStr());
                editor.apply();

            }
        });    //注册监听函数
        initLocation();
        mLocationClient.start();

        setDefaultFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_bar).getActionView();
        return true;
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public void setDefaultFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        if(fragment1 == null)
            fragment1 = new Fragment1();
        fragmentTransaction.replace(R.id.fl_show,fragment1);
        setColor(0);
        fragmentTransaction.commit();
    }

    public void setColor(int position){
        for(TextView textView:textViewList)
            textView.setTextColor(getResources().getColor(android.R.color.tertiary_text_dark));
        textViewList.get(position).setTextColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    public void hideFragment(FragmentTransaction fragmentTransaction){
        if(fragment1!=null)
            fragmentTransaction.hide(fragment1);
        if(fragment2!=null)
            fragmentTransaction.hide(fragment2);
        if(fragment3!=null)
            fragmentTransaction.hide(fragment3);
        if(fragment4!=null)
            fragmentTransaction.hide(fragment4);

    }

    @Override
    public void onClick(View v) {
        fragmentTransaction = fragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        switch (v.getId()){
            case R.id.a:
                if(fragment1 == null) {
                    fragment1 = new Fragment1();
                    fragmentTransaction.add(R.id.fl_show,fragment1);
                }
                else{
                    fragmentTransaction.show(fragment1);
                }
                if(application.getBdLocation()!=null)
                    actionBar.setTitle(application.getBdLocation().getAddrStr());
                else
                    actionBar.setTitle("正在获取地址...");

                setColor(0);
                break;
            case R.id.b:
                if(fragment2 == null) {
                    fragment2 = new Fragment2();
                    fragmentTransaction.add(R.id.fl_show,fragment2);
                }
                else{
                    fragmentTransaction.show(fragment2);
                }
                actionBar.setTitle("附近的图书");
                setColor(1);
                break;
            case R.id.c:
                if(application.ifLogin()) {
                    if(fragment4 == null) {
                        fragment4 = new Fragment4();
                        fragmentTransaction.add(R.id.fl_show,fragment4);
                    }
                    else{
                        fragmentTransaction.show(fragment4);
                    }
                    actionBar.setTitle("我的信息");
                }
                else {
                    if(fragment3 == null) {
                        fragment3 = new Fragment3();
                        fragmentTransaction.add(R.id.fl_show,fragment3);
                    }
                    else{
                        fragmentTransaction.show(fragment3);
                    }
                    actionBar.setTitle("登录");
                }
                setColor(2);
                break;
            default:
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public void doSth(int what) {
        Log.d("MainActivity","doSth");
        fragmentTransaction = fragmentManager.beginTransaction();
        //Toast.makeText(MainActivity.this,"doSth",Toast.LENGTH_SHORT).show();
        switch (what){
            case 1:
                if(application.ifLogin()) {
                    fragmentTransaction.hide(fragment3);
                    if (fragment4 == null) {
                        fragment4 = new Fragment4();
                        fragmentTransaction.add(R.id.fl_show, fragment4);
                    }
                    else
                        fragmentTransaction.show(fragment4);
                }
                setColor(2);
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
