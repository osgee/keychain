package com.superkeychain.keychain.action;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.activity.AccountCase;
import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.https.HttpsPostAsync;
import com.superkeychain.keychain.https.SecureJsonObject;
import com.superkeychain.keychain.utils.AESUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/20/16.
 */
public class UserAppAction extends Action {

    public UserAppAction(Activity activity) {
        super(activity);
    }

    public void getAllApps(final ActionFinishedListener actionFinishedListener) {

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
                if (object != null) {
                    List<ThirdPartApp> apps = (List<ThirdPartApp>) object;
                   /* List<String> appsName = new ArrayList<String>();
                    if (apps != null && apps.size() > 0) {
                        for (ThirdPartApp app : apps) {
                            appsName.add(app.getAppName());
                        }
                        Spinner spinnerApps = (Spinner) activity.findViewById(R.id.spinner_apps);
                        //适配器
                        ArrayAdapter<String> adapterApps = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, appsName) {

                        };
                        //设置样式
                        adapterApps.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //加载适配器
                        spinnerApps.setAdapter(adapterApps);
                        AccountCase a = (AccountCase) activity;
                        a.setApps(apps);

                    }*/

                    actionFinishedListener.doFinished(statusCode,message,apps);
                }
            }

            @Override
            public Object doHttpsResponse(String response) {
                Log.d("response", response);
                JSONObject responseJSONObject = null;
                try {
                    responseJSONObject = new JSONObject(response);
                    statusCode = responseJSONObject.getInt(STATUS_CODE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<ThirdPartApp> apps = new ArrayList<ThirdPartApp>();
                if (statusCode == 1) {
                    try {
                        String cryptJsonString = responseJSONObject.getString(DATA_CRYPT_AES);
                        String appsJsonString = null;
                        try {
                            appsJsonString = AESUtils.decrypt(aesKey, cryptJsonString);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        Log.d("appsJson", appsJsonString);
                        JSONArray appsJSON = new JSONArray(new JSONObject(appsJsonString).getString(APPS_KEY));
                        for (int i = 0; i < appsJSON.length(); i++) {
                            ThirdPartApp app = ThirdPartApp.parseFromJSON(appsJSON.get(i).toString());
                            apps.add(app);
                        }
                        appRepository.saveApps(apps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return apps;
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ALL_APPS), request, aesKey);

    }

    public void saveAllApps() {

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

            }

            @Override
            public Object doHttpsResponse(String response) {
                Log.d("response", response);
                JSONObject responseJSONObject = null;
                try {
                    responseJSONObject = new JSONObject(response);
                    statusCode = responseJSONObject.getInt(STATUS_CODE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<ThirdPartApp> apps = new ArrayList<ThirdPartApp>();
                if (statusCode == 1) {
                    try {
                        String cryptJsonString = responseJSONObject.getString(DATA_CRYPT_AES);
                        String appsJsonString = null;
                        try {
                            appsJsonString = AESUtils.decrypt(aesKey, cryptJsonString);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                        Log.d("appsJson", appsJsonString);
                        JSONArray appsJSON = new JSONArray(new JSONObject(appsJsonString).getString(APPS_KEY));
                        for (int i = 0; i < appsJSON.length(); i++) {
                            ThirdPartApp app = ThirdPartApp.parseFromJSON(appsJSON.get(i).toString());
                            apps.add(app);
                        }
                        appRepository.saveApps(apps);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return apps;
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ALL_APPS), request, aesKey);


    }
}
