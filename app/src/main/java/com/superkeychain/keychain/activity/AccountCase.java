package com.superkeychain.keychain.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.action.ActionFinishedListener;
import com.superkeychain.keychain.action.UserAccountAction;
import com.superkeychain.keychain.action.UserAppAction;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.utils.InputValidateUtils;
import com.superkeychain.keychain.view.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

public class AccountCase extends AppCompatActivity implements View.OnClickListener {
    public static final int MODE_ADD = 1;
    public static final int MODE_REVISE = 2;
    public static final int ACCOUNT_CASE_ADD_SUCCEED = 1;
    public static final int ACCOUNT_CASE_DELETE_SUCCEED = 2;
    public static final int ACCOUNT_CASE_UPDATE_SUCCEED = 3;
    private ImageView ivBack;
    private Button btnAccountAdd, btnAccountUpdate, btnAccountDelete;
    private TextView tvBack, tvContract;
    private EditText etUsername, etPassword, etEmail, etCellphone;
    private CheckBox cbAgree, cbShowPassword;
    private Spinner spinnerApps;
    private UserAccountAction userAccountAction;
    private UserAppAction userAppAction;
    private User user;
    private List<ThirdPartApp> apps;
    private Account account;
    private boolean isChanged = false;
    private int mode = MODE_ADD;

    private int spinnerPosition;
    private boolean isSpinnerPositionChanged = false;

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_case);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        tvBack = (TextView) findViewById(R.id.tv_back2);
        tvContract = (TextView) findViewById(R.id.tv_contract);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etEmail = (EditText) findViewById(R.id.et_email);
        etCellphone = (EditText) findViewById(R.id.et_cellphone);
        cbAgree = (CheckBox) findViewById(R.id.cb_agree);
        cbShowPassword = (CheckBox) findViewById(R.id.cb_show_password);
        btnAccountAdd = (Button) findViewById(R.id.btn_account_add);
        btnAccountUpdate = (Button) findViewById(R.id.btn_account_update);
        btnAccountDelete = (Button) findViewById(R.id.btn_account_delete);
        spinnerApps = (Spinner) findViewById(R.id.spinner_apps);


        Intent intent = getIntent();


        user = User.parseFromJSON(intent.getStringExtra(User.USER_KEY));
        account = Account.parseFromJSON(intent.getStringExtra(Account.ACCOUNT_KEY));
        apps = new ArrayList<>();
