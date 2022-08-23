package com.thepixels.smartscan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class uploadClass {
    private final Activity activity;
    private AlertDialog alertDialog;
    uploadClass(Activity myActivity) {
        activity = myActivity;
    }

    @SuppressLint("InflateParams")
    void startLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.upload_loader,null));
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void stopLoading(){
        alertDialog.dismiss();
    }
}
