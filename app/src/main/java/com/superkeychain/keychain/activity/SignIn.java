package com.superkeychain.keychain.activity;


import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserAction;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.view.ProgressDialogUtil;


public class SignIn extends AppCompatActivity implements View.OnClickListener {

    private static String LANGUAGE;
    private TextView tvLanguage, tvSignUp;
    private EditText etUsername, etPassword;
    private Button btnSignIn;
    private UserAction userAction;
    private CheckBox cbShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        LANGUAGE = getResources().getString(R.string.Language);
        if (!sharedPreferences.contains(LANGUAGE)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(LANGUAGE, R.integer.Chinese);
            editor.apply();
//            editor.commit();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        tvLanguage = (TextView) findViewById(R.id.tv_language);
        tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        cbShowPassword = (CheckBox) findViewById(R.id.cb_show_password);
        userAction = new UserAction(this);

        btnSignIn.setOnClickListener(this);
        tvSignUp.setOnClickListener(this);
        tvLanguage.setOnClickListener(this);
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
                validateSignIn(false);
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
                validateSignIn(false);
            }
        });

        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        signIn();
                    }
                }
                return false;
            }

        });

    }

    private boolean validateSignIn(boolean showToast) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        boolean isValid = userAction.validateInput(username, password, showToast);
        if (isValid) {
            btnSignIn.setBackgroundResource(R.color.sky_blue);
            return true;
        } else {
            btnSignIn.setBackgroundResource(R.color.dark_gray);
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_in:
                signIn();
                break;
            case R.id.tv_sign_up:
                Intent intentSignUp = new Intent(SignIn.this, SignUp.class);
                startActivity(intentSignUp);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
//                this.finish();
                break;
            case R.id.tv_language:
                Intent intentLanguage = new Intent(SignIn.this, LanguageChooser.class);
//                intentLanguage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(intentLanguage);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
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

    private void signIn() {
        if (validateSignIn(true)) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            final Dialog dialog = ProgressDialogUtil.createLoadingDialog(SignIn.this, "Please Wait...");
            dialog.show();
            userAction.signIn(username, password, new ActionFinishedListener() {
                @Override
                public void doFinished(int status, String message, Object user) {
                    dialog.dismiss();
                    Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                    if (status == Action.STATUS_CODE_OK) {
                        Intent intent = new Intent(SignIn.this, KeychainMain.class);
                        intent.putExtra(User.USER_KEY, User.parseToJSON((User) user).toString());
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }


}
