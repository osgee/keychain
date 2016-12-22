package com.superkeychain.keychain.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserServiceAction;
import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.Service;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.utils.ImageService;
import com.superkeychain.keychain.view.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends Activity implements View.OnClickListener {

	private ImageView imgAppLogo;
    private UserServiceAction userServiceAction;
    private User user;
    private Service service;
    private ThirdPartApp app;

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

		imgAppLogo = (ImageView) findViewById(R.id.img_app_logo);
        btnSignIn = (Button) findViewById(R.id.btn_service_sign_in);
        btnSignUp = (Button) findViewById(R.id.btn_service_sign_up);

        lvServiceAccounts = (ListView) findViewById(R.id.lv_service_accounts);

        if (null != extras) {
            user = User.parseFromJSON(extras.getString(User.USER_KEY));
            service = Service.parseFromJSON(extras.getString(Service.SERVICE_KEY));
            app = service.getServiceApp();
            userServiceAction = new UserServiceAction(ResultActivity.this, user);
            accounts = service.getServiceAccounts();

            new ImageService(ResultActivity.this,new ImageService.TaskFinishedListener() {
                @Override
                public void doFinished(Bitmap bitmap) {
                    if(bitmap!=null){
                        imgAppLogo.setImageBitmap(bitmap);
                    }
                }
            }).get(app.getAppLogoURI());
            if(accounts!=null&&accounts.size()>0){
                accountNames = new ArrayList<>();
                for (int i =0; i<accounts.size();i++){
                    accountNames.add(accounts.get(i).getUsername());
                }
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, accountNames);
                lvServiceAccounts.setAdapter(adapter);
                lvServiceAccounts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                lvServiceAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        account = accounts.get(position);
                    }
                });
                lvServiceAccounts.setSelection(0);
                account = accounts.get(0);
                btnSignIn.setOnClickListener(this);
                btnSignUp.setOnClickListener(this);
            }else{
                lvServiceAccounts.setVisibility(View.INVISIBLE);
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
                            finish();
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
