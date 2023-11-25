package com.example.mapdemo.Service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetDataAsync extends AsyncTaskLoader<String> {
    private String linkAPI = "";
    public GetDataAsync(@NonNull Context context) {
        super(context);
    }
    public void setLinkAPI(String linkAPI) {
        this.linkAPI = linkAPI;
    }

    @Nullable
    @Override
    public String loadInBackground() {
        if( linkAPI.trim().equals("")) return null;


        Request request = new Request.Builder().url(linkAPI)
                .get()
                .build();

        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }
}
