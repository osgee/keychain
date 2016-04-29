package com.superkeychain.keychain.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by taofeng on 4/29/16.
 */

public class ServiceAccountArrayAdapter extends BaseAdapter {
    Activity activity;
    List<Account> accounts;
    List<RadioButton> radioGroup = new ArrayList<RadioButton>();
    private LayoutInflater mInflater;
    private int resId;

    public ServiceAccountArrayAdapter(Activity activity, int resId, List<Account> accounts) {
        this.accounts = accounts;
        this.activity = activity;
        this.resId = resId;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

//        return accounts.size();
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 100l;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ServiceAccountHolder accountHolder = null;
        accountHolder = new ServiceAccountHolder();
        convertView = mInflater.inflate(resId, null);
        accountHolder.rlSelectedAccount = (RelativeLayout) convertView.findViewById(R.id.rl_selected_account);
        accountHolder.rbSeletedAccount = (RadioButton) convertView.findViewById(R.id.rb_selected_account);
        radioGroup.add(accountHolder.rbSeletedAccount);
        accountHolder.tvAccountName = (TextView) convertView.findViewById(R.id.tv_account_name);
        accountHolder.tvAccountName.setText(accounts.get(position).toString());
        return convertView;
    }

    public void setSelectedPosition(int position){
        for(int i=0; i<radioGroup.size(); i++){
            if(position==i){
                radioGroup.get(i).setSelected(true);
            }else{
                radioGroup.get(i).setSelected(false);
            }
        }
    }

    class ServiceAccountHolder {
        public RelativeLayout rlSelectedAccount;
        public RadioButton rbSeletedAccount;
        public TextView tvAccountName;
    }

}
