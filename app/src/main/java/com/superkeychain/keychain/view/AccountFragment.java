package com.superkeychain.keychain.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.AccountContent;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.view.dummy.DummyContent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class AccountFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String JSON_USER = "UserJsonString";


    // TODO: Rename and change types of parameters
    private String mJsonUser;


    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types of parameters
    public static AccountFragment newInstance(String jsonUser) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(JSON_USER, jsonUser);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if (getArguments() != null) {
            mJsonUser = getArguments().getString(JSON_USER);
        }
        User user = User.parseFromJSON(mJsonUser);
        List<Account> accounts =null;
        if(user.getAccounts()!=null && user.getAccounts().size()>1){
            accounts = user.getAccounts();
        }else{
            accounts = new ArrayList<>();
        }

        AccountContent.addAccounts(accounts);*/

        List<Account> accounts1 = new ArrayList<>();
        Account account1 = new Account();
        account1.setAccountId("123456");
        account1.setAccountType(Account.AccountType.USERNAME);
        account1.setUsername("taofeng");
        accounts1.add(account1);


        // TODO: Change Adapter to display your content
        setListAdapter(new AccountArrayAdapter(getActivity(),
                R.layout.account_view,  accounts1));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_account_list_view,null,false);
        ListView listView = (ListView) view.findViewById(android.R.id.list);
  /*      if (getArguments() != null) {
            mJsonUser = getArguments().getString(JSON_USER);
        }
        User user = User.parseFromJSON(mJsonUser);
        List<Account> accounts =null;
        if(user.getAccounts()!=null && user.getAccounts().size()>1){
            accounts = user.getAccounts();
        }else{
            accounts = new ArrayList<>();
        }
*/
        List<Account> accounts1 = new ArrayList<>();
        Account account1 = new Account();
        account1.setAccountId("123456");
        account1.setAccountType(Account.AccountType.USERNAME);
        account1.setUsername("taofeng");
        accounts1.add(account1);

//        AccountContent.addAccounts(accounts);
        AccountContent.addAccounts(accounts1);


        // TODO: Change Adapter to display your content

        listView.setAdapter(new AccountArrayAdapter(getActivity(),
                R.layout.account_view, AccountContent.accounts));

        return view;
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(AccountContent.getAccount(position).getAccountId());
        }
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
        public void onFragmentInteraction(String id);
    }

}
