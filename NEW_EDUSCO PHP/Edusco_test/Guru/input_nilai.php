<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$nis = base64_decode($_GET['nis']);
	$kode_kelas = base64_decode($_GET['kode_kelas']);
	$kode_mapel = base64_decode($_GET['kode_mapel']);
	$nig = base64_decode($_GET['nig']);
	$kode_nilai = base64_decode($_GET['kode_nilai']);
	$NilaiKe = base64_decode($_GET['NilaiKe']);
	$Semester = base64_decode($_GET['Semester']);
	$nilai = base64_decode($_GET['nilai']);
	
	$sql = "INSERT INTO `daftar_nilai`(`NIS`, `kode_kelas`, `kode_mapel`, `nig`, `kode_nilai`, `NilaiKe`, `Semester`, `nilai`) VALUES ($nis, $kode_kelas, $kode_mapel, $nig, $kode_nilai, $NilaiKe, $Semester, $nilai)";

	if(mysqli_query($id_mysql,$sql)){
		echo 'Selamat, Anda berhasil memasukkan nilai';
	}else{
		echo 'Maaf, nilai gagal dimasukkan';
	}
	
	mysqli_close($id_mysql);
?>