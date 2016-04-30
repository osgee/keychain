package com.superkeychain.keychain.view;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.utils.ImageService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/24/16.
 */
public class AccountArrayAdapter extends BaseAdapter {
    Activity activity;
    List<AccountHolder> accountHolders;

    List<Account> accounts;
    private LayoutInflater mInflater;
    private int resId;

    public AccountArrayAdapter(final Activity activity, int resId, List<Account> accounts) {
        this.accounts = accounts;
        this.activity = activity;
        this.resId = resId;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        accountHolders = new ArrayList<>();
        this.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                refreshAll();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return accounts.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            AccountHolder accountHolder = new AccountHolder();
            convertView = mInflater.inflate(resId, null);
            accountHolder.view = convertView;
            accountHolder.account = (Account) getItem(position);
            accountHolders.add(accountHolder);
            refresh(convertView, accountHolder);
        }
        return convertView;
    }

    class AccountHolder {
        public ImageView ivAppLogo;
        public TextView tvAccountName;
        public TextView tvAccountPassword;
        public RelativeLayout rlShowPassword;
        public Button btnLock;
        public Account account;
        public View view;
    }

    public void refresh(int position) {
        if(position<accountHolders.size())
        refresh(accountHolders.get(position).view, accountHolders.get(position));
    }

    public void refresh(View view, final AccountHolder accountHolder){
        accountHolder.ivAppLogo = (ImageView) view.findViewById(R.id.iv_app_logo);
        accountHolder.tvAccountName = (TextView) view.findViewById(R.id.tv_account_name);
        accountHolder.tvAccountPassword = (TextView) view.findViewById(R.id.tv_password);
        accountHolder.rlShowPassword = (RelativeLayout) view.findViewById(R.id.rl_show_password);
        accountHolder.btnLock = (Button) view.findViewById(R.id.btn_lock);
        accountHolder.tvAccountName.setText(accountHolder.account.toString());
        accountHolder.tvAccountPassword.setText("******");
        new ImageService(activity, new ImageService.TaskFinishedListener() {
            @Override
            public void doFinished(Bitmap bitmap) {
                if(bitmap!=null)
                    accountHolder.ivAppLogo.setImageBitmap(bitmap);
            }
        }).get(accountHolder.account.getApp().getAppLogoURI());

        final String finalPassword = accountHolder.account.getPassword();
        accountHolder.btnLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    accountHolder.tvAccountPassword.setText(finalPassword);
                    accountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_eye_open);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    accountHolder.tvAccountPassword.setText("******");
                    accountHolder.btnLock.setBackgroundResource(R.mipmap.ic_action_eye_closed);
                }
                return false;
            }
        });


    }

    public void refreshAll(){
        for (int i=0; i<getCount(); i++){
            refresh(i);
        }
    }

}
