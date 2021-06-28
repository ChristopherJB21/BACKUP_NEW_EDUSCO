package com.debritto.edusco.Guru;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.debritto.edusco.Guru.Fragment.BarGuruFragment;
import com.debritto.edusco.Guru.Fragment.ChangeGuruFragment;
import com.debritto.edusco.Guru.Fragment.HomeGuruFragment;
import com.debritto.edusco.Guru.Fragment.InputGuruFragment;
import com.debritto.edusco.Guru.Fragment.ProfileGuruFragment;
import com.debritto.edusco.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainGuruActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guru);

        // Metode untuk set default Fragment
        loadFragment(new HomeGuruFragment());
        // memanggil BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_main);
        // Memberi listerner kepada BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.menu_input:
                // Metode menuju Fragment Input Nilai
                fragment = new InputGuruFragment();
                break;
            case R.id.menu_change:
                // Metode menuju Fragment Change Nilai
                fragment = new ChangeGuruFragment();
                break;
            case R.id.menu_bar:
                // Metode menuju Fragment Bar
                fragment = new BarGuruFragment();
                break;
            case R.id.menu_home:
                // Metode menuju Fragment Home
                fragment = new HomeGuruFragment();
                break;
            case R.id.menu_profile:
                // Metode menuju Fragment Profile
                fragment = new ProfileGuruFragment();
                break;
        }
        return loadFragment(fragment);
    }

    // Metode untuk load fragment yang sesuai
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
