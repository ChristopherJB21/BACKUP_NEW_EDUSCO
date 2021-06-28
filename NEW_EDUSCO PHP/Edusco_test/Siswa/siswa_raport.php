<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	
	$sql = "SELECT DISTINCT kode_mapel, kode_nilai FROM daftar_nilai WHERE NIS = $nis AND kode_nilai BETWEEN 1 AND 3 ORDER BY Semester ASC, kode_mapel ASC, kode_nilai ASC;";
	$sqlkelas = "SELECT DISTINCT kode_kelas FROM daftar_nilai WHERE NIS = $nis;";
	
	$r = mysqli_query($id_mysql,$sql);
	$rkelas = mysqli_query($id_mysql,$sqlkelas);
	
	$rowkelas = mysqli_fetch_array($rkelas);
	
    $kelas = $row['kode_kelas'];
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
        $mapel = $row['kode_mapel'];
        $kodenilai = $row['kode_nilai'];
        
        if($kodenilai == "1"){
            $sql1smt1 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
            
            $sql1smt2 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
        
            $rsmt1  = mysqli_query($id_mysql,$sql1smt1);
            $rsmt2  = mysqli_query($id_mysql,$sql1smt2);
            
            $rowsmt1 = mysqli_fetch_array($rsmt1);
            $rowsmt2 = mysqli_fetch_array($rsmt2);
            
            $NHsmt1 = $rowsmt1['Nilai'];
            $NHsmt2 = $rowsmt2['Nilai'];
            
            $sqlUTSsmt1 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $mapel and kode_nilai = 98;";
            $sqlUASsmt1 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $mapel and kode_nilai = 99;";  
            
            $sqlUTSsmt2 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $mapel and kode_nilai = 98;";
            $sqlUASsmt2 = "SELECT AVG(nilai) AS Nilai FROM daftar_nilai WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $mapel and kode_nilai = 99;";
            
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
            
            if($kelas >= 1 && $kelas <= 9){
                $sqlkkm101 = "SELECT `kkm_pengetahuan` FROM daftar_kkm_10 WHERE kode_mapel = $mapel;";
                
                $rkkm101 = mysqli_query($id_mysql,$sqlkkm101);
                $rowkkm101 = mysqli_fetch_array($rkkm101);
                
                $kkm1 = $rowkkm101['kkm_pengetahuan'];
                
            } else if ($kelas >= 10 && $kelas <= 18){
                $sqlkkm111 = "SELECT `kkm_pengetahuan` FROM daftar_kkm_11 WHERE kode_mapel = $mapel;";
                
                $rkkm111 = mysqli_query($id_mysql,$sqlkkm111);
                $rowkkm111 = mysqli_fetch_array($rkkm111);
                
                $kkm1 = $rowkkm111['kkm_pengetahuan'];
                
            } else {
                $sqlkkm121 = "SELECT `kkm_pengetahuan` FROM daftar_kkm_12 WHERE kode_mapel = $mapel;";
                
                $rkkm121 = mysqli_query($id_mysql,$sqlkkm121);
                $rowkkm121 = mysqli_fetch_array($rkkm121);
                
                $kkm1 = $rowkkm121['kkm_pengetahuan'];
            }
            
            if ($Nilaismt1 >= $kkm1){
                $Tuntassmt1 = "Tuntas";
            } else {
                $Tuntassmt1 = "Tidak Tuntas";
            }
            
            if ($Nilaismt2 >= $kkm1){
                $Tuntassmt2 = "Tuntas";
            } else {
                $Tuntassmt2 = "Tidak Tuntas";
            }
            
            if ($Nilaismt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($rowsmt1['Semester']),
                base64_encode(kode_mapel)=>base64_encode($rowsmt1['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($rowsmt1['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($Nilaismt1),
                base64_encode(nama_guru)=>base64_encode($rowsmt1['nama_guru']),
                base64_encode(Foto)=>base64_encode($rowsmt1['Foto']),
                base64_encode(KKM)=>base64_encode($kkm1),
                base64_encode(Tuntas)=>base64_encode($Tuntassmt1)
                ));
            }
            
            if ($Nilaismt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($rowsmt2['Semester']),
                base64_encode(kode_mapel)=>base64_encode($rowsmt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($rowsmt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($Nilaismt2),
                base64_encode(nama_guru)=>base64_encode($rowsmt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($rowsmt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm1),
                base64_encode(Tuntas)=>base64_encode($Tuntassmt2)
                ));
            }
            
            if ($Nilaismt1 != NULL && $Nilaismt2 != NULL){
                $NA = ($Nilaismt1 + $Nilaismt2) / 2;
                
                if ($NA >= $kkm1){
                    $TuntasNA = "Tuntas";
                } else {
                    $TuntasNA = "Tidak Tuntas";
                }
            
            
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_mapel)=>base64_encode($rowsmt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($rowsmt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NA),
                base64_encode(nama_guru)=>base64_encode($rowsmt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($rowsmt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm1),
                base64_encode(Tuntas)=>base64_encode($TuntasNA)
                ));
            }
            
        } else if ($kodenilai == "2"){
            $sql2smt1 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
            
            $sql2smt2 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
        
            $r2smt1  = mysqli_query($id_mysql,$sql2smt1);
            $r2smt2  = mysqli_query($id_mysql,$sql2smt2);
            
            $row2smt1 = mysqli_fetch_array($r2smt1);
            $row2smt2 = mysqli_fetch_array($r2smt2);
            
            $NH2smt1 = $row2smt1['Nilai'];
            $NH2smt2 = $row2smt2['Nilai'];
            
            if($kelas >= 1 && $kelas <= 9){
                $sqlkkm102 = "SELECT `kkm_keterampilan` FROM daftar_kkm_10 WHERE kode_mapel = $mapel;";
                
                $rkkm102 = mysqli_query($id_mysql,$sqlkkm102);
                $rowkkm102 = mysqli_fetch_array($rkkm102);
                
                $kkm2 = $rowkkm102['kkm_keterampilan'];
                
            } else if ($kelas >= 10 && $kelas <= 18){
                $sqlkkm112 = "SELECT `kkm_keterampilan` FROM daftar_kkm_11 WHERE kode_mapel = $mapel;";
                
                $rkkm112 = mysqli_query($id_mysql,$sqlkkm112);
                $rowkkm112 = mysqli_fetch_array($rkkm112);
                
                $kkm2 = $rowkkm112['kkm_keterampilan'];
                
            } else {
                $sqlkkm122 = "SELECT `kkm_keterampilan` FROM daftar_kkm_12 WHERE kode_mapel = $mapel;";
                
                $rkkm122 = mysqli_query($id_mysql,$sqlkkm122);
                $rowkkm122 = mysqli_fetch_array($rkkm122);
                
                $kkm2 = $rowkkm122['kkm_keterampilan'];
            }
            
            if ($NH2smt1 >= $kkm2){
                $Tuntas2smt1 = "Tuntas";
            } else {
                $Tuntas2smt1 = "Tidak Tuntas";
            }
            
            if ($NH2smt2 >= $kkm1){
                $Tuntas2smt2 = "Tuntas";
            } else {
                $Tuntas2smt2 = "Tidak Tuntas";
            }
            
            if ($NH2smt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row2smt1['Semester']),
                base64_encode(kode_mapel)=>base64_encode($row2smt1['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row2smt1['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NH2smt1),
                base64_encode(nama_guru)=>base64_encode($row2smt1['nama_guru']),
                base64_encode(Foto)=>base64_encode($row2smt1['Foto']),
                base64_encode(KKM)=>base64_encode($kkm2),
                base64_encode(Tuntas)=>base64_encode($Tuntas2smt1)
                ));
            }
            
            if ($NH2smt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row2smt2['Semester']),
                base64_encode(kode_mapel)=>base64_encode($row2smt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row2smt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NH2smt2),
                base64_encode(nama_guru)=>base64_encode($row2smt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($row2smt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm2),
                base64_encode(Tuntas)=>base64_encode($Tuntas2smt2)
                ));
            }
            
            if ($NH2smt1 != NULL && $NH2smt2 != NULL){
                $NA2 = ($NH2smt1 + $NH2smt2) / 2;
                
                if ($NA2 >= $kkm2){
                    $TuntasNA2 = "Tuntas";
                } else {
                    $TuntasNA2 = "Tidak Tuntas";
                }
            
            
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_mapel)=>base64_encode($row2smt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row2smt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NA2),
                base64_encode(nama_guru)=>base64_encode($row2smt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($rowsmt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm2),
                base64_encode(Tuntas)=>base64_encode($TuntasNA2)
                ));
            }
        } else {
            $sql3smt1 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 1 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
            
            $sql3smt2 = "SELECT SMT AS Semester, AES_Encrypt(matapelajaran,'raportmapel') AS kode_mapel, AES_Encrypt(jenis_nilai, 'raportjenis') AS kode_nilai, AVG(nilai) AS Nilai, AES_Encrypt(nama_guru, 'raportguru') AS nama_guru, AES_Encrypt(Foto, 'raportfotoguru') AS Foto FROM daftar_nilai NATURAL JOIN daftar_semester NATURAL JOIN daftar_mapel NATURAL JOIN jenis_nilai NATURAL JOIN daftar_guru WHERE NIS = $nis AND Semester = 2 AND kode_mapel = $mapel and kode_nilai = $kodenilai;";
        
            $r3smt1  = mysqli_query($id_mysql,$sql3smt1);
            $r3smt2  = mysqli_query($id_mysql,$sql3smt2);
            
            $row3smt1 = mysqli_fetch_array($r3smt1);
            $row3smt2 = mysqli_fetch_array($r3smt2);
            
            $NH3smt1 = $row3smt1['Nilai'];
            $NH3smt2 = $row3smt2['Nilai'];
            
            if($kelas >= 1 && $kelas <= 9){
                $sqlkkm103 = "SELECT `kkm_sikap` FROM daftar_kkm_10 WHERE kode_mapel = $mapel;";
                
                $rkkm103 = mysqli_query($id_mysql,$sqlkkm103);
                $rowkkm103 = mysqli_fetch_array($rkkm103);
                
                $kkm3 = $rowkkm103['kkm_sikap'];
                
            } else if ($kelas >= 10 && $kelas <= 18){
                $sqlkkm113 = "SELECT `kkm_sikap` FROM daftar_kkm_11 WHERE kode_mapel = $mapel;";
                
                $rkkm113 = mysqli_query($id_mysql,$sqlkkm113);
                $rowkkm113 = mysqli_fetch_array($rkkm113);
                
                $kkm3 = $rowkkm113['kkm_sikap'];
                
            } else {
                $sqlkkm133 = "SELECT `kkm_sikap` FROM daftar_kkm_12 WHERE kode_mapel = $mapel;";
                
                $rkkm133 = mysqli_query($id_mysql,$sqlkkm133);
                $rowkkm133 = mysqli_fetch_array($rkkm133);
                
                $kkm3 = $rowkkm133['kkm_sikap'];
            }
            
            if ($NH3smt1 >= $kkm3){
                $Tuntas3smt1 = "Tuntas";
            } else {
                $Tuntas3smt1 = "Tidak Tuntas";
            }
            
            if ($NH3smt2 >= $kkm1){
                $Tuntas3smt2 = "Tuntas";
            } else {
                $Tuntas3smt2 = "Tidak Tuntas";
            }
            
            if ($NH3smt1 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row3smt1['Semester']),
                base64_encode(kode_mapel)=>base64_encode($row3smt1['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row3smt1['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NH3smt1),
                base64_encode(nama_guru)=>base64_encode($row3smt1['nama_guru']),
                base64_encode(Foto)=>base64_encode($row3smt1['Foto']),
                base64_encode(KKM)=>base64_encode($kkm3),
                base64_encode(Tuntas)=>base64_encode($Tuntas3smt1)
                ));
            }
            
            if ($NH3smt2 != NULL){
            array_push($result,array(
                base64_encode(Semester)=>base64_encode($row3smt2['Semester']),
                base64_encode(kode_mapel)=>base64_encode($row3smt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row3smt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NH3smt2),
                base64_encode(nama_guru)=>base64_encode($row3smt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($row3smt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm3),
                base64_encode(Tuntas)=>base64_encode($Tuntas3smt2)
                ));
            }
            
            if ($NH3smt1 != NULL && $NH3smt2 != NULL){
                $NA3 = ($NH3smt1 + $NH3smt2) / 2;
                
                if ($NA3 >= $kkm3){
                    $TuntasNA3 = "Tuntas";
                } else {
                    $TuntasNA3 = "Tidak Tuntas";
                }
            
            
            array_push($result,array(
                base64_encode(Semester)=>base64_encode("Nilai Akhir"),
                base64_encode(kode_mapel)=>base64_encode($row3smt2['kode_mapel']),
                base64_encode(kode_nilai)=>base64_encode($row3smt2['kode_nilai']),
                base64_encode(Nilai)=>base64_encode($NA3),
                base64_encode(nama_guru)=>base64_encode($row3smt2['nama_guru']),
                base64_encode(Foto)=>base64_encode($rowsmt2['Foto']),
                base64_encode(KKM)=>base64_encode($kkm3),
                base64_encode(Tuntas)=>base64_encode($TuntasNA3)
                ));
            }
        }
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>