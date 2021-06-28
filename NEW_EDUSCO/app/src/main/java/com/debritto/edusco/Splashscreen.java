package com.debritto.edusco;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.debritto.edusco.Guru.MainGuruActivity;
import com.debritto.edusco.Ortu.MainOrtuActivity;
import com.debritto.edusco.Siswa.MainSiswaActivity;

public class Splashscreen extends AppCompatActivity {

    private int loginStatus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        SharedPreferences sharedPreferences = getSharedPreferences("login_status", Context.MODE_PRIVATE);
        loginStatus = sharedPreferences.getInt("Login", 0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginStatus == 0) {
                    Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                } else if (loginStatus == 1) {
                    Intent intent = new Intent(Splashscreen.this, MainSiswaActivity.class);
                    startActivity(intent);
                } else if (loginStatus == 2) {
                    Intent intent = new Intent(Splashscreen.this, MainGuruActivity.class);
                    startActivity(intent);
                } else if (loginStatus == 3) {
                    Intent intent = new Intent(Splashscreen.this, MainOrtuActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        }, 2000);
    }
}
