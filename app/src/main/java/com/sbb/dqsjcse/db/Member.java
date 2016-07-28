package com.sbb.dqsjcse.db;

import org.litepal.crud.DataSupport;

/**
 * Created by bingbing on 16/7/24.
 */
public class Member extends DataSupport {
    public long mid;
    public String account ;
    public String name;
    public String phone;
    public long beernum;
    public long time;

    public int getDeduction() {
        return deduction;
    }

    public void setDeduction(int deduction) {
        this.deduction = deduction;
    }

    public int deduction;

    public long getMid() {
        return mid;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public long getBeernum() {
        return beernum;
    }

    public long getTime() {
        return time;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBeernum(long beernum) {
        this.beernum = beernum;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Member{" +
                "mid=" + mid +
                ", account='" + account + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", beernum=" + beernum +
                ", time=" + time +
                ", deduction=" + deduction +
                '}';
    }
}
