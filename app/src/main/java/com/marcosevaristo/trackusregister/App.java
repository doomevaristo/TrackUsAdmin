package com.marcosevaristo.trackusregister;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.marcosevaristo.trackusregister.database.SQLiteHelper;
import com.marcosevaristo.trackusregister.model.Municipio;

public class App extends Application {
    private static Context context;
    private static SQLiteHelper sqLiteHelper;
    private static Municipio municipio;
    private static RequestQueue mRequestQueue;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        sqLiteHelper = SQLiteHelper.getInstance(context);
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static SQLiteHelper getSqLiteHelper() {
        return sqLiteHelper;
    }

    public static Municipio getMunicipio() {
        return municipio;
    }
    public static void setMunicipio(Municipio municipio) {
        App.municipio = municipio;
    }

    public static RequestQueue getReqQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public static <T> void addToReqQueue(Request<T> req, String tag) {
        getReqQueue().add(req);
    }

    public static <T> void addToReqQueue(Request<T> req) {
        getReqQueue().add(req);
    }

    public static void cancelPendingReq(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
