<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	
	$sql = "SELECT DISTINCT AES_Encrypt(matapelajaran,'siswamapel') AS kodemapel FROM daftar_nilai NATURAL JOIN daftar_mapel WHERE NIS = $nis ORDER BY kode_mapel;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
	        base64_encode(matapelajaran)=>base64_encode($row['kodemapel'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>