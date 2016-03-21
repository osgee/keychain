package com.superkeychain.keychain.entity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/14/16.
 */
public class User {

    public static final String USER_KEY = "USER";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_PASSWORD = "user_password";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_CELLPHONE = "user_cellphone";
    public static final String USER_DEVICE_ID = "user_device";
    public static final String USER_SECRET = "user_secret";
    public static final String USER_COOKIE = "user_cookie";
    public static final String USER_COOKIE_EXPIRE_TIME = "user_cookie_time";
    public static final String USER_ACCOUNTS = "user_accounts";
    private String id;
    private int type;
    private String name;
    private String password;
    private String rePassword;
    private String email;
    private String cellphone;
    private String deviceID;
    private String secret;
    private String cookie;
    private Long cookieExpireTime;
    private List<Account> accounts;

    public static User parseFromJSON(String json) {
        if (json == null || "null".equals(json)) {
            return null;
        }
        JSONObject jsonUser = null;
        try {
            Log.d("userjson", json);
            jsonUser = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseFromJSON(jsonUser);
    }

    public static User parseFromJSON(JSONObject jsonUser) {
        if (jsonUser != null) {
            User user = new User();
            try {
                user.setId(jsonUser.optString(USER_ID, null));
                user.setName(jsonUser.optString(USER_NAME, null));
                user.setPassword(jsonUser.optString(USER_PASSWORD, null));
                user.setEmail(jsonUser.optString(USER_EMAIL, null));
                user.setCellphone(jsonUser.optString(USER_CELLPHONE, null));
                user.setDeviceID(jsonUser.optString(USER_DEVICE_ID, null));
                user.setSecret(jsonUser.optString(USER_SECRET, null));
                user.setCookie(jsonUser.optString(USER_COOKIE, null));
                user.setCookieExpireTime(jsonUser.optLong(USER_COOKIE_EXPIRE_TIME, 0l));
                String accountsJSON = jsonUser.optString(USER_ACCOUNTS, null);
                if (accountsJSON != null) {
                    JSONArray jsonAccounts = new JSONArray(accountsJSON);
                    List<Account> accounts = new ArrayList<>();
                    for (int i = 0; i < jsonAccounts.length(); i++) {
                        accounts.add(Account.parseFromJSON(jsonAccounts.get(i).toString()));
                    }
                    if (accounts.size() > 0) {
                        user.setAccounts(accounts);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }
        return null;
    }

    public static JSONObject parseToJSON(User user) {
        return parseToJSON(user, true);
    }

    public static JSONObject parseToJSON(User user, boolean withAccount) {
        return parseToJSON(user, withAccount, true);
    }

    public static JSONObject parseToJSON(User user, boolean withAccount, boolean withCert) {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put(USER_ID, user.getId());
            jsonUser.put(USER_NAME, user.getName());
            jsonUser.put(USER_PASSWORD, user.getPassword());
            jsonUser.put(USER_EMAIL, user.getEmail());
            jsonUser.put(USER_CELLPHONE, user.getEmail());
            jsonUser.put(USER_DEVICE_ID, user.getDeviceID());
            jsonUser.put(USER_SECRET, user.getSecret());
            jsonUser.put(USER_COOKIE, user.getCookie());
            jsonUser.put(USER_COOKIE_EXPIRE_TIME, user.getCookieExpireTime());
            if (withAccount) {
                List<Account> accounts = user.getAccounts();
                JSONArray jsonAccounts = new JSONArray();
                if (accounts != null && accounts.size() > 0) {
                    for (Account account : accounts) {
                        JSONObject jsonAccount = Account.parseToJSON(account);
                        jsonAccounts.put(jsonAccount);
                    }
                }
                jsonUser.put(USER_ACCOUNTS, jsonAccounts.toString());
            } else {
                jsonUser.put(USER_ACCOUNTS, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellphone() {
        return cellphone;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public Long getCookieExpireTime() {
        return cookieExpireTime;
    }

    public void setCookieExpireTime(Long cookieExpireTime) {
        this.cookieExpireTime = cookieExpireTime;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return parseToJSON(this).toString();
    }
}
