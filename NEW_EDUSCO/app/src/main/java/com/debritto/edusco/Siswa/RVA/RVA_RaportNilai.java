package com.debritto.edusco.Siswa.RVA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.debritto.edusco.Photo.RoundedTransformation;
import com.debritto.edusco.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RVA_RaportNilai extends RecyclerView.Adapter<RVA_RaportNilai.ViewHolder> {

    private ArrayList<String> v_ket1, v_ket2, v_ket3, Nilai, FotoGuru;
    private Context context;

    // Metode untuk mendapatkan data dari RaportSiswaFragment
    public RVA_RaportNilai(Context mContext, ArrayList<String> mket1, ArrayList<String> mket2,
                           ArrayList<String> mket3, ArrayList<String> mNilai,
                           ArrayList<String> mFotoGuru) {
        context = mContext;
        v_ket1 = mket1;
        v_ket2 = mket2;
        v_ket3 = mket3;
        Nilai = mNilai;
        FotoGuru = mFotoGuru;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_raport_nilai, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvket1.setText(v_ket1.get(position));
        holder.tvket2.setText(v_ket2.get(position));
        holder.tvket3.setText(v_ket3.get(position));
        holder.tvNilai.setText(Nilai.get(position));

        Picasso.with(context)
                .load(FotoGuru.get(position))
                .resize(150, 150)
                .centerCrop()
                .transform(new RoundedTransformation(75, 0))
                .into(holder.iV_Foto);
    }

    @Override
    public int getItemCount() {
        return Nilai.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvket1, tvket2, tvket3, tvNilai;
        public ImageView iV_Foto;

        public ViewHolder(View v) {
            super(v);
            tvket1 = (TextView) v.findViewById(R.id.tV_ket1);
            tvket2 = (TextView) v.findViewById(R.id.tV_ket2);
            tvket3 = (TextView) v.findViewById(R.id.tV_ket3);
            tvNilai = (TextView) v.findViewById(R.id.tV_nilai);
            iV_Foto = (ImageView) v.findViewById(R.id.iV_foto);
        }
    }
}
