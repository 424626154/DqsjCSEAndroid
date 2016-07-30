package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.NetConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.db.Member;

/**
 * Created by bingbing on 16/7/25.
 */
public class UpMemberActivity extends BaseActivity {
    private RelativeLayout backBut;
    private TextView titleTV;
    private TextView midTV;
    private EditText accountET;
    private EditText nameET;
    private EditText phoneET;
    private EditText beernumET;
    private Button upBut;
    private long mid;
    private Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upmember);
        Bundle bundle = this.getIntent().getExtras();
        mid = bundle.getLong(ReceiverConfig.MID,0);
        initUI();
        if(mid == 0){
            showShortTost("参数错误");
        }else{
            requestMember();
        }
    }
    public void initUI(){
        backBut = (RelativeLayout) findViewById(R.id.back);
        titleTV = (TextView) findViewById(R.id.title);
        midTV = (TextView)findViewById(R.id.mid);
        accountET = (EditText)findViewById(R.id.account);
        nameET = (EditText)findViewById(R.id.name);
        phoneET = (EditText)findViewById(R.id.phone);
        beernumET = (EditText)findViewById(R.id.beernum);
        upBut = (Button) findViewById(R.id.up);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
        titleTV.setText("修改会员");
        upBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upMember();
            }
        });
    }

    public void upMember(){
        if (TextUtils.isEmpty(accountET.getText().toString())){
            showShortTost("请输入会员");
            return;
        }
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","up");
        params.put("mid",midTV.getText().toString());
        params.put("account",accountET.getText().toString());
        params.put("name",nameET.getText().toString());
        params.put("phone",phoneET.getText().toString());
        params.put("beernum",beernumET.getText().toString());
        client.post(NetConfig.http_post,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                System.out.println(jsonStr);
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String errcode = jsonObject.getString("errcode");
                    if(errcode.equals("0")){
                        String data = jsonObject.getString("data");
                        Intent intent = new Intent(ReceiverConfig.UP);
                        intent.putExtra("data",data);
                        sendBroadcast(intent);
                        showShortTost("添加成功");
                        finish();
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

    public void requestMember(){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","getone");
        params.put("mid",mid);
        client.post(NetConfig.http_post,params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String jsonStr = new String(responseBody);
                System.out.println(jsonStr);
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String errcode = jsonObject.getString("errcode");
                    if(errcode.equals("0")){
                        String data = jsonObject.getString("data");
                        JSONObject object = new JSONObject(data);
                        member = new Member();
                        member.mid = object.getLong("Id");
                        member.account = object.getString("Account");
                        member.name = object.getString("Name");
                        member.phone = object.getString("Phone");
                        member.beernum = object.getLong("BeerNum");
                        member.time = object.getLong("Time");
                        refreshData();
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

    public void refreshData(){
        if(member != null){
            midTV.setText(member.mid+"");
            accountET.setText(member.account);
            nameET.setText(member.name);
            phoneET.setText(member.phone);
            beernumET.setText(member.beernum+"");
        }
    }
}
