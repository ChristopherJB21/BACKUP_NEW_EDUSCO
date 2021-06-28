<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	
	$sql = "SELECT DISTINCT AES_Encrypt(matapelajaran,'mengajarmapel') AS matapelajaran FROM daftar_mengajar_guru NATURAL JOIN daftar_mapel WHERE NIG = $nig ORDER BY kode_mapel ASC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
            base64_encode(matapelajaran)=>base64_encode($row['matapelajaran'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>