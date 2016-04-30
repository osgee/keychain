package com.superkeychain.keychain.entity;

import com.superkeychain.keychain.utils.InputValidateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/19/16.
 */

public class Account {

    public static final String ACCOUNT_KEY = "ACCOUNT";
    public static final String ACCOUNT_ID = "account_id";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String ACCOUNT_USERNAME = "account_username";
    public static final String ACCOUNT_PASSWORD = "account_password";
    public static final String ACCOUNT_EMAIL = "account_email";
    public static final String ACCOUNT_CELLPHONE = "account_cellphone";
    public static final String ACCOUNT_USER = "account_user";
    public static final String ACCOUNT_APP = "account_app";
    private String accountId;
    private AccountType accountType;
    private String username;
    private String email;
    private String cellphone;
    private String password;
    private User user;
    private ThirdPartApp app;

    public static AccountType parseToAccountType(String type) {
        if (type != null) {
            switch (type) {
                case "U":
                    return AccountType.USERNAME;
                case "E":
                    return AccountType.EMAIL;
                case "C":
                    return AccountType.CELLPHONE;
                default:
                    return null;
            }
        }
        return null;
    }

    public static List<Account> parseFromJSONArray(JSONArray jsonArray) {
        if (jsonArray == null)
            return null;
        List<Account> accounts = new ArrayList<Account>();
        for (int i=0;i<jsonArray.length();i++) {
            try {
                accounts.add(Account.parseFromJSON((String) jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

    public static JSONArray parseToJSONArray(List<Account> accounts) {
        if (accounts == null)
            return null;
        JSONArray jsonArray = new JSONArray();
        for (int i=0;i<accounts.size();i++) {
            try {
                jsonArray.put(i,accounts.get(i).toJSONString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public static Account parseFromJSON(String json) {
        if (json == null || "null".equals(json))
            return null;
        JSONObject jsonAccount = null;
        try {
            jsonAccount = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseFromJSON(jsonAccount, true);
    }

    public static Account parseFromJSON(JSONObject jsonAccount, boolean withUser) {
        if (jsonAccount != null) {
            Account account = new Account();
            try {
                account.setAccountId(jsonAccount.getString(ACCOUNT_ID));
                account.setAccountType(parseToAccountType(jsonAccount.optString(ACCOUNT_TYPE, null)));
                account.setUsername(jsonAccount.optString(ACCOUNT_USERNAME, null));
                account.setPassword(jsonAccount.optString(ACCOUNT_PASSWORD, null));
                account.setEmail(jsonAccount.optString(ACCOUNT_EMAIL, null));
                account.setCellphone(jsonAccount.optString(ACCOUNT_CELLPHONE, null));
                if (withUser)
                    account.setUser(User.parseFromJSON(jsonAccount.optString(ACCOUNT_USER, null)));
                account.setApp(ThirdPartApp.parseFromJSON(jsonAccount.optString(ACCOUNT_APP, null)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return account;
        }
        return null;
    }

    public static JSONObject parseToJSON(Account account) {
        if(account==null)
            return null;
        JSONObject jsonAccount = new JSONObject();
        try {
            jsonAccount.put(ACCOUNT_ID, account.getAccountId());
            jsonAccount.put(ACCOUNT_TYPE, account.getAccountType() == null ? null : account.getAccountType().toString());
            jsonAccount.put(ACCOUNT_USERNAME, account.getUsername());
            jsonAccount.put(ACCOUNT_PASSWORD, account.getPassword());
            jsonAccount.put(ACCOUNT_EMAIL, account.getEmail());
            jsonAccount.put(ACCOUNT_CELLPHONE, account.getCellphone());
            jsonAccount.put(ACCOUNT_USER, account.getUser() == null ? null : User.parseToJSON(account.getUser(), false, false));
            jsonAccount.put(ACCOUNT_APP, account.getApp() == null ? null : ThirdPartApp.parseToJSON(account.getApp()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonAccount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ThirdPartApp getApp() {
        return app;
    }

    public void setApp(ThirdPartApp app) {
        this.app = app;
    }

    @Override
    public String toString() {
        if (accountType == null)
            return username;
        switch (accountType.ordinal()) {
            case 0:
                return username;
            case 1:
                return email;
            case 2:
                return cellphone;
            default:
                return email;
        }
    }


    public String toJSONString() {
        return parseToJSON(this).toString();
    }

    public enum AccountType {
        USERNAME() {
            @Override
            public String toString() {
                return "U";
            }
        },
        EMAIL() {
            @Override
            public String toString() {
                return "E";
            }
        },
        CELLPHONE() {
            @Override
            public String toString() {
                return "C";
            }
        },
    }


//    private String accountId;
//    private AccountType accountType;
//    private String username;
//    private String email;
//    private String cellphone;
//    private String password;
//    private User user;
//    private ThirdPartApp app;

    public void update(Account account){
        this.accountId = account.getAccountId();
        this.accountType = account.getAccountType();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.cellphone = account.getCellphone();
        this.password = account.getPassword();
        this.user = account.getUser();
        this.app = account.getApp();
    }

}
