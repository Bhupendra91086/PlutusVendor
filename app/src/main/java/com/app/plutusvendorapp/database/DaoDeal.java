package com.app.plutusvendorapp.database;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.app.plutusvendorapp.bean.serverdeal.Deal;

import java.util.List;

@Dao
public interface DaoDeal {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Deal> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(Deal post);

    @Update
    void update(Deal post);

    @Delete
    void delete(Deal post);

    @Query("SELECT * FROM Deal")
    LiveData<List<Deal>> findAll();

    @Query("DELETE  FROM Deal")
    void deleteAll();

}
