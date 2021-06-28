package com.debritto.edusco.Siswa.Fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.Photo.RoundedTransformation;
import com.debritto.edusco.R;
import com.debritto.edusco.Siswa.RVA.RVA_ViewNilai;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeSiswaFragment extends Fragment {

    private TextView tV_NIS, tV_Nama, tV_Date;
    private ImageButton btn_before, btn_next;
    private ImageView iV_foto, iV_zerodata;

    private ArrayList<String> v_ket1, v_foto_guru, v_ket2, v_nilai;

    private String NIS, Tanggal;
    private String JSON_STRING;
    private String foto;

    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_siswa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tV_NIS = (TextView) view.findViewById(R.id.tV_nis);
        tV_Nama = (TextView) view.findViewById(R.id.tV_nama);
        tV_Date = (TextView) view.findViewById(R.id.tV_date);

        btn_before = (ImageButton) view.findViewById(R.id.btn_before);
        btn_next = (ImageButton) view.findViewById(R.id.btn_next);

        iV_foto = (ImageView) view.findViewById(R.id.foto);
        iV_zerodata = (ImageView) view.findViewById(R.id.img_zerodata);

        v_ket1 = new ArrayList<String>();
        v_foto_guru = new ArrayList<String>();
        v_ket2 = new ArrayList<String>();
        v_nilai = new ArrayList<String>();

        rvView = (RecyclerView) view.findViewById(R.id.rv_main);
        rvView.setHasFixedSize(true);

        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login_status", Context.MODE_PRIVATE);
            int loginStatus = sharedPreferences.getInt("Login", 0);

            if (loginStatus == 1) {
                // Perintah untuk mengambil data dari Shared Preference
                SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("profile_siswa", Context.MODE_PRIVATE);
                String getNIS = sharedPreferences1.getString("NIS", "");
                String getNama = sharedPreferences1.getString("Nama", "");
                String Kelas = sharedPreferences1.getString("Kelas", "");
                String NoPresensi = sharedPreferences1.getString("NoPresensi", "");

                NIS = AES_Enkripsi.decrypt(getNIS, "profilesiswanis");
                String nama_decrypt = AES_Enkripsi.decrypt(getNama, "profilesiswanama");
                String kelas_decrypt = AES_Enkripsi.decrypt(Kelas, "profilesiswakelas");
                String nopresensi_decrypt = AES_Enkripsi.decrypt(NoPresensi, "profilesiswanomer");

                // Perintah untuk memasukkan data ke TextView
                tV_NIS.setText(NIS + " - " + kelas_decrypt + " - " + nopresensi_decrypt);
                tV_Nama.setText(nama_decrypt);
            } else if (loginStatus == 3) {
                // Perintah untuk mengambil data dari Shared Preference
                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("profile_ortu", Context.MODE_PRIVATE);
                String getNIS = sharedPreferences2.getString("NIS", "");
                String getNama = sharedPreferences2.getString("Nama", "");
                String Kelas = sharedPreferences2.getString("Kelas", "");
                String NoPresensi = sharedPreferences2.getString("NoPresensi", "");

                NIS = AES_Enkripsi.decrypt(getNIS, "profilesiswanis");
                String nama_decrypt = AES_Enkripsi.decrypt(getNama, "profilesiswanama");
                String kelas_decrypt = AES_Enkripsi.decrypt(Kelas, "profilesiswakelas");
                String nopresensi_decrypt = AES_Enkripsi.decrypt(NoPresensi, "profilesiswanomer");

                // Perintah untuk memasukkan data ke TextView
                tV_NIS.setText(NIS + " - " + kelas_decrypt + " - " + nopresensi_decrypt);
                tV_Nama.setText(nama_decrypt);
            }

            getJSONFoto();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Perintah untuk mendapatkan tanggal dan waktu hari ini
        final SimpleDateFormat sDF = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();
        final Calendar c = Calendar.getInstance();
        Tanggal = sDF.format(c.getTime());

        tV_Date.setText(Tanggal);

        tV_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                c.set(year, month, dayOfMonth);
                                Tanggal = sDF.format(c.getTime());
                                tV_Date.setText(Tanggal);

                                // Perintah agar tanggal tidak bisa melewati hari ini
                                if (Tanggal.equals(sDF.format(date))) {
                                    btn_next.setVisibility(View.INVISIBLE);
                                } else {
                                    btn_next.setVisibility(View.VISIBLE);
                                }

                                getJSONNilai();
                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMaxDate(date.getTime());

                datePickerDialog.show();
            }
        });

        // Metode ini berjalan ketika button before di klik
        btn_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk memundurkan satu hari
                c.add(Calendar.DATE, -1);
                Tanggal = sDF.format(c.getTime());

                tV_Date.setText(Tanggal);

                btn_next.setVisibility(View.VISIBLE);
                getJSONNilai();
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk memajukan satu hari
                c.add(Calendar.DATE, 1);
                Tanggal = sDF.format(c.getTime());

                tV_Date.setText(Tanggal);

                // Perintah agar tanggal tidak bisa melewati hari ini
                if (Tanggal.equals(sDF.format(date))) {
                    btn_next.setVisibility(View.INVISIBLE);
                } else {
                    btn_next.setVisibility(View.VISIBLE);
                }

                getJSONNilai();
            }
        });

        getJSONNilai();
    }

    // Metode untuk menuju ke url siswa_home.php
    public String sendGetRequestNilai() {
        StringBuilder sb = new StringBuilder();
        try {
            byte[] bytes_nis = NIS.getBytes();
            byte[] bytes_date = Tanggal.getBytes();

            // Perintah untuk encode Base64 NIS dan Date
            String sendNIS = Base64.encodeToString(bytes_nis, Base64.DEFAULT);
            String sendDate = Base64.encodeToString(bytes_date, Base64.DEFAULT);

            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/siswa_home.php?nis=" + sendNIS + "&date=" + sendDate);
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

    // Metode untuk menuju ke url siswa_foto.php
    public String sendGetRequestFoto() {
        StringBuilder sb = new StringBuilder();

        // Perintah untuk encode nis
        byte[] bytes_nis = NIS.getBytes();
        String nis = Base64.encodeToString(bytes_nis, Base64.DEFAULT);

        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/siswa_foto.php?nis=" + nis);

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
    private void getJSONNilai() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Mengambil data", "Harap tunggu", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showNilai();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestNilai();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengambil foto dari server
    public void getJSONFoto() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showFoto();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestFoto();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola data dari server
    private void showNilai() {
        JSONObject jsonObject = null;

        // Perintah untuk menghapus semua data di Array List
        v_ket1.removeAll(v_ket1);
        v_foto_guru.removeAll(v_foto_guru);
        v_ket2.removeAll(v_ket2);
        v_nilai.removeAll(v_nilai);

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String nama_guru = jo.getString("bmFtYV9ndXJ1");
                String foto_guru = jo.getString("Zm90b19ndXJ1");
                String mapel = jo.getString("bWF0YXBlbGFqYXJhbg==");
                String semester = jo.getString("c2VtZXN0ZXI=");
                String jenis_nilai = jo.getString("amVuaXNuaWxhaQ==");
                String nilaiKe = jo.getString("bmlsYWlLZQ==");
                String nilai = jo.getString("bmlsYWk=");

                String nama_guru_decode = AES_Enkripsi.decrypt(nama_guru, "siswanamaguru");
                String foto_guru_decode = AES_Enkripsi.decrypt(foto_guru, "siswafotoguru");
                String mapel_decode = AES_Enkripsi.decrypt(mapel, "siswamapel");
                String smt_decode = AES_Enkripsi.decrypt(semester, "siswasemester");
                String jenisnilai_decode = AES_Enkripsi.decrypt(jenis_nilai, "siswajenisnilai");
                String nilaike_decode = AES_Enkripsi.decrypt(nilaiKe, "siswanilaike");
                String nilai_decode = AES_Enkripsi.decrypt(nilai, "siswanilai");

                String ket_1 = mapel_decode + " - " + nama_guru_decode;
                String ket_2 = smt_decode + " - " + jenisnilai_decode + " - " + nilaike_decode;
                
                // Perintah untuk memasukkan data ke Array List
                v_ket1.add(ket_1);
                v_foto_guru.add(foto_guru_decode);
                v_ket2.add(ket_2);
                v_nilai.add(nilai_decode);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (v_ket1.isEmpty()) {
            // Perintah berjalan ketika tidak mendapatkan nilai
            iV_zerodata.setVisibility(View.VISIBLE);
        } else {
            iV_zerodata.setVisibility(View.GONE);
        }

        layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        adapter = new RVA_ViewNilai(getActivity(), v_ket1, v_foto_guru, v_ket2, v_nilai);
        rvView.setAdapter(adapter);
    }

    // Metode untuk mengelola foto
    private void showFoto() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String getfoto = jo.getString("Rm90bw==");
                foto = AES_Enkripsi.decrypt(getfoto, "fotosiswa");

                // Perintah untuk memasang foto di ImageView
                Picasso.with(getActivity())
                        .load(foto)
                        .resize(500, 500)
                        .centerCrop()
                        .transform(new RoundedTransformation(250, 0))
                        .into(iV_foto);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
