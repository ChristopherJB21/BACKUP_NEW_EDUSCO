package com.debritto.edusco.Guru;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.MainActivity;
import com.debritto.edusco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginGuruActivity extends AppCompatActivity {

    private EditText eT_NIG, eT_Pass;

    private String nig, pass;
    private String JSON_STRING;
    private String NIG, Nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guru);

        eT_NIG = (EditText) findViewById(R.id.eT_nig);
        eT_Pass = (EditText) findViewById(R.id.eT_pass);

        try {
            // Perintah untuk mendapatkan data dari SharedPreference
            SharedPreferences sharedPreferences = getSharedPreferences("Cari_nig", Context.MODE_PRIVATE);
            String getnig = sharedPreferences.getString("NIG", "");
            nig = AES_Enkripsi.decrypt(getnig, "carinig");
        } catch (Exception e) {
            e.printStackTrace();
        }

        eT_NIG.setText(nig);
        eT_Pass.setText("");
        eT_Pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    // Metode berjalan ketika back ditekan
    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginGuruActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Metode berjalan ketika button Login di klik
    public void Login(View view) {
        // Perintah untuk mendapatkan data dari Edit Text
        String getNIG = eT_NIG.getText().toString();
        String getPass = eT_Pass.getText().toString();

        byte[] bytes_nig = getNIG.getBytes();
        byte[] bytes_pass = getPass.getBytes();

        nig = Base64.encodeToString(bytes_nig, Base64.DEFAULT);
        pass = Base64.encodeToString(bytes_pass, Base64.DEFAULT);

        getJSONLogin();
    }

    // Metode berjalan ketika button Cari di klik
    public void Cari(View view) {
        // Perintah menuju CariNIGActivity
        Intent i = new Intent(LoginGuruActivity.this, CariNIGActivity.class);
        startActivity(i);
        finish();
    }

    // Metode untuk menuju url login_guru.php
    public String sendGetRequestLogin() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/login_guru.php?nig=" + nig + "&password=" + pass);
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

    // Metode untuk mendapatkan data dari server
    private void getJSONLogin() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginGuruActivity.this, "Proses Masuk", "Tunggu Sebentar", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showLogin();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestLogin();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola data dari server
    private void showLogin() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject jo = result.getJSONObject(0);

            String nig = jo.getString("TklH");
            String nama_guru = jo.getString("bmFtYV9ndXJ1");

            NIG = AES_Enkripsi.decrypt(nig, "logingurunig");
            Nama = AES_Enkripsi.decrypt(nama_guru, "logingurunama");

            if (nig.isEmpty() || nama_guru.isEmpty()) {
                // Perintah ini terjadi ketika NIG atau password salah
                Toast.makeText(this, "NIG atau Password salah", Toast.LENGTH_SHORT).show();
                eT_Pass.setText(null);
            } else {
                // Perintah ini terjadi ketika NIG dan Password benar
                try {
                    String nig_encrypt = AES_Enkripsi.encrypt(NIG, "profilegurunig");
                    String nama_encrypt = AES_Enkripsi.encrypt(Nama, "profilegurunama");

                    // Perintah untuk menyimpan data dengan Shared Preference
                    SharedPreferences sharedPreferences = getSharedPreferences("profile_guru", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("NIG", nig_encrypt);
                    editor.putString("Nama", nama_encrypt);
                    editor.apply();


                    String nig_encrypt1 = AES_Enkripsi.encrypt(NIG, "carinig");
                    SharedPreferences sharedPreferences1 = getSharedPreferences("Cari_nig", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("NIG", nig_encrypt1);
                    editor1.apply();

                    SharedPreferences sharedPreferences2 = getSharedPreferences("login_status", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putInt("Login", 2);
                    editor2.apply();

                    // Perintah menuju ke MainGuruActivity
                    Intent i = new Intent(LoginGuruActivity.this, MainGuruActivity.class);
                    startActivity(i);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
