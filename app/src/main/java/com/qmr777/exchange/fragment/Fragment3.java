package com.qmr777.exchange.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qmr777.exchange.MyApplication;
import com.qmr777.exchange.R;
import com.qmr777.exchange.activity.BookMsgAty;
import com.qmr777.exchange.activity.MainActivity;
import com.qmr777.exchange.activity.activityAct;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment3 extends Fragment {
    EditText username,password;
    TextView register,lostpswd;
    Button login,scanner;
    Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            afterLogin((String) msg.obj);
        }
    };


    public Fragment3() {
        // Required empty public constructor
    }

    void afterLogin(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.getString("errcode").equals("0")) {
                ((MyApplication) getActivity().getApplication()).setUsername(jsonObject.getString("username"));
                ((MyApplication) getActivity().getApplication()).setUser_id(Integer.parseInt(jsonObject.getString("user_id")));
                ((MyApplication) getActivity().getApplication()).setIfLogin(true);
                ((activityAct) getActivity()).doSth(1);
            } else {
                ((MyApplication) getActivity().getApplication()).setIfLogin(false);
                Toast.makeText(getActivity(), "用户名/密码错误", Toast.LENGTH_SHORT).show();
                password.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment3, container, false);
        username = (EditText) view.findViewById(R.id.login_username);
        password = (EditText) view.findViewById(R.id.login_password);
        login = (Button) view.findViewById(R.id.btn_login);
        scanner = (Button) view.findViewById(R.id.btn_scanner);
        Log.d("Fragment3","onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录
                if(username.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"用户名/密码 不能为空",Toast.LENGTH_SHORT).show();
                }
                else
                    //new LoginTask().execute(username.getText().toString(),password.getText().toString());
                    new Thread(loginRunn).start();
/*                Intent intent = new Intent(getActivity(), BookMsgAty.class);
                intent.putExtra("isbn","9787111128069");
                startActivity(intent);*/
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(),CaptureActivity.class),0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            String result = bundle.getString("result");
            Log.d("result",result);
            Intent intent = new Intent(getActivity(), BookMsgAty.class);
            intent.putExtra("isbn",result);
            startActivity(intent);
        }
    }

    Runnable loginRunn = new Runnable() {
        String name, pswd;

        @Override
        public void run() {
            this.name = username.getText().toString();
            this.pswd = password.getText().toString();
            Log.d("Fragment3", "login " + username);
            try {
                String urlS = "http://" + MyApplication.ServiceIP + ":8080/Exchange/login";
                URL url = new URL(urlS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                OutputStream outputStream = connection.getOutputStream();
                String data = "username=" + name + "&&password=" + pswd;
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                connection.connect();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String s;
                StringBuilder builder = new StringBuilder();
                while ((s = reader.readLine()) != null)
                    builder.append(s);
                reader.close();
                connection.disconnect();
                //return builder.toString();
                Message message = Message.obtain();
                message.obj = builder.toString();
                mhandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    class LoginTask extends AsyncTask<String,Void,String>{
        String username,pswd;

        @Override
        protected String doInBackground(String... params) {
            username = params[0];
            pswd = params[1];
            Log.d("Fragment3","login "+username);
            try {
                String urlS = "http://"+MyApplication.ServiceIP + ":8080/Exchange/login";
                URL url = new URL(urlS);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                OutputStream outputStream = connection.getOutputStream();
                String data = "username="+username+"&&password="+pswd;
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
                connection.connect();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String s;
                StringBuilder builder = new StringBuilder();
                while ((s = reader.readLine())!=null)
                    builder.append(s);
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
            try {
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getString("errcode").equals("0")){
                    ((MyApplication)getActivity().getApplication()).setUsername(jsonObject.getString("username"));
                    ((MyApplication)getActivity().getApplication()).setUser_id(Integer.parseInt(jsonObject.getString("user_id")));
                    ((MyApplication)getActivity().getApplication()).setIfLogin(true);
                    ((activityAct)getActivity()).doSth(1);
                }
                else{
                    ((MyApplication)getActivity().getApplication()).setIfLogin(false);
                    Toast.makeText(getActivity(),"用户名/密码错误",Toast.LENGTH_SHORT).show();
                    password.setText("");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

