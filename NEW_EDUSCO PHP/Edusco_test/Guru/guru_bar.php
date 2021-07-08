<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	
	$sql = "SELECT DISTINCT NIS, kode_mapel, kode_nilai FROM daftar_nilai NATURAL JOIN daftar_siswa WHERE nig = $nig ORDER BY kode_kelas ASC, No_Presensi ASC, kode_mapel ASC, Semester ASC, kode_nilai ASC;";

	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
        $nis = $row['NIS'];
        $kode_mapel = $row['kode_mapel'];
        $kode_nilai = $row['kode_nilai'];
        
        if($kode_nilai == "1"){
            $sql1smt1 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
            
            $sql1smt2 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
        
            $rsmt1  = mysqli_query($id_mysql,$sql1smt1);
            $rsmt2  = mysqli_query($id_mysql,$sql1smt2);
            
            $rowsmt1 = mysqli_fetch_array($rsmt1);
            $rowsmt2 = mysqli_fetch_array($rsmt2);
            
            $NHsmt1 = $rowsmt1['Nilai'];
            $NHsmt2 = $rowsmt2['Nilai'];
            
            $sqlUTSsmt1 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $kode_mapel and kode_nilai = 98;";
            $sqlUASsmt1 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $kode_mapel and kode_nilai = 99;";  
            
            $sqlUTSsmt2 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $kode_mapel and kode_nilai = 98;";
            $sqlUASsmt2 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $kode_mapel and kode_nilai = 99;";
            
            $rUTSsmt1 = mysqli_query($id_mysql,$sqlUTSsmt1);
            $rUASsmt1 = mysqli_query($id_mysql,$sqlUASsmt1);
            
            $rUTSsmt2 = mysqli_query($id_mysql,$sqlUTSsmt2);
            $rUASsmt2 = mysqli_query($id_mysql,$sqlUASsmt2);
            
            $rowUTSsmt1 = mysqli_fetch_array($rUTSsmt1);
            $rowUASsmt1 = mysqli_fetch_array($rUASsmt1);
            
            $rowUTSsmt2 = mysqli_fetch_array($rUTSsmt2);
            $rowUASsmt2 = mysqli_fetch_array($rUASsmt2);
            
            $NUTSsmt1 = $rowUTSsmt1['Nilai'];
            $NUASsmt1 = $rowUASsmt1['Nilai'];
                        
            $NUTSsmt2 = $rowUTSsmt2['Nilai'];
            $NUASsmt2 = $rowUASsmt2['Nilai'];
            
            if($NUTSsmt1 == NULL and $NUASsmt1 == NULL){
                $Nilaismt1 = $NHsmt1;
            } else if ($NUTSsmt1 == NULL){
                $Nilaismt1 = ($NHsmt1 + $NUASsmt1) / 2;
            } else if ($NUASsmt1 == NULL){
                $Nilaismt1 = ($NHsmt1 + $NUTSsmt1) / 2;
            } else {
                $Nilaismt1 = ($NHsmt1 + $NUTSsmt1 + $NUASsmt1) / 3;
            }
            
            if($NUTSsmt2 == NULL and $NUASsmt2 == NULL){
                $Nilaismt2 = $NHsmt2;
            } else if ($NUTSsmt2 == NULL){
                $Nilaismt2 = ($NHsmt2 + $NUASsmt2) / 2;
            } else if ($NUASsmt2 == NULL){
                $Nilaismt2 = ($NHsmt2 + $NUTSsmt2) / 2;
            } else {
                $Nilaismt2 = ($NHsmt2 + $NUTSsmt2 + $NUASsmt2) / 3;
            }
        
            if ($Nilaismt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($rowsmt1['Semester']),
                base64_encode(kode_nilai)=>base64_encode($rowsmt1['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($rowsmt1['Mapel']),
                base64_encode(NIS)=>base64_encode($rowsmt1['NIS']),
                base64_encode(nama_siswa)=>base64_encode($rowsmt1['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($Nilaismt1)
                ));
            }
            
            if ($Nilaismt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($rowsmt2['Semester']),
                base64_encode(kode_nilai)=>base64_encode($rowsmt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($rowsmt2['Mapel']),
                base64_encode(NIS)=>base64_encode($rowsmt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($rowsmt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($Nilaismt2)
                ));
            }
            
            if ($Nilaismt1 != NULL && $Nilaismt2 != NULL){
                
            $NA = ($Nilaismt1 + $Nilaismt2) / 2;
                
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_nilai)=>base64_encode($rowsmt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($rowsmt2['Mapel']),
                base64_encode(NIS)=>base64_encode($rowsmt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($rowsmt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NA)
                ));
            }
            
        } else if ($kode_nilai == "2"){
            $sql2smt1 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
            
            $sql2smt2 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
        
            $r2smt1  = mysqli_query($id_mysql,$sql2smt1);
            $r2smt2  = mysqli_query($id_mysql,$sql2smt2);
            
            $row2smt1 = mysqli_fetch_array($r2smt1);
            $row2smt2 = mysqli_fetch_array($r2smt2);
            
            $NH2smt1 = $row2smt1['Nilai'];
            $NH2smt2 = $row2smt2['Nilai'];
            
            if ($NH2smt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row2smt1['Semester']),
                base64_encode(kode_nilai)=>base64_encode($row2smt1['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row2smt1['Mapel']),
                base64_encode(NIS)=>base64_encode($row2smt1['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row2smt1['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NH2smt1)
                ));
            }
            
            if ($NH2smt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row2smt2['Semester']),
                base64_encode(kode_nilai)=>base64_encode($row2smt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row2smt2['Mapel']),
                base64_encode(NIS)=>base64_encode($row2smt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row2smt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NH2smt2)
                ));
            }
            
            if ($NH2smt1 != NULL && $NH2smt2 != NULL){
                
            $NA2 = ($NH2smt1 + $NH2smt2) / 2;
                
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_nilai)=>base64_encode($row2smt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row2smt2['Mapel']),
                base64_encode(NIS)=>base64_encode($row2smt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row2smt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NA2)
                ));
            }
        } else {
            $sql3smt1 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
            
            $sql3smt2 = "SELECT SMT AS Semester, AES_ENCRYPT(matapelajaran, 'bargurumapel') AS Mapel, AES_ENCRYPT(jenis_nilai, 'bargurujenis') AS jenis_nilai, AES_ENCRYPT(NIS, 'bargurunis') AS NIS, AES_ENCRYPT(nama_siswa,'bargurusiswa') AS nama_siswa, AVG(nilai) AS Nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $kode_mapel and kode_nilai = $kode_nilai;";
        
            $r3smt1  = mysqli_query($id_mysql,$sql3smt1);
            $r3smt2  = mysqli_query($id_mysql,$sql3smt2);
            
            $row3smt1 = mysqli_fetch_array($r3smt1);
            $row3smt2 = mysqli_fetch_array($r3smt2);
            
            $NH3smt1 = $row3smt1['Nilai'];
            $NH3smt2 = $row3smt2['Nilai'];
            
            if ($NH3smt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row3smt1['Semester']),
                base64_encode(kode_nilai)=>base64_encode($row3smt1['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row3smt1['Mapel']),
                base64_encode(NIS)=>base64_encode($row3smt1['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row3smt1['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NH3smt1)
                ));
            }
            
            if ($NH3smt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row3smt2['Semester']),
                base64_encode(kode_nilai)=>base64_encode($row3smt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row3smt2['Mapel']),
                base64_encode(NIS)=>base64_encode($row3smt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row3smt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NH3smt2)
                ));
            }
            
            if ($NH3smt1 != NULL && $NH3smt2 != NULL){
            $NA3 = ($NH3smt1 + $NH3smt2) / 2;
            
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_nilai)=>base64_encode($row3smt2['jenis_nilai']),
                base64_encode(Mapel)=>base64_encode($row3smt2['Mapel']),
                base64_encode(NIS)=>base64_encode($row3smt2['NIS']),
                base64_encode(nama_siswa)=>base64_encode($row3smt2['nama_siswa']),
                base64_encode(Nilai)=>base64_encode($NA3)
                ));
            }
        }
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>