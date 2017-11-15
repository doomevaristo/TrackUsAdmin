package com.marcosevaristo.trackusadmin;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class App extends Application {
    private static Context context;
    private static RequestQueue mRequestQueue;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static RequestQueue getReqQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public static <T> void addToReqQueue(Request<T> req) {
        getReqQueue().add(req);
    }

    public static void toast(int stringID, String... params) {
        Toast.makeText(context, context.getString(stringID, params), Toast.LENGTH_SHORT).show();
    }
}
