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
            secureJsonObject.addSecureAttribute(COOKIE,this.user.getCookie());
            secureJsonObject.addSecureAttribute(SERVICE_ID,serviceId);
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
                        Log.d("serviceJson", appsJsonString);
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
        }).execute(getURI(PROTOCOl_HTTPS, HOST, ACTION_SERVICE_QUERY), request, aesKey);

    }
}
