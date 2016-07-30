package com.sbb.dqsjcse.data;

/**
 * Created by bingbing on 16/7/29.
 */
public class DqsjData {
    public static DqsjData dqsjData;
    public static DqsjData getSingle(){
        if(dqsjData == null){
            dqsjData = new DqsjData();
        }
        return dqsjData;
    }
}
