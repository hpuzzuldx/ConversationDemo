package com.ldx.conversationbase.jsonparse;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.db.XICardMessageBean;
import com.ldx.conversationbase.db.XIChatMessageBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ldx.conversationbase.common.XIChatConst.XICONVERSATION_ERROR;
import static com.ldx.conversationbase.common.XIChatConst.XICONVERSATION_SERVICE;

/**
 * Created by v-doli1 on 2017/8/24.
 */

public class XIChatResponseParse {
    /**
     * is error
     * @param content
     * @return
     */
     public static boolean haveErrorCode(String content){
         if (TextUtils.isEmpty(content) || content.equals(XIChatConst.XIXIAOICERESPONSEERROR)){
             return true;
         }
         try{
             JSONObject object = new JSONObject(content);
             if (object != null && object.has(XICONVERSATION_ERROR)){
                 JSONObject errorObject =  object.getJSONObject(XIChatConst.XICONVERSATION_ERROR);
                 errorObject.optString(XIChatConst.XICONVERSATION_ERRORCode);
                 return true;
             }
         }catch(Exception e){
             return true;
         }
         return false;
     }

    /**
     * get errorCode
     * @param content
     * @return
     */
    public static String getErrorCode(String content){
        try{
            JSONObject object = new JSONObject(content);
            if (object != null && object.has(XICONVERSATION_ERROR)){
                JSONObject errorObject =  object.getJSONObject(XIChatConst.XICONVERSATION_ERROR);
                String errorcode = errorObject.optString(XIChatConst.XICONVERSATION_ERRORCode);
                return errorcode;
            }
        }catch(Exception e){
            return "";
        }
        return "";
    }

