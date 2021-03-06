package com.superkeychain.keychain.view;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.activity.AccountCase;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.User;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String JSON_USER = "JSON_USER";
    ListView listView;
    BaseAdapter adapter;

    private String mJsonUser;
    private User mUser;
    private List<Account> accounts;
    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jsonUser Parameter 1.
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment newInstance(String jsonUser) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(JSON_USER, jsonUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJsonUser = getArguments().getString(JSON_USER);
            mUser = User.parseFromJSON(mJsonUser);
            if (mUser != null && mUser.getAccounts() != null && mUser.getAccounts().size() > 0) {
                accounts = mUser.getAccounts();
            } else {
                accounts = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        final PtrFrameLayout ptrFrame = (PtrFrameLayout) view;
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ptrFrame.refreshComplete();
                    }
                }, 1800);
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new AccountArrayAdapter(getActivity(),
                R.layout.account_view, accounts);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account a = accounts.get(position);
                Intent intent = new Intent(AccountFragment.this.getActivity(), AccountCase.class);
                intent.putExtra(User.USER_KEY, mUser.toJSONString());
                intent.putExtra(Account.ACCOUNT_KEY, a.toJSONString());
                startActivityForResult(intent, AccountCase.MODE_REVISE);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void refreshAccounts(List<Account> accounts) {
        if (accounts != null && adapter != null) {
            this.accounts.clear();
            this.accounts.addAll(accounts);
        }
    }


    public void addRefreshAccounts(Account account) {
        if (account != null && adapter != null) {
            this.accounts.add(account);
            listView.setAdapter(adapter);
        }
    }

    public void delRefreshAccounts(Account account) {
        if (account != null && adapter != null) {
            int i;
            boolean isExist = false;
            for (i = 0; i < this.accounts.size(); i++) {
                if (account.getAccountId() != null && account.getAccountId().equals(this.accounts.get(i).getAccountId())) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                this.accounts.remove(i);
            }
        }
    }

    public void updateRefreshAccounts(Account account) {
        if (account != null && adapter != null) {
            int i;
            boolean isExist = false;
            for (i = 0; i < this.accounts.size(); i++) {
                if (account.getAccountId() != null && account.getAccountId().equals(this.accounts.get(i).getAccountId())) {
                    isExist = true;
                    break;
                }
            }
            if (isExist) {
                this.accounts.get(i).update(account);
            }
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
