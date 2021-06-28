<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$kode_kelas = base64_decode($_GET['kode_kelas']);
	$kode_mapel = base64_decode($_GET['kode_mapel']);
	$nig = base64_decode($_GET['nig']);
	$kode_nilai = base64_decode($_GET['kode_nilai']);
	$NilaiKe = base64_decode($_GET['NilaiKe']);
	$Semester = base64_decode($_GET['Semester']);
	
	$sql = "SELECT AES_Encrypt(no_data, 'changenodata') AS no_data, AES_Encrypt(NIS,'changenis') AS NIS, AES_Encrypt(nama_siswa,'changenamasiswa') AS nama_siswa, AES_Encrypt(Kelas, 'changekelas') AS Kelas, AES_Encrypt (mapel_singkat, 'changemapel') AS Matapelajaran, AES_Encrypt(SMT, 'changesmt') AS Semester, AES_Encrypt(Jenis_nilai, 'changejenisnilai') AS Jenis_nilai, AES_Encrypt(NilaiKe, 'changenilaike') AS NilaiKe, AES_Encrypt(Foto, 'changefoto') AS Foto, AES_Encrypt(nilai, 'changenilai') AS nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_kelas NATURAL JOIN daftar_mapel NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai WHERE NIS = $nis AND kode_kelas = $kode_kelas AND kode_mapel = $kode_mapel AND NIG = $nig AND kode_nilai = $kode_nilai AND NilaiKe = $NilaiKe AND Semester = $Semester ORDER BY TIME_MODIFIED DESC";
	
	$r = mysqli_query($id_mysql,$sql);
	$result = array();
	
	while($row = mysqli_fetch_array($r)){
	array_push($result,array(
	    base64_encode(no_data)=>base64_encode($row['no_data']),
		base64_encode(NIS)=>base64_encode($row['NIS']),
		base64_encode(nama_siswa)=>base64_encode($row['nama_siswa']),
		base64_encode(kelas)=>base64_encode($row['Kelas']),
		base64_encode(Mapel)=>base64_encode($row['Matapelajaran']),
		base64_encode(Semester)=>base64_encode($row['Semester']),
		base64_encode(Jenis_nilai)=>base64_encode($row['Jenis_nilai']),
		base64_encode(NilaiKe)=>base64_encode($row['NilaiKe']),
		base64_encode(Fotosiswa)=>base64_encode($row['Foto']),
		base64_encode(nilai)=>base64_encode($row['nilai'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>