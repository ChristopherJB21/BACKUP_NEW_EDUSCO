<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$semester = base64_decode($_GET['semester']);
	$kode_mapel = base64_decode($_GET['kode_mapel']);
	$kode_nilai = base64_decode($_GET['kode_nilai']);
	
	$sql = "SELECT AES_Encrypt(nama_panggilan_guru, 'siswanamaguru') AS nama_guru, AES_ENCRYPT(Foto,'siswafotoguru') AS Foto, AES_Encrypt(mapel_singkat, 'siswamapel') AS mapel, AES_Encrypt(Jenis_nilai, 'siswajenisnilai') AS jenis_nilai, AES_Encrypt(SMT, 'siswasemester') AS Semester, AES_Encrypt(NilaiKe, 'siswanilaike') AS NilaiKe, AES_Encrypt(nilai,'siswanilai') AS nilai FROM daftar_nilai NATURAL JOIN daftar_guru NATURAL JOIN daftar_mapel NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai WHERE NIS = $nis AND kode_mapel = $kode_mapel AND kode_nilai = $kode_nilai AND Semester = $semester ORDER BY TIME_MODIFIED DESC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
            base64_encode(nama_guru)=>base64_encode($row['nama_guru']),
            base64_encode(foto_guru)=>base64_encode($row['Foto']),
            base64_encode(matapelajaran)=>base64_encode($row['mapel']),
            base64_encode(semester)=>base64_encode($row['Semester']),
            base64_encode(jenisnilai)=>base64_encode($row['jenis_nilai']),
            base64_encode(nilaiKe)=>base64_encode($row['NilaiKe']),
            base64_encode(nilai)=>base64_encode($row['nilai']),
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>