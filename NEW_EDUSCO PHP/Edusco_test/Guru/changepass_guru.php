<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nig = base64_decode($_GET['nig']);
	$passnew = base64_decode($_GET['passnew']);
	$passold = base64_decode($_GET['passold']);
	
	$sql = "UPDATE `daftar_guru` SET `Password`= AES_ENCRYPT('$passnew', 'ranggajelek') WHERE NIG = $nig AND Password = AES_ENCRYPT('$passold','ranggajelek');";

	if(mysqli_query($id_mysql,$sql)){
		echo 'Selamat, Anda berhasil mengganti kata sandi';
	}else{
		echo 'Mohon maaf, kata sandi gagal diganti';
	}
	
	mysqli_close($id_mysql);
	
?>