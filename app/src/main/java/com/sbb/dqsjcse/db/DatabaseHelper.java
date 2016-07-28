package com.sbb.dqsjcse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bingbing on 16/7/23.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TABLE_NAME = "sqlite-dqsj-cse.db";
    private static final int DatabaseVersion = 1;

    private Map<String, Dao> daos = new HashMap<String, Dao>();

    private static DatabaseHelper instance;
    private DatabaseHelper(Context context)
    {
        super(context, TABLE_NAME, null,DatabaseVersion);
    }
    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context)
    {
        context = context.getApplicationContext();
        if (instance == null)
        {
            synchronized (DatabaseHelper.class)
            {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException
    {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className))
        {
            dao = daos.get(className);
        }
        if (dao == null)
        {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

//        try {
//            TableUtils.createTable(connectionSource,User.class);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
//        try {
//            TableUtils.dropTable(connectionSource,User.class,true);
//            onCreate(sqLiteDatabase,connectionSource);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void close() {
        super.close();
    }
}
