package com.superkeychain.keychain.https;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.superkeychain.keychain.R;
import com.superkeychain.keychain.action.Action;
import com.superkeychain.keychain.utils.AESUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by taofeng on 3/14/16.
 */

public class HttpsPostAsync extends AsyncTask<String, String, Object> {


    private Context context = null;
    private String urlStr;
    private String requestJson;
    private SSLSocketFactory sslSocketFactory;
    private HostnameVerifier hostnameVerifier;
    private HttpsCustomListener httpsCustomListener;
    private String aesKey;
    private int responseCode = HttpURLConnection.HTTP_OK;
    private boolean networkStatus = true;


    public HttpsPostAsync(Context context) {
        this.context = context;
        try {
            initSSL();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public void initSSL() throws CertificateException, IOException, KeyStoreException,
            NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        //TO DO pubkey should not get from local file but from CA
        InputStream in = context.getResources().openRawResource(R.raw.c9users_io);
//        InputStream in = context.getResources().openRawResource(R.raw.taofeng);
        Certificate ca = cf.generateCertificate(in);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null, null);
        keystore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keystore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");

        context.init(null, tmf.getTrustManagers(), null);
        sslSocketFactory = context.getSocketFactory();
        hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                if (hostname.matches("\\d*.\\d*.\\d*.\\d*"))
                    return true;
                String[] allowedHosts = {"localhost", "superkeychain.com", "www.superkeychain.com", "keychain-miui.c9users.io"};
                for (String host : allowedHosts) {
                    if (host.equalsIgnoreCase(hostname))
                        return true;
                }
                return false;
            }
        };
    }


    /**
     * HttpUrlConnection 方式，支持指定server.crt证书验证，此种方式Android官方建议
     *
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private String post(String message) throws IOException {

        HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
//        URL url = new URL("https://192.168.0.100/keychain/client/httptest/decodersa/");
        URL url = new URL(urlStr);
        //URL url = new URL("https://baidu.com");
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        //urlConnection.setSSLSocketFactory(context.getSocketFactory());

        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");

        urlConnection.setRequestProperty("Accept", "application/json;q=0.9,image/webp,*/*;");
        urlConnection.setRequestProperty("Cache-Control", "max-age=0");
        urlConnection.setRequestProperty("Connection", "keep-alive");
        urlConnection.setRequestProperty("User-Agent", "keychain/0.1 (Android)");
        urlConnection.setConnectTimeout(3000);
        OutputStream os = urlConnection.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        bw.write(message);
        bw.flush();
        osw.flush();
        os.flush();
        bw.close();
        osw.close();
        os.close();

        responseCode = urlConnection.getResponseCode();
        httpsCustomListener.setResponseCode(responseCode);

        InputStream is = urlConnection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        StringBuffer sb = new StringBuffer();
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
//        Log.d("HttpsResponse", sb.toString());
        return sb.toString();
    }

    @Override
    protected Object doInBackground(String... params) {
        if (params.length == 3) {
            this.urlStr = params[0];
            this.requestJson = params[1];
            this.aesKey = params[2];
        }
        this.httpsCustomListener.setAesKey(this.aesKey);
        this.httpsCustomListener.setContext(context);
        try {
            String response = post(requestJson);
            return httpsCustomListener.doHttpsResponse(response);

        } catch (IOException e) {
            httpsCustomListener.setNetworkStatus(false);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (this.httpsCustomListener != null)
            this.httpsCustomListener.doHttpsFinished(o);

    }

    public HttpsPostAsync setHttpsCustomListener(HttpsCustomListener httpsCustomListener) {
        this.httpsCustomListener = httpsCustomListener;
        return this;
    }

    public static abstract class HttpsCustomListener {
        protected int responseCode = HttpURLConnection.HTTP_OK;
        protected int statusCode = 0;
        protected boolean networkStatus = true;
        protected String aesKey;
        protected Context context;
        protected String message="";

        private void setContext(Context context) {
            this.context = context;
        }

        public void setAesKey(String aesKey) {
            this.aesKey = aesKey;
        }


        public void doHttpsFinished(Object object) {
            doHttpsFinished(object, true);
        }

        public void doHttpsFinished(Object object, boolean showToast) {
            if (showToast) {
                if (statusCode != 0) {
                    String errorMessage = Action.getStatusMessage(statusCode);
                    if (errorMessage != null)
                       message = Action.getStatusMessage(statusCode);
                } else {
                    switch (responseCode) {
                        case HttpURLConnection.HTTP_NOT_FOUND:
                            message ="Invalid Action";
                            break;
                        case HttpURLConnection.HTTP_INTERNAL_ERROR:
                            message = "Internal Error";
                            break;
                        default:
                            if (!networkStatus) {
                                message ="Network Error";
                            }
                            break;
                    }
                }
            }

        }

        public Object doHttpsResponse(String response) {
            String responseData = "{}";
            try {
                JSONObject responseJSONObject = new JSONObject(response);
                statusCode = responseJSONObject.getInt(Action.STATUS_CODE);
                String dataCrypto = responseJSONObject.optString(Action.DATA_CRYPT_AES, null);
                if (dataCrypto != null) {
                    try {
                        responseData = AESUtils.decrypt(aesKey, dataCrypto);
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return responseData;
        }

        public void setResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }

        public void setNetworkStatus(boolean networkStatus) {
            this.networkStatus = networkStatus;
        }
    }

}