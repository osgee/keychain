package com.superkeychain.keychain.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.activity.AccountCase;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.AccountContent;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String JSON_USER = "JSON_USER";

    // TODO: Rename and change types of parameters
    private String mJsonUser;
    private User mUser;
    private List<Account> accounts;

    ListView listView;
    BaseAdapter adapter;


    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jsonUser Parameter 1.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String jsonUser) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(JSON_USER, jsonUser);
        fragment.setArguments(args);
        return fragment;
    }

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mJsonUser = getArguments().getString(JSON_USER);
            mUser = User.parseFromJSON(mJsonUser);
            if(mUser!=null&&mUser.getAccounts()!=null&&mUser.getAccounts().size()>0){
                accounts = mUser.getAccounts();
            }else{
                accounts = new ArrayList<>();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_account, container, false);
         listView = (ListView) view.findViewById(R.id.list);
//        AccountContent.accounts=accounts;
        adapter = new AccountArrayAdapter(getActivity(),
                R.layout.account_view, accounts);
        listView.setAdapter(adapter);
//        listView.setFocusable(false);
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



    public void refreshAccounts(List<Account> accounts){
        if(accounts!=null&&adapter!=null){
//            this.accounts = accounts;
            this.accounts.clear();
            this.accounts.addAll(accounts);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public void addRefreshAccounts(Account account){
        if(account!=null&&adapter!=null){
//            this.accounts = accounts;
//            this.accounts.clear();
            this.accounts.add(account);
            adapter.notifyDataSetChanged();
        }
    }

    public void delRefreshAccounts(Account account) {
        if(account!=null&&adapter!=null){
//            this.accounts = accounts;
//            this.accounts.clear();
            int i;
            boolean isExist = false;
            for(i=0;i<this.accounts.size();i++){
                if(account.getAccountId()!=null&&account.getAccountId().equals(this.accounts.get(i).getAccountId())) {
                    isExist = true;
                    break;
                }
            }
            if(isExist){
                this.accounts.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void updateRefreshAccounts(Account account) {
        if(account!=null&&adapter!=null){
//            this.accounts = accounts;
//            this.accounts.clear();
            int i;
            boolean isExist = false;
            for(i=0;i<this.accounts.size();i++){
                if(account.getAccountId()!=null&&account.getAccountId().equals(this.accounts.get(i).getAccountId())) {
                    isExist = true;
                    break;
                }
            }
            if(isExist){
                this.accounts.remove(i);
                this.accounts.add(account);
                adapter.notifyDataSetChanged();
            }
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
