package com.superkeychain.keychain.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.superkeychain.keychain.entity.ThirdPartApp;
import com.superkeychain.keychain.utils.JSONListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 3/20/16.
 */
public class AppRepository {
    private Activity activity;
    private Context context;
    private String APP_KEY = "apps";
    private SharedPreferences sharedPreferences;

    public AppRepository(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveApps(List<ThirdPartApp> apps) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONListAdapter<ThirdPartApp> adapter = new JSONListAdapter<ThirdPartApp>(apps);
        JSONArray array = adapter.getJSONArray();
        if (sharedPreferences.contains(APP_KEY)) {
            editor.remove(APP_KEY);
        }
        editor.putString(APP_KEY, array.toString());
        editor.apply();
    }

    public List<ThirdPartApp> getAppsList() {
        List<ThirdPartApp> apps = new ArrayList<>();
        String appsJSON = sharedPreferences.getString(APP_KEY, null);
        if (appsJSON == null) {
            JSONArray array = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject appJSON = null;
                try {
                    appJSON = (JSONObject) array.get(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ThirdPartApp app = ThirdPartApp.parseFromJSON(appJSON);
                apps.add(app);
            }
        }
        return apps;
    }

    public String getAppsJSONString() {
        return sharedPreferences.getString(APP_KEY, null);
    }

    public int getAppPosition(String app_id) {
        List<ThirdPartApp> apps = getAppsList();
        if (app_id != null && apps != null && apps.size() > 0) {
            for (int i = 0; i < apps.size(); i++) {
                ThirdPartApp app = apps.get(i);
                if (app.getAppId() != null && app_id.equals(app.getAppId())) {
                    return i;
                }
            }
        }
        return 0;
    }
}
