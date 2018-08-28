package com.ldx.conversationbase.network.httpasync;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ldx.conversationbase.common.XIChatConst;
import com.ldx.conversationbase.db.XIChatMessageBean;
import com.ldx.conversationbase.jsonparse.XIChatResponseParse;
import com.ldx.conversationbase.network.XIResponseInformationTool;
import com.ldx.conversationbase.network.callbackinterface.XIAsyncTaskListener;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by v-doli1 on 2017/8/24.
 */

public class XIInfoGetAsyncTask extends AsyncTask<String, Void, String> {
    private XIAsyncTaskListener listener;
    private String responseContent = "";
    private Context mContext;
    private long mChatbeanId;

    public XIInfoGetAsyncTask(Context context,XIAsyncTaskListener listener,long position) {
        this.mContext = context;
        this.listener = listener;
        this.mChatbeanId = position;
    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String path = params[1];
        if (type.equals(XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT)){
            //responseContent = XIResponseInformationTool.getResponseInformation(mContext,"",path,XIChatConst.XICONVERSATION_REQUESTTYPE_TEXT);
           // Random r1 = new Random();
            int num1 = (int)(1+Math.random()*(10-1+1));
            int num2 = (int)(1+Math.random()*(10-1+1));
            if (num1 % 2 == 0 && num2 % 2 == 0){
                responseContent = "{\n" +
                        "    \"Answer\": [\n" +
                        "        {\n" +
                        "            \"Content\": \"hello，欢迎您和我聊天！\",\n" +
                        "            \"Type\": \"Text\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"Service\": \"good\"\n" +
                        "}";
            }else{
                responseContent = "{\n" +
                        "    \"Answer\": [\n" +
                        "        {\n" +
                        "            \"CoverUrl\": \"https://avatar.csdn.net/0/6/F/3_u010126792.jpg\",\n" +
                        "            \"CreateTime\": \"1535101577\",\n" +
                        "            \"Description\": \"miaoshu\",\n" +
                        "            \"FeedID\": \"id\",\n" +
                        "            \"Reserved1\": \"{}\",\n" +
                        "            \"Title\": \"我是新闻标题\",\n" +
                        "            \"Type\": \"Card\",\n" +
                        "            \"Url\": \"https://www.baidu.com\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"Service\": \"good\"\n" +
                        "}";
            }

        }else if (type.equals(XIChatConst.XICONVERSATION_REQUESTTYPE_IMAGE)){
            //responseContent = XIResponseInformationTool.getResponseInformation(mContext,"",path,XIChatConst.XICONVERSATION_REQUESTTYPE_IMAGE);
            responseContent = "{\n" +
                    "    \"Answer\": [\n" +
                    "        {\n" +
                    "            \"Type\": \"Image\",\n" +
                    "            \"Url\": \"https://avatar.csdn.net/0/6/F/3_u010126792.jpg\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Service\": \"good\"\n" +
                    "}";
        }else if (type.equals(XIChatConst.XICONVERSATION_REQUESTTYPE_WAVSPEECH)
                || type.equals(XIChatConst.XICONVERSATION_REQUESTTYPE_AMRSPEECH )){
            //responseContent = XIResponseInformationTool.getResponseInformation(mContext, "",path,XIChatConst.XICONVERSATION_REQUESTTYPE_AMRSPEECH);
            responseContent ="{\n" +
                    "    \"Answer\": [\n" +
                    "        {\n" +
                    "            \"Duration\": \"222\",\n" +
                    "            \"Type\": \"Speech\",\n" +
                    "            \"Url\": \"www.sldfjsl\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"Service\": \"good\"\n" +
                    "}" ;
        }
        return responseContent;
    }

    @Override
    protected void onPostExecute(String msg) {
        if (!TextUtils.isEmpty(msg) && !msg.equals(XIChatConst.XIXIAOICERESPONSEERROR)){
            //parse
           // XIChatResponseParse.getErrorCode(msg);//get error code konw upload success or fail
            ArrayList<XIChatMessageBean> beanList = XIChatResponseParse.praseresult(msg);
            if (listener != null){
                listener.onComplete(beanList,mChatbeanId);
            }else{
                if (listener != null){
                    listener.onError("error",mChatbeanId);
                }

            }
        }else{
            if (listener != null){
                listener.onError("error",mChatbeanId);
            }
        }
    }
}

