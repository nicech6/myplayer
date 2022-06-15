package com.training.myplayer;

import android.app.Application;
import android.content.Context;

import java.io.IOException;

public class MyApp extends Application {
    static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        this.context = base;
        MyServerSocket.INSTANCE.launch();
        UdpClient.INSTANCE.start();
    }
}
