package com.superkeychain.keychain.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by taofeng on 3/19/16.
 */
public class ThirdPartApp {
    public static final String APP_KEY = "APP";
    public static final String APPS_KEY = "APPS";
    public static final String APP_ID = "app_id";
    public static final String APP_NAME = "app_name";
    public static final String APP_SIGN_IN_ACTION_URI = "app_sign_in_action_uri";
    public static final String APP_LOGO_URI = "app_logo_uri";
    public static final String APP_PUBLIC_KEY_URI = "app_public_key_uri";
    private String appId;
    private String appName;
    private String signInActionURI;
    private String appLogoURI;
    private String publicKeyURI;

    public static ThirdPartApp parseFromJSON(String json) {
        if (json == null || "null".equals(json))
            return null;
        JSONObject jsonApp = null;
        try {
            jsonApp = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseFromJSON(jsonApp);
    }

    public static ThirdPartApp parseFromJSON(JSONObject jsonAPP) {
        if (jsonAPP != null) {
            ThirdPartApp app = new ThirdPartApp();
            app.setAppId(jsonAPP.optString(APP_ID, null));
            app.setAppName(jsonAPP.optString(APP_NAME, null));
            app.setSignInActionURI(jsonAPP.optString(APP_SIGN_IN_ACTION_URI, null));
            app.setAppLogoURI(jsonAPP.optString(APP_LOGO_URI, null));
            app.setPublicKeyURI(jsonAPP.optString(APP_PUBLIC_KEY_URI, null));
            return app;
        }
        return null;
    }

    public static JSONObject parseToJSON(ThirdPartApp app) {
        JSONObject jsonAPP = new JSONObject();
        try {
            jsonAPP.put(APP_ID, app.getAppId());
            jsonAPP.put(APP_NAME, app.getAppName());
            jsonAPP.put(APP_SIGN_IN_ACTION_URI, app.getSignInActionURI());
            jsonAPP.put(APP_LOGO_URI, app.getAppLogoURI());
            jsonAPP.put(APP_PUBLIC_KEY_URI, app.getPublicKeyURI());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonAPP;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSignInActionURI() {
        return signInActionURI;
    }

    public void setSignInActionURI(String signInActionURI) {
        this.signInActionURI = signInActionURI;
    }

    public String getAppLogoURI() {
        return appLogoURI;
    }

    public void setAppLogoURI(String appLogoURI) {
        this.appLogoURI = appLogoURI;
    }

    public String getPublicKeyURI() {
        return publicKeyURI;
    }

    public void setPublicKeyURI(String publicKeyURI) {
        this.publicKeyURI = publicKeyURI;
    }

    @Override
    public String toString() {
        return parseToJSON(this).toString();
    }
}
