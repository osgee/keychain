package com.superkeychain.keychain.action;

import android.app.Activity;
import android.util.Log;

import com.superkeychain.keychain.entity.Account;
import com.superkeychain.keychain.entity.Service;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.https.HttpsPostAsync;
import com.superkeychain.keychain.https.SecureJsonObject;
import com.superkeychain.keychain.utils.AESUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 4/28/16.
 */
public class UserServiceAction extends Action{



    public UserServiceAction(Activity activity, User user) {
        super(activity);
        this.user = user;
    }

    public void queryService(final ActionFinishedListener actionFinishedListener, String serviceId){

        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureAttribute(Service.SERVICE_ID, serviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String request = secureJsonObject.toString();
        Log.d("queryService", secureJsonObject.toPlainString());
        new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
            @Override
            public void doHttpsFinished(Object object) {
                actionFinishedListener.doFinished(statusCode, message, object);
            }

            @Override
            public Object doHttpsResponse(String response) {
                String responseData = (String) super.doHttpsResponse(response);
                Log.d("response", responseData);
                Service service = null;
                if(statusCode==Action.STATUS_CODE_OK){
                    try {
                        JSONObject jsonService = new JSONObject(responseData);
                        service = Service.parseFromJSON(jsonService.getString(SERVICE_KEY));
                        JSONArray accountsJson = new JSONArray(jsonService.getString(Service.SERVICE_ACCOUNTS));
                        service.setServiceAccounts(Account.parseFromJSONArray(accountsJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return service;
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SERVICE_QUERY), request, aesKey);

    }

    public void confirmService(final ActionFinishedListener actionFinishedListener, Service service){

        SecureJsonObject secureJsonObject = getRawSecureJsonObject();
        try {
            secureJsonObject.addAttribute(ACCOUNT_TYPE, ACCOUNT_TYPE_COOKIE);
            secureJsonObject.addSecureAttribute(Service.SERVICE_ID, service.getServiceId());
            secureJsonObject.addSecureAttribute(Account.ACCOUNT_ID,service.getServiceAccount().getAccountId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String request = secureJsonObject.toString();
        new HttpsPostAsync(context).setHttpsCustomListener(new HttpsPostAsync.HttpsCustomListener() {
            @Override
            public void doHttpsFinished(Object object) {
                actionFinishedListener.doFinished(statusCode, message, object);
            }

            @Override
            public Object doHttpsResponse(String response) {
                String responseData = (String) super.doHttpsResponse(response);
                Log.d("response", responseData);
                Service service = null;
                if(statusCode==Action.STATUS_CODE_OK){
                    try {
                        JSONObject jsonService = new JSONObject(responseData);
                        service = Service.parseFromJSON(jsonService.getString(SERVICE_KEY));
                        JSONArray accountsJson = new JSONArray(jsonService.getString(Service.SERVICE_ACCOUNTS));
                        service.setServiceAccounts(Account.parseFromJSONArray(accountsJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return service;
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SERVICE_CONFIRM), request, aesKey);

    }


}
