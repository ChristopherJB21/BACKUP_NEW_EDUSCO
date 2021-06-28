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

public class RVA_ViewNilai extends RecyclerView.Adapter<RVA_ViewNilai.ViewHolder> {

    private ArrayList<String> v_ket1, v_ket2, v_foto_guru, v_nilai;
    private Context context;

    // Perintah untuk mendapatkan data dari ViewSiswaFragment
    public RVA_ViewNilai(Context mContext, ArrayList<String> mguru, ArrayList<String> mfotoguru,
                         ArrayList<String> mmapel, ArrayList<String> mNilai) {
        context = mContext;
        v_ket1 = mguru;
        v_foto_guru = mfotoguru;
        v_ket2 = mmapel;
        v_nilai = mNilai;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_view_nilai, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_ket1.setText(v_ket1.get(position));
        holder.tv_ket2.setText(v_ket2.get(position));
        holder.tvNilai.setText(v_nilai.get(position));

        // Perintah untuk memasang foto di ImageView
        Picasso.with(context)
                .load(v_foto_guru.get(position))
                .resize(150, 150)
                .centerCrop()
                .transform(new RoundedTransformation(75, 0))
                .into(holder.iV_foto);
    }

    @Override
    public int getItemCount() {
        return v_ket1.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_ket1, tv_ket2, tvNilai;
        public ImageView iV_foto;

        public ViewHolder(View v) {
            super(v);
            tv_ket1 = (TextView) v.findViewById(R.id.tV_ket1);
            iV_foto = (ImageView) v.findViewById(R.id.iV_foto);
            tv_ket2 = (TextView) v.findViewById(R.id.tV_ket2);
            tvNilai = (TextView) v.findViewById(R.id.tV_nilai);
            iV_foto = (ImageView) v.findViewById(R.id.iV_foto);
        }
    }
}
