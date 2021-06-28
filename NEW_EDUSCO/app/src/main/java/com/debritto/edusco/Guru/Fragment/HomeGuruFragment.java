package com.debritto.edusco.Guru.Fragment;

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
import com.debritto.edusco.Guru.RVA.RVA_ChangeNilai;
import com.debritto.edusco.Photo.RoundedTransformation;
import com.debritto.edusco.R;
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

public class HomeGuruFragment extends Fragment {

    private TextView tV_nig, tV_nama, tV_Date;
    private ImageButton btn_next, btn_before;
    private ImageView iV_foto, iV_zerodata;

    private ArrayList<String> v_no_data, v_ket1, v_ket2, v_ket3, v_foto, v_nilai;

    private String NIG, Tanggal;
    private String JSON_STRING;
    private String foto;

    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_guru, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        tV_nig = (TextView) view.findViewById(R.id.tV_nig);
        tV_nama = (TextView) view.findViewById(R.id.tV_nama);
        tV_Date = (TextView) view.findViewById(R.id.tV_date);

        btn_before = (ImageButton) view.findViewById(R.id.btn_before);
        btn_next = (ImageButton) view.findViewById(R.id.btn_next);

        iV_foto = (ImageView) view.findViewById(R.id.foto);
        iV_zerodata = (ImageView) view.findViewById(R.id.img_zerodata);

        v_no_data = new ArrayList<String>();
        v_ket1 = new ArrayList<String>();
        v_ket2 = new ArrayList<String>();
        v_ket3 = new ArrayList<String>();
        v_foto = new ArrayList<String>();
        v_nilai = new ArrayList<String>();

        try {
            // Perintah untuk mendapatkan data dari Shared Preference
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile_guru", Context.MODE_PRIVATE);
            String getNIG = sharedPreferences.getString("NIG", "");
            String getNama = sharedPreferences.getString("Nama", "");

            NIG = AES_Enkripsi.decrypt(getNIG, "profilegurunig");
            String nama_decrypt = AES_Enkripsi.decrypt(getNama, "profilegurunama");

            // Perintah untuk memasukkan data ke TextView
            tV_nig.setText(NIG);
            tV_nama.setText(nama_decrypt);

            rvView = (RecyclerView) view.findViewById(R.id.rv);
            rvView.setHasFixedSize(true);

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

        // Metode berjalan ketika button before di klik
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

        // Metode berjalan ketika button next di klik
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk menambahkan satu hari
                c.add(Calendar.DATE, 1);
                Tanggal = sDF.format(c.getTime());

                tV_Date.setText(Tanggal);

                // Perintah agar tidak bisa melebih hari ini
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

    // Perintah untuk menuju url guru_home.php
    public String sendGetRequestNilai() {
        StringBuilder sb = new StringBuilder();
        try {
            // Perintah untuk encode Base64
            byte[] bytes_nig = NIG.getBytes();
            byte[] bytes_date = Tanggal.getBytes();

            String sendNIG = Base64.encodeToString(bytes_nig, Base64.DEFAULT);
            String sendDate = Base64.encodeToString(bytes_date, Base64.DEFAULT);

            URL url = new URL("https://eduscotest.educationscoring.com/Guru/guru_home.php?nig=" + sendNIG + "&date=" + sendDate);
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

    // Metode untuk menuju guru_foto.pho
    public String sendGetRequestFoto() {
        StringBuilder sb = new StringBuilder();

        // Metode untuk encode Base64
        byte[] bytes_nig = NIG.getBytes();
        String nig = Base64.encodeToString(bytes_nig, Base64.DEFAULT);

        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/guru_foto.php?nig=" + nig);

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
    private void getJSONNilai() {
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

    // Metode untuk mendapatkan foto dari server
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
        v_no_data.removeAll(v_no_data);
        v_ket1.removeAll(v_ket1);
        v_ket2.removeAll(v_ket2);
        v_ket3.removeAll(v_ket3);
        v_foto.removeAll(v_foto);
        v_nilai.removeAll(v_nilai);

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String no_data = jo.getString("bm9fZGF0YQ==");
                String nis = jo.getString("TklT");
                String nama = jo.getString("bmFtYV9zaXN3YQ==");
                String kelas = jo.getString("a2VsYXM=");
                String mapel = jo.getString("TWFwZWw=");
                String smt = jo.getString("U2VtZXN0ZXI=");
                String jenis_nilai = jo.getString("SmVuaXNfbmlsYWk=");
                String nilaiKe = jo.getString("TmlsYWlLZQ==");
                String foto = jo.getString("Rm90b3Npc3dh");
                String nilai = jo.getString("bmlsYWk=");

                String no_data_decode = AES_Enkripsi.decrypt(no_data, "homenodata");
                String nis_decode = AES_Enkripsi.decrypt(nis, "homenis");
                String nama_decode = AES_Enkripsi.decrypt(nama, "homenamasiswa");
                String kelas_decode = AES_Enkripsi.decrypt(kelas, "homekelas");
                String mapel_decode = AES_Enkripsi.decrypt(mapel, "homemapel");
                String smt_decode = AES_Enkripsi.decrypt(smt, "homesmt");
                String jenis_nilai_decode = AES_Enkripsi.decrypt(jenis_nilai, "homejenisnilai");
                String nilaiKe_decode = AES_Enkripsi.decrypt(nilaiKe, "homenilaike");
                String foto_decode = AES_Enkripsi.decrypt(foto, "homefoto");
                String nilai_decode = AES_Enkripsi.decrypt(nilai, "homenilai");

                String ket1 = nama_decode;
                String ket2 = mapel_decode + " - " + kelas_decode + " - " + nis_decode;
                String ket3 = smt_decode + " - " + jenis_nilai_decode + " - " + nilaiKe_decode;

                // Perintah untuk memasukkan data ke ArrayList
                v_no_data.add(no_data_decode);
                v_ket1.add(ket1);
                v_ket2.add(ket2);
                v_ket3.add(ket3);
                v_foto.add(foto_decode);
                v_nilai.add(nilai_decode);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (v_no_data.isEmpty()) {
            // Perintah berjalan ketika tidak ada data
            iV_zerodata.setVisibility(View.VISIBLE);
        } else {
            iV_zerodata.setVisibility(View.GONE);
        }

        layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        adapter = new RVA_ChangeNilai(getActivity(), v_no_data, v_ket1, v_ket2, v_ket3, v_foto, v_nilai);
        rvView.setAdapter(adapter);
    }

    // Metode untuk mengelola foto dari server
    private void showFoto() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String getfoto = jo.getString("Rm90bw==");
                foto = AES_Enkripsi.decrypt(getfoto, "fotoguru");

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
