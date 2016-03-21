package com.superkeychain.keychain.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.utils.AESUtils;
import com.superkeychain.keychain.utils.PhoneUtils;

import java.security.GeneralSecurityException;

/**
 * Created by taofeng on 3/15/16.
 */
public class UserRepository {
    private static final String KEY = "user_data";
    private static String AES_KEY_USER;
    private static PhoneUtils phoneUtils;
    private Activity activity;
    private Context context;
    private SharedPreferences sharedPreferences;

    public UserRepository(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
        if (AES_KEY_USER == null) {
            if (phoneUtils == null) {
                phoneUtils = new PhoneUtils(context);
            }
            //TODO add a user pattern here
            AES_KEY_USER = phoneUtils.getUniqueString("");
        }
    }

    public void save(User user) {
        try {
            String userCrypto = AESUtils.encrypt(AES_KEY_USER, User.parseToJSON(user).toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY, userCrypto);
            editor.apply();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public User get() {
        String userCrypto = sharedPreferences.getString(KEY, null);
        if (userCrypto == null)
            return null;
        try {
            String userJSONString = AESUtils.decrypt(AES_KEY_USER, userCrypto);
            Log.d("userRepositoryLoad", userJSONString);
            return User.parseFromJSON(userJSONString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete() {
        if (sharedPreferences.contains(KEY)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY);
            editor.apply();
        }
    }

}
