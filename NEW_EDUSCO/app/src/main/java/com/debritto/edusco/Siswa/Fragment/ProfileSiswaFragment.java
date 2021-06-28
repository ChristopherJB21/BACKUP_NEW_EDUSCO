package com.debritto.edusco.Siswa.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.debritto.edusco.AES_Enkripsi;
import com.debritto.edusco.MainActivity;
import com.debritto.edusco.Photo.RoundedTransformation;
import com.debritto.edusco.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProfileSiswaFragment extends Fragment {

    Boolean check = true;
    private String nis, oldpass, newpass, confirmpass;
    private String nis_decrypt;
    private String foto;
    private Bitmap bitmap;
    private TextView tV_nis, tV_nama;
    private Button btn_changepass, btn_ubahfoto, btn_logout;
    private String NIS, Nama, Kelas, NoPresensi;
    private String JSON_STRING;
    private Dialog d_changepassold, d_changepassnew;
    private ImageView iV_foto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_siswa, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        tV_nis = (TextView) view.findViewById(R.id.tV_nis);
        tV_nama = (TextView) view.findViewById(R.id.tV_nama);

        btn_changepass = (Button) view.findViewById(R.id.btn_changepass);
        btn_ubahfoto = (Button) view.findViewById(R.id.btn_ubahfoto);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        d_changepassold = new Dialog(getActivity());
        d_changepassnew = new Dialog(getActivity());
        d_changepassold.setContentView(R.layout.dialog_change_passold);
        d_changepassnew.setContentView(R.layout.dialog_change_passnew);

        iV_foto = (ImageView) view.findViewById(R.id.foto);

        try {
            // Perintah untuk mendapatkan data dari Shared Preference
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile_siswa", Context.MODE_PRIVATE);
            NIS = sharedPreferences.getString("NIS", "");
            Nama = sharedPreferences.getString("Nama", "");
            Kelas = sharedPreferences.getString("Kelas", "");
            NoPresensi = sharedPreferences.getString("NoPresensi", "");

            nis_decrypt = AES_Enkripsi.decrypt(NIS, "profilesiswanis");
            String nama_decrypt = AES_Enkripsi.decrypt(Nama, "profilesiswanama");
            String kelas_decrypt = AES_Enkripsi.decrypt(Kelas, "profilesiswakelas");
            String nopresensi_decrypt = AES_Enkripsi.decrypt(NoPresensi, "profilesiswanomer");

            // Perintah untuk memasukan data TextView
            tV_nama.setText(nama_decrypt);
            tV_nis.setText(nis_decrypt + " - " + kelas_decrypt + " - " + nopresensi_decrypt);

            getJSONFoto();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Perintah berjalan ketika button Change Pass di kilik
        btn_changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk menampilkan dialog change pass old
                d_changepassold.show();

                final EditText eT_passold = (EditText) d_changepassold.findViewById(R.id.eT_passold);

                final Button btn_next = d_changepassold.findViewById(R.id.btn_next);
                final Button btn_cancel = d_changepassold.findViewById(R.id.btn_cancel);

                eT_passold.setText(null);

                eT_passold.setTransformationMethod(PasswordTransformationMethod.getInstance());

                // Perintah berjalan ketika button next di klik
                btn_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String getoldpass = eT_passold.getText().toString();

                        byte[] bytes_nis = nis_decrypt.getBytes();
                        byte[] bytes_oldpass = getoldpass.getBytes();

                        nis = Base64.encodeToString(bytes_nis, Base64.DEFAULT);
                        oldpass = Base64.encodeToString(bytes_oldpass, Base64.DEFAULT);

                        getJSONOldPass();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d_changepassold.dismiss();
                    }
                });
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk menghapus data dengan Shared Preference
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("profile_siswa", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("NIS");
                editor.remove("Nama");
                editor.remove("Kelas");
                editor.remove("NoPresensi");
                editor.apply();

                SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("Cari_nis", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.remove("NIS");
                editor1.apply();

                SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("login_status", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.remove("Login");
                editor2.apply();

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        // Perintah berjalan ketika ImageView di klik
        iV_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 1);
            }
        });

        // Perintah berjalan ketika button ubah foto diklik
        btn_ubahfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageUploadToServerFunction();
            }
        });
    }

    // Metode untuk mengambil foto dari smartphone
    @Override
    public void onActivityResult(int RC, int RQC, Intent I) {
        super.onActivityResult(RC, RQC, I);
        if (RC == 1 && RQC == -1 && I != null && I.getData() != null) {
            Uri uri = I.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                // Perintah untuk menampilkan preview foto
                Picasso.with(getActivity())
                        .load(uri)
                        .resize(500, 500)
                        .centerCrop()
                        .transform(new RoundedTransformation(250, 0))
                        .into(iV_foto);

                btn_ubahfoto.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Metode untuk mengubah foto dan mengirimkan foto ke server
    public void ImageUploadToServerFunction() {

        // Perintah untuk compress foto
        ByteArrayOutputStream byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStreamObject);
        final byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {

            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(getActivity(), "Menyimpan perubahan foto", "Tunggu Sebentar", false, false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(getActivity(), string1, Toast.LENGTH_LONG).show();

                btn_ubahfoto.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(Void... params) {
                String ImagePath = "image_path";
                String ImageNIS = "nis";
                String ImageName = "nama";

                byte[] bytes_nis = nis_decrypt.getBytes();
                String sendNIS = Base64.encodeToString(bytes_nis, Base64.DEFAULT);

                // Perintah untuk mendapatkan tanggal dan waktu sekarang
                final SimpleDateFormat sDF = new SimpleDateFormat("EEEEddMMyyyyHHmmssSS");
                final Calendar c = Calendar.getInstance();
                String Tanggal = sDF.format(c.getTime());

                // Perintah untuk mengirimkan data ke server
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String, String> HashMapParams = new HashMap<String, String>();
                HashMapParams.put(ImageNIS, sendNIS);
                HashMapParams.put(ImageName, Tanggal);
                HashMapParams.put(ImagePath, ConvertImage);

                // Perintah untuk menuju url siswa_change_foto.php
                String ServerUploadPath = "https://eduscotest.educationscoring.com/Siswa/siswa_change_foto.php";
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);
                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

        AsyncTaskUploadClassOBJ.execute();
    }

    // Perintah untuk menuju ke url showpass_nis.php
    public String sendGetRequestOldPass() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/showpass_siswa.php?nis=" + nis + "&password=" + oldpass);

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

    // Perintah untuk menuju ke url changepass_siswa.php
    public String sendGetRequestNewPass() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Siswa/changepass_siswa.php?nis=" + nis +
                    "&passnew=" + newpass + "&passold=" + oldpass);

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

    // Perintah untuk menuju siswa_foto.php
    public String sendGetRequestFoto() {
        StringBuilder sb = new StringBuilder();

        byte[] bytes_nis = nis_decrypt.getBytes();
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

    // Metode untuk mendapatkan password lama dari server
    public void getJSONOldPass() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                showPassOld();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestOldPass();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk mengirim dan menyimpan password baru di server
    public void getJSONNewPass() {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                d_changepassnew.dismiss();
                JSON_STRING = s;
                Toast.makeText(getActivity(), JSON_STRING, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestNewPass();
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

    // Metode untuk mengelola password lama
    private void showPassOld() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String getpassold = jo.getString("UGFzc3dvcmQ=");

                if (getpassold.isEmpty()) {
                    // Perintah berjalan ketika password lama salah
                    Toast.makeText(getActivity(), "Password salah", Toast.LENGTH_SHORT).show();
                } else {
                    // Perintah berjalan ketika password lama benar

                    // Perintah untuk menutup change old password
                    d_changepassold.dismiss();
                    // Perintah untuk menampilkan change new password
                    d_changepassnew.show();

                    final EditText eT_passnew = (EditText) d_changepassnew.findViewById(R.id.eT_passnew);
                    final EditText eT_passconfirm = (EditText) d_changepassnew.findViewById(R.id.eT_passconfirm);

                    final Button btn_change = d_changepassnew.findViewById(R.id.btn_change);
                    final Button btn_cancel = d_changepassnew.findViewById(R.id.btn_cancel);

                    eT_passnew.setText(null);
                    eT_passconfirm.setText(null);

                    eT_passnew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eT_passconfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    // Metode berjalan ketika button change di klik
                    btn_change.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String getNewPass = eT_passnew.getText().toString();
                            String getConfirmPass = eT_passconfirm.getText().toString();

                            if (getNewPass.length() < 8) {
                                // Perintah berjalan ketika jumlah karakter password baru kurang dari 8 karakter
                                Toast.makeText(getActivity(), "Password minimal 8 karakter", Toast.LENGTH_SHORT).show();
                            } else if (!getNewPass.equals(getConfirmPass)) {
                                // Perintah berjalan ketika password baru tidak sama dengan password konfirmasi
                                Toast.makeText(getActivity(), "Password tidak sama", Toast.LENGTH_SHORT).show();
                            } else {
                                // Perintah berjalan ketika password baru sama dengan password konfirmasi
                                byte[] bytes_newpass = getNewPass.getBytes();
                                byte[] bytes_confirmpass = getConfirmPass.getBytes();

                                newpass = Base64.encodeToString(bytes_newpass, Base64.DEFAULT);
                                confirmpass = Base64.encodeToString(bytes_confirmpass, Base64.DEFAULT);

                                if (oldpass.equals(newpass)) {
                                    // Perintah berjalan ketika password sama dengan password lama
                                    Toast.makeText(getActivity(), "Tidak ada perbedaan password lama dengan password baru", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Perintah untuk mengubah password dan di simpan di server
                                    getJSONNewPass();
                                }
                            }
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            d_changepassnew.dismiss();
                        }
                    });
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                foto = AES_Enkripsi.decrypt(getfoto, "fotosiswa");
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
            Toast.makeText(getActivity(), "Gagal menghubungkan", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        iV_foto.setVisibility(View.VISIBLE);

        // Perintah untuk memasang foto ke ImageView
        Picasso.with(getActivity())
                .load(foto)
                .resize(500, 500)
                .centerCrop()
                .transform(new RoundedTransformation(250, 0))
                .into(iV_foto);
    }

    // Metode untuk mengubah foto ke server dengan perintah JSON POST
    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {

                URL url;
                HttpURLConnection httpURLConnectionObject;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject;
                BufferedReader bufferedReaderObject;
                int RC;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, StandardCharsets.UTF_8));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();
                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();
                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null) {
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check)
                    check = false;
                else
                    stringBuilderObject.append("&");

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }
}
