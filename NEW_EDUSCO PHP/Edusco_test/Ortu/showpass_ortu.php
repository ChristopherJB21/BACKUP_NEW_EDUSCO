<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$password = base64_decode($_GET['password']);
	
	$sql = "SELECT Password FROM daftar_ortu_siswa WHERE NIS = $nis AND Password = AES_Encrypt('$password','ranggajelek');";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
	$row = mysqli_fetch_array($r);
	
	    array_push($result,array(
		    base64_encode(Password)=>base64_encode($row['Password'])
		));
	
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>