package com.app.plutusvendorapp.database;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.app.plutusvendorapp.bean.item.Item;


@Database(entities = {Item.class} , version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class ItemDatabase extends RoomDatabase {

    public abstract ItemDaoAccess itemDaoAccess();
}


