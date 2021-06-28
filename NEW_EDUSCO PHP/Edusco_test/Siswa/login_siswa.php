<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$password = base64_decode($_GET['password']);
	
	$sql = "SELECT AES_Encrypt(NIS, 'loginsiswanis') AS NIS, AES_Encrypt(nama_siswa, 'loginsiswanama') AS nama_siswa, AES_Encrypt(kelas,'loginsiswakelas') AS kelas, AES_Encrypt(no_presensi, 'loginpresensi') AS no_presensi FROM `daftar_siswa` NATURAL JOIN `daftar_kelas` WHERE `NIS` = $nis AND `Password` = AES_ENCRYPT('$password','ranggajelek');";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	$row = mysqli_fetch_array($r);
	array_push($result,array(
		base64_encode(NIS)=>base64_encode($row['NIS']),
		base64_encode(nama_siswa)=>base64_encode($row['nama_siswa']),
		base64_encode(kelas)=>base64_encode($row['kelas']),
		base64_encode(no_presensi)=>base64_encode($row['no_presensi'])
		));
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>