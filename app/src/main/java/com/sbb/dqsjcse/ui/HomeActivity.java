package com.sbb.dqsjcse.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.LockConfig;
import com.sbb.dqsjcse.config.NetConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.config.TipsConfig;
import com.sbb.dqsjcse.db.Member;
import com.sbb.dqsjcse.util.SharedUtil;
import com.sbb.dqsjcse.util.Utils;


/**
 * Created by bingbing on 16/7/23.
 */
public class HomeActivity extends BaseActivity{
    private LoginContactReceiver loginContactReceiver ;
    private ListContactReceiver listContactReceiver ;
    private UpContactReceiver upContactReceiver ;
    private LockContactReceiver lockContactReceiver;
    private ListView listview;
    private HomeAdapter adapter;
    private List<Member>list;
    private EditText likeET ;
    private Button searchBut;
    private String like = "";
    private Button addTV ;
    private RelativeLayout settingRL;
    private RelativeLayout timeout;
    private Member temp_member ;

    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeLayout;
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

        IntentFilter filter_lock_contact = new IntentFilter(LockConfig.LOCK);
        if (lockContactReceiver == null)
            lockContactReceiver = new LockContactReceiver();
        registerReceiver(lockContactReceiver, filter_lock_contact);
        if (SharedUtil.getIsLogin(HomeActivity.this)){
            requestMember(like);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNetWork();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginContactReceiver);
        unregisterReceiver(listContactReceiver);
        unregisterReceiver(upContactReceiver);
        unregisterReceiver(lockContactReceiver);
    }

    public void initUI(){
        timeout = (RelativeLayout)findViewById(R.id.timeout);
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
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopMember((Member) adapter.getItem(position));
            }
        });
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
        addTV = (Button)findViewById(R.id.add);
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
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    /**
     * 刷新数据
     */
    public void refreshData(){
        like = "";
        if (likeET != null){
            like = likeET.getText().toString();
        }
        requestMember(like);
    }
    /**
     * 刷新网络状态
     */
    public void refreshNetWork(){
        if(Utils.isNetworkAvailable(HomeActivity.this)){
            timeout.setVisibility(View.GONE);
        }else{
            timeout.setVisibility(View.VISIBLE);
        }
    }


    protected class LoginContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (SharedUtil.getIsLogin(HomeActivity.this)){
                requestMember(like);
            }
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
                member.deduction = (int)SharedUtil.getDeduction(HomeActivity.this);
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


    protected class LockContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Bundle bundle = arg1.getExtras();
            String lock_type = bundle.getString(LockConfig.LOCK,"");
            if(TextUtils.equals(lock_type,LockConfig.LOCK_DEL)){
                requestDeleteMember(temp_member.mid);
            }else if(TextUtils.equals(lock_type,LockConfig.LOCK_DED)){
                requestDeduction(temp_member);
            }else if(TextUtils.equals(lock_type,LockConfig.LOCK_UP)){
                toUpMember(temp_member);
            }
        }
    }

    public void requestMember(final String like){
        if (!SharedUtil.getIsLogin(HomeActivity.this)){
            mSwipeLayout.setRefreshing(false);
            showShortTost("请先登录");
            return;
        }
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
                                    member.deduction = (int)SharedUtil.getDeduction(HomeActivity.this);
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
                    mSwipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showShortTost("连接超时,请检查网络");
                if (dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 删除会员
     * @param mid
     */
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


    public void requestDeduction(Member member){
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


    public void showPopMember(final Member member){
        View contentView = LayoutInflater.from(HomeActivity.this).inflate(
                R.layout.popup_member, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAtLocation(findViewById(R.id.home), Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
        popupWindow.update();
        TextView pop_account = (TextView)contentView.findViewById(R.id.pop_account);
        pop_account.setText(member.account);
        TextView pop_num = (TextView)contentView.findViewById(R.id.pop_num);
        pop_num.setText(SharedUtil.getDeduction(HomeActivity.this)+"");
        Button pop_deduction = (Button) contentView.findViewById(R.id.pop_deduction);
        pop_deduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_member = member;
                goLock(LockConfig.LOCK_DED);
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        Button pop_up = (Button) contentView.findViewById(R.id.pop_up);
        pop_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_member = member;
                goLock(LockConfig.LOCK_UP);
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        Button pop_del = (Button) contentView.findViewById(R.id.pop_del);
        pop_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temp_member = member;
                goLock(LockConfig.LOCK_DEL);
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        RelativeLayout popup =  (RelativeLayout) contentView.findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
    }
    /*************go*******************/
    public void goLock(String lock){
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this,LockActivity.class);
        intent.putExtra(LockConfig.LOCK,lock);
        startActivity(intent);
    }

    public void goLogin(){
        Intent intent = new Intent();
        intent.setClass(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
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
}
