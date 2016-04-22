package com.qmr777.exchange;

import android.app.Application;
import android.location.Location;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by qmr777 on 16-4-19.
 */
public class MyApplication extends Application {

    public static String ServiceIP = "192.168.253.1";


    private boolean ifLogin = false;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private int user_id;

    public BDLocation getBdLocation() {
        return bdLocation;
    }

    public void setBdLocation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
    }

    private BDLocation bdLocation;

    public Location getMlocation() {
        return mlocation;
    }

    public void setMlocation(Location mlocation) {
        this.mlocation = mlocation;
    }

    private Location mlocation;
    public boolean ifLogin(){
        return ifLogin;
    }
    public void setIfLogin(boolean status){
        this.ifLogin = status;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
    }
}
