package com.superkeychain.keychain.action;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.superkeychain.keychain.activity.KeychainMain;
import com.superkeychain.keychain.activity.SignIn;
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
 * Created by taofeng on 3/15/16.
 */
public class UserAction extends Action {
    public UserAction(Activity activity, Context context) {
        super(activity, context);
    }

    public boolean validateInput(String username, String password, boolean showToast) {
        username = username.trim();
        password = password.trim();
        user.setName(username);
        user.setPassword(password);
        if (!("".equals(username) || "".equals(password))) {
            if (InputValidateUtils.isInputLengthEnough(password)) {
                if (InputValidateUtils.isEmail(username)) {
                    user.setType(ACCOUNT_TYPE_EMAIL);
                    return true;
                } else if (InputValidateUtils.isCellphone(username)) {
                    user.setType(ACCOUNT_TYPE_CELLPHONE);
                    return true;
                } else if (InputValidateUtils.isInputLengthEnough(username)) {
                    if (InputValidateUtils.isInputAllDigits(username)) {
                        if (showToast)
                            Toast.makeText(context, "Incorrect Cellphone", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    user.setType(ACCOUNT_TYPE_USERNAME);
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


    public void signIn(String username, String password) {
        /**
         *
         * first sign in
         request
         {
         cookie:''
         time:
         random_token
         random_crypt
         cert_encrypt_rsa(rsa):{
         time
         username
         password
         device_id
         aeskey
         }
         data_crypt_aes(aes):{
         target-object
         }
         }
         */

        if (!validateInput(username, password, true))
            return;
        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
        dialog.show();

        try {
            SecureJsonObject requestJsonObject = getRawSecureJsonObject();
            requestJsonObject.addAttribute(ACCOUNT_TYPE, user.getType());

            requestJsonObject.addSecureAttribute(USERNAME, user.getName());
            requestJsonObject.addSecureAttribute(PASSWORD, user.getPassword());
            requestJsonObject.addSecureAttribute(DEVICE_ID, deviceId);
            final String requestJsonString = requestJsonObject.toString();
            Log.d("request", requestJsonString);
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    Log.d("response", responseData);
                    User user = null;
                    JSONObject responseJSONObject = null;
                    try {
                        JSONObject dataJSONObject = new JSONObject(responseData);
                        String userJSONString = dataJSONObject.getString(USER_KEY);
                        user = User.parseFromJSON(userJSONString);
                        String accountsJSONString = dataJSONObject.getString(ACCOUNTS_KEY);
                        JSONArray accountsJSON = new JSONArray(accountsJSONString);
                        List<Account> accounts = new ArrayList<Account>();
                        if (accountsJSON != null && accountsJSON.length() > 0 && !"".equals(accountsJSONString.trim())) {
                            for (int i = 0; i < accountsJSON.length(); i++) {
                                Account account = Account.parseFromJSON(accountsJSON.get(i).toString());
                                accounts.add(account);
                            }
                            user.setAccounts(accounts);
                        } else {
                            user.setAccounts(null);
                        }

                        String appsJSONString = dataJSONObject.getString(APPS_KEY);
                        JSONArray appsJSON = new JSONArray(appsJSONString);
                        List<ThirdPartApp> apps = new ArrayList<ThirdPartApp>();
                        if (appsJSON != null && appsJSON.length() > 0 && !"".equals(appsJSONString)) {
                            for (int i = 0; i < appsJSON.length(); i++) {
                                ThirdPartApp app = ThirdPartApp.parseFromJSON(appsJSON.get(i).toString());
                                apps.add(app);
                            }
                        }
                        appRepository.saveApps(apps);
                        Log.d("response", User.parseToJSON(user).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return user;
                }

                @Override
                public void doHttpsFinished(Object user) {
                    super.doHttpsFinished(user);
                    dialog.dismiss();
                    if (user != null && user instanceof User) {
                        Intent intent = new Intent(context, KeychainMain.class);
                        intent.putExtra(User.USER_KEY, User.parseToJSON((User) user).toString());
                        context.startActivity(intent);
                        activity.finish();

                    }
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_IN), requestJsonString, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void signUp(String username, String password) {
        if (!validateInput(username, password, true))
            return;
        final Dialog dialog = ProgressDialogUtil.createLoadingDialog(context, "Please Wait...");
        dialog.show();
        try {
            SecureJsonObject requestJsonObject = getRawSecureJsonObject();
            requestJsonObject.addAttribute(ACCOUNT_TYPE, user.getType());
                /*

                sign up
                request{
                time
                account_type
                random_token
                random_crypt

                cert_crypt_rsa (rsa){
                time
                username
                password
                aeskey
                device_id
                sim_serial_number
                line_1_number
                }
                 */
            requestJsonObject.addSecureAttribute(USERNAME, user.getName());
            requestJsonObject.addSecureAttribute(PASSWORD, user.getPassword());
            requestJsonObject.addSecureAttribute(DEVICE_ID, deviceId);
            requestJsonObject.addSecureAttribute(SIM_SERIAL_NUMBER, sim_serial_number);
            requestJsonObject.addSecureAttribute(LINE_1_NUMBER, line_1_number);

            final String requestJsonString = requestJsonObject.toString();
            Log.d("request", requestJsonString);
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    Log.d("response", responseData);
                    User user = null;
                    JSONObject responseJSONObject = null;
                    try {
                        JSONObject dataJSONObject = new JSONObject(responseData);
                        String userJSONString = dataJSONObject.getString(USER_KEY);
                        user = User.parseFromJSON(userJSONString);
                        Log.d("response", User.parseToJSON(user).toString());

                        String accountsJSONString = dataJSONObject.getString(ACCOUNTS_KEY);
                        JSONArray accountsJSON = new JSONArray(accountsJSONString);
                        List<Account> accounts = new ArrayList<Account>();
                        if (accountsJSON != null && accountsJSON.length() > 0 && !"".equals(accountsJSONString.trim())) {
                            for (int i = 0; i < accountsJSON.length(); i++) {
                                Account account = Account.parseFromJSON(accountsJSON.get(i).toString());
                                accounts.add(account);
                            }
                            user.setAccounts(accounts);
                        } else {
                            user.setAccounts(null);
                        }

                        String appsJSONString = dataJSONObject.getString(APPS_KEY);
                        JSONArray appsJSON = new JSONArray(appsJSONString);
                        List<ThirdPartApp> apps = new ArrayList<ThirdPartApp>();
                        if (appsJSON != null && appsJSON.length() > 0 && !"".equals(appsJSONString)) {
                            for (int i = 0; i < appsJSON.length(); i++) {
                                ThirdPartApp app = ThirdPartApp.parseFromJSON(appsJSON.get(i).toString());
                                apps.add(app);
                            }
                        }
                        appRepository.saveApps(apps);
                        Log.d("response", User.parseToJSON(user).toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return user;
                }

                @Override
                public void doHttpsFinished(Object user) {
                    super.doHttpsFinished(user);
                    dialog.dismiss();
                    if (user != null && user instanceof User) {
                        Intent intent = new Intent(context, KeychainMain.class);
                        intent.putExtra(User.USER_KEY, User.parseToJSON((User) user).toString());
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_UP), requestJsonString, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int checkCookie(User userIn) {
        int statusCode = 0;
        user = userIn;
        Object httpsOut = null;
        try {
            SecureJsonObject secureJsonObject = getRawSecureJsonObject();
            try {
                secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String request = secureJsonObject.toString();
            httpsOut = new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_CHECK_COOKIE), request, aesKey).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (httpsOut == null)
            statusCode = 0;
        else statusCode = (int) httpsOut;
        return statusCode;
    }

    public void signOut(User userIn) {
        user = userIn;
        userRepository.delete();
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String request = secureJsonObject.toString();
        new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_OUT), request, aesKey);
        Intent intent = new Intent(activity, SignIn.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public int getAccountType() {
        return user.getType();
    }





}
