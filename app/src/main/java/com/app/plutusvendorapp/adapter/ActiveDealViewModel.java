package com.app.plutusvendorapp.adapter;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.bean.serverdeal.DealActive;
import com.app.plutusvendorapp.database.ActiveDealDatabase;
import com.app.plutusvendorapp.database.DaoActiveDeal;
import com.app.plutusvendorapp.database.DaoDeal;
import com.app.plutusvendorapp.database.DealDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActiveDealViewModel extends AndroidViewModel {

    private DaoActiveDeal daoActiveDeal;
    private ExecutorService executorService;

    public ActiveDealViewModel(@NonNull Application application) {
        super(application);
        daoActiveDeal = ActiveDealDatabase.getInstance(application).daoActiveDeal();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<DealActive>> getAllPosts() {
        return daoActiveDeal.findAll();
    }

    public void savePost(final DealActive post) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoActiveDeal.save(post);
            }
        });
    }

    public void deletePost(final DealActive post) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoActiveDeal.delete(post);
            }
        });
    }
}

