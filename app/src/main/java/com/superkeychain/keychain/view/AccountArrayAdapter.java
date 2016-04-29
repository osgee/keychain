package com.superkeychain.keychain.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;

import java.util.List;

/**
 * Created by taofeng on 3/24/16.
 */
public class AccountArrayAdapter extends BaseAdapter {
    Activity activity;

    List<Account> accounts;
    private LayoutInflater mInflater;
    private int resId;

    public AccountArrayAdapter(Activity activity, int resId, List<Account> accounts) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        AccountHolder accountHolder = null;
//        if(convertView==null){
        accountHolder = new AccountHolder();
        convertView = mInflater.inflate(resId, null);
        accountHolder.ivAppLogo = (ImageView) convertView.findViewById(R.id.iv_app_logo);
        accountHolder.tvAccountName = (TextView) convertView.findViewById(R.id.tv_account_name);
        accountHolder.tvAccountPassword = (TextView) convertView.findViewById(R.id.tv_password);
        accountHolder.rlShowPassword = (RelativeLayout) convertView.findViewById(R.id.rl_show_password);
        accountHolder.btnLock = (Button) convertView.findViewById(R.id.btn_lock);
//        }else{
//            accountHolder = (AccountHolder) convertView.getTag();
//        }

        accountHolder.ivAppLogo.setImageResource(R.drawable.logo);
        accountHolder.tvAccountName.setText(accounts.get(position).toString());
//        accountHolder.tvAccountName.setText(accounts.get(position).toString());
        accountHolder.tvAccountPassword.setText("******");
//        accountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_lock_closed);

/*        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    v.setBackgroundResource(R.color.gray_background);
                }else{
                    v.setBackgroundResource(R.color.gray_account_view);
                }
                return false;
            }
        });*/

        final AccountHolder finalAccountHolder = accountHolder;
        final String finalPassword = accounts.get(position).getPassword();
        accountHolder.btnLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    finalAccountHolder.tvAccountPassword.setText(finalPassword);
                    finalAccountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_eye_open);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    finalAccountHolder.tvAccountPassword.setText("******");
                    finalAccountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_eye_closed);
                }
                return false;
            }
        });

        return convertView;
    }

    class AccountHolder {
        public ImageView ivAppLogo;
        public TextView tvAccountName;
        public TextView tvAccountPassword;
        public RelativeLayout rlShowPassword;
        public Button btnLock;
    }

}
