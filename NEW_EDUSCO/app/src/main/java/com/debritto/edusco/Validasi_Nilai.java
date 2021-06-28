package com.debritto.edusco;

public class Validasi_Nilai {

    // Metode untuk validasi nilai
    public static String cekNilai(String nilai) throws Exception {
        try {
            if (nilai.isEmpty()) {
                return "Masukkan nilai";
            } else {
                int cek_nilai = Integer.parseInt(nilai);

                if (cek_nilai > 100) {
                    return "Tidak ada nilai lebih dari 100";
                } else {
                    return String.valueOf(cek_nilai);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
