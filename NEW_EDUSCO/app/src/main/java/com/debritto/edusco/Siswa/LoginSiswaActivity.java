package com.debritto.edusco.Siswa;

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

public class LoginSiswaActivity extends AppCompatActivity {

    private EditText eT_NIS, eT_Pass;

    private String nis, pass;
    private String JSON_STRING;
    private String NIS, Nama, Kelas, NoPresensi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_siswa);

        eT_NIS = (EditText) findViewById(R.id.eT_nis);
        eT_Pass = (EditText) findViewById(R.id.eT_pass);

        try {
            // Metode untuk mengambil NIS dari Shared Preference (Jika ada)
            SharedPreferences sharedPreferences = getSharedPreferences("Cari_nis", Context.MODE_PRIVATE);
            String getnis = sharedPreferences.getString("NIS", "");
            nis = AES_Enkripsi.decrypt(getnis, "carinis");

            // Metode untuk memasukkan NIS ke Edit Text
            eT_NIS.setText(nis);
            eT_Pass.setText("");
            eT_Pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metode berjalan ketika back ditekan
    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginSiswaActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Metode berjalan ketika button Login di klik
    public void Login(View view) {
        // Perintah untuk mengambil data dari Edit Text
        String getNIS = eT_NIS.getText().toString();
        String getPass = eT_Pass.getText().toString();

        byte[] bytes_nis = getNIS.getBytes();
        byte[] bytes_pass = getPass.getBytes();

        nis = Base64.encodeToString(bytes_nis, Base64.DEFAULT);
        pass = Base64.encodeToString(bytes_pass, Base64.DEFAULT);

        getJSONLogin();
    }

    // Metode berjalan ketika button Cari di klik
    public void Cari(View view) {
        // Perintah untuk menuju CariNISActivity
        Intent i = new Intent(LoginSiswaActivity.this, CariNISActivity.class);
        startActivity(i);
        finish();
    }

    // Metode untuk menuju url login_siswa.php
    public String sendGetRequestLogin() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/login_siswa.php?nis=" + nis + "&password=" + pass);
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
                loading = ProgressDialog.show(LoginSiswaActivity.this, "Proses Masuk", "Tunggu Sebentar", false, false);
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

            String nis = jo.getString("TklT");
            String nama = jo.getString("bmFtYV9zaXN3YQ==");
            String kelas = jo.getString("a2VsYXM=");
            String noPresensi = jo.getString("bm9fcHJlc2Vuc2k=");

            NIS = AES_Enkripsi.decrypt(nis, "loginsiswanis");
            Nama = AES_Enkripsi.decrypt(nama, "loginsiswanama");
            Kelas = AES_Enkripsi.decrypt(kelas, "loginsiswakelas");
            NoPresensi = AES_Enkripsi.decrypt(noPresensi, "loginpresensi");

            if (nis.isEmpty() || nama.isEmpty() || kelas.isEmpty() || noPresensi.isEmpty()) {
                // Perintah ini terjadi ketika NIS atau Password salah
                Toast.makeText(this, "NIS atau Password salah", Toast.LENGTH_SHORT).show();
                eT_Pass.setText(null);
            } else {
                // Perintah ini terjadi ketika NIS dan Password benar
                try {
                    String nis_encrypt = AES_Enkripsi.encrypt(NIS, "profilesiswanis");
                    String nama_encrypt = AES_Enkripsi.encrypt(Nama, "profilesiswanama");
                    String kelas_encrypt = AES_Enkripsi.encrypt(Kelas, "profilesiswakelas");
                    String nopresensi_encrypt = AES_Enkripsi.encrypt(NoPresensi, "profilesiswanomer");

                    // Perintah untuk menyimpan data dengan Shared Preference
                    SharedPreferences sharedPreferences = getSharedPreferences("profile_siswa", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("NIS", nis_encrypt);
                    editor.putString("Nama", nama_encrypt);
                    editor.putString("Kelas", kelas_encrypt);
                    editor.putString("NoPresensi", nopresensi_encrypt);
                    editor.apply();

                    String nis_encrypt1 = AES_Enkripsi.encrypt(NIS, "carinis");
                    SharedPreferences sharedPreferences1 = getSharedPreferences("Cari_nis", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                    editor1.putString("NIS", nis_encrypt1);
                    editor1.apply();

                    SharedPreferences sharedPreferences2 = getSharedPreferences("login_status", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putInt("Login", 1);
                    editor2.apply();

                    // Perintah untuk menuju MainSiswaActivity
                    Intent i = new Intent(LoginSiswaActivity.this, MainSiswaActivity.class);
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
