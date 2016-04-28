package com.superkeychain.keychain.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserAccountAction;
import com.superkeychain.keychain.action.UserAction;
import com.superkeychain.keychain.action.UserAppAction;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.repository.UserRepository;
import com.superkeychain.keychain.view.AccountFragment;
import com.superkeychain.keychain.view.BaseFragment;
import com.superkeychain.keychain.view.IconPagerAdapter;
import com.superkeychain.keychain.view.IconTabPageIndicator;
import com.superkeychain.keychain.view.MineFragment;
import com.superkeychain.keychain.view.ProgressDialogUtil;
import com.superkeychain.keychain.view.ScanFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KeychainMain extends FragmentActivity implements MineFragment.OnFragmentInteractionListener,
        AccountFragment.OnFragmentInteractionListener, ScanFragment.OnFragmentInteractionListener, View.OnClickListener {

    private UserRepository userRepository;
    private UserAction userAction;
    private UserAppAction userAppAction;
    private UserAccountAction userAccountAction;
    private User user;
    private boolean isVerified = false;

    private ViewPager mViewPager;
    private IconTabPageIndicator mIndicator;
    private ImageView ivAccountAdd;

    private List<RelativeLayout> rlActionBars = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(this);

        Intent intentFromSignIn = getIntent();
        String userJson = intentFromSignIn.getStringExtra(User.USER_KEY);
        User user = null;
        if (userJson != null) {
            user = User.parseFromJSON(userJson);
            userRepository.save(user);
        } else {
            user = userRepository.get();
        }

        userAction = new UserAction(this);
        if (user == null) {
            user = new User();
            Intent intent = new Intent(KeychainMain.this, SignIn.class);
            startActivity(intent);
            finish();
        } else {
            userAccountAction = new UserAccountAction(this, user);
            userAppAction = new UserAppAction(this);
            userAppAction.saveAllApps();
            Long cookieExpireTime = user.getCookieExpireTime();
            Long now = Calendar.getInstance().getTimeInMillis() / 1000;
            Log.d("cookieTime", cookieExpireTime + ":" + now);
            userAction.checkCookie(new ActionFinishedListener() {
                @Override
                public void doFinished(int status, String message, Object user) {
                    if (status < 0) {
                        Toast.makeText(KeychainMain.this, "Expired, Please Sign In", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(KeychainMain.this, SignIn.class);
                        startActivity(intent);
                        userAction.signOut(new ActionFinishedListener() {
                            @Override
                            public void doFinished(int status, String message, Object object) {
                            }
                        });
                        finish();
                    }
                    isVerified = true;
//                    refreshAccounts();
                }
            });

        }
        this.user = user;
        setContentView(R.layout.activity_keychain_main);
        ivAccountAdd = (ImageView) findViewById(R.id.iv_account_add);
        RelativeLayout rlBarAccount = (RelativeLayout) findViewById(R.id.rl_bar_account);
        RelativeLayout rlBarScan = (RelativeLayout) findViewById(R.id.rl_bar_scan);
        RelativeLayout rlBarMine = (RelativeLayout) findViewById(R.id.rl_bar_mine);
        rlActionBars.add(rlBarAccount);
        rlActionBars.add(rlBarScan);
        rlActionBars.add(rlBarMine);
        ivAccountAdd.setOnClickListener(this);

        initViews();
        showActionBarAt(0);
    }

    public void showActionBarAt(int i) {
        for (int j = 0; j < rlActionBars.size(); j++) {
            if (i == j) {
                rlActionBars.get(j).setVisibility(View.VISIBLE);
            } else {
                rlActionBars.get(j).setVisibility(View.GONE);
            }
        }
    }


    private void refreshAccountsFromServer() {
        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(KeychainMain.this, "Please Wait...");
        dialog.show();
        userAccountAction.getAccounts(new ActionFinishedListener() {
            @Override
            public void doFinished(int status, String message, Object object) {
                if (status == Action.STATUS_CODE_OK) {
                    User user = (User) object;
                    userRepository.save(user);
//                    initViews();
                } else {
                    Toast.makeText(KeychainMain.this, message, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

    }

    private void refreshAccounts(Account account) {
        FragmentAdapter adapter = (FragmentAdapter) mViewPager.getAdapter();
        AccountFragment accountFragment = (AccountFragment) adapter.getFragments().get(0);
        accountFragment.refreshAccounts(user.getAccounts());
    }

    private void addRefreshAccounts(Account account) {
        FragmentAdapter adapter = (FragmentAdapter) mViewPager.getAdapter();
        AccountFragment accountFragment = (AccountFragment) adapter.getFragments().get(0);
        accountFragment.addRefreshAccounts(account);
    }

    private void delRefreshAccounts(Account account) {
        FragmentAdapter adapter = (FragmentAdapter) mViewPager.getAdapter();
        AccountFragment accountFragment = (AccountFragment) adapter.getFragments().get(0);
        accountFragment.delRefreshAccounts(account);
    }

    private void updateRefreshAccounts(Account account) {
        FragmentAdapter adapter = (FragmentAdapter) mViewPager.getAdapter();
        AccountFragment accountFragment = (AccountFragment) adapter.getFragments().get(0);
        accountFragment.updateRefreshAccounts(account);
    }

    private void initViews() {
//        this.user = userRepository.get();
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mIndicator = (IconTabPageIndicator) findViewById(R.id.indicator);
        List<BaseFragment> fragments = initFragments();
        FragmentAdapter adapter = new FragmentAdapter(fragments, getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mIndicator.setViewPager(mViewPager);
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                showActionBarAt(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private List<BaseFragment> initFragments() {
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();

        BaseFragment userFragment = AccountFragment.newInstance(this.user.toJSONString());
//        BaseFragment userFragment = AccountFragmentBackUp.newInstance(this.user.toJSONString());
        userFragment.setTitle("账号");
        userFragment.setIconId(R.drawable.tab_account_selector);
        fragments.add(userFragment);

        BaseFragment noteFragment = ScanFragment.newInstance(this.user.toJSONString());
        noteFragment.setTitle("扫码");
        noteFragment.setIconId(R.drawable.tab_scan_selector);
        fragments.add(noteFragment);
/*
        BaseFragment contactFragment = new BaseFragment();
        contactFragment.setTitle("朋友");
        contactFragment.setIconId(R.drawable.tab_user_selector);
        fragments.add(contactFragment);*/

        BaseFragment recordFragment = MineFragment.newInstance(this.user.toJSONString());
        recordFragment.setTitle("我的");
        recordFragment.setIconId(R.drawable.tab_user_selector);
        fragments.add(recordFragment);

        return fragments;
    }

    @Override
    public void onFragmentInteraction(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_out:
                userAction.signOut(new ActionFinishedListener() {
                    @Override
                    public void doFinished(int status, String message, Object object) {
                    }
                });
                userRepository.delete();
                Intent intent = new Intent(KeychainMain.this, SignIn.class);
                startActivity(intent);
                finish();
                break;
            case R.id.rl_mine_info:
                Toast.makeText(KeychainMain.this, "not implemented yet", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_account_add:
                Intent intentAccountAdd = new Intent(KeychainMain.this, AccountCase.class);
                intentAccountAdd.putExtra(User.USER_KEY, User.parseToJSON(user).toString());
                startActivityForResult(intentAccountAdd, AccountCase.MODE_ADD);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//                refreshAccounts();
                break;
            case R.id.iv_notification:

                break;
            case R.id.iv_settings:

                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case AccountCase.ACCOUNT_CASE_ADD_SUCCEED:
//                Log.d("reenter", data.getStringExtra(Account.ACCOUNT_KEY));
                Account accountAdd = Account.parseFromJSON(data.getStringExtra(Account.ACCOUNT_KEY));
                if (accountAdd != null) {
                    user.getAccounts().add(accountAdd);
                    userRepository.save(user);
                    addRefreshAccounts(accountAdd);
                }
                break;
            case AccountCase.ACCOUNT_CASE_DELETE_SUCCEED:
                Account accountDel = Account.parseFromJSON(data.getStringExtra(Account.ACCOUNT_KEY));
                Log.d("DELETE_SUCCEED", accountDel.toJSONString());
                int i;
                boolean isExist = false;
                for (i = 0; i < user.getAccounts().size(); i++) {
                    if (accountDel != null && accountDel.getAccountId() != null && accountDel.getAccountId().equals(user.getAccounts().get(i).getAccountId())) {
                        isExist = true;
                        break;
                    }
                }
                if (isExist) {
                    user.getAccounts().remove(i);
                    userRepository.save(user);
                    delRefreshAccounts(accountDel);
                }
                break;
            case AccountCase.ACCOUNT_CASE_UPDATE_SUCCEED:
                Account accountUpdate = Account.parseFromJSON(data.getStringExtra(Account.ACCOUNT_KEY));
                Log.d("UPDATE_SUCCEED", accountUpdate.toJSONString());
                int i2;
                boolean isExist2 = false;
                for (i2 = 0; i2 < user.getAccounts().size(); i2++) {
                    if (accountUpdate != null && accountUpdate.getAccountId() != null && accountUpdate.getAccountId().equals(user.getAccounts().get(i2).getAccountId())) {
                        isExist2 = true;
                        break;
                    }
                }
                if (isExist2) {
                    user.getAccounts().remove(i2);
                    user.getAccounts().add(accountUpdate);
                    userRepository.save(user);
                    updateRefreshAccounts(accountUpdate);
                }
                break;
        }
    }

    @Override
    public void onFragmentInteraction(int id) {
        switch (id){

            case R.id.rl_scan:
                        Intent openCameraIntent = new Intent(KeychainMain.this, CaptureActivity.class);
                        startActivity(openCameraIntent);
                break;
                default:
                    break;
        }
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

        public List<BaseFragment> getFragments() {
            return mFragments;
        }
    }

}
