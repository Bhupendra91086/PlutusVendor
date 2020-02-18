package com.app.plutusvendorapp.util;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.app.plutusvendorapp.activity.HomeActivity;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HandleAppCrash implements Thread.UncaughtExceptionHandler {
    private final Context myContext;
    Class<?> intentClass;

    public HandleAppCrash(Context context, Class<?> intentClass) {
        this.myContext = context;
        this.intentClass = intentClass;
    }

    public static void deploy(Context context, Class<?> intentClass) {
        Thread.setDefaultUncaughtExceptionHandler(new HandleAppCrash(context, intentClass));
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);

        // Intent intent = new Intent(this.myContext, this.intentClass);
        //  intent.putExtra("stackTrace", stackTrace.toString());
        Intent intent = new Intent(myContext, HomeActivity.class);
        myContext.startActivity(intent);
        this.myContext.startActivity(intent);
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}