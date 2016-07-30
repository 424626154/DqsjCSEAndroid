package com.sbb.dqsjcse.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.sbb.dqsjcse.R;
import com.sbb.dqsjcse.db.Member;

/**
 * Created by bingbing on 16/7/24.
 */
public class HomeAdapter extends BaseAdapter{
    private List<Member>list ;
    private HomeActivity activity;
    private LayoutInflater mInflater;
    public HomeAdapter(HomeActivity activity,List<Member>list) {
        this.activity = activity;
        this.list = list;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (list != null){
            return  list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(list != null){
            return  list.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_item, null);
            holder = new ViewHolder();
            holder.accountView = (TextView)convertView.findViewById(R.id.account);
            holder.nameView = (TextView)convertView.findViewById(R.id.name);
            holder.phoneView = (TextView)convertView.findViewById(R.id.phone);
            holder.beernumView = (TextView)convertView.findViewById(R.id.beernum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final Member member = list.get(position);
        holder.accountView.setText(member.account);
        holder.nameView.setText(member.name);
        holder.phoneView.setText(member.phone);
        holder.beernumView.setText(member.beernum+"");
        return convertView;
    }
    public static class ViewHolder {
        public TextView accountView;
        public TextView nameView;
        public TextView phoneView;
        public TextView beernumView;


    }
    public void refreshData(List<Member>list){
        this.list = list;
        notifyDataSetChanged();
    }
}
