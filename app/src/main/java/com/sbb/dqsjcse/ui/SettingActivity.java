package com.sbb.dqsjcse.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.util.SharedUtil;

/**
 * Created by bingbing on 16/7/26.
 */
public class SettingActivity extends BaseActivity {
    private Button backBut;
    private TextView titleTV;
    private LinearLayout userlayout;
    private LinearLayout nouserlayout;
    private TextView userTV;
    private RelativeLayout beernumlayout;
    private RelativeLayout deductionlayout;
    private TextView beernumTV;
    private TextView deductionTV;
    private RelativeLayout logofflayout;
    private Button logoffBut;
    private EditText beernumET;
    private EditText deductionET;
    private LoginContactReceiver loginContactReceiver ;

    protected class LoginContactReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            refreshData();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        initUI();
        refreshData();
        IntentFilter filter_login_contact = new IntentFilter(ReceiverConfig.LOGIN);
        if (loginContactReceiver == null)
            loginContactReceiver = new LoginContactReceiver();
        registerReceiver(loginContactReceiver, filter_login_contact);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginContactReceiver);
    }

    public void initUI(){
        backBut = (Button) findViewById(R.id.back);
        titleTV = (TextView) findViewById(R.id.title);
        userlayout = (LinearLayout)findViewById(R.id.userlayout);
        nouserlayout = (LinearLayout)findViewById(R.id.nouserlayout);
        userTV = (TextView) findViewById(R.id.user);
        beernumlayout = (RelativeLayout) findViewById(R.id.beernumlayout);
        deductionlayout = (RelativeLayout)findViewById(R.id.deductionlayout);
        beernumTV = (TextView) findViewById(R.id.beernum);
        deductionTV = (TextView)findViewById(R.id.deduction);
        logofflayout = (RelativeLayout)findViewById(R.id.logofflayout);
        logoffBut = (Button) findViewById(R.id.logoff);
        backBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
        titleTV.setText("设置");
        nouserlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        logoffBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedUtil.saveIsLogin(SettingActivity.this,false);
                refreshData();
                showShortTost("退出成功");
            }
        });
        beernumET = new EditText(SettingActivity.this);
        beernumET.setInputType(InputType.TYPE_CLASS_NUMBER);
        beernumlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SharedUtil.getIsLogin(SettingActivity.this)){
                            goLogin();
                            return;
                        }
                        if(TextUtils.isEmpty(beernumET.getText())){
                            showShortTost("参数错误");
                            return;
                        }
                        new AlertDialog.Builder(SettingActivity.this).setTitle("请输入会员默认添加啤酒数量").setIcon(
                                R.mipmap.ic_launcher).setView(beernumET).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int beernum = 0;
                                try {
                                    beernum = Integer.valueOf(beernumET.getText().toString());
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    beernum = 0;
                                }
                                if(beernum > 0 ){
                                    SharedUtil.setBeerNum(SettingActivity.this,beernum);
                                    refreshData();
                                    showShortTost("设置成功");
                                }else{
                                    showShortTost("设置失败");
                                }
                            }
                        })
                                .setNegativeButton("取消", null).show();
                    }
                });
        deductionET = new EditText(SettingActivity.this);
        deductionET.setInputType(InputType.TYPE_CLASS_NUMBER);
        deductionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedUtil.getIsLogin(SettingActivity.this)){
                    goLogin();
                    return;
                }
                if (TextUtils.isEmpty(deductionET.getText())){
                    showShortTost("参数错误");
                    return;
                }
                new AlertDialog.Builder(SettingActivity.this).setTitle("请输入会员默认添加啤酒数量").setIcon(
                        R.mipmap.ic_launcher).setView(deductionET).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deduction = 0;
                        try {
                            deduction = Integer.valueOf(deductionET.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            deduction = 0;
                        }
                        if(deduction > 0 ){
                            SharedUtil.setDeduction(SettingActivity.this,deduction);
                            refreshData();
                            showShortTost("设置成功");
                        }else{
                            showShortTost("设置失败");
                        }
                    }
                })
                        .setNegativeButton("取消", null).show();
            }
        });
    }
    public void refreshData(){
        if(userlayout != null&&nouserlayout != null&& logofflayout != null){
            if(SharedUtil.getIsLogin(SettingActivity.this)){
                userlayout.setVisibility(View.VISIBLE);
                nouserlayout.setVisibility(View.INVISIBLE);
                logofflayout.setVisibility(View.VISIBLE);
                if (userTV != null){
                    userTV.setText(SharedUtil.getUserName(SettingActivity.this));
                }

            }else{
                userlayout.setVisibility(View.INVISIBLE);
                nouserlayout.setVisibility(View.VISIBLE);
                logofflayout.setVisibility(View.INVISIBLE);
            }
        }
        if(beernumTV != null){
            beernumTV.setText(SharedUtil.getBeerNum(SettingActivity.this)+"");
        }
        if(deductionTV != null){
            deductionTV.setText(SharedUtil.getDeduction(SettingActivity.this)+"");
        }
    }

    public void goLogin(){
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
