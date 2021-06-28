<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$password = base64_decode($_GET['password']);
	
	$sql = "SELECT NIS FROM daftar_ortu_siswa WHERE NIS = $nis AND Password = AES_ENCRYPT('$password', 'ranggajelek')";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$row = mysqli_fetch_array($r);
	
	$NIS = $row['NIS'];
	
	$sql1 = "SELECT AES_Encrypt(NIS, 'loginortunis') AS NIS, AES_Encrypt(nama_siswa, 'loginortunama') AS nama_siswa, AES_Encrypt(kelas,'loginortukelas') AS kelas, AES_Encrypt(no_presensi, 'loginpresensi') AS no_presensi FROM `daftar_siswa` NATURAL JOIN `daftar_kelas` WHERE `NIS` = $NIS;";
	
	$r1 = mysqli_query($id_mysql,$sql1);
	
	$result = array();
	$row1 = mysqli_fetch_array($r1);
	
	array_push($result,array(
		base64_encode(NIS)=>base64_encode($row1['NIS']),
		base64_encode(nama_siswa)=>base64_encode($row1['nama_siswa']),
		base64_encode(kelas)=>base64_encode($row1['kelas']),
		base64_encode(no_presensi)=>base64_encode($row1['no_presensi'])
		));
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>