package com.sbb.dqsjcse.util;

import android.content.Context;

/**
 * Created by bingbing on 16/7/24.
 */
public class SharedUtil {
    public static final String DQSJ_CSE = "dqsj_cse";
    public static final String USER_NAME = "user_name";
    public static final String IS_LOGIN = "is_login";
    public static final String BEERNUM = "beernum";
    public static final String DEDUCTION = "deduction";
    public static void saveUserName(Context context,String username){
        SharedPrefsUtil.putValue(context,DQSJ_CSE,USER_NAME,username);
    }
    public static String getUserName(Context context){
       return SharedPrefsUtil.getValue(context,DQSJ_CSE,USER_NAME,"");
    }
    public static void saveIsLogin(Context context,boolean isLogin){
        SharedPrefsUtil.putValue(context,DQSJ_CSE,IS_LOGIN,isLogin);
    }
    public static boolean getIsLogin(Context context){
        return SharedPrefsUtil.getValue(context,DQSJ_CSE,IS_LOGIN,false);
    }
    public static void setBeerNum(Context context,long beernum){
        SharedPrefsUtil.putValue(context,DQSJ_CSE,BEERNUM,beernum);
    }
    public static int getBeerNum(Context context){
        return SharedPrefsUtil.getValue(context,DQSJ_CSE,BEERNUM,500);
    }
    public static void setDeduction(Context context,long beernum){
        SharedPrefsUtil.putValue(context,BEERNUM,DEDUCTION,beernum);
    }
    public static int getDeduction(Context context){
        return SharedPrefsUtil.getValue(context,BEERNUM,DEDUCTION,1);
    }
}
