package com.app.plutusvendorapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.bean.serverdeal.DealActive;

import java.util.List;

@Dao
public interface DaoActiveDeal {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<DealActive> posts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(DealActive post);

    @Update
    void update(DealActive post);

    @Delete
    void delete(DealActive post);

    @Query("SELECT * FROM DealActive")
    LiveData<List<DealActive>> findAll();

    @Query("DELETE  FROM DealActive")
    void deleteAll();
}
