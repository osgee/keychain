package com.superkeychain.keychain.view;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.UserAccountAction;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.repository.UserRepository;

/**
 * User: Geek_Soledad(msdx.android@qq.com)
 * Date: 2014-08-27
 * Time: 09:01
 * FIXME
 */
public class BaseFragment extends ListFragment{

    private Button btnSignOut, btnAccountAdd, btnAccountsGet;
    private UserRepository userRepository;
    private User user;

    private UserAccountAction userAccountAction;

    private String title;
    private int iconId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null, false);

   /*   btnSignOut = (Button) view.findViewById(R.id.btn_sign_out);
        btnAccountAdd = (Button) view.findViewById(R.id.btn_account_add);
        btnAccountsGet = (Button) view.findViewById(R.id.btn_accounts_get);

        userRepository = new UserRepository(BaseFragment.this.getActivity(), BaseFragment.this.getContext());
        user = userRepository.get();

        userAccountAction = new UserAccountAction(BaseFragment.this.getActivity());


        btnSignOut.setOnClickListener(this);
        btnAccountAdd.setOnClickListener(this);
        btnAccountsGet.setOnClickListener(this);*/

        return view;
    }
  /*  @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_out:
               *//* UserAction userAction = new UserAction(BaseFragment.this.getActivity());
                userAction.signOut(user, new ActionFinishedListener() {
                    @Override
                    public void doFinished(int status, String message, Object object) {
                        Toast.makeText(BaseFragment.this.getActivity().getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    }
                });*//*
                break;
            case R.id.btn_account_add:
                Intent intentAccountAdd = new Intent(BaseFragment.this.getContext(), AccountCase.class);
                intentAccountAdd.putExtra(User.USER_KEY, User.parseToJSON(user).toString());
                BaseFragment.this.startActivity(intentAccountAdd);
                break;
            case R.id.btn_accounts_get:
                userAccountAction.getAccounts();
                break;
            default:
                break;

        }
    }*/

}
