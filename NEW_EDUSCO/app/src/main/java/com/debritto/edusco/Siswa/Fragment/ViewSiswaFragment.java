package com.debritto.edusco.Siswa.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.R;
import com.debritto.edusco.Siswa.RVA.RVA_ViewNilai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewSiswaFragment extends Fragment {

    private String NIS, kode_mapel, semester, kode_nilai;
    private String JSON_STRING;

    private ArrayList<String> list_mapel;
    private ArrayList<String> v_ket1, v_fotoguru, v_ket2, v_nilai;
    private String[] Semester = {"Gasal", "Genap"};
    private String[] jenis_nilai = {"Pengetahuan", "Keterampilan", "Sikap", "UTS", "UAS"};

    private ImageView zerodata;
    private Spinner sp_mapel, sp_semester, sp_jenis_nilai;
    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_siswa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceSaved) {

        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login_status", Context.MODE_PRIVATE);
            int loginStatus = sharedPreferences.getInt("Login", 0);

            if (loginStatus == 1) {
                // Perintah untuk mengambil data dari Shared Preference
                SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("profile_siswa", Context.MODE_PRIVATE);
                String getNIS = sharedPreferences1.getString("NIS", "");
                String nis_decrypt = AES_Enkripsi.decrypt(getNIS, "profilesiswanis");
                final byte[] bytes_nig = nis_decrypt.getBytes();
                NIS = Base64.encodeToString(bytes_nig, Base64.DEFAULT);
            } else if (loginStatus == 3) {
                // Perintah untuk mengambil data dari Shared Preference
                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("profile_ortu", Context.MODE_PRIVATE);
                String getNIS = sharedPreferences2.getString("NIS", "");
                String nis_decrypt = AES_Enkripsi.decrypt(getNIS, "profilesiswanis");
                final byte[] bytes_nig = nis_decrypt.getBytes();
                NIS = Base64.encodeToString(bytes_nig, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        zerodata = (ImageView) view.findViewById(R.id.img_zerodata);
        sp_mapel = (Spinner) view.findViewById(R.id.sp_mapel);
        sp_semester = (Spinner) view.findViewById(R.id.sp_semester);
        sp_jenis_nilai = (Spinner) view.findViewById(R.id.sp_jenis_nilai);

        rvView = (RecyclerView) view.findViewById(R.id.rv_main);
        rvView.setHasFixedSize(true);

        list_mapel = new ArrayList<String>();

        v_ket1 = new ArrayList<String>();
        v_fotoguru = new ArrayList<String>();
        v_ket2 = new ArrayList<String>();
        v_nilai = new ArrayList<String>();

        getJSONMapel();

        // Perintah untuk memasukkan data ke Spinner
        final ArrayAdapter<String> adapterSemester = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, Semester);
        final ArrayAdapter<String> adapterJenisNilai = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, jenis_nilai);

        sp_semester.setAdapter(adapterSemester);
        sp_jenis_nilai.setAdapter(adapterJenisNilai);

        // Perintah berjalan ketika spinner semester di klik
        sp_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String smt = adapterSemester.getItem(position);

                // Perintah untuk mendapatkan data semester
                switch (smt) {
                    case "Gasal":
                        semester = "1";
                        break;
                    case "Genap":
                        semester = "2";
                        break;
                }

                byte[] bytes_smt = semester.getBytes();
                semester = Base64.encodeToString(bytes_smt, Base64.DEFAULT);

                getJSONNilai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Perintah berjalan ketika spinner jenis_nilai di klik
        sp_jenis_nilai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String jenis = adapterJenisNilai.getItem(position);

                // Perintah untuk mendapatkan data kode_nilai
                switch (jenis) {
                    case "Pengetahuan":
                        kode_nilai = "1";
                        break;
                    case "Keterampilan":
                        kode_nilai = "2";
                        break;
                    case "Sikap":
                        kode_nilai = "3";
                        break;
                    case "UTS":
                        kode_nilai = "98";
                        break;
                    case "UAS":
                        kode_nilai = "99";
                        break;
                }

                byte[] bytes_kode_nilai = kode_nilai.getBytes();
                kode_nilai = Base64.encodeToString(bytes_kode_nilai, Base64.DEFAULT);

                getJSONNilai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Metode untuk menuju ke url siswa_mapel.php
    public String sendGetRequestMapel() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/siswa_mapel.php?nis=" + NIS);
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

    // Metode untuk menuju ke url siswa_view_nilai.php
    public String sendGetRequestNilai() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/siswa_view_nilai.php?nis="
                    + NIS + "&semester=" + semester + "&kode_mapel=" + kode_mapel + "&kode_nilai=" + kode_nilai);
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

    // Metode untuk mendapatkan data mapel dari server
    private void getJSONMapel() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
           }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showMapel();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestMapel();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mendapatkan data nilai dari server
    public void getJSONNilai() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Mengambil data", "Tunggu sebentar", false, false);
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

    // Metode untuk mengelola data mapel dari server
    private void showMapel() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String mapel = jo.getString("bWF0YXBlbGFqYXJhbg==");

                String mapel_decode = AES_Enkripsi.decrypt(mapel, "siswamapel");

                list_mapel.add(mapel_decode);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayAdapter adapterMapel = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list_mapel);

        sp_mapel.setAdapter(adapterMapel);

        // Perintah ketika Spinner mapel diklik
        sp_mapel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mapel = adapterMapel.getItem(position).toString();

                // Perintah untuk mendapatkan kode_mapel
                switch (mapel) {
                    case "Pendidikan Agama dan Budi Pekerti":
                        kode_mapel = "1";
                        break;
                    case "Pendidikan Pancasila dan Kewarganegaraan":
                        kode_mapel = "2";
                        break;
                    case "Bahasa Indonesia":
                        kode_mapel = "3";
                        break;
                    case "Matematika":
                        kode_mapel = "4";
                        break;
                    case "Sejarah Indonesia":
                        kode_mapel = "5";
                        break;
                    case "Bahasa Inggris":
                        kode_mapel = "6";
                        break;
                    case "Seni Budaya":
                        kode_mapel = "7";
                        break;
                    case "Pendidikan jasmani, OR, dan Kesehatan":
                        kode_mapel = "8";
                        break;
                    case "Prakarya dan Kewirausahaan":
                        kode_mapel = "9";
                        break;
                    case "Matematika Minat":
                        kode_mapel = "10";
                        break;
                    case "Biologi":
                        kode_mapel = "11";
                        break;
                    case "Fisika":
                        kode_mapel = "12";
                        break;
                    case "Kimia":
                        kode_mapel = "13";
                        break;
                    case "Geografi":
                        kode_mapel = "14";
                        break;
                    case "Sejarah Minat":
                        kode_mapel = "15";
                        break;
                    case "Ekonomi":
                        kode_mapel = "16";
                        break;
                    case "Bahasa dan Sastra Indonesia":
                        kode_mapel = "17";
                        break;
                    case "Bahasa Perancis":
                        kode_mapel = "18";
                        break;
                    case "Bahasa dan Sastra Inggris":
                        kode_mapel = "19";
                        break;
                    case "Antropologi":
                        kode_mapel = "20";
                        break;
                    case "Bahasa Jerman":
                        kode_mapel = "21";
                        break;
                    case "Bahasa Inggris Minat":
                        kode_mapel = "22";
                        break;
                    case "Bahasa Mandarin":
                        kode_mapel = "23";
                        break;
                    case "Ekonomi Minat":
                        kode_mapel = "24";
                        break;
                }

                byte[] bytes_kode_mapel = kode_mapel.getBytes();
                kode_mapel = Base64.encodeToString(bytes_kode_mapel, Base64.DEFAULT);

                getJSONNilai();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Metode untuk mengelola data nilai dari server
    private void showNilai() {
        JSONObject jsonObject = null;

        v_ket1.removeAll(v_ket1);
        v_fotoguru.removeAll(v_fotoguru);
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

                // Perintah untuk memasukkan data ke ArrayList
                v_ket1.add(ket_1);
                v_ket2.add(ket_2);
                v_fotoguru.add(foto_guru_decode);
                v_nilai.add(nilai_decode);
            }

            if (v_ket1.isEmpty() || v_nilai.isEmpty()) {
                // Perintah terjadi ketika tidak ada nilai yang ditemukan
                zerodata.setVisibility(View.VISIBLE);
            } else {
                // Perintah terjadi ketika ada nilai yang ditemukan
                zerodata.setVisibility(View.GONE);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        // Perintah untuk mengirim data ke RVA_ViewNilai
        adapter = new RVA_ViewNilai(getActivity(), v_ket1, v_fotoguru, v_ket2, v_nilai);
        rvView.setAdapter(adapter);
    }
}
