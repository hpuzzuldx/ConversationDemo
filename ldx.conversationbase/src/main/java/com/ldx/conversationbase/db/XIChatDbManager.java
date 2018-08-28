package com.ldx.conversationbase.db;

import android.content.Context;

import com.ldx.conversationbase.common.XICacheResourceManager;
import com.ldx.conversationbase.db.base.XIBaseManager;

import org.greenrobot.greendao.AbstractDao;

public class XIChatDbManager extends XIBaseManager<XIChatMessageBean,Long> {
    @Override
    public AbstractDao<XIChatMessageBean, Long> getChatMessageBeanDao(Context context) {
        String dbname = XICacheResourceManager.getDBName();
        return XIConversactionGreenDaoHelper.getDaoInstance(context,dbname).getXIChatMessageBeanDao();
    }
}
