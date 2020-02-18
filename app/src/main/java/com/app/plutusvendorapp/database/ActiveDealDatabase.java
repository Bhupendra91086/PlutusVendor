package com.app.plutusvendorapp.database;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.bean.serverdeal.DealActive;


@Database(entities = {DealActive.class} , version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ActiveDealDatabase extends RoomDatabase {
    private static ActiveDealDatabase INSTANCE;
    public abstract DaoActiveDeal daoActiveDeal();
    private static final Object sLock = new Object();
    public static ActiveDealDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ActiveDealDatabase.class, "Deal_active.db")
                        .allowMainThreadQueries()
                        .addCallback(new Callback() {
                            @Override
                            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                super.onCreate(db);
                               /* Executors.newSingleThreadExecutor().execute(
                                        () -> getInstance(context).postDao().saveAll(POSTS));*/
                            }
                        })
                        .build();
            }
            return INSTANCE;
        }
    }

}


