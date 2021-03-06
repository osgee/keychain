package com.superkeychain.keychain.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final String JSON_USER = "UserJsonString";

    private String mJsonUser;
    private User user;

    private OnFragmentInteractionListener mListener;

    private TextView tvUsername;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param jsonUser Parameter 1
     * @return A new instance of fragment MineFragment.
     */
    public static MineFragment newInstance(String jsonUser) {
        MineFragment fragment = new MineFragment();
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
            user = User.parseFromJSON(mJsonUser);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mine_list_view, container, false);
        Button btnSignOut = (Button) view.findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(this);
        RelativeLayout rlMineInfo = (RelativeLayout) view.findViewById(R.id.rl_mine_info);
        rlMineInfo.setOnClickListener(this);
        RelativeLayout rlMinePattern = (RelativeLayout) view.findViewById(R.id.rl_pattern);
        rlMinePattern.setOnClickListener(this);
        tvUsername = (TextView) view.findViewById(R.id.tv_user_name);
        tvUsername.setText(user.getAccountName());
        return view;
    }

    public void onPressed(View view) {
        if (mListener != null) {
            mListener.onMineFragmentInteraction(view);
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

    @Override
    public void onClick(View v) {
        onPressed(v);
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
        public void onMineFragmentInteraction(View view);
    }

}
