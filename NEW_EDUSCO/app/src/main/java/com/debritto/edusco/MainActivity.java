package com.debritto.edusco;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.debritto.edusco.Guru.LoginGuruActivity;
import com.debritto.edusco.Ortu.LoginOrtuActivity;
import com.debritto.edusco.Siswa.LoginSiswaActivity;

public class MainActivity extends AppCompatActivity {

    private Spinner sp_masuk;
    private String[] masuk = {"Siswa", "Guru", "Orangtua Siswa"};

    private String get_masuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp_masuk = (Spinner) findViewById(R.id.sp_masuk);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.spinner_item, masuk);

        sp_masuk.setAdapter(adapter);

        sp_masuk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               get_masuk = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void Masuk(View view) {
        if (get_masuk.equals("Siswa")){
            Intent i = new Intent(MainActivity.this, LoginSiswaActivity.class);
            startActivity(i);
            finish();
        } else if (get_masuk.equals("Guru")){
            Intent i = new Intent(MainActivity.this, LoginGuruActivity.class);
            startActivity(i);
            finish();
        } else if (get_masuk.equals("Orangtua Siswa")){
            Intent i = new Intent(MainActivity.this, LoginOrtuActivity.class);
            startActivity(i);
            finish();
        }
    }
}
