package com.superkeychain.keychain.action;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.superkeychain.keychain.entity.Account;
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
 * Created by taofeng on 3/19/16.
 */
public class UserAccountAction extends Action {
    private List<Account> accounts;

    private Account account;

    public UserAccountAction(Activity activity, User user) {
        super(activity);
        this.user = user;
        if (this.user == null || this.user.getAccounts() == null) {
            accounts = new ArrayList<>();
        } else if (this.user.getAccounts() == null) {
            accounts = new ArrayList<>();
        } else {
            accounts = this.user.getAccounts();
        }
    }

    public void addAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object account) {
                    super.doHttpsFinished(account);
                    actionFinishedListener.doFinished(statusCode, message, account);

                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    Log.d("response_account", responseData);
                    if (statusCode == STATUS_CODE_OK) {
                        try {
                            JSONObject accountJSON = new JSONObject(responseData);
                            return Account.parseFromJSON(accountJSON.getString(ACCOUNT));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_ADD_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureData(ACCOUNT, Account.parseToJSON(account).toString());
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object, false);
                    actionFinishedListener.doFinished(statusCode, message, object);
                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    JSONObject accountJSON = null;
                    try {
                        accountJSON = new JSONObject(new JSONObject(responseData).getString(ACCOUNT));
                        return accountJSON.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getAccounts(final ActionFinishedListener actionFinishedListener) {
        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureData(USER_KEY, User.parseToJSON(user, false).toString());
            String request = secureJsonObject.toString();
            new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
                @Override
                public void doHttpsFinished(Object object) {
                    super.doHttpsFinished(object, false);
                    List<Account> accounts = (List<Account>) object;
                    if (accounts != null && accounts.size() > 0) {
                        user.setAccounts(accounts);
                    }
                    actionFinishedListener.doFinished(statusCode, message, user);
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
                    actionFinishedListener.doFinished(statusCode, message, account);
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_DELETE_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateAccount(final Account account, final ActionFinishedListener actionFinishedListener) {
        this.account = account;
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
                    actionFinishedListener.doFinished(statusCode, message, object);
                }

                @Override
                public Object doHttpsResponse(String response) {
                    String responseData = (String) super.doHttpsResponse(response);
                    if (statusCode == STATUS_CODE_OK) {
                        try {
                            JSONObject accountJSON = new JSONObject(responseData);
                            return Account.parseFromJSON(accountJSON.getString(ACCOUNT));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_UPDATE_ACCOUNT), request, aesKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
