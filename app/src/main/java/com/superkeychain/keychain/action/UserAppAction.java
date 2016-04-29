package com.superkeychain.keychain.action;

import android.app.Activity;
import android.util.Log;

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

    public UserAppAction(Activity activity, User user) {
        super(activity);
        this.user = user;
    }

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
                    actionFinishedListener.doFinished(statusCode, message, apps);
                }
            }
            @Override
            public Object doHttpsResponse(String response) {
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
                        JSONArray appsJSON = new JSONArray(new JSONObject(appsJsonString).getString(APPS_KEY));
                        for (int i = 0; i < appsJSON.length(); i++) {
                            ThirdPartApp app = ThirdPartApp.parseFromJSON(appsJSON.get(i).toString());
                            apps.add(app);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                return apps;
            }
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_GET_ALL_APPS), request, aesKey);

    }

}
