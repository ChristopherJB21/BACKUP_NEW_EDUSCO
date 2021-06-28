<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);

	$sql = "SELECT AES_ENCRYPT(Foto,'fotoguru') AS Foto FROM `daftar_guru` WHERE NIG = $nig";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	$row = mysqli_fetch_array($r);
	array_push($result,array(
		base64_encode(Foto)=>base64_encode($row['Foto'])
		));
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>