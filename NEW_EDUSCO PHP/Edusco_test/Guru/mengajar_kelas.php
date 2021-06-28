<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	$kode_mapel = base64_decode($_GET['kode_mapel']);
	
	$sql = "SELECT DISTINCT AES_Encrypt(kelas, 'mengajarkelas') AS kelas FROM daftar_mengajar_guru NATURAL JOIN daftar_kelas WHERE NIG = $nig AND kode_mapel = $kode_mapel ORDER BY kode_kelas ASC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
            base64_encode(kelas)=>base64_encode($row['kelas'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>