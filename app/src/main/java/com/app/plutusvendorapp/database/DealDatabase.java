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

import java.util.concurrent.Executors;


@Database(entities = {Deal.class} , version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class DealDatabase extends RoomDatabase {
    private static DealDatabase INSTANCE;
    public abstract DaoDeal daoDeal();
    private static final Object sLock = new Object();
    public static DealDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        DealDatabase.class, "Deal_9.db")
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


