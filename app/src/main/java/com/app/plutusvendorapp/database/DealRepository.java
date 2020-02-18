package com.app.plutusvendorapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;


import com.app.plutusvendorapp.bean.Deal;

import java.util.List;

public class DealRepository {

    private String DB_NAME = "db_item_4";

    private DealDatabase dealDatabase;
    public DealRepository(Context context) {
        dealDatabase = Room.databaseBuilder(context, DealDatabase.class, DB_NAME).build();
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

  /*  public void insertTask(final Deal deal) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    dealDatabase.daoAccess().insertTask(deal);
                }
                catch (Exception e)
                {

                }
                return null;
            }
        }.execute();
    }

    public void updateTask(final Deal deal) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dealDatabase.daoAccess().updateTask(deal);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final int id) {
        final LiveData<Deal> task = getTask(id);
        if(task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    dealDatabase.daoAccess().deleteTask(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    public void deleteTask(final Deal deal) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                dealDatabase.daoAccess().deleteTask(deal);
                return null;
            }
        }.execute();
    }

    public LiveData<Deal> getTask(int id) {
        return dealDatabase.daoAccess().getTask(id);
    }

    public LiveData<List<Deal>> getTasks() {
        return dealDatabase.daoAccess().fetchAllTasks();
    }


    public LiveData<List<Deal>> getDeletedList() {
        return dealDatabase.daoAccess().fetchAllDeleteTasks();
    }
    public LiveData<List<Deal>> getPurchaseList() {
        return dealDatabase.daoAccess().fetchAllPurchaseTasks();
    }
    public LiveData<List<Deal>> getSelectedlist() {
        return dealDatabase.daoAccess().fetchAllSelectedTasks();
    }
*/
}