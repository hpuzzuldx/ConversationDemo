package com.ldx.conversationbase.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.ldx.conversationbase.utils.XIConLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by v-doli1 on 2017/8/24.
 */

public class XIResponseInformationTool {
    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";
    private static final String TAG = "XIResponseInformationTool";

    /**
     * create requestbody
     * @param parameter request string or base64string
     * @param type request type
     * @return string (json)
     */
    public static String getRequestBody(String parameter,String type){
        if (TextUtils.isEmpty(parameter))return null;
        JSONObject queryJson = new JSONObject();
        try {
            queryJson.put("query", parameter);
            queryJson.put("messageType", type);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        String content = String.valueOf(queryJson);
        return content;
    }

    /**
     * get response result
     * @param urlStr    url
     * @param parameter  base64String or string
     * @param type  type(text image voice )
     * @return  string json
     */
    public static String getResponseInformation (Context context, String urlStr, String parameter, String type)
    {
        return "";
    }

    /**
     * Build request for SAI.
     * @param url : request url.
     * @param appID : app ID.
     * @param userID : user ID.
     * @param timestamp : the number of seconds since January 1, 1970, 00:00:00 UTC.
     * @param signature : computed from request url, headers and body.
     * @param requestBody : request body.
     * @return SAI request.
     **/
    public static HttpURLConnection BuildRequest(
            URL url,
            String appID,
            String userID,
            long timestamp,
            String signature,
            String requestBody) throws Exception {
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
    try{
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("x-msxiaoice-request-app-id", appID);
        con.setRequestProperty("x-msxiaoice-request-timestamp", String.valueOf(timestamp));
        con.setRequestProperty("x-msxiaoice-request-user-id", userID);
        con.setRequestProperty("x-msxiaoice-request-signature", signature);
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setConnectTimeout(20000);
        con.setReadTimeout(20000);
        byte[] outputBytes = requestBody.getBytes(ENCODING);
        OutputStream os = con.getOutputStream();
        os.write( outputBytes );
        os.close();
    }catch (Exception e){
        XIConLogUtil.d(TAG,"responseString444:    "+e);
        e.printStackTrace();
    }
        return con;
    }

    /**
     * HmacSHA1 Encrypt.
     * @param encryptText : content to be encrypted.
     * @param encryptKey : secret key.
     * @return Encrypted bytes.
     **/
    public static byte[] HmacSHA1Encrypt(
            String encryptText,
            String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        byte[] digest = mac.doFinal(text);
        return digest;
    }

    private static String join(String[] strs,String splitter) {
        StringBuffer sb = new StringBuffer();
        for(String s:strs){
            sb.append(s+splitter);
        }
        return sb.toString().substring(0, sb.toString().length()-1);
    }

    /**
     * get file's base64 string
     * @param filePath file's path
     * @return string
     */
    public static String getBase64Str(String filePath){
        byte[] readData = null;
        String rtStr = "";
        try
        {
            File file = new File(filePath);

            FileInputStream fis = new FileInputStream(filePath);
            if(fis != null && fis.getChannel() != null) {
                readData = new byte[(int)file.length()];
                int i = fis.read(readData);
                fis.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        if (readData != null) {
            rtStr = Base64.encodeToString(readData, Base64.NO_WRAP);
        }
        return rtStr;
    }

    public static String ComputeSignature(
            String verb,
            String path,
            String[] paramList,
            String[] headerList,
            String requestBody,
            long timestamp,
            String secret) throws Exception {
       //加密过程
        return "";
    }

}