    /**
     * get infomation from jsonobject
     * @param object
     * @return
     */
    public static ArrayMap<Integer, HashMap<String,String>> getResponseInfo(JSONObject object) {
        ArrayMap<Integer, HashMap<String,String>> itemList = new ArrayMap<>();
        if (object == null) return null;
        String serviceType = object.optString(XICONVERSATION_SERVICE);
        JSONArray anwserArray = null;
        try {
            anwserArray = object.getJSONArray(XIChatConst.XICONVERSATION_ANSWER);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject anwserObject = null;
        if (anwserArray != null && anwserArray.length() > 0) {
            try {
                for (int i = 0;i<anwserArray.length();i++){
                    anwserObject = anwserArray.getJSONObject(i);
                    HashMap<String,String> hashMap = parseDetailResponse(anwserObject,serviceType);
                    if (hashMap != null){
                        itemList.put(i,hashMap);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return itemList;
    }

    public static HashMap<String, String> parseDetailResponse(JSONObject anwserObject,String serviceType){
        HashMap<String, String> hashMap = new HashMap<String, String>();
        if (anwserObject == null) return null;
        if (!TextUtils.isEmpty(serviceType)) {
            if (anwserObject != null) {
                String responseType = anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE);
                if (!TextUtils.isEmpty(responseType)) {
                    if (responseType.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_TEXT)) {
                        hashMap.put("type", XIChatConst.XICONVERSATION_RESPONSETYPE_TEXT);
                        if (anwserObject.isNull(XIChatConst.XICONVERSATION_RESPONSETYPE_TEXTCONTENT)){
                            hashMap.put("content", "");
                        }else{
                            hashMap.put("content", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_TEXTCONTENT));
                        }

                    } else if (responseType.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_SPEECH)) {
                        hashMap.put("type", XIChatConst.XICONVERSATION_RESPONSETYPE_SPEECH);
                        hashMap.put("url", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_SPEECHURL));
                        hashMap.put("duration", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_DURATION));

                    } else if (responseType.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_IMAGE)) {
                        hashMap.put("type", XIChatConst.XICONVERSATION_RESPONSETYPE_IMAGE);
                        hashMap.put("url", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_IMAGEURL));
                    }else if (responseType.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_MULTIPLE)){
                        hashMap.put("type", XIChatConst.XICONVERSATION_RESPONSETYPE_MULTIPLE);
                        hashMap.put("weburl", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_CARDTITLE));
                        hashMap.put("title", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_CARDURL));
                        hashMap.put("imgurl", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_CARDCOV));
                        hashMap.put("description", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_CARDDES));
                        hashMap.put("createtime", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_CREATETIME));
                        hashMap.put("feedid", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_FEEDID));
                        hashMap.put("reserved1", anwserObject.optString(XIChatConst.XICONVERSATION_RESPONSETYPE_RESERVED1));

                    }
                }
            }
        }
        return hashMap;
    }

    public static ArrayList<XIChatMessageBean> praseresult(String result){
        ArrayList<XIChatMessageBean> chatMessageList = new ArrayList<>();
        ArrayList<XIChatMessageBean> chatCardList = new ArrayList<>();
        if (XIChatResponseParse.haveErrorCode(result)){
            return chatMessageList;
        }else{
            JSONObject object = null;
            try {
                object = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (object != null){
                ArrayMap<Integer, HashMap<String,String>> arrayMap = XIChatResponseParse.getResponseInfo(object);
                if (arrayMap != null && arrayMap.size() >0 ){
                    for (HashMap.Entry<Integer, HashMap<String,String>> example : arrayMap.entrySet()) {
                        if(example.getValue().size() > 0) {
                            String hashtype = example.getValue().get("type");
                            if (hashtype.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_TEXT)){
                                XIChatMessageBean chatMessageBean =  new XIChatMessageBean();
                                chatMessageBean.setUserName(XIChatConst.XICONVERSATION_XIAOICE_NAME);
                                String time = returnTime();
                                chatMessageBean.setTime(time);
                                chatMessageBean.setType(XIChatConst.FROM_USER_MSG);
                                chatMessageBean.setUserContent(example.getValue().get("content"));
                                if (!TextUtils.isEmpty(chatMessageBean.getUserContent())){
                                    chatMessageList.add(chatMessageBean);
                                }
                            }else if (hashtype.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_SPEECH)){
                                XIChatMessageBean  chatMessageBean =  new XIChatMessageBean();
                                chatMessageBean.setUserName(XIChatConst.XICONVERSATION_XIAOICE_NAME);
                                String time = returnTime();
                                chatMessageBean.setTime(time);
                                chatMessageBean.setType(XIChatConst.FROM_USER_VOICE);
                                chatMessageBean.setUserVoiceUrl(example.getValue().get("url"));
                                chatMessageBean.setUserVoiceTime((Float.parseFloat(example.getValue().get("duration")))/1000);
                                chatMessageList.add(chatMessageBean);

                            }else if (hashtype.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_IMAGE)){
                                XIChatMessageBean chatMessageBean =  new XIChatMessageBean();
                                chatMessageBean.setUserName(XIChatConst.XICONVERSATION_XIAOICE_NAME);
                                String time = returnTime();
                                chatMessageBean.setTime(time);
                                chatMessageBean.setType(XIChatConst.FROM_USER_IMG);
                                chatMessageBean.setImageUrl(example.getValue().get("url"));
                                chatMessageList.add(chatMessageBean);

                            }else if (hashtype.equals(XIChatConst.XICONVERSATION_RESPONSETYPE_MULTIPLE)){
                                //mul type
                                XIChatMessageBean  chatMessageBean =  new XIChatMessageBean();
                                chatMessageBean.setUserName(XIChatConst.XICONVERSATION_XIAOICE_NAME);
                                String time = returnTime();
                                chatMessageBean.setTime(time);
                                chatMessageBean.setType(XIChatConst.FROM_USER_MULTILE);
                                chatMessageBean.setCardDescription(example.getValue().get("description"));
                                chatMessageBean.setCardTitle(example.getValue().get("title"));
                                chatMessageBean.setWebUrl(example.getValue().get("weburl"));
                                chatMessageBean.setImageUrl(example.getValue().get("imgurl"));
                                chatMessageBean.setFeedId(example.getValue().get("feedid"));
                                chatMessageBean.setReserved1(example.getValue().get("reserved1"));
                                try {
                                    if (example.getValue().get("createtime") != ""){
                                        chatMessageBean.setCreateTime(Long.parseLong(example.getValue().get("createtime")));
                                    }else{
                                        chatMessageBean.setCreateTime(System.currentTimeMillis());
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                    chatMessageBean.setCreateTime(1535101581);
                                }

                                //chatMessageList.add(chatMessageBean);
                                chatCardList.add(chatMessageBean);
                            }
                        }
                    }
                }
            }
        }
        if (chatCardList.size() >0 ){
            XIChatMessageBean  chatMessageBean2 =  new XIChatMessageBean();
            chatMessageBean2.setUserName(XIChatConst.XICONVERSATION_XIAOICE_NAME);
            String time2 = returnTime();
            chatMessageBean2.setTime(time2);
            chatMessageBean2.setType(XIChatConst.FROM_USER_MULTILE);
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            JSONObject tmpObj = null;
            for (XIChatMessageBean xiChatMessageBean : chatCardList){
                tmpObj = new JSONObject();
                try {
                    tmpObj.put("mesgTitle", xiChatMessageBean.getCardTitle());
                    tmpObj.put("mesgType" , xiChatMessageBean.getType());
                    tmpObj.put("mesgTime", xiChatMessageBean.getTime());
                    tmpObj.put("mesgCoverurl", xiChatMessageBean.getImageUrl());
                    tmpObj.put("mesgDescription", xiChatMessageBean.getCardDescription());
                    tmpObj.put("mesgUrl", xiChatMessageBean.getWebUrl());
                    tmpObj.put("mesgTime", xiChatMessageBean.getCreateTime());
                    tmpObj.put("mesgFeedId", xiChatMessageBean.getFeedId());
                    tmpObj.put("mesgReserved1", xiChatMessageBean.getReserved1());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(tmpObj);
                tmpObj = null;
            }
            try {
                jsonObject.put("CardBean", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (jsonObject != null ){
                if (!TextUtils.isEmpty(jsonObject.toString())){
                    chatMessageBean2.setJsonString(jsonObject.toString());
                }
            }
            if (chatMessageList.size() > 0){
                chatMessageList.add(0,chatMessageBean2);
            }else{
                chatMessageList.add(chatMessageBean2);
            }
        }
        return chatMessageList;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static ArrayList<XICardMessageBean> praseCardJson(String result) {
        ArrayList<XICardMessageBean> chatCardList = new ArrayList<>();
        JSONObject object = null;
        JSONArray jsonArray = null;
        try {
            object = new JSONObject(result);
            if (object != null) {
                jsonArray = object.getJSONArray("CardBean");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jsonArray != null && jsonArray.length() > 0){
            for (int i = 0;i<jsonArray.length();i++){
                try {
                    JSONObject tempJsonObject = jsonArray.getJSONObject(i);
                    XICardMessageBean xiCardMessageBean = new XICardMessageBean();
                    xiCardMessageBean.setMesgTitle(tempJsonObject.optString("mesgTitle"));
                    xiCardMessageBean.setMesgDescription(tempJsonObject.optString("mesgDescription"));
                    xiCardMessageBean.setMesgCoverurl(tempJsonObject.optString("mesgCoverurl"));
                    xiCardMessageBean.setMesgUrl(tempJsonObject.optString("mesgUrl"));
                    xiCardMessageBean.setMesgType(tempJsonObject.optInt("mesgType"));
                    xiCardMessageBean.setFeedId(tempJsonObject.optString("mesgFeedId"));
                    xiCardMessageBean.setReserved1(tempJsonObject.optString("mesgReserved1"));
                    if (tempJsonObject.optInt("mesgTime") != 0){
                        xiCardMessageBean.setMesgTime(tempJsonObject.optLong("mesgTime"));
                    }else{
                        xiCardMessageBean.setMesgTime(0);
                    }

                    chatCardList.add(xiCardMessageBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return chatCardList;
    }
}
