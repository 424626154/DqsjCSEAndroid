package com.sbb.dqsjcse;

import com.umeng.update.UmengUpdateAgent;

import org.litepal.LitePalApplication;

/**
 * Created by bingbing on 16/7/24.
 */
public class DqsjApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePalApplication.initialize(this);
        UmengUpdateAgent.update(this);
    }
}
