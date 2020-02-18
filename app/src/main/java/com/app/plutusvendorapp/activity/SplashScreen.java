package com.app.plutusvendorapp.activity;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.plutusvendorapp.R;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.database.ItemRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;
    private ItemRepository itemRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.appSharedPref), 0); // 0 - for private mode
        editor = pref.edit();
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        itemRepository = new ItemRepository(this.getApplicationContext());
        itemRepository.getAllItem().observe(this, new Observer<List<Item>>() {
            @Override
            public void onChanged(@Nullable List<Item> items) {
                if(items.size() > 0) {
                    for (Item item : items) {
                        itemRepository.deleteTask(item);
                    }
                }
                else
                {

                }

            }
        });
        FirebaseUser mUser = mAuth.getCurrentUser();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


                goToNextActivity();
            }
        }, 2000);
    }

    private void goToNextActivity() {

        String email = pref.getString(getString(R.string.user_email_to_reg), "");

System.out.println("Email Id "+email);

        String reguser = pref.getString(getString(R.string.reg_user), "");

        if (email.length() > 0 && reguser.length() > 0) {


                // Check permission
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);


            }
        else if(email.length() > 0 && reguser.length() == 0) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);


        }else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        this.finish();
    }

}