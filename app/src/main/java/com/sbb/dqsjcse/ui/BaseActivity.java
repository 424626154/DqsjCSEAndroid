package com.sbb.dqsjcse.ui;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by bingbing on 16/7/23.
 */
public class BaseActivity extends Activity{

    public void showShortTost(String text){
        Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_SHORT).show();
    }
}
