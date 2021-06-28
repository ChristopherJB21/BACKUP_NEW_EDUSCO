<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nama = base64_decode($_GET['nama']);
	
	$sql = "SELECT AES_Encrypt(NIG, 'daftargurunig') AS NIG, AES_Encrypt(nama_guru, 'daftargurunama') AS nama_guru FROM `daftar_guru` WHERE `nama_guru` LIKE '%$nama%'";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
	        base64_encode(NIG)=>base64_encode($row['NIG']),
            base64_encode(nama_guru)=>base64_encode($row['nama_guru'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>