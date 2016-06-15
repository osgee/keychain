package com.superkeychain.keychain.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.utils.AESUtils;
import com.superkeychain.keychain.utils.Digest;
import com.superkeychain.keychain.utils.PhoneUtils;

import java.security.GeneralSecurityException;

/**
 * Created by taofeng on 3/15/16.
 */

public class UserRepository {
    private static final String KEY = "user_data";
    private static final String PASSWORD_HASH = "hash";
    private static final String PASSWORD_SALT = "salt";
    private static String AES_KEY_USER;
    private static PhoneUtils phoneUtils;
    private Activity activity;
    private Context context;
    private SharedPreferences sharedPreferences;

    public UserRepository(Activity activity){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
        if (AES_KEY_USER == null) {
            if (phoneUtils == null) {
                phoneUtils = new PhoneUtils(context);
            }
            //TODO add a user pattern here
            AES_KEY_USER = phoneUtils.getUniqueString("");
        }
    }

    public UserRepository(Activity activity, String password) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        sharedPreferences = this.activity.getPreferences(Context.MODE_PRIVATE);
        if (AES_KEY_USER == null) {
            if (phoneUtils == null) {
                phoneUtils = new PhoneUtils(context);
            }
            AES_KEY_USER = phoneUtils.getUniqueString(password);
        }
    }

    public void save(User user) {
        try {
//            Log.d("userRepositorySave", User.parseToJSON(user).toString());
            String userCrypto = AESUtils.encrypt(AES_KEY_USER, User.parseToJSON(user).toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY, userCrypto);
            editor.apply();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void savePassword(String password){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String salt = AESUtils.getRandomKey(8);
        editor.putString(PASSWORD_SALT, salt);
        editor.putString(PASSWORD_HASH, getHash(password, salt));
        editor.apply();
    }

    private String getHash(String password, String salt){
        return  Digest.Builder.bySHA256().getDigest(salt+password);
    }

    public boolean isPasswordValid(String password){
        String salt = sharedPreferences.getString(PASSWORD_SALT, null);
        String hash = sharedPreferences.getString(PASSWORD_HASH, null);
        if(salt!=null&&hash!=null&&!"".equals(salt)&&!"".equals(hash)){
            String hashCa = getHash(password, salt);
            if(hash.equals(hashCa))
                return true;
        }
        return false;
    }

    public User get() {
        String userCrypto = sharedPreferences.getString(KEY, null);
        if (userCrypto == null)
            return null;
        try {
            String userJSONString = AESUtils.decrypt(AES_KEY_USER, userCrypto);
//            Log.d("userRepositoryLoad", userJSONString);
            return User.parseFromJSON(userJSONString);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void delete() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains(KEY)) {
            editor.remove(KEY);
        }
        if (sharedPreferences.contains(PASSWORD_SALT)) {
            editor.remove(PASSWORD_SALT);
        }
        if (sharedPreferences.contains(PASSWORD_HASH)) {
            editor.remove(PASSWORD_HASH);
        }
        editor.apply();
    }

    public void setPassword(String password) {
        if (AES_KEY_USER == null) {
            if (phoneUtils == null) {
                phoneUtils = new PhoneUtils(context);
            }
            AES_KEY_USER = phoneUtils.getUniqueString(password);
        }
    }
}
