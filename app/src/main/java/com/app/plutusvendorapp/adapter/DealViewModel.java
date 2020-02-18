package com.app.plutusvendorapp.adapter;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.database.DaoDeal;
import com.app.plutusvendorapp.database.DealDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DealViewModel extends AndroidViewModel {

    private DaoDeal daoDeal;
    private ExecutorService executorService;

    public DealViewModel(@NonNull Application application) {
        super(application);
        daoDeal = DealDatabase.getInstance(application).daoDeal();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Deal>> getAllPosts() {
        return daoDeal.findAll();
    }

    public void savePost(final Deal post) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoDeal.save(post);
            }
        });
    }

    public void deletePost(final Deal post) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                daoDeal.delete(post);
            }
        });
    }
}

