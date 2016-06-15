package com.superkeychain.keychain.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by taofeng on 3/14/16.
 */
public class PhoneUtils {

    private static String simSerialNumber;
    private static String imei;
    private static String deviceSoftwareVersion;
    private static String line1Number;

    Context context;
    TelephonyManager tm;

    public PhoneUtils(Context context) {
        this.context = context;
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getIMEI() {
        if (imei == null) {
            try {
                imei = tm.getDeviceId();
            } catch (SecurityException e) {
                e.printStackTrace();
                imei = "1458781847";
            }
        }
        return imei;
    }

    public String getSimSerialNumber() {
        if (simSerialNumber == null) {
            try {
                simSerialNumber = tm.getSimSerialNumber();
            } catch (SecurityException e) {
                e.printStackTrace();
                simSerialNumber = "c4363aca7d6d95b00a68a35";
            }

        }
        return simSerialNumber;
    }

    public String getDeviceSoftwareVersion() {
        if (deviceSoftwareVersion == null) {
            try {
                deviceSoftwareVersion = tm.getDeviceSoftwareVersion();
            } catch (SecurityException e) {
                e.printStackTrace();
                deviceSoftwareVersion = "e7325394c5c5672c44968";
            }
        }
        return deviceSoftwareVersion;
    }

    public String getLine1Number() {
        if (line1Number == null) {
            try {
                line1Number = tm.getLine1Number();
            } catch (SecurityException e) {
                e.printStackTrace();
                line1Number = "b59097495f44161";
            }
        }
        return line1Number;
    }

    public String getUniqueString(String pattern) {
//        Log.d("uniqueString", getIMEI() + getSimSerialNumber() + getLine1Number() + getDeviceSoftwareVersion() + pattern);
        return Digest.Builder.bySHA256().getDigest(getIMEI() + getSimSerialNumber() + getLine1Number() + getDeviceSoftwareVersion() + pattern);
    }
}
