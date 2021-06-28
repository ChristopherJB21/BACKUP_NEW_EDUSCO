package com.debritto.edusco.Siswa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class CariNISActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String nama;
    private String JSON_STRING;
    private String NIS, Nama;

    private SearchView sV_nama;
    private ListView lV_siswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_nis);

        sV_nama = (SearchView) findViewById(R.id.search_nis);

        lV_siswa = (ListView) findViewById(R.id.lV_siswa);
        lV_siswa.setOnItemClickListener(this);

        sV_nama.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                nama = sV_nama.getQuery().toString();
                byte[] bytes_nama = nama.getBytes();
                nama = Base64.encodeToString(bytes_nama, Base64.DEFAULT);

                getJSONSiswa();

                return false;
            }
    });
    }

    // Metode berjalan ketika button click di klik
    @Override
    public void onBackPressed() {
        Intent i = new Intent(CariNISActivity.this, LoginSiswaActivity.class);
        startActivity(i);
        finish();
    }

    // Metode berjalan ketika salah satu item di klik
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
            String nis = map.get("NIS");
            String nis_encrypt = AES_Enkripsi.encrypt(nis, "carinis");

            // Metode untuk menyimpan NIS dengan Shared Preference
            SharedPreferences sharedPreferences = getSharedPreferences("Cari_nis", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("NIS", nis_encrypt);
            editor.apply();

            // Metode untuk menuju ke LoginSiswaActivity
            Intent i = new Intent(CariNISActivity.this, LoginSiswaActivity.class);
            startActivity(i);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metode untuk menuju ke url cari_nis.php
    public String sendGetRequestSiswa() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/cari_nis.php?nama=" + nama);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s + "\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    // Metode untuk mengambil data dari server
    private void getJSONSiswa() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showSiswa();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestSiswa();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola data dari server
    private void showSiswa() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                NIS = jo.getString("TklT");
                Nama = jo.getString("TmFtYV9zaXN3YQ==");

                String nis_decode = AES_Enkripsi.decrypt(NIS, "daftarsiswanis");
                String nama_decode = AES_Enkripsi.decrypt(Nama, "daftarsiswanama");

                HashMap<String, String> siswa = new HashMap<>();
                siswa.put("NIS", nis_decode);
                siswa.put("Nama", nama_decode);
                list.add(siswa);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Perintah untuk memasukkan data ke ListView
        ListAdapter adapter = new SimpleAdapter(
                CariNISActivity.this, list, R.layout.list_cari,
                new String[]{"NIS", "Nama"},
                new int[]{R.id.tV_id, R.id.tV_nama});

        lV_siswa.setAdapter(adapter);

    }
}
