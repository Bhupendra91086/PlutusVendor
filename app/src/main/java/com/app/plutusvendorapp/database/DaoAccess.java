package com.app.plutusvendorapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.app.plutusvendorapp.bean.Deal;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    Long insertTask(Deal deal);


    /**
     * Avaiable deals
     *
     * @return
     */
    @Query("SELECT * FROM Deal WHERE STATUS ='A' ORDER BY id desc")
    LiveData<List<Deal>> fetchAllTasks();

    /* Discarded Deal List*/
    @Query("SELECT * FROM Deal WHERE STATUS = 'D' ORDER BY id desc")
    LiveData<List<Deal>> fetchAllDeleteTasks();

    /* Purchased deal List */
    @Query("SELECT * FROM Deal WHERE STATUS = 'P' ORDER BY id desc")
    LiveData<List<Deal>> fetchAllPurchaseTasks();

    /* Purchased deal List */
    @Query("SELECT * FROM Deal WHERE STATUS = 'S' ORDER BY id desc")
    LiveData<List<Deal>> fetchAllSelectedTasks();

    @Query("SELECT * FROM Deal WHERE id =:taskId")
    LiveData<Deal> getTask(int taskId);


    @Update
    void updateTask(Deal note);


    @Delete
    void deleteTask(Deal note);
}