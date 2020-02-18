package com.app.plutusvendorapp.communicator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.plutusvendorapp.activity.HomeActivity;
import com.app.plutusvendorapp.activity.SplashScreen;
import com.app.plutusvendorapp.bean.item.Item;
import com.app.plutusvendorapp.bean.serverdeal.Deal;
import com.app.plutusvendorapp.util.ExceptionHandler;
import com.app.plutusvendorapp.util.HandleAppCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;

public class MyApplication extends android.app.Application {

    private static final String MY_PREFS_NAME = "GIMME_Customer_Pref";
    private static MyApplication mInstance;
    private boolean isProfileRegistred;
    public static SharedPreferences preferences;
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences prefs = null;
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    public static Item tempItem = new Item();
    public static String shortingOrder = "";

    public static Deal deal;
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
            //  editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            //  prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        }
        return mInstance;
    }



    @Override
    public void onCreate() {
        super.onCreate();



      // enableLogging();

        mInstance = new MyApplication();
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        // Required initialization logic here!
        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        preferences = getSharedPreferences(getPackageName() + "_preferences", MODE_PRIVATE);
        mInstance = this;






     /*   final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(false)

        // Enables Crashlytics debugger
        .build();Fabric.with(fabric);*/


    }

    void enableLogging()
    {
        try {
            Thread
                    .setDefaultUncaughtExceptionHandler(
                            new Thread.UncaughtExceptionHandler() {

                                @Override
                                public void uncaughtException(Thread thread, Throwable e) {
                                    Log.e("TAG",
                                            "Uncaught Exception thread: "+thread.getName()+""+e.getStackTrace()
                                                    +"\n"+e.getMessage() );
                                    e.printStackTrace();

                                    handleUncaughtException (thread, e);
                                }
                            });
        } catch (SecurityException e) {
            Log.e("TAG",
                    "Could not set the Default Uncaught Exception Handler:"
                            +e.getStackTrace());
        }
    }

    void handleUncaughtException (Thread thread, Throwable e2){

        // e.printStackTrace();

        try {

            File[] all = getApplicationContext().getExternalFilesDirs("external");
            File folder = new File(all[0]+"/gimme");
            if(!folder.exists())
            folder.mkdirs();

            //Save the path as a string value
            String extStorageDirectory = folder.getAbsolutePath();

            //Create New file and name it Image2.PNG
            File file = new File(extStorageDirectory, "log_"+new Date() +".txt");
            file.createNewFile();
            Log.e("Path of file ", file.getAbsolutePath());
            Log.e("TAG", "uncaughtException:" + e2.getStackTrace().toString());

            /*OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().
                    openFileOutput("log.txt", Context.MODE_APPEND));*/
           // FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(file);
            e2.printStackTrace(pw);
            pw.close();
           // fileOutputStream.write(e2.getStackTrace().toString().getBytes());
           // fileOutputStream.flush();
           // fileOutputStream.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


        int Pid = android.os.Process.myPid();
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.app.plutusvendorapp");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
        android.os.Process.killProcess(Pid);

    }




    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

  /*  public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }*/

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }



}
