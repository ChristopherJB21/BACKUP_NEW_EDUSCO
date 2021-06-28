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

public class RVA_ChangeNilai extends RecyclerView.Adapter<RVA_ChangeNilai.ViewHolder> {

    Dialog mydialog, d_delete;
    private String data, Nilai, cek_nilai;
    private String JSON_STRING;
    private ArrayList<String> v_no_data, v_ket1, v_ket2, v_ket3, v_foto, v_nilai;
    private Context context;

    // Perintah untuk mendapatkan data dari ChangeGuruFragment
    public RVA_ChangeNilai(Context mContext, ArrayList<String> mdata,
                           ArrayList<String> mket1, ArrayList<String> mket2, ArrayList<String> mket3,
                           ArrayList<String> mfoto, ArrayList<String> mNilai) {
        context = mContext;
        v_no_data = mdata;
        v_ket1 = mket1;
        v_ket2 = mket2;
        v_ket3 = mket3;
        v_foto = mfoto;
        v_nilai = mNilai;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_change_nilai, parent, false);
        final ViewHolder vh = new ViewHolder(v);

        mydialog = new Dialog(context);
        d_delete = new Dialog(context);

        mydialog.setContentView(R.layout.dialog_change_nilai);
        d_delete.setContentView(R.layout.dialog_delete_nilai);

        // Perintah berjalan ketika salah satu item di klik
        vh.item_siswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perintah untuk menampilkan dialog change nilai
                mydialog.show();

                final EditText eT_nilai = (EditText) mydialog.findViewById(R.id.eT_nilai);
                Button btn_change = (Button) mydialog.findViewById(R.id.btn_change);
                TextView btn_delete = (TextView) mydialog.findViewById(R.id.btn_delete);
                final Button btn_yes = (Button) d_delete.findViewById(R.id.btn_yes);
                final Button btn_no = (Button) d_delete.findViewById(R.id.btn_no);

                // Perintah berjalan ketika button change di klik
                btn_change.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = v_no_data.get(vh.getAdapterPosition());
                        byte[] bytes_data = data.getBytes();
                        data = Base64.encodeToString(bytes_data, Base64.DEFAULT);

                        Nilai = eT_nilai.getText().toString();
                        try {
                            cek_nilai = Validasi_Nilai.cekNilai(Nilai);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (cek_nilai.equals("Masukkan nilai")) {
                            Toast.makeText(context, cek_nilai, Toast.LENGTH_SHORT).show();
                        } else if (cek_nilai.equals("Tidak ada nilai lebih dari 100")) {
                            Toast.makeText(context, cek_nilai, Toast.LENGTH_SHORT).show();
                        } else {
                            byte[] bytes_nilai = cek_nilai.getBytes();
                            Nilai = Base64.encodeToString(bytes_nilai, Base64.DEFAULT);

                            getJSONChange();

                            v_no_data.remove(vh.getAdapterPosition());
                            v_ket1.remove(vh.getAdapterPosition());
                            v_ket2.remove(vh.getAdapterPosition());
                            v_ket3.remove(vh.getAdapterPosition());
                            v_nilai.remove(vh.getAdapterPosition());
                            v_foto.remove(vh.getAdapterPosition());
                            
                            notifyItemRemoved(vh.getAdapterPosition());

                            // Perintah untuk menutup dialog change nilai
                            mydialog.dismiss();
                        }
                    }
                });

                // Perintah berjalan ketika button delete di klik
                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = v_no_data.get(vh.getAdapterPosition());
                        byte[] bytes_data = data.getBytes();
                        data = Base64.encodeToString(bytes_data, Base64.DEFAULT);

                        // Perintah untuk menutup dialog change nilai
                        mydialog.dismiss();
                        // Perintah untuk menampilkan dialog delete nilai
                        d_delete.show();

                        // Perintah berjalan ketika button yes diklik
                        btn_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getJSONDelete();

                                v_no_data.remove(vh.getAdapterPosition());
                                v_ket1.remove(vh.getAdapterPosition());
                                v_ket2.remove(vh.getAdapterPosition());
                                v_ket3.remove(vh.getAdapterPosition());
                                v_nilai.remove(vh.getAdapterPosition());
                                v_foto.remove(vh.getAdapterPosition());
                                
                                notifyItemRemoved(vh.getAdapterPosition());

                                // Perintah untuk menutup dialog delete nilai
                                d_delete.dismiss();
                            }
                        });

                        btn_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Perintah untuk menutup dialog delete nilai
                                d_delete.dismiss();
                            }
                        });
                    }
                });
            }
        });

        return vh;
    }

    // Metode untuk menuju ke url change_nilai.php
    public String sendGetRequestChange() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://eduscotest.educationscoring.com/Guru/change_nilai.php?nodata=" + data + "&nilai=" + Nilai);

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

    // Metode untuk menuju ke url delete_nilai.php
    public String sendGetRequestDelete() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://www.eduscotest.educationscoring.com/Guru/delete_nilai.php?nodata=" + data);

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

    // Metode untuk mengubah nilai di server
    private void getJSONChange() {
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
                String s = sendGetRequestChange();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // Metode untuk menghapus nilai di server
    private void getJSONDelete() {
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
                String s = sendGetRequestDelete();
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tV_no_data.setText(v_no_data.get(position));
        holder.tV_ket1.setText(v_ket1.get(position));
        holder.tV_ket2.setText(v_ket2.get(position));
        holder.tV_ket3.setText(v_ket3.get(position));
        holder.tV_Nilai.setText(v_nilai.get(position));

        // Perintah untuk memasang foto di ImageView
        Picasso.with(context)
                .load(v_foto.get(position))
                .resize(150, 150)
                .centerCrop()
                .transform(new RoundedTransformation(75, 0))
                .into(holder.iV_foto);
    }

    @Override
    public int getItemCount() {
        return v_no_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item_siswa;
        public TextView tV_no_data, tV_ket1, tV_ket2, tV_ket3, tV_Nilai;
        public ImageView iV_foto;

        public ViewHolder(View v) {
            super(v);
            item_siswa = (LinearLayout) v.findViewById(R.id.item_siswa);
            tV_no_data = (TextView) v.findViewById(R.id.tV_no_data);
            tV_ket1 = (TextView) v.findViewById(R.id.tV_ket1);
            tV_ket2 = (TextView) v.findViewById(R.id.tV_ket2);
            tV_ket3 = (TextView) v.findViewById(R.id.tV_ket3);
            tV_Nilai = (TextView) v.findViewById(R.id.tV_nilai);
            iV_foto = (ImageView) v.findViewById(R.id.iV_foto);
        }
    }
}
