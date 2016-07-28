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
            holder.updateBut = (Button) convertView.findViewById(R.id.update);
            holder.deleteBut = (Button)convertView.findViewById(R.id.delete);
            holder.deductionBut = (Button)convertView.findViewById(R.id.deduction);
            holder.numET = (TextView) convertView.findViewById(R.id.num);
            holder.addBut = (Button) convertView.findViewById(R.id.add);
            holder.dedBut = (Button) convertView.findViewById(R.id.ded);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final Member member = list.get(position);
        holder.accountView.setText(member.account);
        holder.nameView.setText(member.name);
        holder.phoneView.setText(member.phone);
        holder.beernumView.setText(member.beernum+"");
        holder.numET.setText(member.deduction+"");
        holder.updateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.toUpMember(member);
            }
        });
        holder.deleteBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.requestDeleteMember(member.mid);
            }
        });
        holder.deductionBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onDeduction(member);
            }
        });
        holder.addBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.deduction ++;
                if(member.deduction > member.beernum){
                    member.deduction = (int)member.beernum;
                }
                notifyDataSetChanged();
            }
        });
        holder.dedBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.deduction --;
                if(member.deduction < 0){
                    member.deduction = 0;
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    public static class ViewHolder {
        public TextView accountView;
        public TextView nameView;
        public TextView phoneView;
        public TextView beernumView;
        public Button updateBut;
        public Button deleteBut;
        public Button deductionBut;
        public TextView numET;
        public Button dedBut;
        public Button addBut;


    }
    public void refreshData(List<Member>list){
        this.list = list;
        notifyDataSetChanged();
    }
}
