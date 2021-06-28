<?php 

	require_once('koneksi.php');

	mysqli_select_db($id_mysql, "cyihspjz_db_edusco_test") or
		die("Gagal membuka database. " . mysqli_error($id_mysql));

	$kode_kelas = base64_decode($_GET['kode_kelas']);
	
	$sql = "SELECT AES_Encrypt(NIS, 'daftarsiswanis') AS NIS, AES_Encrypt(nama_siswa,'daftarsiswanama') AS nama_siswa, AES_Encrypt(no_presensi,'daftarpresensi') AS presensi, AES_Encrypt(Kelas, 'daftarkelas') AS Kelas, AES_Encrypt(Foto, 'daftarsiswafoto') AS Foto FROM daftar_siswa NATURAL JOIN daftar_kelas WHERE kode_kelas = $kode_kelas ORDER BY No_Presensi ASC;";
	
	$r = mysqli_query($id_mysql,$sql);
	
	$result = array();
	
    while($row = mysqli_fetch_array($r)){
	    array_push($result,array(
            base64_encode(NIS)=>base64_encode($row['NIS']),
            base64_encode(nama_siswa)=>base64_encode($row['nama_siswa']),
            base64_encode(no_presensi)=>base64_encode($row['presensi']),
            base64_encode(Kelas)=>base64_encode($row['Kelas']),
            base64_encode(Foto)=>base64_encode($row['Foto'])
		));
	}
 
	echo json_encode(array('result'=>$result));
	
	mysqli_close($id_mysql);
?>