package com.ldx.conversationbase.db.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public interface XIIDatabase<M, K> {
    boolean insert(Context context, M m);

    boolean delete(Context context, M m);

    boolean deleteByKey(Context context, K key);

    boolean deleteList(Context context, List<M> mList);

    boolean deleteByKeyInTx(Context context, K... key);

    boolean deleteAll(Context context);

    boolean insertOrReplace(Context context, @NonNull M m);

    boolean update(Context context, M m);

    boolean updateInTx(Context context, M... m);

    boolean updateList(Context context, List<M> mList);

    M selectByPrimaryKey(Context context, K key);

    List<M> loadAll(Context context);

    List<M> loadPages(Context context, int page, int number);

    long getPages(Context context, int number);

    boolean refresh(Context context, M m);

    void clearDaoSession(Context context);

    boolean dropDatabase(Context context);

    boolean insertList(Context context, List<M> mList);

    boolean insertOrReplaceList(Context context, List<M> mList);

    QueryBuilder<M> getQueryBuilder(Context context);

    List<M> queryRaw(Context context, String where, String... selectionArg);

}
