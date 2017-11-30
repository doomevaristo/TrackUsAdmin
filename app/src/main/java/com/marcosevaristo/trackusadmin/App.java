package com.marcosevaristo.trackusadmin;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.marcosevaristo.trackusadmin.activities.ICrud;

public class App extends Application {
    private static Context context;
    private static RequestQueue mRequestQueue;
    private static ProgressDialog progressDialog;

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

    public static void showLoadingDialog(Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(App.getAppContext().getString(R.string.carregando));
        progressDialog.show();
    }

    public static void hideLoadingDialog() {
        if(progressDialog != null) {
            progressDialog.hide();
        }
    }

    public static void askDeleteConfirmation(final ICrud crud) {

    }
}
