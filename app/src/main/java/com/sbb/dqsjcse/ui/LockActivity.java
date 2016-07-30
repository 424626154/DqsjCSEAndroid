package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.LockConfig;
import com.sbb.dqsjcse.config.NetConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.util.SharedUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by bingbing on 16/7/29.
 */
public class LockActivity extends  BaseActivity implements View.OnClickListener{
    private RelativeLayout[] passs;
    private ImageView[] dots;
    private int pass_num = 6;
    private List<String> passList = new ArrayList<>();
    private Button key0 ;
    private Button key1 ;
    private Button key2 ;
    private Button key3 ;
    private Button key4 ;
    private Button key5 ;
    private Button key6 ;
    private Button key7 ;
    private Button key8 ;
    private Button key9 ;
    private Button keydel ;
    private Button keyok;
    private String lock_type ;
    private int err_num ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock);
        Bundle bundle = this.getIntent().getExtras();
        lock_type = bundle.getString(LockConfig.LOCK,"");
        err_num = 0;
        initUI();
    }

    public void initUI(){
        passs = new RelativeLayout[pass_num];
        dots = new ImageView[pass_num];
        passs[0] = (RelativeLayout)findViewById(R.id.pass01);
        passs[1] = (RelativeLayout)findViewById(R.id.pass02);
        passs[2] = (RelativeLayout)findViewById(R.id.pass03);
        passs[3] = (RelativeLayout)findViewById(R.id.pass04);
        passs[4] = (RelativeLayout)findViewById(R.id.pass05);
        passs[5] = (RelativeLayout)findViewById(R.id.pass06);

        dots[0] = (ImageView)findViewById(R.id.dot01);
        dots[1] = (ImageView)findViewById(R.id.dot02);
        dots[2] = (ImageView)findViewById(R.id.dot03);
        dots[3] = (ImageView)findViewById(R.id.dot04);
        dots[4] = (ImageView)findViewById(R.id.dot05);
        dots[5] = (ImageView)findViewById(R.id.dot06);

        key0  = (Button)findViewById(R.id.key0);
        key1  = (Button)findViewById(R.id.key1);
        key2  = (Button)findViewById(R.id.key2);
        key3  = (Button)findViewById(R.id.key3);
        key4  = (Button)findViewById(R.id.key4);
        key5  = (Button)findViewById(R.id.key5);
        key6  = (Button)findViewById(R.id.key6);
        key7  = (Button)findViewById(R.id.key7);
        key8  = (Button)findViewById(R.id.key8);
        key9  = (Button)findViewById(R.id.key9);
        keydel  = (Button)findViewById(R.id.key_del);
        keyok  = (Button)findViewById(R.id.key_ok);
        key0.setOnClickListener(this);
        key1.setOnClickListener(this);
        key2.setOnClickListener(this);
        key3.setOnClickListener(this);
        key4.setOnClickListener(this);
        key5.setOnClickListener(this);
        key6.setOnClickListener(this);
        key7.setOnClickListener(this);
        key8.setOnClickListener(this);
        key9.setOnClickListener(this);
        keydel.setOnClickListener(this);
        keyok.setOnClickListener(this);
        refreshPass();
    }

    public void refreshPass(){
        for(int i = 0;i < pass_num;i++ ){
            if(i < passList.size()){
                dots[i].setVisibility(View.VISIBLE);
            }else{
                dots[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.key0:
                addPass("0");
                break;
            case R.id.key1:
                addPass("1");
                break;
            case R.id.key2:
                addPass("2");
                break;
            case R.id.key3:
                addPass("3");
                break;
            case R.id.key4:
                addPass("4");
                break;
            case R.id.key5:
                addPass("5");
                break;
            case R.id.key6:
                addPass("6");
                break;
            case R.id.key7:
                addPass("7");
                break;
            case R.id.key8:
                addPass("8");
                break;
            case R.id.key9:
                addPass("9");
                break;
            case R.id.key_ok:
                refreshPass();
                if(passList.size() < pass_num){
                    err_num ++;
                    if(err_num > 3){
                        showShortTost("密码输入错误3次以上");
                    }else{
                        showShortTost("密码格式错误");
                    }
                }else{
                    requestPass(getPass());
                }
                break;
            case R.id.key_del:
                if (passList.size() >0){
                    passList.remove(passList.size()-1);
                }
                refreshPass();
                break;
        }
    }

    public void addPass(String pass){
        if (passList.size() < pass_num){
            passList.add(pass);
        }
        refreshPass();
    }
    public String getPass(){
        String pass = "";
        for(int i = 0;i < passList.size();i++ ) {
            pass =  pass+ passList.get(i);
        }
        return pass;
    }

    public void requestPass(String pass){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","lock");
        params.put("pass",pass); // 设置请求的参数名和参数值
        client.post(NetConfig.http_post,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                System.out.println(jsonStr);
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String errcode = jsonObject.getString("errcode");
                    if(errcode.equals("0")){
                        Intent intent = new Intent(LockConfig.LOCK);
                        intent.putExtra(LockConfig.LOCK,lock_type);
                        sendBroadcast(intent);
                        SharedUtil.setLock(LockActivity.this,true);
                        finish();
                    }else{
                        err_num ++;
                        if(err_num > 3){
                            showShortTost("密码输入错误3次以上");
                        }else{
                            showShortTost("密码格式错误");
                        }
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
