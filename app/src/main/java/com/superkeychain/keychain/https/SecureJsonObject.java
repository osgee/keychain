package com.superkeychain.keychain.https;

import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.utils.AESUtils;
import com.superkeychain.keychain.utils.Base64Utils;
import com.superkeychain.keychain.utils.Digest;
import com.superkeychain.keychain.utils.RSAUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;

/**
 * Created by taofeng on 3/17/16.
 */
public class SecureJsonObject {
    public static final String DATA_KEY = "data";
    public static final String SIGN_KEY = "sign";
    private JSONObject secureJsonObject;
    private JSONObject signJsonObject;
    private JSONObject safeJsonObject;
    private JSONObject safeDataJsonObject;
    private PublicKey publicKey;
    private String aesKey;
    private String secureAttrsKey = Action.CERT_CRYPT_RSA;
    private String secureDataKey = Action.DATA_CRYPT_AES;

    private JSONObject plainJsonObject;

    public SecureJsonObject(PublicKey publicKey, String aesKey) {
        safeJsonObject = new JSONObject();
        secureJsonObject = new JSONObject();
        signJsonObject = new JSONObject();
        safeDataJsonObject = new JSONObject();
        this.publicKey = publicKey;
        this.aesKey = aesKey;
    }

    public SecureJsonObject addSecureAttribute(String attributeKey, String attributeValue) throws JSONException {
        safeJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public SecureJsonObject addSecureAttribute(String attributeKey, long attributeValue) throws JSONException {
        safeJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public SecureJsonObject addSecureAttribute(String attributeKey, int attributeValue) throws JSONException {
        safeJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public SecureJsonObject addAttribute(String attributeKey, String attributeValue) throws JSONException {
        secureJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public SecureJsonObject addAttribute(String attributeKey, long attributeValue) throws JSONException {
        secureJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public SecureJsonObject addAttribute(String attributeKey, int attributeValue) throws JSONException {
        secureJsonObject.put(attributeKey, attributeValue);
        return this;
    }

    public JSONObject getSafeJsonObject() {
        return safeJsonObject;
    }

    public void setSafeJsonObject(JSONObject safeJsonObject) {
        this.safeJsonObject = safeJsonObject;
    }

    public JSONObject getSecureJsonObject() {
        return secureJsonObject;
    }

    public void setSecureJsonObject(JSONObject secureJsonObject) {
        this.secureJsonObject = secureJsonObject;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getSecureAttrsKey() {
        return secureAttrsKey;
    }

    public void setSecureAttrsKey(String secureAttrsKey) {
        this.secureAttrsKey = secureAttrsKey;
    }

    public void addSecureData(String dataKey, String data) throws JSONException {
        safeDataJsonObject.put(dataKey, data);
    }

    public String toPlainString(){
        plainJsonObject = new JSONObject();
        try {
            plainJsonObject.put(secureAttrsKey,safeJsonObject.toString());
            plainJsonObject.put(secureDataKey,safeDataJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return plainJsonObject.toString();
    }

    @Override
    public String toString() {
        String safe = safeJsonObject.toString();
        byte[] safeByte = null;
        byte[] cryptByte = null;
        String crypt = null;
        String cryptData = null;
        try {
            safeByte = safe.getBytes("utf-8");
            cryptByte = RSAUtils.encryptData(safeByte, publicKey);
            crypt = Base64Utils.encode(cryptByte);
            cryptData = AESUtils.encrypt(aesKey, safeDataJsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        try {
            secureJsonObject.put(secureAttrsKey, crypt);
            secureJsonObject.put(secureDataKey, cryptData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String secureJsonString = secureJsonObject.toString();

        String secureJsonCrypt = signature(secureJsonString, aesKey);
        String signDigest = Digest.Builder.bySHA256().getDigest(secureJsonCrypt);


        try {
            signJsonObject.put(DATA_KEY, secureJsonString);
            signJsonObject.put(SIGN_KEY, signDigest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return signJsonObject.toString();
    }

    private String signature(String message, String key) {
        //simple implementation of sign
        return message + key;
    }

}
