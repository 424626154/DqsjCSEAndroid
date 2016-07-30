package com.sbb.dqsjcse.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.config.TipsConfig;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by bingbing on 16/7/23.
 */
public class BaseActivity extends Activity{
    protected SweetAlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new SweetAlertDialog(BaseActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(R.color.color_bg01);
        dialog.setTitleText(TipsConfig.LODING_TIPS);
        dialog.setCancelable(true);
    }

    public void showShortTost(String text){
//        Toast.makeText(getApplicationContext(), text,
//                Toast.LENGTH_SHORT).show();
        SuperActivityToast.create(this)
                .setText(text)
                .setDuration(Style.DURATION_SHORT)
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.tran_in,
                R.anim.tran_out);
    }
}
