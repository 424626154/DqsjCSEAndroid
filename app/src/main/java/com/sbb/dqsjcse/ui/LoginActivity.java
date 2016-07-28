package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.NetConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.db.User;
import com.sbb.dqsjcse.util.SharedUtil;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bingbing on 16/7/23.
 */
public class LoginActivity extends BaseActivity{
    private EditText username;
    private EditText password;
    private Button login;
    public String  usernameStr;
    public String passwordStr;
    public ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loging);
        initUI();
    }

    public void initUI(){
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        dialog = new ProgressDialog(LoginActivity.this);// 创建ProgressDialog对象
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条风格，风格为圆形，旋转的
        dialog.setIndeterminate(false);
        dialog.setCancelable(true); // 设置ProgressDialog 是否可以按退回键取消
    }

    public void onLogin(){
        if(username.getText().length() == 0 ){
            Toast.makeText(getApplicationContext(), "请输入用户名",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.getText().length() == 0 ){
            Toast.makeText(getApplicationContext(), "请输入密码",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        usernameStr = username.getText().toString();
        passwordStr = password.getText().toString();
        SharedUtil.saveIsLogin(LoginActivity.this,false);
        SharedUtil.saveUserName(LoginActivity.this,"");
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","login");
        params.put("username", username.getText()); // 设置请求的参数名和参数值
        params.put("password", password.getText());// 设置请求的参数名和参数
        client.post(NetConfig.http_post,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               String jsonStr = new String(responseBody);
                System.out.println(jsonStr);
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String errcode = jsonObject.getString("errcode");
                    if(errcode.equals("0")){
                        User u = new User();
                        u.setUsername(usernameStr);
                        u.setPassword(passwordStr);
                        u.save();
                        SharedUtil.saveIsLogin(LoginActivity.this,true);
                        SharedUtil.saveUserName(LoginActivity.this,usernameStr);
                        finish();
                        Intent intent = new Intent(ReceiverConfig.LOGIN);
                        sendBroadcast(intent);
                    }else{
                        String errmsg = jsonObject.getString("errmsg");
                        showShortTost(errmsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showShortTost("连接超时,请检查网络");
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
    }
}
