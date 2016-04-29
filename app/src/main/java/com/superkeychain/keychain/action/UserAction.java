package com.superkeychain.keychain.action;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.https.HttpsPostAsync;
import com.superkeychain.keychain.https.SecureJsonObject;
import com.superkeychain.keychain.utils.InputValidateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/15/16.
 */
public class UserAction extends Action {
    public UserAction(Activity activity) {
        super(activity);
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


    public void signIn(String username, String password, final ActionFinishedListener actionFinishedListener) {

        if (!validateInput(username, password, true))
            return;
        try {
            SecureJsonObject requestJsonObject = getRawSecureJsonObject();
            requestJsonObject.addAttribute(ACCOUNT_TYPE, user.getType());
            requestJsonObject.addSecureAttribute(USERNAME, user.getName());
            requestJsonObject.addSecureAttribute(PASSWORD, user.getPassword());
            requestJsonObject.addSecureAttribute(DEVICE_ID, deviceId);
            final String requestJsonString = requestJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return user;
                }

                @Override
                public void doHttpsFinished(Object user) {
                    super.doHttpsFinished(user);
                    actionFinishedListener.doFinished(statusCode, message, user);

                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_IN), requestJsonString, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void signUp(String username, String password, final ActionFinishedListener actionFinishedListener) {
        if (!validateInput(username, password, true))
            return;
        try {
            SecureJsonObject requestJsonObject = getRawSecureJsonObject();
            requestJsonObject.addAttribute(ACCOUNT_TYPE, user.getType());
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return user;
                }

                @Override
                public void doHttpsFinished(Object user) {
                    super.doHttpsFinished(user);
                    actionFinishedListener.doFinished(statusCode, message, user);
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_UP), requestJsonString, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void checkCookie(final ActionFinishedListener actionFinishedListener) {
        try {
            SecureJsonObject secureJsonObject = getRawSecureJsonObject();
            try {
                secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object);
                    actionFinishedListener.doFinished(statusCode, message, object);

                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_CHECK_COOKIE), request, aesKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void signOut(final ActionFinishedListener actionFinishedListener) {

        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String request = secureJsonObject.toString();
        new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
            @Override
            public void doHttpsFinished(Object object) {
                super.doHttpsFinished(object);
                actionFinishedListener.doFinished(statusCode, message, object);
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SIGN_OUT), request, aesKey);
        userRepository.delete();
    }

    public int getAccountType() {
        return user.getType();
    }

    public void getUser(final ActionFinishedListener actionFinishedListener) {
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureAttribute(USERNAME, user.getAccountName());
            secureJsonObject.addSecureAttribute(PASSWORD, user.getPassword());
            secureJsonObject.addSecureAttribute(DEVICE_ID, deviceId);
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object userObject) {
                    super.doHttpsFinished(userObject);
                    if (userObject != null) {
                        user = (User) userObject;
                        actionFinishedListener.doFinished(statusCode, message, user);
                    }
                    actionFinishedListener.doFinished(statusCode, message, null);
                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    if (statusCode == STATUS_CODE_OK) {
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return user;
                    }
                    return null;
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_USER), request, aesKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
