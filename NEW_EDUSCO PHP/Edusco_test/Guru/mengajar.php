<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = $_GET['nig'];
	
	$sql = "SELECT AES_Encrypt(kelas,'daftarmengajargurukelas') AS kelas, AES_Encrypt(matapelajaran,'daftarmengajargurumapel') AS matapelajaran FROM daftar_mengajar_guru NATURAL JOIN daftar_guru NATURAL JOIN daftar_mapel NATURAL JOIN daftar_kelas WHERE NIG = $nig ORDER BY kode_mapel, kode_kelas ASC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
	        base64_encode(kelas)=>base64_encode($row['kelas']),
            base64_encode(matapelajaran)=>base64_encode($row['matapelajaran'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>