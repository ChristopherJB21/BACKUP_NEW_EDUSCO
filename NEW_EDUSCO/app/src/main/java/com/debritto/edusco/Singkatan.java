package com.debritto.edusco;

public class Singkatan {

    public static String menyingkatMapel(String mapel_decode) throws Exception {
        try {
            String matapelajaran = null;

            if (mapel_decode.equals("Pendidikan Agama dan Budi Pekerti")) {
                matapelajaran = "Agama";
            } else if (mapel_decode.equals("Pendidikan Pancasila dan Kewarganegaraan")) {
                matapelajaran = "PPKN";
            } else if (mapel_decode.equals("Bahasa Indonesia")) {
                matapelajaran = "B. Indo";
            } else if (mapel_decode.equals("Matematika")) {
                matapelajaran = "Mat. Wajib";
            } else if (mapel_decode.equals("Sejarah Indonesia")) {
                matapelajaran = "Sej. Indo";
            } else if (mapel_decode.equals("Bahasa Inggris")) {
                matapelajaran = "B. Inggris";
            } else if (mapel_decode.equals("Seni Rupa")) {
                matapelajaran = "Senrup";
            } else if (mapel_decode.equals("Pendidikan jasmani, OR, dan Kesehatan")) {
                matapelajaran = "PJOK";
            } else if (mapel_decode.equals("Prakarya dan Kewirausahaan")) {
                matapelajaran = "PKWU";
            } else if (mapel_decode.equals("Matematika Minat")) {
                matapelajaran = "Mat. Minat";
            } else if (mapel_decode.equals("Biologi")) {
                matapelajaran = "Biologi";
            } else if (mapel_decode.equals("Fisika")) {
                matapelajaran = "Fisika";
            } else if (mapel_decode.equals("Kimia")) {
                matapelajaran = "Kimia";
            } else if (mapel_decode.equals("Geografi")) {
                matapelajaran = "Geografi";
            } else if (mapel_decode.equals("Sejarah Minat")) {
                matapelajaran = "Sej. Minat";
            } else if (mapel_decode.equals("Sosiologi")) {
                matapelajaran = "Sosiologi";
            } else if (mapel_decode.equals("Ekonomi")) {
                matapelajaran = "Ekonomi";
            } else if (mapel_decode.equals("Bahasa dan Sastra Indonesia")) {
                matapelajaran = "Sas. Indo";
            } else if (mapel_decode.equals("Bahasa Perancis")) {
                matapelajaran = "B. Perancis";
            } else if (mapel_decode.equals("Bahasa dan Sastra Inggris")) {
                matapelajaran = "Sas Inggris";
            } else if (mapel_decode.equals("Antropologi")) {
                matapelajaran = "Antropologi";
            } else if (mapel_decode.equals("Bahasa Jerman")) {
                matapelajaran = "B. Jerman";
            } else if (mapel_decode.equals("Bahasa Inggris Minat")) {
                matapelajaran = "B. Ing Minat";
            } else if (mapel_decode.equals("Bahasa Mandarin")) {
                matapelajaran = "B. Mandarin";
            } else if (mapel_decode.equals("Ekonomi Minat")) {
                matapelajaran = "Eko Minat";
            } else if (mapel_decode.equals("Seni Teater")) {
                matapelajaran = "Teater";
            } else if (mapel_decode.equals("Pendidikan Nilai")) {
                matapelajaran = "P. Nilai";
            } else if (mapel_decode.equals("Bimbingan Konseling")) {
                matapelajaran = "BK";
            }
            return String.valueOf(matapelajaran);

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public static String menyingkatGuru(String namaguru_decode) throws Exception {
        try {
            String namaguru = null;

            if (namaguru_decode.equals("D. Pujiyono, S.Fk")){
                namaguru = "Puji";
            } else if (namaguru_decode.equals("Y.Bambang Maryono, S.S")){
                namaguru = "Maryono";
            } else if (namaguru_decode.equals("Nova Tri Utomo, S.Pd")){
                namaguru = "Nova";
            } else if (namaguru_decode.equals("F. Dimas D, S.Pd")){
                namaguru = "Dimas";
            } else if (namaguru_decode.equals("Rio Prabowo, S.Pd.")){
                namaguru = "Rio";
            } else if (namaguru_decode.equals("M. Dwi Prasetyo, S.S")){
                namaguru = "Martin";
            } else if (namaguru_decode.equals("Y. Sumardiyanto, S.Pd")){
                namaguru = "Sumar";
            } else if (namaguru_decode.equals("A. Prima Adhi Putra, S.Pd.")){
                namaguru = "Prima";
            } else if (namaguru_decode.equals("Drs. St. Kartono, M. Hum")){
                namaguru = "Kartono";
            } else if (namaguru_decode.equals("Ag. Prih Adiartanto, S.Pd., M. Ed")){
                namaguru = "Prih";
            } else if (namaguru_decode.equals("D. Sanusi SH Murti, S.Pd")){
                namaguru = "Sanusi";
            } else if (namaguru_decode.equals("Ant. Didik Kristantohadi, S.Pd")){
                namaguru = "Didik";
            } else if (namaguru_decode.equals("Karina Heksari, S.Pd.")){
                namaguru = "Karin";
            } else if (namaguru_decode.equals("Prima Ibnu Wijaya, S.Pd")){
                namaguru = "Prima";
            } else if (namaguru_decode.equals("Drs. B. Widi Nugroho, M. Ed")){
                namaguru = "Widi";
            } else if (namaguru_decode.equals("Ag. Triwinanta, S.Pd")){
                namaguru = "Triwin";
            } else if (namaguru_decode.equals("P. Gandhi Prastowo, S.Pd")){
                namaguru = "Gandhi";
            } else if (namaguru_decode.equals("A. Denny Setia Utama, S.Pd")){
                namaguru = "Denny";
            } else if (namaguru_decode.equals("Rosalia Suryani , S.Pd")){
                namaguru = "Ros";
            } else if (namaguru_decode.equals("Pauline Rian Kunthi, S.Pd.")){
                namaguru = "Ipo";
            } else if (namaguru_decode.equals("R. Anggara Tri Laksana, S.Pd.")){
                namaguru = "Anggara";
            } else if (namaguru_decode.equals("HJ. Sriyanto, S.Pd")){
                namaguru = "Joyo";
            } else if (namaguru_decode.equals("FX. Catur Supatmono, M.Pd.")){
                namaguru = "Catur";
            } else if (namaguru_decode.equals("Drs. Th. Sukristiyono")){
                namaguru = "Sukris";
            } else if (namaguru_decode.equals("Agnes Reswari Ingkansari, M.Pd")){
                namaguru = "Agnes";
            } else if (namaguru_decode.equals("E. Jevina Lintang Puspita, S. Pd")){
                namaguru = "Emil";
            } else if (namaguru_decode.equals("Dra. Endah Sulastriningsih")){
                namaguru = "Endah";
            } else if (namaguru_decode.equals("Dra. M. Th. Nanik Ismarjiati")){
                namaguru = "Nanik";
            } else if (namaguru_decode.equals("Fr. Ratna Dwi Astuti, M. Pd")){
                namaguru = "Ratna";
            } else if (namaguru_decode.equals("Ir. Sebastiana Susiani")){
                namaguru = "Susi";
            } else if (namaguru_decode.equals("R. Arifin Nugroho, S.Si., M.Pd.")){
                namaguru = "Arifin";
            } else if (namaguru_decode.equals("MM. Sudewi Fajarina, S.Si")){
                namaguru = "Dewi";
            } else if (namaguru_decode.equals("Ign. Agus Yulianto, S.Pd, M.Pd.")){
                namaguru = "Yuli";
            } else if (namaguru_decode.equals("Drs. H. Suradi")){
                namaguru = "Suradi";
            } else if (namaguru_decode.equals("Dra. C. Suci Puji Setyowati")){
                namaguru = "Suci";
            } else if (namaguru_decode.equals("YB. Aprin Sugeng Jatmiko, S.Pd")){
                namaguru = "Aprin";
            } else if (namaguru_decode.equals("H. Franky Ari Andri Prianto, S.Pd")){
                namaguru = "Franky";
            } else if (namaguru_decode.equals("FX. Agus Hariyanto, S.Pd., SE")){
                namaguru = "Agus";
            } else if (namaguru_decode.equals("Y. Iwan Prasetyo, S.Pd.")){
                namaguru = "Iwan";
            } else if (namaguru_decode.equals("Sri Endah Setia Rini, S.S., M.Pd.")){
                namaguru = "Sri";
            } else if (namaguru_decode.equals("V. Yenny Indrayanti, SH.")){
                namaguru = "Yenny";
            } else if (namaguru_decode.equals("Bintari Damanin Sani, S.Pd")){
                namaguru = "Binta";
            } else if (namaguru_decode.equals("Iwan Susanto, S.Pd")){
                namaguru = "Iwan";
            } else if (namaguru_decode.equals("Novianto Eka Saputra, S.Pd.")){
                namaguru = "Novi";
            } else if (namaguru_decode.equals("Y. David Mahadjatun, S. Pd")){
                namaguru = "David";
            } else if (namaguru_decode.equals("Y. Hendrabudi Prabawa, S.Pd.")){
                namaguru = "Hendra";
            } else if (namaguru_decode.equals("Chr. Danang Wahyu Prasetyo, S.Or")){
                namaguru = "Danang";
            } else if (namaguru_decode.equals("E. Megia Nofita, S.T.")){
                namaguru = "Megi";
            } else if (namaguru_decode.equals("H. Heri Istiyanto, S.Si., M.Kom.")){
                namaguru = "Heri";
            } else if (namaguru_decode.equals("Br. Y. Triyana  SJ")){
                namaguru = "Tri";
            } else if (namaguru_decode.equals("Fr. Bangkit Adi Nugroho SJ")){
                namaguru = "Bangkit";
            } else if (namaguru_decode.equals("Rm. N. Devianto Fajar Trinugroho, SJ")){
                namaguru = "Fajar";
            } else if (namaguru_decode.equals("St. Arintoko, S.Pd.")){
                namaguru = "Arintoko";
            } else if (namaguru_decode.equals("Antonita Ardian Nugraheni, S.Pd., M.A.")){
                namaguru = "Nita";
            } else if (namaguru_decode.equals("Okdarina Krisputranti, S.Pd")){
                namaguru = "Okda";
            } else {
                namaguru = "noname";
            }

                return String.valueOf(namaguru);

        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }
}
