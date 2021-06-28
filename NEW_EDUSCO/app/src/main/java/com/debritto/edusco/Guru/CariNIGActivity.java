package com.debritto.edusco.Guru;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class CariNIGActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String nama;
    private String JSON_STRING;
    private String NIG, Nama;

    private EditText eT_nama;
    private ListView lV_guru;
    private SearchView sV_nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cari_nig);

        sV_nama = (SearchView) findViewById(R.id.search_nig);

        lV_guru = (ListView) findViewById(R.id.lV_guru);
        lV_guru.setOnItemClickListener(this);


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

                getJSONGuru();

                return false;
            }
        });
    }

    // Metode berjalan ketika button click di klik
    @Override
    public void onBackPressed() {
        Intent i = new Intent(CariNIGActivity.this, LoginGuruActivity.class);
        startActivity(i);
        finish();
    }

    // Metode berjalan ketika salah satu item di klik
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            HashMap<String, String> map = (HashMap) parent.getItemAtPosition(position);
            String nig = map.get("NIG");
            String nig_encrypt = AES_Enkripsi.encrypt(nig, "carinig");

            // Metode untuk menyimpan NIG dengan shared preference
            SharedPreferences sharedPreferences = getSharedPreferences("Cari_nig", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("NIG", nig_encrypt);
            editor.apply();

            // Metode untuk menuju ke LoginGuruActivity
            Intent i = new Intent(CariNIGActivity.this, LoginGuruActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metode untuk menuju ke url cari_nig.php
    public String sendGetRequestGuru() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/cari_nig.php?nama=" + nama);
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
    private void getJSONGuru() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showGuru();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestGuru();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola data dari server
    private void showGuru() {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                NIG = jo.getString("TklH");
                Nama = jo.getString("bmFtYV9ndXJ1");

                String decode_NIG = AES_Enkripsi.decrypt(NIG, "daftargurunig");
                String decode_nama = AES_Enkripsi.decrypt(Nama, "daftargurunama");

                HashMap<String, String> guru = new HashMap<>();
                guru.put("NIG", decode_NIG);
                guru.put("Nama", decode_nama);
                list.add(guru);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Metode untuk memasukkan data ke ListView
        ListAdapter adapter = new SimpleAdapter(
                CariNIGActivity.this, list, R.layout.list_cari,
                new String[]{"NIG", "Nama"},
                new int[]{R.id.tV_id, R.id.tV_nama});

        lV_guru.setAdapter(adapter);

    }
}
