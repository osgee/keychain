package com.superkeychain.keychain.action;

import android.app.Activity;
import android.content.Context;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.entity.User;
import com.superkeychain.keychain.https.SecureJsonObject;
import com.superkeychain.keychain.repository.AppRepository;
import com.superkeychain.keychain.repository.UserRepository;
import com.superkeychain.keychain.utils.AESUtils;
import com.superkeychain.keychain.utils.PhoneUtils;
import com.superkeychain.keychain.utils.RSAUtils;

import org.json.JSONException;

import java.io.InputStream;
import java.security.PublicKey;
import java.util.Calendar;

/**
 * Created by taofeng on 3/19/16.
 */
public abstract class Action {
    public static final String USER_KEY = "user";
    public static final String ACCOUNTS_KEY = "accounts";
    public static final String APPS_KEY = "apps";
    public static final String TIME = "time";
    public static final String AES_KEY = "aes_key";
    public static final String COOKIE = "user_cookie";
    public static final String CERT_CRYPT_RSA = "cert_crypt_rsa";
    public static final String DATA_CRYPT_AES = "data_crypt_aes";
    public static final String USERNAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String PASSWORD = "user_password";
    public static final String ACCOUNT_TYPE = "account_type";
    public static final String DEVICE_ID = "device_id";
    public static final String ACCOUNT = "account";
    public static final String STATUS_CODE = "status_code";
    public static final String RANDOM_TOKEN = "random";
    public static final String SIGN = "token";
    public static final String SIM_SERIAL_NUMBER = "sim_serial_number";
    public static final String LINE_1_NUMBER = "line_1_number";
    //Actions URI
    public static final String PROTOCOl_HTTPS = "https://";
    //    public static final String HOST = "192.168.43.119";
    public static final String HOST = "keychain-miui.c9users.io";
    public static final String ACTION_SIGN_IN = "/keychain/client/user/signin/";
    public static final String ACTION_SIGN_UP = "/keychain/client/user/signup/";
    public static final String ACTION_SIGN_OUT = "/keychain/client/user/signout/";
    public static final String ACTION_CHECK_COOKIE = "/keychain/client/user/cookie/check/";
    public static final String ACTION_GET_ACCOUNTS = "/keychain/client/user/accounts/get/";
    public static final String ACTION_GET_ACCOUNT = "/keychain/client/user/account/get/";
    public static final String ACTION_ADD_ACCOUNT = "/keychain/client/user/account/add/";
    public static final String ACTION_UPDATE_ACCOUNT = "/keychain/client/user/account/update/";
    public static final String ACTION_DELETE_ACCOUNT = "/keychain/client/user/account/delete/";
    public static final String ACTION_GET_ALL_APPS = "/keychain/client/user/apps/getall/";
    public static final int ACCOUNT_TYPE_USERNAME = 1;
    public static final int ACCOUNT_TYPE_EMAIL = 2;
    public static final int ACCOUNT_TYPE_CELLPHONE = 3;
    public static final int ACCOUNT_TYPE_COOKIE = 4;
    /**
     * status_code{
     * OK :1
     * error:
     * Account Not Exists -1
     * Username Exists -2
     * Invalid User -3
     * Account Type Error -4
     * Time Out of Range -5
     * Method Error -6
     * Captcha Error -7
     * }
     */

    public static final int STATUS_CODE_OK = 1;
    public static final int STATUS_CODE_ACCOUNT_NOT_EXISTS = -1;
    public static final int STATUS_CODE_ACCOUNT_EXISTS = -2;
    public static final int STATUS_CODE_INVALID_USER = -3;
    public static final int STATUS_CODE_ACCOUNT_TYPE_ERROR = -4;
    public static final int STATUS_CODE_METHOD_ERROR = -5;
    public static final int STATUS_CODE_CAPTCHA_ERROR = -6;
    public static final int STATUS_CODE_SIGN_ERROR = -7;
    public static final int STATUS_CODE_COOKIE_ERROR = -8;
    protected static User user;
    protected static PhoneUtils phoneUtils;
    //    protected static int accountType;
    protected static String deviceId;
    protected static String aesKey;
    protected static String sim_serial_number;
    protected static String line_1_number;
    protected final InputStream inpub;
    protected Activity activity;
    protected Context context;
    protected String randomToken;
    protected UserRepository userRepository;
    protected AppRepository appRepository;
    protected PublicKey publicKey;

    public Action(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        user = new User();
        try {
            phoneUtils = phoneUtils == null ? new PhoneUtils(context) : phoneUtils;
            deviceId = deviceId == null ? phoneUtils.getIMEI() : deviceId;
            sim_serial_number = sim_serial_number == null ? phoneUtils.getSimSerialNumber() : sim_serial_number;
            line_1_number = line_1_number == null ? phoneUtils.getLine1Number() : line_1_number;
        } catch (Exception e) {
            e.printStackTrace();
        }
        aesKey = aesKey == null ? AESUtils.getRandomKey(16) : aesKey;
        randomToken = AESUtils.getRandomKey(8);
        userRepository = new UserRepository(activity, context);
        appRepository = new AppRepository(activity, context);
        inpub = context.getApplicationContext().getResources().openRawResource(R.raw.rsa_public_key);
        try {
            publicKey = RSAUtils.loadPublicKey(inpub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case STATUS_CODE_COOKIE_ERROR:
                return "Sign In Expired";
            case STATUS_CODE_OK:
                return "Succeed";
            case STATUS_CODE_CAPTCHA_ERROR:
                return "Captcha Error";
            case STATUS_CODE_ACCOUNT_NOT_EXISTS:
                return "Account Not Exists";
            case STATUS_CODE_ACCOUNT_EXISTS:
                return "Account Exists";
            case STATUS_CODE_INVALID_USER:
                return "Invalid User";
            case STATUS_CODE_ACCOUNT_TYPE_ERROR:
                return "Account Type Not Supported";
            case STATUS_CODE_METHOD_ERROR:
                return "Method Error";
            default:
                return null;
        }
    }

    protected SecureJsonObject getRawSecureJsonObject() {
        SecureJsonObject secureJsonObject = new SecureJsonObject(publicKey, aesKey);
        Long time = Calendar.getInstance().getTimeInMillis() / 1000;
        try {
            secureJsonObject.addAttribute(TIME, time);
            secureJsonObject.addAttribute(RANDOM_TOKEN, randomToken);
            if (user != null) {
                secureJsonObject.addSecureAttribute(USER_ID, user.getId());
                secureJsonObject.addSecureAttribute(COOKIE, user.getCookie());
            }
            secureJsonObject.addSecureAttribute(AES_KEY, aesKey);
            secureJsonObject.addSecureAttribute(TIME, time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return secureJsonObject;
    }

    protected String getURI(String protocol, String host, String action) {
        return protocol + host + action;
    }

}
