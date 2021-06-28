package com.debritto.edusco.Siswa.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.R;
import com.debritto.edusco.Singkatan;
import com.debritto.edusco.Siswa.RVA.RVA_RaportNilai;

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

public class RaportSiswaFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private String NIS;
    private String nameFile;
    private String JSON_STRING;
    private int save;
    private ArrayList<String> v_ket1, v_ket2, v_ket3, Nilai, NamaGuru, FotoGuru, KKM, Tuntas;

    private ImageView zeroData;
    private ImageButton btn_ekspor;
    private Dialog d_ekspor;
    private String [] Ekspor = {"Dokumen Teks (*.txt)", "Microsoft Excel (*.xls)"};

    private RecyclerView rvView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raport_siswa, container, false);
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

        zeroData = (ImageView) view.findViewById(R.id.img_zerodata);
        btn_ekspor = (ImageButton) view.findViewById(R.id.btn_ekspor);

        d_ekspor = new Dialog (getActivity());
        d_ekspor.setContentView(R.layout.dialog_ekspor);

        rvView = (RecyclerView) view.findViewById(R.id.rv_main);
        rvView.setHasFixedSize(true);

        v_ket1 = new ArrayList<String>();
        v_ket2 = new ArrayList<String>();
        v_ket3 = new ArrayList<String>();
        Nilai = new ArrayList<String>();
        NamaGuru = new ArrayList<String>();
        FotoGuru = new ArrayList<>();
        KKM = new ArrayList<>();
        Tuntas = new ArrayList<>();

        save = 0;
        getJSONRaport();

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
    }

    // Metode untuk menuju ke url siswa_raport.php
    public String sendGetRequestRaport() {
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
    private void getJSONRaport() {
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
                    showRaport();
                } else if (save == 1) {
                    showSaveRaport();
                } else if (save == 2) {
                    showSaveExcel();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestRaport();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengelola nilai dari server
    private void showRaport() {
        JSONObject jsonObject = null;

        v_ket1.removeAll(v_ket1);
        v_ket2.removeAll(v_ket2);
        v_ket3.removeAll(v_ket3);
        Nilai.removeAll(Nilai);
        FotoGuru.removeAll(FotoGuru);

        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String smt = jo.getString("U2VtZXN0ZXI=");
                String mapel = jo.getString("a29kZV9tYXBlbA==");
                String jeniNilai = jo.getString("a29kZV9uaWxhaQ==");
                String nilai = jo.getString("TmlsYWk=");
                String nama_guru = jo.getString("bmFtYV9ndXJ1");
                String foto_guru = jo.getString("Rm90bw==");
                String kkm = jo.getString("S0tN");
                String tuntas = jo.getString("VHVudGFz");

                byte[] bytes_smt = smt.getBytes();
                byte[] bytes_nilai = nilai.getBytes();
                byte[] bytes_kkm = kkm.getBytes();
                byte[] bytes_tuntas = tuntas.getBytes();

                String smt_decode = new String(android.util.Base64.decode(bytes_smt, Base64.DEFAULT));
                String mapel_decode = AES_Enkripsi.decrypt(mapel, "raportmapel");
                String jenisNilai_decode = AES_Enkripsi.decrypt(jeniNilai, "raportjenis");
                String nilai_decode = new String(android.util.Base64.decode(bytes_nilai, Base64.DEFAULT));
                String namaguru_decode = AES_Enkripsi.decrypt(nama_guru, "raportguru");
                String fotoguru_decode = AES_Enkripsi.decrypt(foto_guru, "raportfotoguru");
                String kkm_decode = new String(android.util.Base64.decode(bytes_kkm, Base64.DEFAULT));
                String tuntas_decode = new String(android.util.Base64.decode(bytes_tuntas, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);

                String mapel_singkat = Singkatan.menyingkatMapel(mapel_decode);
                String guru_singkat = Singkatan.menyingkatGuru(namaguru_decode);

                String ket1 = mapel_singkat + " - " + guru_singkat;
                String ket2 = smt_decode + " - " + jenisNilai_decode;
                String ket3 = "KKM: " + kkm_decode + " - " + tuntas_decode;

                // Metode untuk memasukkan nilai ke ArrayList
                v_ket1.add(ket1);
                v_ket2.add(ket2);
                v_ket3.add(ket3);
                Nilai.add(pembulatan_nilai);
                FotoGuru.add(fotoguru_decode);
            }

            if (v_ket1.isEmpty() || v_ket2.isEmpty() || v_ket3.isEmpty() || Nilai.isEmpty()) {
                // Perintah terjadi ketika tidak ada nilai yang ditemukan
                zeroData.setVisibility(View.VISIBLE);
                btn_ekspor.setVisibility(View.GONE);
            } else {
                // Perintah terjadi ketika ada nilai yang ditemukan
                zeroData.setVisibility(View.GONE);
                btn_ekspor.setVisibility(View.VISIBLE);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        // Perintah untuk memasukkan data ke RVA_RaportNilai
        adapter = new RVA_RaportNilai(getActivity(), v_ket1, v_ket2, v_ket3, Nilai, FotoGuru);
        rvView.setAdapter(adapter);
    }

    private void MakeNameFile() {
        String paternDate = "EddMMyyyy_HHmmssSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paternDate);
        String currentDate = simpleDateFormat.format(new Date());

        byte[] bytes_nis = NIS.getBytes();
        String nis_decode = new String(Base64.decode(bytes_nis, Base64.DEFAULT));

        nameFile = "EDUSCO_" + nis_decode + "_" + currentDate;

        getJSONRaport();
    }

    // Metode untuk menyimpan nilai dari server
    private void showSaveRaport() {
        JSONObject jsonObject = null;

        v_ket1.removeAll(v_ket1);
        v_ket2.removeAll(v_ket2);
        v_ket3.removeAll(v_ket3);
        Nilai.removeAll(Nilai);
        FotoGuru.removeAll(FotoGuru);

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
            printWriter.println("");

            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String smt = jo.getString("U2VtZXN0ZXI=");
                String mapel = jo.getString("a29kZV9tYXBlbA==");
                String jeniNilai = jo.getString("a29kZV9uaWxhaQ==");
                String nilai = jo.getString("TmlsYWk=");
                String nama_guru = jo.getString("bmFtYV9ndXJ1");
                String foto_guru = jo.getString("Rm90bw==");
                String kkm = jo.getString("S0tN");
                String tuntas = jo.getString("VHVudGFz");

                byte[] bytes_smt = smt.getBytes();
                byte[] bytes_nilai = nilai.getBytes();
                byte[] bytes_kkm = kkm.getBytes();
                byte[] bytes_tuntas = tuntas.getBytes();

                String smt_decode = new String(Base64.decode(bytes_smt, Base64.DEFAULT));
                String mapel_decode = AES_Enkripsi.decrypt(mapel, "raportmapel");
                String jenisNilai_decode = AES_Enkripsi.decrypt(jeniNilai, "raportjenis");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));
                String namaguru_decode = AES_Enkripsi.decrypt(nama_guru, "raportguru");
                String fotoguru_decode = AES_Enkripsi.decrypt(foto_guru, "raportfotoguru");
                String kkm_decode = new String(android.util.Base64.decode(bytes_kkm, Base64.DEFAULT));
                String tuntas_decode = new String(android.util.Base64.decode(bytes_tuntas, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);

                printWriter.println("SEMESTER\t: " + smt_decode);
                printWriter.println("MATA PELAJARAN\t: " + mapel_decode);
                printWriter.println("JENIS NILAI\t: " + jenisNilai_decode);
                printWriter.println("NILAI\t\t: " + pembulatan_nilai);
                printWriter.println("KKM\t\t: " + kkm_decode);
                printWriter.println("Ketuntasan\t: " + tuntas_decode);
                printWriter.println("GURU\t\t: " + namaguru_decode);
                printWriter.println("---------------------------------------");
                printWriter.println("");

                String mapel_singkat = Singkatan.menyingkatMapel(mapel_decode);
                String guru_singkat = Singkatan.menyingkatGuru(namaguru_decode);

                String ket1 = mapel_singkat + " - " + guru_singkat;
                String ket2 = smt_decode + " - " + jenisNilai_decode;
                String ket3 = "KKM: " + kkm_decode + " - " + tuntas_decode;

                // Metode untuk memasukkan nilai ke ArrayList
                v_ket1.add(ket1);
                v_ket2.add(ket2);
                v_ket3.add(ket3);
                Nilai.add(pembulatan_nilai);
                FotoGuru.add(fotoguru_decode);
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
        }

        // Perintah untuk memasukkan data ke RVA_RaportNilai
        adapter = new RVA_RaportNilai(getActivity(), v_ket1, v_ket2, v_ket3, Nilai, FotoGuru);
        rvView.setAdapter(adapter);
    }

    // Metode untuk menyimpan nilai dari server
    private void showSaveExcel() {
        JSONObject jsonObject = null;

        v_ket1.removeAll(v_ket1);
        v_ket2.removeAll(v_ket2);
        v_ket3.removeAll(v_ket3);
        Nilai.removeAll(Nilai);
        FotoGuru.removeAll(FotoGuru);

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

            byte[] bytes_nis = NIS.getBytes();
            String nis_decode = new String(Base64.decode(bytes_nis, Base64.DEFAULT));

            sheet = wb.createSheet(nis_decode);

            // Create First Row
            Row first_row = sheet.createRow(0);

            cell = first_row.createCell(0);
            cell.setCellValue("MATA PELAJARAN");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(1);
            cell.setCellValue("SEMESTER");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(2);
            cell.setCellValue("JENIS NILAI");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(3);
            cell.setCellValue("KKM");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(4);
            cell.setCellValue("NILAI");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(5);
            cell.setCellValue("KETUNTASAN");
            cell.setCellStyle(first_cellStyle);

            cell = first_row.createCell(6);
            cell.setCellValue("GURU");
            cell.setCellStyle(first_cellStyle);

            cell = null;

            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String smt = jo.getString("U2VtZXN0ZXI=");
                String mapel = jo.getString("a29kZV9tYXBlbA==");
                String jeniNilai = jo.getString("a29kZV9uaWxhaQ==");
                String nilai = jo.getString("TmlsYWk=");
                String nama_guru = jo.getString("bmFtYV9ndXJ1");
                String foto_guru = jo.getString("Rm90bw==");
                String kkm = jo.getString("S0tN");
                String tuntas = jo.getString("VHVudGFz");

                byte[] bytes_smt = smt.getBytes();
                byte[] bytes_nilai = nilai.getBytes();
                byte[] bytes_kkm = kkm.getBytes();
                byte[] bytes_tuntas = tuntas.getBytes();

                String smt_decode = new String(Base64.decode(bytes_smt, Base64.DEFAULT));
                String mapel_decode = AES_Enkripsi.decrypt(mapel, "raportmapel");
                String jenisNilai_decode = AES_Enkripsi.decrypt(jeniNilai, "raportjenis");
                String nilai_decode = new String(Base64.decode(bytes_nilai, Base64.DEFAULT));
                String namaguru_decode = AES_Enkripsi.decrypt(nama_guru, "raportguru");
                String fotoguru_decode = AES_Enkripsi.decrypt(foto_guru, "raportfotoguru");
                String kkm_decode = new String(android.util.Base64.decode(bytes_kkm, Base64.DEFAULT));
                String tuntas_decode = new String(android.util.Base64.decode(bytes_tuntas, Base64.DEFAULT));

                Double nilai_double = Double.valueOf(nilai_decode);
                String pembulatan_nilai = String.format("%.2f", nilai_double);

                String mapel_singkat = Singkatan.menyingkatMapel(mapel_decode);
                String guru_singkat = Singkatan.menyingkatGuru(namaguru_decode);

                String ket1 = mapel_singkat + " - " + guru_singkat;
                String ket2 = smt_decode + " - " + jenisNilai_decode;
                String ket3 = "KKM: " + kkm_decode + " - " + tuntas_decode;

                // Metode untuk memasukkan nilai ke ArrayList
                v_ket1.add(ket1);
                v_ket2.add(ket2);
                v_ket3.add(ket3);
                Nilai.add(pembulatan_nilai);
                FotoGuru.add(fotoguru_decode);

                // Create row
                Row row = sheet.createRow(i + 1);

                // Create cell
                cell = row.createCell(0);
                cell.setCellValue(mapel_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(1);
                cell.setCellValue(smt_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(2);
                cell.setCellValue(jenisNilai_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(3);
                cell.setCellValue(kkm_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(4);
                cell.setCellValue(pembulatan_nilai);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(5);
                cell.setCellValue(tuntas_decode);
                cell.setCellStyle(cellStyle);

                cell = row.createCell(6);
                cell.setCellValue(namaguru_decode);
                cell.setCellStyle(cellStyle);
            }

            sheet.setColumnWidth(0, (125 * 100));
            sheet.setColumnWidth(1, (30 * 100));
            sheet.setColumnWidth(2, (30 * 100));
            sheet.setColumnWidth(3, (20 * 100));
            sheet.setColumnWidth(4, (20 * 100));
            sheet.setColumnWidth(5, (30 * 100));
            sheet.setColumnWidth(6, (100 * 100));

            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(textFile);
                wb.write(outputStream);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    Toast.makeText(getActivity(), "Telah disimpan di " + getActivity().getFilesDir(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Telah disimpan di " + getActivity().getExternalFilesDir("/EDUSCO"), Toast.LENGTH_SHORT).show();
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
        }

        // Perintah untuk memasukkan data ke RVA_RaportNilai
        adapter = new RVA_RaportNilai(getActivity(), v_ket1, v_ket2, v_ket3, Nilai, FotoGuru);
        rvView.setAdapter(adapter);
    }
}
