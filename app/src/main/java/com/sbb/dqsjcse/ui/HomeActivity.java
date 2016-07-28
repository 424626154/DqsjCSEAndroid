package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.NetConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.db.Member;
import com.sbb.dqsjcse.util.SharedUtil;


/**
 * Created by bingbing on 16/7/23.
 */
public class HomeActivity extends BaseActivity{
    private LoginContactReceiver loginContactReceiver ;
    private ListContactReceiver listContactReceiver ;
    private UpContactReceiver upContactReceiver ;
    private ProgressDialog dialog;
    private ListView listview;
    private HomeAdapter adapter;
    private List<Member>list;
    private EditText likeET ;
    private Button searchBut;
    private String like = "";
    private TextView addTV ;
    private RelativeLayout settingRL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initUI();
        IntentFilter filter_login_contact = new IntentFilter(ReceiverConfig.LOGIN);
        if (loginContactReceiver == null)
            loginContactReceiver = new LoginContactReceiver();
        registerReceiver(loginContactReceiver, filter_login_contact);
        IntentFilter filter_list_contact = new IntentFilter(ReceiverConfig.LIST);
        if (listContactReceiver == null)
            listContactReceiver = new ListContactReceiver();
        registerReceiver(listContactReceiver, filter_list_contact);

        IntentFilter filter_up_contact = new IntentFilter(ReceiverConfig.UP);
        if (upContactReceiver == null)
            upContactReceiver = new UpContactReceiver();
        registerReceiver(upContactReceiver, filter_up_contact);
        if (SharedUtil.getIsLogin(HomeActivity.this)){
            requestMember(like);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginContactReceiver);
        unregisterReceiver(listContactReceiver);
        unregisterReceiver(upContactReceiver);
    }

    public void initUI(){
        settingRL = (RelativeLayout) findViewById(R.id.setting);
        settingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        listview = (ListView)findViewById(R.id.listview);
        list = new ArrayList<Member>();
        adapter = new HomeAdapter(HomeActivity.this,list);
        listview.setAdapter(adapter);
        likeET = (EditText) findViewById(R.id.like) ;
        searchBut = (Button) findViewById(R.id.search);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedUtil.getIsLogin(HomeActivity.this)){
                    goLogin();
                    return;
                }
                requestMember(likeET.getText().toString());
            }
        });
        addTV = (TextView)findViewById(R.id.add);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedUtil.getIsLogin(HomeActivity.this)){
                    goLogin();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(HomeActivity.this,AddMemberActivity.class);
                startActivity(intent);
            }
        });
        dialog = new ProgressDialog(HomeActivity.this);// 创建ProgressDialog对象
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条风格，风格为圆形，旋转的
        dialog.setIndeterminate(false);
        dialog.setCancelable(true); // 设置ProgressDialog 是否可以按退回键取消
        refreshUser();
    }
    public void refreshUser(){

    }
    protected class LoginContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            refreshUser();
        }
    }
    protected class ListContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            refreshData();
        }
    }
    protected class UpContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Bundle bundle = arg1.getExtras();
            String data = bundle.getString("data");
            try {
                JSONObject object = new JSONObject(data);
                Member member = new Member();
                member.mid = object.getLong("Id");
                member.account = object.getString("Account");
                member.name = object.getString("Name");
                member.phone = object.getString("Phone");
                member.beernum = object.getLong("BeerNum");
                member.time = object.getLong("Time");
                member.deduction = SharedUtil.getDeduction(HomeActivity.this);
                if (list != null){
                    for(int i = 0 ; i < list.size();i++){
                        if (list.get(i).mid == member.mid){
                            list.set(i,member);
                        }
                    }
                }
                adapter.refreshData(list);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void requestMember(final String like){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","member");
        params.put("like",like); // 设置请求的参数名和参数值
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
                        list.clear();
                        if (!data.isEmpty()&&!data.equals("null")){
                            JSONArray memberArry = new JSONArray(data);
                            if (memberArry != null){
                                for (int i = 0 ; i < memberArry.length() ; i++){
                                    JSONObject object = (JSONObject)memberArry.get(i);
                                    Member member = new Member();
                                    member.mid = object.getLong("Id");
                                    member.account = object.getString("Account");
                                    member.name = object.getString("Name");
                                    member.phone = object.getString("Phone");
                                    member.beernum = object.getLong("BeerNum");
                                    member.time = object.getLong("Time");
                                    member.deduction = SharedUtil.getDeduction(HomeActivity.this);
                                    list.add(member);
                                }
                            }
                        }
                        adapter.refreshData(list);
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

    public void requestDeleteMember(final long mid){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","memberdel");
        params.put("id",mid); // 设置请求的参数名和参数值
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
                        long mid = Long.valueOf(data);
                        for(int i = list.size()-1 ; i >= 0 ;i-- ){
                            if (list.get(i).mid == mid){
                                list.remove(i);
                            }
                        }

                        adapter.refreshData(list);
                        showShortTost("删除成功");
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
        like = "";
        if (likeET != null){
            like = likeET.getText().toString();
        }
        requestMember(like);
    }

    public void toUpMember(Member member){
        if(! (member.mid > 0)){
            showShortTost("参数错误");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this,UpMemberActivity.class);
        intent.putExtra(ReceiverConfig.MID,member.mid);
        startActivity(intent);
    }
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                showShortTost("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void goLogin(){
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    public void onDeduction(Member member){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数的封装的对象
        RequestParams params = new RequestParams();
        params.put("op","deduction");
        params.put("id",member.mid);
        params.put("num",member.deduction);
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
                        long mid = Long.valueOf(object.getString("Id"));
                        long beernum = Long.valueOf(object.getString("BeerNum"));
                        for(int i = list.size()-1 ; i >= 0 ;i-- ){
                            if (list.get(i).mid == mid){
                                list.get(i).beernum = beernum;
                            }
                        }

                        adapter.refreshData(list);
                        showShortTost("扣除成功");
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
