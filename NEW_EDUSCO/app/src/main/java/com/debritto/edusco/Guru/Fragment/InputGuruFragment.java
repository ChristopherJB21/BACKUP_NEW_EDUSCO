package com.debritto.edusco.Guru.Fragment;

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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.Guru.RVA.RVA_InputNilai;
import com.debritto.edusco.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class InputGuruFragment extends Fragment {

    private String NIG, kode_mapel, kode_kelas, semester, kode_nilai, NilaiKe;
    private String JSON_STRING;

    private ArrayList<String> list_mapel, list_kelas;
    private ArrayList<String> nilaiKe = new ArrayList<String>();
    private ArrayList<String> NIS, Nama, Presensi, Kelas, Foto;
    private String[] Semester = {"Gasal", "Genap"};
    private String[] jenis_nilai = {"Pengetahuan", "Keterampilan", "Sikap", "UTS", "UAS"};

    private Spinner sp_mapel, sp_kelas, sp_semester, sp_jenis_nilai, sp_nilaiKe;
    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_guru, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        try {
            // Perintah untuk mendapatkan data dari Shared Preference
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile_guru", Context.MODE_PRIVATE);
            String getNIG = sharedPreferences.getString("NIG", "");
            String nig_decrypt = AES_Enkripsi.decrypt(getNIG, "profilegurunig");
            byte[] bytes_nig = nig_decrypt.getBytes();
            NIG = Base64.encodeToString(bytes_nig, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sp_mapel = (Spinner) view.findViewById(R.id.sp_mapel);
        sp_kelas = (Spinner) view.findViewById(R.id.sp_kelas);
        sp_semester = (Spinner) view.findViewById(R.id.sp_semester);
        sp_jenis_nilai = (Spinner) view.findViewById(R.id.sp_jenis_nilai);
        sp_nilaiKe = (Spinner) view.findViewById(R.id.sp_nilaiKe);

        rvView = (RecyclerView) view.findViewById(R.id.rv_main);
        rvView.setHasFixedSize(true);

        list_mapel = new ArrayList<String>();
        list_kelas = new ArrayList<String>();
        NIS = new ArrayList<String>();
        Nama = new ArrayList<String>();
        Presensi = new ArrayList<String>();
        Kelas = new ArrayList<String>();
        Foto = new ArrayList<String>();

        // Perintah untuk membuat data NilaiKe
        for (int i = 1; i < 101; i++) {
            String a = String.valueOf(i);
            nilaiKe.add(a);
        }

        // Perintah untuk memasukkan data ke Spinner
        final ArrayAdapter<String> adapterSemester = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, Semester);
        final ArrayAdapter<String> adapterJenisNilai = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, jenis_nilai);
        final ArrayAdapter<String> adapterNilaiKe = new ArrayAdapter<String>(getActivity(),
                R.layout.support_simple_spinner_dropdown_item, nilaiKe);

        sp_semester.setAdapter(adapterSemester);
        sp_jenis_nilai.setAdapter(adapterJenisNilai);
        sp_nilaiKe.setAdapter(adapterNilaiKe);

        // Perintah berjalan ketika Spinner Semester di klik
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

                // Perintah untuk menghapus data di ArrayList
                NIS.removeAll(NIS);
                Nama.removeAll(Nama);
                Presensi.removeAll(Presensi);
                Kelas.removeAll(Kelas);
                Foto.removeAll(Foto);

                getJSONSiswa();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Perintah berjalan ketika spinner jenis nilai
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

                // Perintah untuk menghapus data di Array List
                NIS.removeAll(NIS);
                Nama.removeAll(Nama);
                Presensi.removeAll(Presensi);
                Kelas.removeAll(Kelas);
                Foto.removeAll(Foto);

                getJSONSiswa();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Perintah berjalan ketika spinner nilaike di klik
        sp_nilaiKe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Perintah untuk mendapatkan data nilaike
                NilaiKe = adapterNilaiKe.getItem(position);
                byte[] bytes_nilaike = NilaiKe.getBytes();
                NilaiKe = Base64.encodeToString(bytes_nilaike, Base64.DEFAULT);

                // Perintah untuk menghapus data di ArrayList
                NIS.removeAll(NIS);
                Nama.removeAll(Nama);
                Presensi.removeAll(Presensi);
                Kelas.removeAll(Kelas);
                Foto.removeAll(Foto);

                getJSONSiswa();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getJSONMapel();
    }

    // Metode untuk menuju ke url mengajar_mapel.php
    public String sendGetRequestMapel() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/mengajar_mapel.php?nig=" + NIG);
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

    // Metode untuk menuju ke url mengajar_kelas.php
    public String sendGetRequestKelas() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/mengajar_kelas.php?nig=" + NIG + "&kode_mapel=" + kode_mapel);
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

    // Metode untuk menuju ke url mengajar_daftar_siswa.php
    public String sendGetRequestSiswa() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/mengajar_daftar_siswa.php?kode_kelas=" + kode_kelas);
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

    // Metode untuk mengambil data mapel dari server
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

    // Metode untuk mengambil data Kelas dari server
    private void getJSONKelas() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showKelas();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestKelas();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengambil data Siswa dari server
    private void getJSONSiswa() {
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

    // Metode untuk mengelola data mapel dari server
    private void showMapel() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String mapel = jo.getString("bWF0YXBlbGFqYXJhbg==");

                String mapel_decode = AES_Enkripsi.decrypt(mapel, "mengajarmapel");

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

        // Perintah berjalan ketika spinner mapel di klik
        sp_mapel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String mapel = adapterMapel.getItem(position).toString();

                // Perintah untuk mendapatkan data kode_mapel
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
                    case "Seni Rupa":
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
                    case "Sosiologi":
                        kode_mapel = "16";
                        break;
                    case "Ekonomi":
                        kode_mapel = "17";
                        break;
                    case "Bahasa dan Sastra Indonesia":
                        kode_mapel = "18";
                        break;
                    case "Bahasa Perancis":
                        kode_mapel = "19";
                        break;
                    case "Bahasa dan Sastra Inggris":
                        kode_mapel = "20";
                        break;
                    case "Antropologi":
                        kode_mapel = "21";
                        break;
                    case "Bahasa Jerman":
                        kode_mapel = "22";
                        break;
                    case "Bahasa Inggris Minat":
                        kode_mapel = "23";
                        break;
                    case "Bahasa Mandarin":
                        kode_mapel = "24";
                        break;
                    case "Ekonomi Minat":
                        kode_mapel = "25";
                        break;
                    case "Seni Teater":
                        kode_mapel = "26";
                        break;
                    case "Pendidikan Nilai":
                        kode_mapel = "27";
                        break;
                    case "Bimbingan Konseling":
                        kode_mapel = "28";
                        break;
                }

                byte[] bytes_kode_mapel = kode_mapel.getBytes();
                kode_mapel = Base64.encodeToString(bytes_kode_mapel, Base64.DEFAULT);

                list_kelas.removeAll(list_kelas);

                getJSONKelas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Metode untuk mengelola data kelas dari server
    private void showKelas() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String kelas = jo.getString("a2VsYXM=");

                String kelas_decode = AES_Enkripsi.decrypt(kelas, "mengajarkelas");

                list_kelas.add(kelas_decode);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ArrayAdapter adapterKelas = new ArrayAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, list_kelas);

        sp_kelas.setAdapter(adapterKelas);

        // Perintah berjalan ketika spinner kelas di klik
        sp_kelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String kelas = adapterKelas.getItem(position).toString();

                // Perintah untuk mendapatkan data kode_kelas
                switch (kelas) {
                    case "X MIPA 1":
                        kode_kelas = "1";
                        break;
                    case "X MIPA 2":
                        kode_kelas = "2";
                        break;
                    case "X MIPA 3":
                        kode_kelas = "3";
                        break;
                    case "X MIPA 4":
                        kode_kelas = "4";
                        break;
                    case "X MIPA 5":
                        kode_kelas = "5";
                        break;
                    case "X IPS 1":
                        kode_kelas = "6";
                        break;
                    case "X IPS 2":
                        kode_kelas = "7";
                        break;
                    case "X IPS 3":
                        kode_kelas = "8";
                        break;
                    case "X Bahasa dan Budaya":
                        kode_kelas = "9";
                        break;
                    case "XI MIPA 1":
                        kode_kelas = "10";
                        break;
                    case "XI MIPA 2":
                        kode_kelas = "11";
                        break;
                    case "XI MIPA 3":
                        kode_kelas = "12";
                        break;
                    case "XI MIPA 4":
                        kode_kelas = "13";
                        break;
                    case "XI MIPA 5":
                        kode_kelas = "14";
                        break;
                    case "XI IPS 1":
                        kode_kelas = "15";
                        break;
                    case "XI IPS 2":
                        kode_kelas = "16";
                        break;
                    case "XI IPS 3":
                        kode_kelas = "17";
                        break;
                    case "XI Bahasa dan Budaya":
                        kode_kelas = "18";
                        break;
                    case "XII MIPA 1":
                        kode_kelas = "19";
                        break;
                    case "XII MIPA 2":
                        kode_kelas = "20";
                        break;
                    case "XII MIPA 3":
                        kode_kelas = "21";
                        break;
                    case "XII MIPA 4":
                        kode_kelas = "22";
                        break;
                    case "XII MIPA 5":
                        kode_kelas = "23";
                        break;
                    case "XII IPS 1":
                        kode_kelas = "24";
                        break;
                    case "XII IPS 2":
                        kode_kelas = "25";
                        break;
                    case "XII IPS 3":
                        kode_kelas = "26";
                        break;
                    case "XII Bahasa dan Budaya":
                        kode_kelas = "27";
                        break;
                }

                byte[] bytes_kode_kelas = kode_kelas.getBytes();
                kode_kelas = Base64.encodeToString(bytes_kode_kelas, Base64.DEFAULT);

                NIS.removeAll(NIS);
                Nama.removeAll(Nama);
                Presensi.removeAll(Presensi);
                Kelas.removeAll(Kelas);
                Foto.removeAll(Foto);

                getJSONSiswa();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Metode untuk mengelola data Siswa dari server
    private void showSiswa() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String nis = jo.getString("TklT");
                String nama = jo.getString("bmFtYV9zaXN3YQ==");
                String presensi = jo.getString("bm9fcHJlc2Vuc2k=");
                String kelas = jo.getString("S2VsYXM=");
                String foto = jo.getString("Rm90bw==");

                String nis_decode = AES_Enkripsi.decrypt(nis, "daftarsiswanis");
                String nama_decode = AES_Enkripsi.decrypt(nama, "daftarsiswanama");
                String presensi_decode = AES_Enkripsi.decrypt(presensi, "daftarpresensi");
                String kelas_decode = AES_Enkripsi.decrypt(kelas, "daftarkelas");
                String foto_decode = AES_Enkripsi.decrypt(foto, "daftarsiswafoto");

                // Perintah untuk memasukkan data ke ArrayList
                NIS.add(nis_decode);
                Nama.add(nama_decode);
                Presensi.add(presensi_decode);
                Kelas.add(kelas_decode);
                Foto.add(foto_decode);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        // Perintah untuk menuuju ke RVA_InputNilai
        adapter = new RVA_InputNilai(getActivity(), NIS, Nama, Presensi, Kelas, Foto, NIG, kode_mapel, kode_kelas, semester, kode_nilai, NilaiKe);
        rvView.setAdapter(adapter);
    }
}
