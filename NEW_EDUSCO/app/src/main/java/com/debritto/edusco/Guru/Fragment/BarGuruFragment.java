package com.debritto.edusco.Guru.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.R;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BarGuruFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private String NIG;
    private String nameFile;
    private String semester, kode_nilai, kode_mapel;
    private String JSON_STRING;
    private Spinner sp_mapel, sp_semester, sp_jenis_nilai;
    private int save;
    private ArrayList<String> list_mapel = new ArrayList<String>();
    private String[] Semester = {"Gasal", "Genap", "Nilai Akhir"};
    private String[] jenis_nilai = {"Pengetahuan", "Keterampilan", "Sikap"};

    private ImageView zerodata;
    private ImageButton btn_ekspor;
    private Dialog d_ekspor;
    private String [] Ekspor = {"Dokumen Teks (*.txt)", "Microsoft Excel (*.xls)"};

    private ArrayList<String> nis = new ArrayList<>();
    private ArrayList<BarEntry> nilai = new ArrayList<>();
    private HorizontalBarChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bar_guru, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceSaved) {
        try {
            // Perintah untuk mendapatkan data dari Shared Preference
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile_guru", Context.MODE_PRIVATE);
            String getNIG = sharedPreferences.getString("NIG", "");
            final String nig_decrypt = AES_Enkripsi.decrypt(getNIG, "profilegurunig");
            byte[] bytes_nig = nig_decrypt.getBytes();
            NIG = Base64.encodeToString(bytes_nig, Base64.DEFAULT);

            zerodata = (ImageView) view.findViewById(R.id.img_zerodata);
            chart = view.findViewById(R.id.chart);
            sp_mapel = (Spinner) view.findViewById(R.id.sp_mapel);
            sp_semester = (Spinner) view.findViewById(R.id.sp_semester);
            sp_jenis_nilai = (Spinner) view.findViewById(R.id.sp_jenis_nilai);

            btn_ekspor = (ImageButton) view.findViewById(R.id.btn_ekspor);
            d_ekspor = new Dialog(getActivity());
            d_ekspor.setContentView(R.layout.dialog_ekspor);

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

                    nis.removeAll(nis);
                    nilai.removeAll(nilai);

                    save = 0;
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

                    nis.removeAll(nis);
                    nilai.removeAll(nilai);

                    save = 0;
                    getJSONBar();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_ekspor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(getActivity(), "Izin ini diperlukan untuk menyimpan raport", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    Window window = d_ekspor.getWindow();
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.BOTTOM;
                    window.setAttributes(wlp);
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                    d_ekspor.show();

                    final Spinner sp_ekspor = (Spinner) d_ekspor.findViewById(R.id.sp_simpan);
                    final Button btn_save = (Button) d_ekspor.findViewById(R.id.btn_save);

                    final ArrayAdapter<String> adapterSimpan = new ArrayAdapter<String>(getActivity(),
                            R.layout.support_simple_spinner_dropdown_item, Ekspor);

                    sp_ekspor.setAdapter(adapterSimpan);

                    sp_ekspor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String ekspor = adapterSimpan.getItem(position);

                            if (ekspor.equals("Dokumen Teks (*.txt)")){
                                save = 1;
                            } else if (ekspor.equals("Microsoft Excel (*.xls)")){
                                save = 2;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    btn_save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MakeNameFile();

                            d_ekspor.dismiss();
                        }
                    });
                }
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

    // Metode untuk menuju ke url guru.bar.php
    public String sendGetRequestBar() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/guru_bar.php?nig=" + NIG + "&kode_mapel=" + kode_mapel + "&semester=" + semester + "&kode_nilai=" + kode_nilai);

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
                super.onPreExecute();}

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
                if (save == 0) {
                    showBar();
                } else if (save == 1) {
                    showSave();
                } else if (save == 2) {
                    showSaveExcel();
                }
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
                kode_mapel = adapterMapel.getItem(position).toString();

                nis.removeAll(nis);
                nilai.removeAll(nilai);

                save = 0;
                getJSONBar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // Metode untuk mengelola nilai dari server menjadi bar
    private void showBar() {
        JSONObject jsonObject = null;

        int URUTAN_NILAI = 0;

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            nis.removeAll(nis);
            nilai.removeAll(nilai);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String get_smt = jo.getString("U2VtZXN0ZXI=");
                String get_jenisNIlai = jo.getString("a29kZV9uaWxhaQ==");
                String get_mapel = jo.getString("TWFwZWw=");
                String get_nis = jo.getString("TklT");
                String get_nilai = jo.getString("TmlsYWk=");

                byte[] bytes_smt = get_smt.getBytes();
                byte[] bytes_nilai = get_nilai.getBytes();

                String smt_decode = new String(Base64.decode(bytes_smt, Base64.DEFAULT));
                String jenisNilai_decode = AES_Enkripsi.decrypt(get_jenisNIlai, "bargurujenis");
                String mapel_decode = AES_Enkripsi.decrypt(get_mapel, "bargurumapel");
                String nis_decode = AES_Enkripsi.decrypt(get_nis, "bargurunis");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);
                float float_nilai = Float.parseFloat(pembulatan_nilai);

                if (kode_mapel.equals(mapel_decode) && semester.equals(smt_decode) && kode_nilai.equals(jenisNilai_decode)) {
                    nis.add(nis_decode);
                    nilai.add(new BarEntry(URUTAN_NILAI, float_nilai));
                    URUTAN_NILAI++;
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (nis.isEmpty() && nilai.isEmpty()) {
                zerodata.setVisibility(View.VISIBLE);
                btn_ekspor.setVisibility(View.GONE);
            } else {
                zerodata.setVisibility(View.GONE);
                btn_ekspor.setVisibility(View.VISIBLE);
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setLabelCount(nis.size());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(nis));
            BarDataSet set1 = new BarDataSet(nilai, "Nilai");
            set1.setColor(Color.parseColor("#191970"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            configureChartAppearance();
            prepareChartData(data);
        }
    }

    // Metode untuk menyimpan nilai dari server dalam text file
    private void showSave() {
        JSONObject jsonObject = null;

        int URUTAN_NILAI = 0;

        nis.removeAll(nis);
        nilai.removeAll(nilai);

        try {
            File textFile = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File directory = new File(Environment.getExternalStorageDirectory() + "/EDUSCO");

                boolean checkFolder = directory.exists();

                if (!checkFolder) {
                    boolean makeFolder = directory.mkdirs();
                }

                textFile = new File(Environment.getExternalStorageDirectory() + "/EDUSCO", nameFile + ".txt");
            } else {
                textFile = new File(getActivity().getExternalFilesDir("/EDUSCO"), nameFile + ".txt");
            }

            FileWriter fileWriter = new FileWriter(textFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("EDUSCO");
            printWriter.println("Aplikasi Pengelolaan Nilai Pertama di Indonesia");
            printWriter.println("---------------------------------------");
            printWriter.println("");
            printWriter.println("");
            printWriter.println("---------------------------------------");

            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String get_smt = jo.getString("U2VtZXN0ZXI=");
                String get_jenisNIlai = jo.getString("a29kZV9uaWxhaQ==");
                String get_mapel = jo.getString("TWFwZWw=");
                String get_nis = jo.getString("TklT");
                String get_nama = jo.getString("bmFtYV9zaXN3YQ==");
                String get_nilai = jo.getString("TmlsYWk=");

                byte[] bytes_smt = get_smt.getBytes();
                byte[] bytes_nilai = get_nilai.getBytes();

                String smt_decode = new String(Base64.decode(bytes_smt, Base64.DEFAULT));
                String jenisNilai_decode = AES_Enkripsi.decrypt(get_jenisNIlai, "bargurujenis");
                String mapel_decode = AES_Enkripsi.decrypt(get_mapel, "bargurumapel");
                String nis_decode = AES_Enkripsi.decrypt(get_nis, "bargurunis");
                String nama_decode = AES_Enkripsi.decrypt(get_nama, "bargurusiswa");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);
                float float_nilai = Float.parseFloat(pembulatan_nilai);

                if (kode_mapel.equals(mapel_decode) && semester.equals(smt_decode) && kode_nilai.equals(jenisNilai_decode)) {
                    nis.add(nis_decode);
                    nilai.add(new BarEntry(URUTAN_NILAI, float_nilai));
                    URUTAN_NILAI++;
                }

                printWriter.println("");
                printWriter.println("NIS\t\t: " + nis_decode);
                printWriter.println("NAMA\t\t: " + nama_decode);
                printWriter.println("MATA PELAJARAN\t: " + mapel_decode);
                printWriter.println("SEMESTER\t: " + smt_decode);
                printWriter.println("JENIS NILAI\t: " + jenisNilai_decode);
                printWriter.println("NILAI\t\t: " + pembulatan_nilai);
                printWriter.println("---------------------------------------");
            }

            printWriter.close();
            fileWriter.close();

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Toast.makeText(getActivity(), "Telah disimpan di " + getActivity().getFilesDir(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Telah disimpan di " + getActivity().getExternalFilesDir("/EDUSCO"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Gagal: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            if (nis.isEmpty() && nilai.isEmpty()) {
                zerodata.setVisibility(View.VISIBLE);
                btn_ekspor.setVisibility(View.GONE);
            } else {
                zerodata.setVisibility(View.GONE);
                btn_ekspor.setVisibility(View.VISIBLE);
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setLabelCount(nis.size());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(nis));
            BarDataSet set1 = new BarDataSet(nilai, "Nilai");
            set1.setColor(Color.parseColor("#191970"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            configureChartAppearance();
            prepareChartData(data);
        }
    }

    // Metode untuk menyimpan nilai dari server dalam Microsoft Excel
    private void showSaveExcel() {
        JSONObject jsonObject = null;

        int URUTAN_NILAI = 0;

        nis.removeAll(nis);
        nilai.removeAll(nilai);

        try {
            File textFile = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                File directory = new File(Environment.getExternalStorageDirectory() + "/EDUSCO");

                boolean checkFolder = directory.exists();

                if (!checkFolder) {
                    boolean makeFolder = directory.mkdirs();
                }

                textFile = new File(Environment.getExternalStorageDirectory() + "/EDUSCO", nameFile + ".xls");
            } else {
                textFile = new File(getActivity().getExternalFilesDir("/EDUSCO"), nameFile + ".xls");
            }

            // Create Workbook
            Workbook wb = new HSSFWorkbook();

            // Create cell
            Cell cell = null;

            HSSFFont font = (HSSFFont) wb.createFont();
            font.setBold(true);

            CellStyle first_cellStyle = wb.createCellStyle();
            first_cellStyle.setAlignment(HorizontalAlignment.CENTER);
            first_cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            first_cellStyle.setBorderTop(BorderStyle.THICK);
            first_cellStyle.setBorderLeft(BorderStyle.THICK);
            first_cellStyle.setBorderRight(BorderStyle.THICK);
            first_cellStyle.setBorderBottom(BorderStyle.THICK);
            first_cellStyle.setFont(font);

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.MEDIUM);
            cellStyle.setBorderLeft(BorderStyle.MEDIUM);
            cellStyle.setBorderRight(BorderStyle.MEDIUM);
            cellStyle.setBorderBottom(BorderStyle.MEDIUM);

            // Create Sheet
            Sheet sheet = null;
            sheet = wb.createSheet(kode_mapel);

            // Create First Row
            Row first_row = sheet.createRow(0);

            cell = first_row.createCell(0);
            cell.setCellValue("NIS");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(1);
            cell.setCellValue("NAMA SISWA");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(2);
            cell.setCellValue("MATA PELAJARAN");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(3);
            cell.setCellValue("SEMESTER");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(4);
            cell.setCellValue("JENIS NILAI");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(5);
            cell.setCellValue("NILAI");
            cell.setCellStyle(first_cellStyle);

            cell = null;

            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String get_smt = jo.getString("U2VtZXN0ZXI=");
                String get_jenisNIlai = jo.getString("a29kZV9uaWxhaQ==");
                String get_mapel = jo.getString("TWFwZWw=");
                String get_nis = jo.getString("TklT");
                String get_nama = jo.getString("bmFtYV9zaXN3YQ==");
                String get_nilai = jo.getString("TmlsYWk=");

                byte[] bytes_smt = get_smt.getBytes();
                byte[] bytes_nilai = get_nilai.getBytes();

                String smt_decode = new String(Base64.decode(bytes_smt, Base64.DEFAULT));
                String jenisNilai_decode = AES_Enkripsi.decrypt(get_jenisNIlai, "bargurujenis");
                String mapel_decode = AES_Enkripsi.decrypt(get_mapel, "bargurumapel");
                String nis_decode = AES_Enkripsi.decrypt(get_nis, "bargurunis");
                String nama_decode = AES_Enkripsi.decrypt(get_nama, "bargurusiswa");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);
                float float_nilai = Float.parseFloat(pembulatan_nilai);

                if (kode_mapel.equals(mapel_decode) && semester.equals(smt_decode) && kode_nilai.equals(jenisNilai_decode)) {
                    nis.add(nis_decode);
                    nilai.add(new BarEntry(URUTAN_NILAI, float_nilai));
                    URUTAN_NILAI++;
                }

                // Create row
                Row row = sheet.createRow(i + 1);

                // Create cell
                cell = row.createCell(0);
                cell.setCellValue(nis_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(1);
                cell.setCellValue(nama_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(2);
                cell.setCellValue(mapel_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(3);
                cell.setCellValue(smt_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(4);
                cell.setCellValue(jenisNilai_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(5);
                cell.setCellValue(pembulatan_nilai);
                cell.setCellStyle(cellStyle);
            }

            sheet.setColumnWidth(0, (20 * 100));
            sheet.setColumnWidth(1, (100 * 100));
            sheet.setColumnWidth(2, (125 * 100));
            sheet.setColumnWidth(3, (30 * 100));
            sheet.setColumnWidth(4, (30 * 100));
            sheet.setColumnWidth(5, (20 * 100));

            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(textFile);
                wb.write(outputStream);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Toast.makeText(getActivity(), "Telah disimpan di " + getActivity().getFilesDir(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Telah disimpan di  " + getActivity().getExternalFilesDir("/EDUSCO"), Toast.LENGTH_SHORT).show();
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();

                Toast.makeText(getActivity(), "Gagal", Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Gagal: " + e, Toast.LENGTH_SHORT).show();
        } finally {
            if (nis.isEmpty() && nilai.isEmpty()) {
                zerodata.setVisibility(View.VISIBLE);
                btn_ekspor.setVisibility(View.GONE);
            } else {
                zerodata.setVisibility(View.GONE);
                btn_ekspor.setVisibility(View.VISIBLE);
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setLabelCount(nis.size());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(nis));
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
        xAxis.setTextSize(14f);
        xAxis.setTextColor(Color.parseColor("#191970"));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setDrawAxisLine(true);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setAxisLineColor(Color.parseColor("#000000"));
        yAxis.setTextSize(14f);
        yAxis.setTextColor(Color.parseColor("#191970"));

        YAxis yAxis1 = chart.getAxisRight();
        yAxis1.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis1.setDrawAxisLine(true);
        yAxis1.setDrawGridLines(false);
        yAxis1.setAxisMinimum(0f);
        yAxis1.setAxisMaximum(100f);
        yAxis1.setAxisLineColor(Color.parseColor("#000000"));
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

    private void MakeNameFile() {
        String paternDate = "E_dd_MM_yyyy_HH_mm_ss_SS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paternDate);
        String currentDate = simpleDateFormat.format(new Date());

        byte[] bytes_nis = NIG.getBytes();
        String nig_decode = new String(Base64.decode(bytes_nis, Base64.DEFAULT));

        nameFile = "EDUSCO_" + nig_decode + "_" + currentDate;

        getJSONBar();
    }
}
