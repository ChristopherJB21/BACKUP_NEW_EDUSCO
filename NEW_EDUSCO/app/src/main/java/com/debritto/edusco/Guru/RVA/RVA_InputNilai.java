package com.debritto.edusco.Guru.RVA;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.Photo.RoundedTransformation;
import com.debritto.edusco.R;
import com.debritto.edusco.Validasi_Nilai;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RVA_InputNilai extends RecyclerView.Adapter<RVA_InputNilai.ViewHolder> {


    Dialog mydialog;
    private String nis, NIG, kode_mapel, kode_kelas, semester, kode_nilai, NilaiKe, nilai;
    private String JSON_STRING, cek_nilai;
    private ArrayList<String> NIS, Nama, Presensi, Kelas, Foto;
    private Context context;

    // Metode untuk mengambil data dari InputGuruFragment
    public RVA_InputNilai(Context mContext, ArrayList<String> mNIS,
                          ArrayList<String> mNama, ArrayList<String> mPresensi, ArrayList<String> mKelas,
                          ArrayList<String> mFoto, String mNIG, String mkode_mapel,
                          String mkode_kelas, String msemester, String mkode_nilai,
                          String mNilaiKe) {
        context = mContext;
        NIS = mNIS;
        Nama = mNama;
        Presensi = mPresensi;
        Kelas = mKelas;
        Foto = mFoto;
        NIG = mNIG;
        kode_mapel = mkode_mapel;
        kode_kelas = mkode_kelas;
        semester = msemester;
        kode_nilai = mkode_nilai;
        NilaiKe = mNilaiKe;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_input_nilai, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        mydialog = new Dialog(context);
        mydialog.setContentView(R.layout.dialog_input_nilai);

        // Perintah berjalan ketika salah satu item di klik
        vh.item_siswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk menampilkan dialog input nilai
                mydialog.show();

                final EditText eT_nilai = (EditText) mydialog.findViewById(R.id.eT_nilai);
                Button btn_save = (Button) mydialog.findViewById(R.id.btn_save);

                eT_nilai.setText(null);

                // Perintah berjalan ketika button save diklik
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nis = NIS.get(vh.getAdapterPosition());
                        byte[] bytes_nis = nis.getBytes();
                        nis = Base64.encodeToString(bytes_nis, Base64.DEFAULT);

                        nilai = eT_nilai.getText().toString();
                        try {
                            cek_nilai = Validasi_Nilai.cekNilai(nilai);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (cek_nilai.equals("Masukkan nilai")) {
                            Toast.makeText(context, cek_nilai, Toast.LENGTH_SHORT).show();
                        } else if (cek_nilai.equals("Tidak ada nilai lebih dari 100")) {
                            Toast.makeText(context, cek_nilai, Toast.LENGTH_SHORT).show();
                        } else {
                            byte[] bytes_nilai = cek_nilai.getBytes();
                            nilai = Base64.encodeToString(bytes_nilai, Base64.DEFAULT);

                            getJSONInput();

                            NIS.remove(vh.getAdapterPosition());
                            Nama.remove(vh.getAdapterPosition());
                            Presensi.remove(vh.getAdapterPosition());
                            Foto.remove(vh.getAdapterPosition());
                            notifyItemRemoved(vh.getAdapterPosition());

                            // Perintah untuk menutup dialog input nilai
                            mydialog.dismiss();
                        }
                    }
                });
            }
        });

        return vh;
    }

    // Metode untuk menuju ke url input_nilai.php
    public String sendGetRequestInput() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/input_nilai.php?nis=" + nis
                    + "&kode_kelas=" + kode_kelas + "&kode_mapel=" + kode_mapel + "&nig=" + NIG + "&kode_nilai=" + kode_nilai
                    + "&NilaiKe=" + NilaiKe + "&Semester=" + semester + "&nilai=" + nilai);

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

    // Metode untuk memasukkan nilai ke server
    private void getJSONInput() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context, "Mengambil data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                String s = sendGetRequestInput();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String id = NIS.get(position) + " - " + Kelas.get(position) + " - " + Presensi.get(position);

        holder.tvNama.setText(Nama.get(position));
        holder.tvNIS.setText(id);

        Picasso.with(context)
                .load(Foto.get(position))
                .resize(150, 150)
                .centerCrop()
                .transform(new RoundedTransformation(75, 0))
                .into(holder.iV_foto);
    }

    @Override
    public int getItemCount() {
        return NIS.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item_siswa;
        public TextView tvNama;
        public TextView tvNIS;
        public ImageView iV_foto;

        public ViewHolder(View v) {
            super(v);
            item_siswa = (LinearLayout) v.findViewById(R.id.item_siswa);
            tvNama = (TextView) v.findViewById(R.id.tV_nama);
            tvNIS = (TextView) v.findViewById(R.id.tV_NIS);
            iV_foto = (ImageView) v.findViewById(R.id.iV_foto);
        }
    }
}
