package com.superkeychain.keychain.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.UserAction;
import com.superkeychain.keychain.action.UserAppAction;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.repository.AppRepository;
import com.superkeychain.keychain.repository.UserRepository;
import com.superkeychain.keychain.view.BaseFragment;
import com.superkeychain.keychain.view.IconPagerAdapter;
import com.superkeychain.keychain.view.IconTabPageIndicator;
import com.superkeychain.keychain.view.MineFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KeychainMain extends FragmentActivity implements MineFragment.OnFragmentInteractionListener{

    private UserRepository userRepository;
    private AppRepository appRepository;
    private UserAction userAction;
    private UserAppAction userAppAction;
    private boolean isVerified = false;

    private ViewPager mViewPager;
    private IconTabPageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(this, KeychainMain.this);
        appRepository = new AppRepository(this, KeychainMain.this);

        Intent intentFromSignIn = getIntent();
        String userJson = intentFromSignIn.getStringExtra(User.USER_KEY);
        if (userJson != null) {
            Log.d("inuserRepository", userJson);
            userRepository.save(User.parseFromJSON(userJson));
        }

        userAction = new UserAction(this, KeychainMain.this);
        User user = userRepository.get();
        if (user == null) {
            Intent intent = new Intent(KeychainMain.this, SignIn.class);
            startActivity(intent);
            finish();
        } else {
            userAppAction = new UserAppAction(this, KeychainMain.this, user);
            userAppAction.saveAllApps();
            Long cookieExpireTime = user.getCookieExpireTime();
            Long now = Calendar.getInstance().getTimeInMillis() / 1000;
            Log.d("cookieTime", cookieExpireTime + ":" + now);
            if (now > cookieExpireTime) {
                if (userAction.checkCookie(user) < 0) {
                    Intent intent = new Intent(KeychainMain.this, SignIn.class);
                    startActivity(intent);
                    userAction.signOut(user);
                    finish();
                }
                isVerified = true;
            }
        }


        setContentView(R.layout.activity_keychain_main);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        initViews();
    }

    private void initViews() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (IconTabPageIndicator) findViewById(R.id.indicator);
        List<BaseFragment> fragments = initFragments();
        FragmentAdapter adapter = new FragmentAdapter(fragments, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
    }

    private List<BaseFragment> initFragments() {
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();

        BaseFragment userFragment = new BaseFragment();
        userFragment.setTitle("账号");
        userFragment.setIconId(R.drawable.tab_account_selector);
        fragments.add(userFragment);

        BaseFragment noteFragment = new BaseFragment();
        noteFragment.setTitle("扫码");
        noteFragment.setIconId(R.drawable.tab_scan_selector);
        fragments.add(noteFragment);
/*
        BaseFragment contactFragment = new BaseFragment();
        contactFragment.setTitle("朋友");
        contactFragment.setIconId(R.drawable.tab_user_selector);
        fragments.add(contactFragment);*/

        BaseFragment recordFragment = new MineFragment();
        recordFragment.setTitle("我的");
        recordFragment.setIconId(R.drawable.tab_user_selector);
        fragments.add(recordFragment);

        return fragments;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class FragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
        private List<BaseFragment> mFragments;

        public FragmentAdapter(List<BaseFragment> fragments, FragmentManager fm) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getIconResId(int index) {
            return mFragments.get(index).getIconId();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getTitle();
        }
    }

}
