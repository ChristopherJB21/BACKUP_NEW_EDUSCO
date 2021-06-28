package com.debritto.edusco.Siswa.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.R;
import com.debritto.edusco.Singkatan;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BarSiswaFragment extends Fragment {

    private String NIS, matapelajaran;
    private String semester, kode_nilai;
    private String JSON_STRING;

    private Spinner sp_semester, sp_jenis_nilai;
    private String[] Semester = {"Gasal", "Genap", "Nilai Akhir"};
    private String[] jenis_nilai = {"Pengetahuan", "Keterampilan", "Sikap"};

    private ArrayList<String> mapel = new ArrayList<>();
    private ArrayList<BarEntry> nilai = new ArrayList<>();
    private ImageView zerodata;
    private HorizontalBarChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_siswa, container, false);
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

            zerodata = (ImageView) view.findViewById(R.id.img_zerodata);
            chart = view.findViewById(R.id.chart);
            sp_semester = (Spinner) view.findViewById(R.id.sp_semester);
            sp_jenis_nilai = (Spinner) view.findViewById(R.id.sp_jenis_nilai);

            final ArrayAdapter<String> adapterSemester = new ArrayAdapter<String>(getActivity(),
                    R.layout.support_simple_spinner_dropdown_item, Semester);
            final ArrayAdapter<String> adapterJenisNilai = new ArrayAdapter<String>(getActivity(),
                    R.layout.support_simple_spinner_dropdown_item, jenis_nilai);

            sp_semester.setAdapter(adapterSemester);
            sp_jenis_nilai.setAdapter(adapterJenisNilai);

            // Perintah berjalan ketika Spinner Semester di klik
            sp_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    semester = adapterSemester.getItem(position);

                    mapel.removeAll(mapel);
                    nilai.removeAll(nilai);

                    getJSONBar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Perintah berjalan ketika spinner jenis nilai
            sp_jenis_nilai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    kode_nilai = adapterJenisNilai.getItem(position);

                    mapel.removeAll(mapel);
                    nilai.removeAll(nilai);

                    getJSONBar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metode untuk menuju ke url siswa_raport.php
    public String sendGetRequestBar() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/siswa_raport.php?nis=" + NIS);

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

    // Metode untuk mendapatkan data nilai dari server
    private void getJSONBar() {
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
                showBar();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestBar();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola nilai dari server
    private void showBar() {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            int URUTAN_NILAI = 0;

            mapel.removeAll(mapel);
            nilai.removeAll(nilai);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String get_smt = jo.getString("U2VtZXN0ZXI=");
                String get_jenisNilai = jo.getString("a29kZV9uaWxhaQ==");
                String get_mapel = jo.getString("a29kZV9tYXBlbA==");
                String get_nilai = jo.getString("TmlsYWk=");

                byte[] bytes_smt = get_smt.getBytes();
                byte[] bytes_nilai = get_nilai.getBytes();

                String smt_decode = new String(android.util.Base64.decode(bytes_smt, Base64.DEFAULT));
                String jenisNilai_decode = AES_Enkripsi.decrypt(get_jenisNilai, "raportjenis");
                String mapel_decode = AES_Enkripsi.decrypt(get_mapel, "raportmapel");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));


                if (smt_decode.equals(semester) && kode_nilai.equals(jenisNilai_decode)) {
                    Double nilai_double = Double.valueOf(nilai_decode);
                    String pembulatan_nilai = String.format("%.2f", nilai_double);
                    float float_nilai = Float.parseFloat(pembulatan_nilai);

                    matapelajaran = Singkatan.menyingkatMapel(mapel_decode);

                    mapel.add(matapelajaran);
                    nilai.add(new BarEntry(URUTAN_NILAI, float_nilai));

                    URUTAN_NILAI++;
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Gagal", Toast.LENGTH_SHORT).show();
        } finally {
            if (mapel.isEmpty() && nilai.isEmpty()) {
                zerodata.setVisibility(View.VISIBLE);
            } else {
                zerodata.setVisibility(View.GONE);
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setLabelCount(mapel.size());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(mapel));
            BarDataSet set1 = new BarDataSet(nilai, "Nilai");
            set1.setColor(Color.parseColor("#191970"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            configureChartAppearance();
            prepareChartData(data);
        }
    }

    private void configureChartAppearance() {
        chart.getDescription().setEnabled(false);
        chart.animateXY(2000, 2000);
        chart.setBackgroundColor(Color.parseColor("#ffffff"));

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.parseColor("#000000"));
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.parseColor("#191970"));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineColor(Color.parseColor("#000000"));
        yAxis.setAxisLineWidth(2f);
        yAxis.setTextSize(14f);
        yAxis.setTextColor(Color.parseColor("#191970"));

        YAxis yAxis1 = chart.getAxisRight();
        yAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis1.setDrawAxisLine(true);
        yAxis1.setDrawGridLines(false);
        yAxis1.setAxisMinimum(0f);
        yAxis1.setAxisMaximum(100f);
        yAxis1.setAxisLineColor(Color.parseColor("#000000"));
        yAxis1.setAxisLineWidth(2f);
        yAxis1.setLabelCount(10);
        yAxis1.setTextSize(14f);
        yAxis1.setTextColor(Color.parseColor("#191970"));

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
    }

    private void prepareChartData(BarData data) {
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.parseColor("#ffffff"));
        chart.setDrawValueAboveBar(false);
        chart.setData(data);
        chart.invalidate();
    }
}
