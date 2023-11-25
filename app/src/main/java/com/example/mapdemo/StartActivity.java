package com.example.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mapdemo.Shipper.DirectionActivity;
import com.example.mapdemo.Shipper.MapViewActivity;
import com.example.mapdemo.Shipper.RecivedOrder;
import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity {

    MaterialButton btnLoginUser, btnLoginShipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLoginShipper = findViewById(R.id.btn_login_shipper);
        btnLoginUser = findViewById(R.id.btn_login_user);

        btnLoginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
            }
        });

        btnLoginShipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecivedOrder.class);

                startActivity(intent);
            }
        });
    }
}