//        String appJSONString = intent.getStringExtra(ThirdPartApp.APPS_KEY);

        userAccountAction = new UserAccountAction(this, user);
        userAppAction = new UserAppAction(this, user);

        if (account != null) {
            setMode(MODE_REVISE);
            etUsername.setText(account.getUsername());
            etPassword.setText(account.getPassword());
            if (!(account.getEmail() == null || "null".equals(account.getEmail())))
                etEmail.setText(account.getEmail());
            if (!(account.getCellphone() == null || "null".equals(account.getCellphone())))
                etCellphone.setText(account.getCellphone());
//            spinnerApps.setSelection();
            btnAccountAdd.setVisibility(View.INVISIBLE);
            btnAccountUpdate.setVisibility(View.VISIBLE);
            btnAccountDelete.setVisibility(View.VISIBLE);
            userAppAction.getAllApps(new ActionFinishedListener() {
                @Override
                public void doFinished(int status, String message, Object appsObject) {
                    if (status == Action.STATUS_CODE_OK) {
                        apps = (List<ThirdPartApp>) appsObject;
                        List<String> appsName = new ArrayList<String>();
                        if (apps != null && apps.size() > 0) {
                            for (ThirdPartApp app : apps) {
                                appsName.add(app.getAppName());
                            }
                            //适配器
                            ArrayAdapter<String> adapterApps = new ArrayAdapter<String>(AccountCase.this, android.R.layout.simple_spinner_item, appsName) {

                            };
                            //设置样式
                            adapterApps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //加载适配器
                            spinnerApps.setAdapter(adapterApps);
                        }

                        if (!(account.getApp() == null)) {
                            spinnerApps.setSelection(getAppPosition(account.getApp().getAppId()));
                            spinnerPosition = spinnerApps.getSelectedItemPosition();
                        }

                    }


                }
            });

        } else {
            setMode(MODE_ADD);
            account = new Account();
            userAppAction.getAllApps(new ActionFinishedListener() {
                @Override
                public void doFinished(int status, String message, Object appsObject) {
                    apps = (List<ThirdPartApp>) appsObject;
                    List<String> appsName = new ArrayList<String>();
                    if (apps != null && apps.size() > 0) {
                        for (ThirdPartApp app : apps) {
                            appsName.add(app.getAppName());
                        }
                        //适配器
                        ArrayAdapter<String> adapterApps = new ArrayAdapter<String>(AccountCase.this, android.R.layout.simple_spinner_item, appsName) {

                        };
                        //设置样式
                        adapterApps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        spinnerApps.setAdapter(adapterApps);
                    }

                }
            });
        }

        ivBack.setOnClickListener(this);
        tvBack.setOnClickListener(this);
        tvContract.setOnClickListener(this);
        btnAccountAdd.setOnClickListener(this);
        btnAccountUpdate.setOnClickListener(this);
        btnAccountDelete.setOnClickListener(this);
        cbAgree.setOnClickListener(this);
        cbShowPassword.setOnClickListener(this);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mode == MODE_REVISE) {
                    isChanged = getIsChanged();
                }
                validateInput(false);
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
                if (mode == MODE_REVISE) {
                    isChanged = getIsChanged();
                }
                validateInput(false);
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mode == MODE_REVISE) {
                    isChanged = getIsChanged();
                }
                validateInput(false);
            }
        });

        etCellphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mode == MODE_REVISE) {
                    isChanged = getIsChanged();
                }
                validateInput(false);
            }
        });


        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && validateInput(true)) {
                        addAccount();
                    }
                }
                return false;
            }

        });

        spinnerApps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSpinnerPositionChanged = position != spinnerPosition;
                if (mode == MODE_REVISE)
                    isChanged = getIsChanged();
                validateInput(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean validateInput(boolean showToast) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();
        String cellphone = etCellphone.getText().toString();

        boolean isInputValid = cbAgree.isChecked() && userAccountAction.validateInput(username, password, showToast);

        isInputValid = isInputValid && ("".equals(email) || InputValidateUtils.isEmail(email));
        isInputValid = isInputValid && ("".equals(cellphone) || InputValidateUtils.isCellphone(cellphone));
        isInputValid = isInputValid && apps != null && spinnerApps != null && spinnerApps.getSelectedItemPosition() >= 0 && spinnerApps.getSelectedItemPosition() < apps.size();

        if (isInputValid) {
            btnAccountAdd.setBackgroundResource(R.color.sky_blue);
            if (mode == MODE_REVISE && isChanged)
                btnAccountUpdate.setBackgroundResource(R.color.sky_blue);
            return true;
        } else {
            btnAccountAdd.setBackgroundResource(R.color.dark_gray);
            if (mode == MODE_REVISE && !isChanged)
                btnAccountUpdate.setBackgroundResource(R.color.dark_gray);
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
            case R.id.tv_back2:
                this.finish();
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                break;
            case R.id.btn_account_add:
                addAccount();
                break;
            case R.id.btn_account_update:
                updateAccount();
                break;
            case R.id.btn_account_delete:
                deleteAccount();
                break;
            case R.id.cb_agree:
                validateInput(false);
                break;
            case R.id.tv_contract:
                Toast.makeText(AccountCase.this, "Not Implemented Yet", Toast.LENGTH_SHORT).show();
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

    private void deleteAccount() {
        if (mode == MODE_REVISE && validateInput(false)) {
            btnAccountDelete.setClickable(false);
            final Dialog dialog = ProgressDialogUtil.createLoadingDialog(AccountCase.this, "Please Wait...");
            dialog.show();
            userAccountAction.deleteAccount(account, new ActionFinishedListener() {

                @Override
                public void doFinished(int status, String message, Object object) {
                    dialog.dismiss();
                    Toast.makeText(AccountCase.this, message, Toast.LENGTH_SHORT).show();
                    if (status == Action.STATUS_CODE_OK) {
                        Intent intent = new Intent();
                        Account account = (Account) object;
                        intent.putExtra(Account.ACCOUNT_KEY, account.toJSONString());
                        setResult(ACCOUNT_CASE_DELETE_SUCCEED, intent);
                        finish();
                    }
                }
            });
        }
        btnAccountDelete.setClickable(true);
    }

    private void updateAccount() {
        if (mode == MODE_REVISE && validateInput(true)) {
            if (getIsChanged()) {
                btnAccountUpdate.setClickable(false);

                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String email = etEmail.getText().toString();
                String cellphone = etCellphone.getText().toString();

                account.setUsername(username);
                account.setPassword(password);
                account.setUser(user);
                Log.d("selected", "" + spinnerApps.getSelectedItemPosition());
                if (apps != null) {
                    account.setApp(apps.get(spinnerApps.getSelectedItemPosition()));
                }
                if (InputValidateUtils.isCellphone(username)) {
                    account.setAccountType(Account.AccountType.CELLPHONE);
                } else if (InputValidateUtils.isEmail(username)) {
                    account.setAccountType(Account.AccountType.EMAIL);
                } else if (InputValidateUtils.isUsername(username)) {
                    account.setAccountType(Account.AccountType.USERNAME);
                }

                account.setEmail(email);
                account.setCellphone(cellphone);

                final Dialog dialog = ProgressDialogUtil.createLoadingDialog(AccountCase.this, "Please Wait...");
                dialog.show();
                userAccountAction.updateAccount(account, new ActionFinishedListener() {

                    @Override
                    public void doFinished(int status, String message, Object object) {
                        dialog.dismiss();
                        Toast.makeText(AccountCase.this, message, Toast.LENGTH_SHORT).show();
                        if (status == Action.STATUS_CODE_OK) {
                            Intent intent = new Intent();
                            Account account = (Account) object;
                            intent.putExtra(Account.ACCOUNT_KEY, account.toJSONString());
                            setResult(ACCOUNT_CASE_UPDATE_SUCCEED, intent);
                            finish();
                        }
                    }
                });
            } else {
                Toast.makeText(AccountCase.this, "Not Changed", Toast.LENGTH_SHORT).show();
            }
        }
        btnAccountUpdate.setClickable(true);
    }

    private void addAccount() {
        if (mode == MODE_ADD && validateInput(true)) {
            btnAccountAdd.setClickable(false);
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            String email = etEmail.getText().toString();
            String cellphone = etCellphone.getText().toString();

            account.setUsername(username);
            account.setPassword(password);
            account.setUser(user);
            if (apps != null)
                account.setApp(apps.get(spinnerApps.getSelectedItemPosition()));

            if (InputValidateUtils.isCellphone(username)) {
                account.setAccountType(Account.AccountType.CELLPHONE);
            } else if (InputValidateUtils.isEmail(username)) {
                account.setAccountType(Account.AccountType.EMAIL);
            } else if (InputValidateUtils.isUsername(username)) {
                account.setAccountType(Account.AccountType.USERNAME);
            }

            if (!"".equals(email)) {
                account.setEmail(email);
            }

            if (!"".equals(cellphone)) {
                account.setCellphone(cellphone);
            }
            final Dialog dialog = ProgressDialogUtil.createLoadingDialog(AccountCase.this, "Please Wait...");
            dialog.show();
            userAccountAction.addAccount(account, new ActionFinishedListener() {
                @Override
                public void doFinished(int status, String message, Object object) {
                    dialog.dismiss();
                    if (!"".equals(message)) {
                        Toast.makeText(AccountCase.this, message, Toast.LENGTH_SHORT).show();
                    }
                    if (status == 1) {
                        Account account = (Account) object;
                        Intent intent = new Intent();
                        intent.putExtra(Account.ACCOUNT_KEY, account.toJSONString());
                        setResult(ACCOUNT_CASE_ADD_SUCCEED, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    }
                }
            });
        }
        btnAccountAdd.setClickable(true);
    }

    private int getAppPosition(String appId) {
        if (appId != null && apps != null && apps.size() > 0) {
            for (int i = 0; i < apps.size(); i++) {
                ThirdPartApp app = apps.get(i);
                if (app.getAppId() != null && appId.equals(app.getAppId())) {
                    return i;
                }
            }
        }
        return 0;
    }

    public boolean getIsChanged() {
        boolean un = account.getUsername().equals(etUsername.getText().toString().trim());
        boolean pw = account.getPassword().equals(etPassword.getText().toString().trim());
        if ("null".equals(account.getEmail())) {
            if ("null".equals(account.getCellphone())) {
                boolean em = "".equals(etEmail.getText().toString().trim());
                boolean cp = "".equals(etCellphone.getText().toString().trim());
                return !(un && pw && em && cp && !isSpinnerPositionChanged);
            } else {
                boolean em = "".equals(etEmail.getText().toString().trim());
                boolean cp = account.getCellphone().equals(etCellphone.getText().toString().trim());
                return !(un && pw && em && cp && !isSpinnerPositionChanged);
            }
        } else if ("null".equals(account.getCellphone())) {
            boolean em = account.getEmail().equals(etEmail.getText().toString().trim());
            boolean cp = "".equals(etCellphone.getText().toString().trim());
            return !(un && pw && em && cp && !isSpinnerPositionChanged);
        } else {
            boolean em = account.getEmail().equals(etEmail.getText().toString().trim());
            boolean cp = account.getCellphone().equals(etCellphone.getText().toString().trim());
            return !(un && pw && em && cp && !isSpinnerPositionChanged);
        }
    }

    public void setApps(List<ThirdPartApp> apps) {
        this.apps = apps;
    }
}
