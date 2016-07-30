package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.sbb.dqsjcse.util.SharedUtil;

/**
 * Created by bingbing on 16/7/25.
 */
public class AddMemberActivity extends BaseActivity {
    private RelativeLayout backBut;
    private TextView titleTV;
    private EditText accountET;
    private EditText nameET;
    private EditText phoneET;
    private EditText beernumET;
    private Button addBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmember);
        initUI();
    }
    public void initUI(){
        backBut = (RelativeLayout)findViewById(R.id.back);
        titleTV = (TextView) findViewById(R.id.title);
        accountET = (EditText)findViewById(R.id.account);
        nameET = (EditText)findViewById(R.id.name);
        phoneET = (EditText)findViewById(R.id.phone);
        beernumET = (EditText)findViewById(R.id.beernum);
        addBut = (Button) findViewById(R.id.add);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
        titleTV.setText("添加会员");
        addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMember();
            }
        });

        beernumET.setText(SharedUtil.getBeerNum(AddMemberActivity.this)+"");
    }

    public void addMember(){
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
        params.put("op","add");
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
                        Intent intent = new Intent(ReceiverConfig.LIST);
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
}
