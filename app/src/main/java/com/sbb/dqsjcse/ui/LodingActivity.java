package com.sbb.dqsjcse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.util.Utils;

/**
 * Created by bingbing on 16/7/23.
 */
public class LodingActivity extends BaseActivity{
    private TextView versionTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loding);
        init();
    }

    public void init(){
        versionTV = (TextView)findViewById(R.id.version);
        versionTV.setText(Utils.getVersionName(this.getBaseContext())+"Bate版");
        new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               Intent intent = new Intent();
               intent.setClass(getBaseContext(),HomeActivity.class);
                startActivity(intent);
               finish();
           }
       },1000);
    }
}
