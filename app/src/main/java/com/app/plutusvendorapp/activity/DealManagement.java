package com.app.plutusvendorapp.activity;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.adapter.DealAdapter;
import com.app.plutusvendorapp.adapter.DealViewModel;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.communicator.MyApplication;
import com.app.plutusvendorapp.database.ItemRepository;

import java.util.List;

public class DealManagement extends AppCompatActivity implements   DealAdapter.OnDeleteButtonClickListener,
        DealAdapter.OnListItemClickListener {
    private ItemRepository itemRepository;
    private DealAdapter dealAdapter;
    private DealViewModel dealViewModel;
    private List<Deal> dealList;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_management);
        itemRepository = new ItemRepository(this.getApplicationContext());
        init();
        activity = this;


                getLifecycle().addObserver(new MainActivityObserver());


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DealManagement.this, CreateDeal.class);
                startActivity(intent);

            }
        });
    }

    private void init() {

        dealAdapter = new DealAdapter(this, this,this,itemRepository, getLifecycle());

        dealViewModel = ViewModelProviders.of(this).get(DealViewModel.class);
        dealViewModel.getAllPosts().observe(this, new Observer<List<Deal>>() {
            @Override
            public void onChanged(@Nullable List<Deal> deals) {
                dealList = deals;
                dealAdapter.setData(deals);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.itemRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dealAdapter);

    }

    @Override
    public void onDeleteButtonClicked(Deal post) {

    }


    @Override
    public void onItemClick(int position) {
        System.out.println("position >>>>>>>>> "+position);
        if(dealList.size()>0) {
            Intent intent = new Intent(DealManagement.this, DealLandingInfo.class);
            MyApplication.deal = dealList.get(position);
            startActivity(intent);
        }

    }
}
