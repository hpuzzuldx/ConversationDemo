package com.ldx.conversationbase.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class XIConversactionGreenDaoHelper {
    private static DaoSession daoSession;

    private static void setupDatabase(Context context,String dbname) {
        XIConversationSQLiteOpenHelper helper = new XIConversationSQLiteOpenHelper(context.getApplicationContext(), dbname, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstance(Context context ,String dbname) {
        try{
            if (daoSession == null) {
                synchronized (XIConversactionGreenDaoHelper.class) {
                    if (daoSession == null && !TextUtils.isEmpty(dbname)) {
                        setupDatabase(context,dbname);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return daoSession;
    }
}