<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	$date = base64_decode($_GET['date']);
	
	$sql = "SELECT AES_Encrypt(no_data, 'homenodata') AS no_data, AES_Encrypt(NIS,'homenis') AS NIS, AES_Encrypt(nama_siswa,'homenamasiswa') AS nama_siswa, AES_Encrypt(Kelas, 'homekelas') AS Kelas, AES_Encrypt (mapel_singkat, 'homemapel') AS Matapelajaran, AES_Encrypt(SMT, 'homesmt') AS Semester, AES_Encrypt(Jenis_nilai, 'homejenisnilai') AS Jenis_nilai, AES_Encrypt(NilaiKe, 'homenilaike') AS NilaiKe, AES_Encrypt(Foto, 'homefoto') AS Foto, AES_Encrypt(nilai, 'homenilai') AS nilai FROM daftar_nilai NATURAL JOIN daftar_siswa NATURAL JOIN daftar_kelas NATURAL JOIN daftar_mapel NATURAL JOIN daftar_semester NATURAL JOIN jenis_nilai WHERE NIG = $nig AND DATE(TIME_MODIFIED) = DATE('$date') ORDER BY TIME_MODIFIED DESC;";
	
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