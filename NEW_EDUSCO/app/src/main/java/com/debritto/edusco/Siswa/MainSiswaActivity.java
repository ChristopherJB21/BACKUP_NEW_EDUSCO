package com.debritto.edusco.Siswa;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.debritto.edusco.R;
import com.debritto.edusco.Siswa.Fragment.BarSiswaFragment;
import com.debritto.edusco.Siswa.Fragment.HomeSiswaFragment;
import com.debritto.edusco.Siswa.Fragment.ProfileSiswaFragment;
import com.debritto.edusco.Siswa.Fragment.RaportSiswaFragment;
import com.debritto.edusco.Siswa.Fragment.ViewSiswaFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainSiswaActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_siswa);

        // Metode untuk set default fragment
        loadFragment(new HomeSiswaFragment());
        // Memanggil BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_main);
        // Memberi listener kepada BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.menu_home:
                // Menuju Fragment Home
                fragment = new HomeSiswaFragment();
                break;
            case R.id.menu_view:
                // Menuju Fragment View Nilai
                fragment = new ViewSiswaFragment();
                break;
            case R.id.menu_report:
                // Menuju Fragment Raport Nilai
                fragment = new RaportSiswaFragment();
                break;
            case R.id.menu_bar:
                // Menuju Fragment Grafik Nilai
                fragment = new BarSiswaFragment();
                break;
            case R.id.menu_profile:
                // Menuju Fragment Profile Siswa
                fragment = new ProfileSiswaFragment();
                break;
        }

        return loadFragment(fragment);
    }

    // metode untuk load fragment yang sesuai
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
