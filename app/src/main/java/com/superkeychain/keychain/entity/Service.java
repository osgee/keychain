package com.superkeychain.keychain.entity;

import android.util.Log;

import com.superkeychain.keychain.action.Action;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taofeng on 4/28/16.
 */

public class Service {

    public static final String SERVICE_KEY = "SERVICE";
    public static final String SERVICE_ID = "service_id";
    public static final String SERVICE_ACTION = "service_action";
    public static final String SERVICE_SECRET = "service_secret";
    public static final String SERVICE_TIME = "service_time";
    public static final String SERVICE_STATUS = "service_status";
    public static final String SERVICE_APP = "service_app";
    public static final String SERVICE_ACCOUNT = "service_account";
    public static final String SERVICE_ACCOUNTS = "service_accounts";
    public static final String SERVICE_QRCODE = "service_qrcode";

    private String serviceId;
    private String serviceAction;
    private String serviceSecret;
    private Long serviceTime;
    private String serviceStatus;
    private ThirdPartApp serviceApp;
    private Account serviceAccount;
    private List<Account> serviceAccounts;
    private String serviceQRCode;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceAction() {
        return serviceAction;
    }

    public void setServiceAction(String serviceAction) {
        this.serviceAction = serviceAction;
    }

    public String getServiceSecret() {
        return serviceSecret;
    }

    public void setServiceSecret(String serviceSecret) {
        this.serviceSecret = serviceSecret;
    }

    public Long getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(Long serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public ThirdPartApp getServiceApp() {
        return serviceApp;
    }

    public void setServiceApp(ThirdPartApp serviceApp) {
        this.serviceApp = serviceApp;
    }

    public Account getServiceAccount() {
        return serviceAccount;
    }

    public void setServiceAccount(Account serviceAccount) {
        this.serviceAccount = serviceAccount;
    }

    public String getServiceQRCode() {
        return serviceQRCode;
    }

    public void setServiceQRCode(String serviceQRCode) {
        this.serviceQRCode = serviceQRCode;
    }

    public List<Account> getServiceAccounts() {
        return serviceAccounts;
    }

    public void setServiceAccounts(List<Account> serviceAccounts) {
        this.serviceAccounts = serviceAccounts;
    }

    public static Service parseFromJSON(String json) {
        if (json == null || "null".equals(json)) {
            return null;
        }
        JSONObject jsonService = null;
        try {
            jsonService = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseFromJSON(jsonService);
    }

    public static Service parseFromJSON(JSONObject jsonService) {
        if (jsonService != null) {
            Service service = new Service();
            service.setServiceId(jsonService.optString(SERVICE_ID, null));
            service.setServiceAction(jsonService.optString(SERVICE_ACTION, null));
            service.setServiceSecret(jsonService.optString(SERVICE_SECRET, null));
            service.setServiceTime(jsonService.optLong(SERVICE_TIME, 0l));
            service.setServiceStatus(jsonService.optString(SERVICE_STATUS, null));
            service.setServiceApp(ThirdPartApp.parseFromJSON(jsonService.optString(SERVICE_APP, null)));
            service.setServiceAccount(Account.parseFromJSON(jsonService.optString(SERVICE_ACCOUNT, null)));
            try {
                String jsonArrayString =jsonService.optString(SERVICE_ACCOUNTS, null);
                if(jsonArrayString!=null)
                service.setServiceAccounts(Account.parseFromJSONArray(new JSONArray(jsonArrayString)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            service.setServiceQRCode(jsonService.optString(SERVICE_QRCODE,null));
            return service;
        }
        return null;
    }



    public static JSONObject parseToJSON(Service service) {
        if(service==null)
            return null;
        JSONObject jsonService = new JSONObject();
        try {
            jsonService.put(SERVICE_ID, service.getServiceId());
            jsonService.put(SERVICE_ACTION, service.getServiceAction());
            jsonService.put(SERVICE_SECRET,service.getServiceSecret());
            jsonService.put(SERVICE_TIME,service.getServiceTime());
            jsonService.put(SERVICE_STATUS,service.getServiceStatus());
            jsonService.put(SERVICE_APP, ThirdPartApp.parseToJSON(service.getServiceApp()).toString());
            jsonService.put(SERVICE_ACCOUNT, Account.parseToJSON(service.getServiceAccount()));
            jsonService.put(SERVICE_ACCOUNTS,Account.parseToJSONArray(service.getServiceAccounts()));
            jsonService.put(SERVICE_QRCODE,service.getServiceQRCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonService;
    }


    public String toJSONString() {
        return parseToJSON(this).toString();
    }

}
