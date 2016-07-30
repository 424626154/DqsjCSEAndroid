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
    public static final String LOCK = "lock";
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
    public static long getBeerNum(Context context){
        return SharedPrefsUtil.getValue(context,DQSJ_CSE,BEERNUM,(long)500);
    }
    public static void setDeduction(Context context,long beernum){
        SharedPrefsUtil.putValue(context,BEERNUM,DEDUCTION,beernum);
    }
    public static long getDeduction(Context context){
        return SharedPrefsUtil.getValue(context,BEERNUM,DEDUCTION,(long)1);
    }

    public static boolean isLock(Context context){
        return SharedPrefsUtil.getValue(context,BEERNUM,LOCK,true);
    }
    public static void setLock(Context context,Boolean lock){
        SharedPrefsUtil.putValue(context,BEERNUM,LOCK,lock);
    }
}
