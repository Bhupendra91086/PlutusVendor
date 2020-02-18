package com.app.plutusvendorapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.app.plutusvendorapp.bean.item.Item;

import java.util.List;

@Dao
public interface ItemDaoAccess {

    @Insert
    Long insertTask(Item item);


    @Update
    void updateTask(Item item);


    @Delete
    void deleteTask(Item item);


    @Query("SELECT * FROM Item  ORDER BY id asc")
    LiveData<List<Item>> fetchAllItem();


    @Query("SELECT * FROM Item WHERE id =:taskId")
    LiveData<Item> getTask(int taskId);

    @Query("SELECT * FROM Item WHERE id =:taskId")
    LiveData<Item> getTask(String taskId);

    @Query("SELECT * FROM Item WHERE id =:taskId")
    Item  getTaskOnly(String taskId);


}