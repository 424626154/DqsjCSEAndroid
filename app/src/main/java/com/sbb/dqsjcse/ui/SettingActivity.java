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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.LockConfig;
import com.sbb.dqsjcse.config.ReceiverConfig;
import com.sbb.dqsjcse.util.SharedUtil;

/**
 * Created by bingbing on 16/7/26.
 */
public class SettingActivity extends BaseActivity {
    private RelativeLayout backBut;
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
        backBut = (RelativeLayout) findViewById(R.id.back);
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
                finish();
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
        beernumlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!SharedUtil.getIsLogin(SettingActivity.this)){
                            goLogin();
                            return;
                        }
                        showPopupBeerNum();
                    }
                });
        deductionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedUtil.getIsLogin(SettingActivity.this)){
                    goLogin();
                    return;
                }
                showPopupDed();
            }
        });
    }
    public void refreshData(){
        if(userlayout != null&&nouserlayout != null&& logofflayout != null){
            if(SharedUtil.getIsLogin(SettingActivity.this)){
                userlayout.setVisibility(View.VISIBLE);
                nouserlayout.setVisibility(View.GONE);
                logofflayout.setVisibility(View.VISIBLE);
                if (userTV != null){
                    userTV.setText(SharedUtil.getUserName(SettingActivity.this));
                }

            }else{
                userlayout.setVisibility(View.GONE);
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


    public void showPopupBeerNum(){
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(
                R.layout.popup_set, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
        TextView pop_title = (TextView)contentView.findViewById(R.id.pop_title);
        pop_title.setText("修改会员默认添加啤酒数量");
        TextView pop_tips = (TextView)contentView.findViewById(R.id.pop_tips);
        pop_tips.setText("请输入默认添加啤酒数量");
        final EditText pop_num = (EditText)contentView.findViewById(R.id.pop_num);
        pop_num.setText(SharedUtil.getBeerNum(SettingActivity.this)+"");
        pop_num.setSelection(pop_num.getText().toString().length());
        Button pop_cancel = (Button) contentView.findViewById(R.id.pop_cancel);
        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        Button pop_ok = (Button) contentView.findViewById(R.id.pop_ok);
        pop_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deduction = 0;
                try {
                    deduction = Integer.valueOf(pop_num.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    deduction = 0;
                }
                if(deduction > 0 ){
                    SharedUtil.setBeerNum(SettingActivity.this,deduction);
                    refreshData();
                    showShortTost("设置成功");
                }else{
                    showShortTost("设置失败");
                }

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

    public void showPopupDed(){
        View contentView = LayoutInflater.from(SettingActivity.this).inflate(
                R.layout.popup_set, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAtLocation(findViewById(R.id.main), Gravity.RIGHT|Gravity.BOTTOM, 0, 0);
        TextView pop_title = (TextView)contentView.findViewById(R.id.pop_title);
        pop_title.setText("修改扣除啤酒数量");
        TextView pop_tips = (TextView)contentView.findViewById(R.id.pop_tips);
        pop_tips.setText("请输入扣除啤酒数量");
        final EditText pop_num = (EditText)contentView.findViewById(R.id.pop_num);
        pop_num.setText(SharedUtil.getDeduction(SettingActivity.this)+"");
        pop_num.setSelection(pop_num.getText().toString().length());
        Button pop_cancel = (Button) contentView.findViewById(R.id.pop_cancel);
        pop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != popupWindow && popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
            }
        });
        Button pop_ok = (Button) contentView.findViewById(R.id.pop_ok);
        pop_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int deduction = 0;
                try {
                    deduction = Integer.valueOf(pop_num.getText().toString());
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
}
