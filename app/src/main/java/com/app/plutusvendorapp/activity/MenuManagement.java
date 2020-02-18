package com.app.plutusvendorapp.activity;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.adapter.ItemListAdapter;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.communicator.ItemCommunicator;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MenuManagement extends AppCompatActivity {

    private ItemRepository itemRepository;
    private RecyclerView itemRecyclerView;
    private ItemListAdapter itemListAdapter;
    private List<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_management);
        System.out.println("In on Create method... ");
        itemRepository = new ItemRepository(this.getApplicationContext());
        itemRecyclerView = (RecyclerView)findViewById(R.id.itemRecyclerView);
        itemListAdapter = new ItemListAdapter(MenuManagement.this);
        itemList = new ArrayList<>();

        itemListAdapter.setItems(itemList );
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemRecyclerView.setAdapter(itemListAdapter);
        itemRepository.getAllItem().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                try {
                    System.out.println("Ras >>>>>>> items.size() >>>>> " + items.size());
                    itemList.removeAll(itemList);

                } catch (Exception e) {
                    System.out.println(">>>>>>>>>>>>>  "+e.getMessage());
                }
                for (Item item : items) {

                    itemList.add(item);
                    System.out.println("Ras >>>>>>>>>>>> " + item.getId());
                }
                itemListAdapter.setItems(itemList );
                itemRecyclerView.setHasFixedSize(true);
                itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                itemRecyclerView.setAdapter(itemListAdapter);
            }
        });
/*

        itemListAdapter.setItems(itemList );
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemRecyclerView.setAdapter(itemListAdapter);
*/


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItem(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemRepository.getAllItem().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                try {
                    System.out.println("Ras >>>>>>> items.size() >>>>> " + items.size());
                    itemList.removeAll(itemList);

                } catch (Exception e) {
                    System.out.println(">>>>>>>>>>>>>  "+e.getMessage());
                }
                for (Item item : items) {

                    itemList.add(item);
                    System.out.println("Ras >>>>>>>>>>>> " + item.getId());
                }
                itemListAdapter.setItems(itemList );
                itemRecyclerView.setHasFixedSize(true);
                itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                itemRecyclerView.setAdapter(itemListAdapter);
            }
        });
    }

    private void loadItemFromServer(final List<Item> items) {
        try {
            itemList.removeAll(itemList);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (Item item : items) {

            itemList.add(item);
            System.out.println("Ras >>>>>>>>>>>> " + item.getId());
        }
        itemListAdapter.setItems(itemList );
        itemRecyclerView.setHasFixedSize(true);
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        itemRecyclerView.setAdapter(itemListAdapter);

    }


    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("In Resume method... ");


    }

    private void updateItem(boolean b) {

        Intent intent = new Intent(MenuManagement.this,AddItem.class);
        intent.putExtra("update",b);
        startActivity(intent);
        MenuManagement.this.finish();

    }



}
