package com.app.plutusvendorapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.app.plutusvendorapp.bean.item.Item;

import java.util.List;

public class ItemRepository {

    private String DB_NAME = "db_item_3";

    private ItemDatabase itemDatabase;

    public ItemRepository(Context context) {
        itemDatabase = Room.databaseBuilder(context, ItemDatabase.class, DB_NAME).build();
    }

    public void insertTask(String title,
                           String description) {

        insertTask(title, description, false, null);
    }

    public void insertTask(String title,
                           String description,
                           boolean encrypt,
                           String password) {


        //   insertTask(note);
    }

    public void insertTask(final Item item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    itemDatabase.itemDaoAccess().insertTask(item);
                } catch (Exception e) {

                }
                return null;
            }
        }.execute();
    }

    public void updateTask(final Item item) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                itemDatabase.itemDaoAccess().updateTask(item);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<Item> task = getTask(id);
        if (task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    itemDatabase.itemDaoAccess().deleteTask(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final Item item) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                itemDatabase.itemDaoAccess().deleteTask(item);
                return null;
            }
        }.execute();
    }

    public LiveData<Item> getTask(int id) {
        return itemDatabase.itemDaoAccess().getTask(id);
    }

    public LiveData<Item> getTask(String id) {
        return itemDatabase.itemDaoAccess().getTask(id);
    }

    public Item getTaskOnly(String id) {
        return itemDatabase.itemDaoAccess().getTaskOnly(id);
    }

/*
    public LiveData<List<Item>> getTasks() {
        return itemDatabase.itemDaoAccess().fetchAllTasks();
    }*/


    public LiveData<List<Item>> getAllItem() {
        return itemDatabase.itemDaoAccess().fetchAllItem();
    }


}