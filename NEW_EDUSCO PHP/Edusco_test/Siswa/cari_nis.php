<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nama = base64_decode($_GET['nama']);
	
	$sql = "SELECT AES_Encrypt(NIS, 'daftarsiswanis') AS NIS_Siswa, AES_Encrypt(Nama_siswa, 'daftarsiswanama') AS Nama_siswa FROM `daftar_siswa` WHERE `Nama_siswa` LIKE '%$nama%' ORDER BY NIS ASC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
	        base64_encode(NIS)=>base64_encode($row['NIS_Siswa']),
            base64_encode(Nama_siswa)=>base64_encode($row['Nama_siswa'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>