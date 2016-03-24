package com.superkeychain.keychain.view;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.AccountContent;

import java.util.List;

/**
 * Created by taofeng on 3/24/16.
 */
public class AccountArrayAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private int resId;
    Activity activity;
    List<Account> accounts;
    public AccountArrayAdapter(Activity activity, int resId, List<Account> accounts){
        this.accounts = accounts;
        this.activity = activity;
        this.resId = resId;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    class AccountHolder{
        public ImageView ivAppLogo;
        public TextView tvAccountName;
        public TextView tvAccountPassword;
        public Button btnLock;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountHolder accountHolder = null;
        Log.d("in accountArray","ddddddddddddd");
        if(convertView==null){
            accountHolder = new AccountHolder();
            convertView = mInflater.inflate(resId,null);
            accountHolder.ivAppLogo = (ImageView)convertView.findViewById(R.id.iv_app_logo);
            accountHolder.tvAccountName = (TextView)convertView.findViewById(R.id.tv_account_name);
            accountHolder.tvAccountPassword = (TextView) convertView.findViewById(R.id.tv_password);
            accountHolder.btnLock = (Button) convertView.findViewById(R.id.btn_lock);
        }else{
            accountHolder = (AccountHolder) convertView.getTag();
        }

        accountHolder.ivAppLogo.setImageResource(R.drawable.logo);
        accountHolder.tvAccountName.setText("Account Name");
        accountHolder.tvAccountPassword.setText("******");
        accountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_lock_closed);

        accountHolder.btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity,"Not Implemented Yet",Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }
}
