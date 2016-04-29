package com.superkeychain.keychain.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserServiceAction;
import com.superkeychain.keychain.decode.DecodeThread;
import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.Service;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.view.ProgressDialogUtil;
import com.superkeychain.keychain.view.ServiceAccountArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends Activity implements View.OnClickListener {

	private ImageView mAppImage;
    private UserServiceAction userServiceAction;
    private User user;
    private Service service;

    private ListView lvServiceAccounts;
    private Button btnSignIn;
    private Button btnSignUp;


    private ArrayAdapter<String> adapter;

    private List<Account> accounts;
    private Account account;
    private List<String> accountNames;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		Bundle extras = getIntent().getExtras();

		mAppImage = (ImageView) findViewById(R.id.img_app_logo);
        btnSignIn = (Button) findViewById(R.id.btn_service_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_service_sign_up);

        lvServiceAccounts = (ListView) findViewById(R.id.lv_service_accounts);

        if (null != extras) {
            user = User.parseFromJSON(extras.getString(User.USER_KEY));
            service = Service.parseFromJSON(extras.getString(Service.SERVICE_KEY));
            userServiceAction = new UserServiceAction(ResultActivity.this, user);
            accounts = service.getServiceAccounts();
            if(accounts!=null&&accounts.size()>0){
                accountNames = new ArrayList<>();
                for (int i =0; i<accounts.size();i++){
                    accountNames.add(accounts.get(i).toString());
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, accountNames);
                lvServiceAccounts.setAdapter(adapter);
                lvServiceAccounts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                lvServiceAccounts.setSelection(0);
                lvServiceAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        account = accounts.get(position);
                        Toast.makeText(ResultActivity.this, "clicked" + position, Toast.LENGTH_SHORT).show();
                    }
                });
                btnSignIn.setOnClickListener(this);
                btnSignUp.setOnClickListener(this);
            }else{
                lvServiceAccounts.setVisibility(View.GONE);
                btnSignIn.setVisibility(View.GONE);
                btnSignUp.setOnClickListener(this);
            }
		}
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_service_sign_in:
                service.setServiceAccount(account);
                final Dialog dialog = ProgressDialogUtil.createLoadingDialog(ResultActivity.this,"Please Wait...");
                dialog.show();
                userServiceAction.confirmService(new ActionFinishedListener() {
                    @Override
                    public void doFinished(int status, String message, Object object) {
                        dialog.dismiss();
                        if(status==Action.STATUS_CODE_OK){
                            Toast.makeText(ResultActivity.this,"Sign In Succeed!",Toast.LENGTH_SHORT).show();
                        }
//                        ResultActivity.this.finish();
                    }
                },service);
                break;
            case R.id.btn_service_sign_up:

                break;
            default:
                break;
        }
    }
}
