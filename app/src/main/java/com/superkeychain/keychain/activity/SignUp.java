package com.superkeychain.keychain.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserAction;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.view.ProgressDialogUtil;

public class SignUp extends AppCompatActivity implements View.OnClickListener {

    UserAction userAction;
    private ImageView ivBack;
    private TextView tvBack, tvContract;
    private EditText etUsername, etPassword;
    private CheckBox cbAgree, cbShowPassword;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvBack = (TextView) findViewById(R.id.tv_back2);
        tvContract = (TextView) findViewById(R.id.tv_contract);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        cbAgree = (CheckBox) findViewById(R.id.cb_agree);
        cbShowPassword = (CheckBox) findViewById(R.id.cb_show_password);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        userAction = new UserAction(this);

        ivBack.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tvContract.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        cbShowPassword.setOnClickListener(this);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (after < count) {
                    etPassword.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateSignUp();
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateSignUp();
            }
        });

        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        String username = etUsername.getText().toString();
                        String password = etPassword.getText().toString();
                        userAction.signUp(username, password, new ActionFinishedListener() {
                            @Override
                            public void doFinished(int status, String message, Object user) {
                                Toast.makeText(SignUp.this,message,Toast.LENGTH_SHORT).show();
                                if(status== Action.STATUS_CODE_OK){
                                    Intent intent = new Intent(SignUp.this, KeychainMain.class);
                                    intent.putExtra(User.USER_KEY, User.parseToJSON((User) user).toString());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    }
                }
                return false;
            }

        });

        cbAgree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.tv_back2:
                this.finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case R.id.btn_sign_up:
                if (validateSignUp()) {
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    final Dialog dialog = ProgressDialogUtil.createLoadingDialog(SignUp.this,"Please Wait...");
                    dialog.show();
                    userAction.signUp(username, password, new ActionFinishedListener() {
                        @Override
                        public void doFinished(int status, String message, Object user) {
                            dialog.dismiss();
                            Toast.makeText(SignUp.this,message,Toast.LENGTH_SHORT).show();
                            if(status==Action.STATUS_CODE_OK){
                                Intent intent = new Intent(SignUp.this, KeychainMain.class);
                                intent.putExtra(User.USER_KEY, User.parseToJSON((User) user).toString());
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                break;
            case R.id.cb_agree:
                validateSignUp();
                break;
            case R.id.tv_contract:
                Toast.makeText(SignUp.this, "Not Implemented Yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cb_show_password:
                if (cbShowPassword.isChecked()) {
                    int i = etPassword.getSelectionEnd();
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(i);
                } else {
                    int i = etPassword.getSelectionEnd();
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etPassword.setSelection(i);
                }
                break;
            default:
                break;

        }
    }

    private boolean validateSignUp() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        boolean isInputValid = cbAgree.isChecked() && userAction.validateInput(username, password, false);
        if (isInputValid && userAction.getAccountType() == UserAction.ACCOUNT_TYPE_EMAIL) {
            btnSignUp.setBackgroundResource(R.color.sky_blue);
            return true;
        } else {
            btnSignUp.setBackgroundResource(R.color.dark_gray);
            return false;
        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
