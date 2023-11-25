package com.example.mapdemo.Utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.example.mapdemo.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }


    public void startLoading(){
            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.loadingDialogStyle);
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null, false);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.primaryLightColor), PorterDuff.Mode.SRC_IN);
            builder.setView(view);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
    }


    public void stopLoading() {
        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

}
