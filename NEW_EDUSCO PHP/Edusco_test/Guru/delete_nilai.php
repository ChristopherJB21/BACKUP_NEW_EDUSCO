<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nodata = base64_decode($_GET['nodata']);
	
	$sql = "DELETE FROM `daftar_nilai` WHERE no_data = $nodata;";

	if(mysqli_query($id_mysql,$sql)){
		echo 'Selamat, Anda berhasil menghapus nilai';
	}else{
		echo 'Maaf, nilai gagal dihapus';
	}
	
	mysqli_close($id_mysql);
?>