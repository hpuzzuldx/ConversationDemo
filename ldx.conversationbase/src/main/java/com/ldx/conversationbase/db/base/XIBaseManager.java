package com.ldx.conversationbase.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.ldx.conversationbase.common.XICacheResourceManager;
import com.ldx.conversationbase.db.XIChatMessageBeanDao;
import com.ldx.conversationbase.db.XIConversactionGreenDaoHelper;
import com.ldx.conversationbase.db.interfaces.XIIDatabase;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Collection;
import java.util.List;

public abstract class XIBaseManager<M, K> implements XIIDatabase<M, K> {

    @Override
    public boolean insert(Context context,@NonNull M m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).insert(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertOrReplace(Context context,@NonNull M m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).insertOrReplace(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Context context,@NonNull M m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).delete(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKey(Context context,K key) {
        try {
            if (key.toString().isEmpty())
                return false;
            getChatMessageBeanDao(context).deleteByKey(key);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteByKeyInTx(Context context,K... key) {
        try {
            getChatMessageBeanDao(context).deleteByKeyInTx(key);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteList(Context context,List<M> mList) {
        try {
            if (mList == null || mList.size() == 0)
                return false;
            getChatMessageBeanDao(context).deleteInTx(mList);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAll(Context context) {
        try {
            getChatMessageBeanDao(context).deleteAll();
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(Context context,@NonNull M m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).update(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateInTx(Context context,M... m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).updateInTx(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean updateList(Context context,List<M> mList) {
        try {
            if (mList == null || mList.size() == 0)
                return false;
            getChatMessageBeanDao(context).updateInTx(mList);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public M selectByPrimaryKey(Context context,@NonNull K key) {
        try {
            return getChatMessageBeanDao(context).load(key);
        } catch (SQLiteException e) {
            return null;
        }
    }

    @Override
    public List<M> loadAll(Context context) {
        return getChatMessageBeanDao(context).loadAll();
    }

    @Override
    public List<M> loadPages(Context context,int page, int number) {
        return getChatMessageBeanDao(context).queryBuilder()
                .offset(page * number).limit(number).list();
    }

    @Override
    public long getPages(Context context,int number) {
        long count = getChatMessageBeanDao(context).queryBuilder().count();
        long page = count / number;
        if (page > 0 && count % number == 0) {
            return page - 1;
        }
        return page;
    }

    @Override
    public boolean refresh(Context context,@NonNull M m) {
        try {
            if (m == null)
                return false;
            getChatMessageBeanDao(context).refresh(m);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean insertList(Context context,@NonNull List<M> list) {
        try {
            if (list == null || list.size() == 0)
                return false;
            getChatMessageBeanDao(context).insertInTx(list);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    /**
     * @param list
     * @return
     */
    @Override
    public boolean insertOrReplaceList(Context context,@NonNull List<M> list) {
        try {
            if (list == null || list.size() == 0)
                return false;
            getChatMessageBeanDao(context).insertOrReplaceInTx(list);
        } catch (SQLiteException e) {
            return false;
        }
        return true;
    }

    @Override
    public QueryBuilder<M> getQueryBuilder(Context context) {
        return getChatMessageBeanDao(context).queryBuilder();
    }

    /**
     * @param where
     * @param selectionArg
     * @return
     */
    @Override
    public List<M> queryRaw(Context context,String where, String... selectionArg) {
        return getChatMessageBeanDao(context).queryRaw(where, selectionArg);
    }

    public Query<M> queryRawCreate(Context context,String where, Object... selectionArg) {
        return getChatMessageBeanDao(context).queryRawCreate(where, selectionArg);
    }

    public Query<M> queryRawCreateListArgs(Context context,String where, Collection<Object> selectionArg) {
        return getChatMessageBeanDao(context).queryRawCreateListArgs(where, selectionArg);
    }

    @Override
    public boolean dropDatabase(Context context) {
        try{
            XIChatMessageBeanDao.dropTable(getChatMessageBeanDao(context).getDatabase(),true);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public void clearDaoSession(Context context) {
        String dbname = XICacheResourceManager.getDBName();
        XIConversactionGreenDaoHelper.getDaoInstance(context,dbname).clear();
    }

    /**
     * get Dao
     * @return
     */
    public abstract AbstractDao<M, K> getChatMessageBeanDao(Context context);

}
