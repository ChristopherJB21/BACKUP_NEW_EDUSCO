<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	$password = base64_decode($_GET['password']);
	
	$sql = "SELECT AES_Encrypt(NIG,'logingurunig') AS NIG, AES_Encrypt(nama_guru, 'logingurunama') AS nama_guru FROM daftar_guru WHERE NIG = $nig AND Password = AES_ENCRYPT('$password', 'ranggajelek');";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	$row = mysqli_fetch_array($r);
	array_push($result,array(
	    base64_encode(NIG)=>base64_encode($row['NIG']),
		base64_encode(nama_guru)=>base64_encode($row['nama_guru'])
		));
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>