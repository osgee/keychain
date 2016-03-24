package com.superkeychain.keychain.action;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.activity.AccountCase;
import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.https.HttpsPostAsync;
import com.superkeychain.keychain.https.SecureJsonObject;
import com.superkeychain.keychain.utils.InputValidateUtils;
import com.superkeychain.keychain.view.ProgressDialogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/19/16.
 */
public class UserAccountAction extends Action {
    private List<Account> accounts;

    private Account account;

    public UserAccountAction(Activity activity) {
        super(activity);
        if (user == null || user.getAccounts() == null) {
            accounts = new ArrayList<>();
        } else if (user.getAccounts() == null) {
            accounts = new ArrayList<>();
        } else {
            accounts = user.getAccounts();
        }
    }

    public void addAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
//        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
//        dialog.show();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            Log.d("account", Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object);
//                    dialog.dismiss();
                    /*if (statusCode == 1) {
                        accounts.add(account);
                        user.setAccounts(accounts);
                        userRepository.save(user);
                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    }*/
                    actionFinishedListener.doFinished(statusCode,message,user);

                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_ADD_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
//        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
//        dialog.show();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            Log.d("account", Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object, false);
//                    dialog.dismiss();
                    actionFinishedListener.doFinished(statusCode, message, object);
                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    JSONObject accountJSON = null;
                    try {
                        accountJSON = new JSONObject(new JSONObject(responseData).getString(ACCOUNT));
                        Log.d("account", accountJSON.toString());
                        return accountJSON.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("get account", accountJSON.toString());
                    return null;
                }

            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAccounts() {
//        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
//        dialog.show();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object, false);
//                    dialog.dismiss();
                    List<Account> accounts = (List<Account>) object;
                    if (accounts != null && accounts.size() > 0) {

                        Intent intent = new Intent(activity, AccountCase.class);
                        intent.putExtra(User.USER_KEY, User.parseToJSON(user).toString());
                        intent.putExtra(Account.ACCOUNT_KEY, Account.parseToJSON(accounts.get(0)).toString());
                        intent.putExtra(ThirdPartApp.APPS_KEY, appRepository.getAppsJSONString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                    } else {
                        Toast.makeText(context, "Account Not Exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    Log.d("accounts", responseData);
                    List<Account> accounts = new ArrayList<Account>();
                    try {
                        JSONObject responseJSONObject = new JSONObject(responseData);
                        String accountsJSONString = responseJSONObject.optString(ACCOUNTS_KEY, null);
                        if (accountsJSONString != null) {
                            JSONArray accountJSONArray = new JSONArray(accountsJSONString);
                            for (int i = 0; i < accountJSONArray.length(); i++) {
                                Account account = null;
                                try {
                                    account = Account.parseFromJSON(accountJSONArray.get(i).toString());
                                    Log.d("account", accountJSONArray.get(i).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                accounts.add(account);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return accounts;
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ACCOUNTS), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean validateInput(String username, String password, boolean showToast) {
        username = username.trim();
        password = password.trim();
        if (!("".equals(username) || "".equals(password))) {
            if (InputValidateUtils.isInputLengthEnough(password)) {
                if (InputValidateUtils.isEmail(username)) {
                    return true;
                } else if (InputValidateUtils.isCellphone(username)) {
                    return true;
                } else if (InputValidateUtils.isInputLengthEnough(username)) {
                    if (InputValidateUtils.isInputAllDigits(username)) {
                        if (showToast)
                            Toast.makeText(context, "Incorrect Cellphone", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
                } else if (!InputValidateUtils.isInputLengthEnough(username)) {
                    if (showToast)
                        Toast.makeText(context, "Username 6-20 Letters and Digits", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    if (showToast)
                        Toast.makeText(context, "Illegal Username", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                if (showToast)
                    Toast.makeText(context, "Password 6-20 Length", Toast.LENGTH_SHORT).show();
                return false;
            }
            //TODO add country code
        }
        return false;
    }


    public void deleteAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
//        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
//        dialog.show();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object);
//                    dialog.dismiss();
                    accounts.remove(account);
                    user.setAccounts(accounts);
                   /* if (statusCode == 1) {
                        accounts.remove(account);
                        user.setAccounts(accounts);
                        userRepository.save(user);
                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                    }*/
                    actionFinishedListener.doFinished(statusCode,message,user);
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_DELETE_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
        dialog.show();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            Log.d("account", Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object);
                    dialog.dismiss();
                 /*   if (statusCode == 1) {

                        activity.finish();
                        activity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                        actionFinishedListener.doFinished(user, message);
                    }else{
                        actionFinishedListener.doFinished(null,message);
                    }*/
                    actionFinishedListener.doFinished(statusCode,message,object);
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_UPDATE_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
