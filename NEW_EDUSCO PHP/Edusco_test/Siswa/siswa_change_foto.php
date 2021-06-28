<?php

    require_once('koneksi.php');
 
 if($_SERVER['REQUEST_METHOD'] == 'POST') {
     
    $nis = base64_decode($_POST['nis']);
    $ImageNama = $_POST['nama'];
    $ImageData = $_POST['image_path'];

    $nis_encode = base64_encode($nis);
    $nama_encode = base64_encode($ImageNama);
    
    $imagenama_encode = $nis_encode . $nama_encode;

    $ImagePath = "Photo/$imagenama_encode.jpeg";
 
    $ServerURL = "https://eduscotest.educationscoring.com/Siswa/$ImagePath";
 
    $InsertSQL = "UPDATE `daftar_siswa` SET `Foto` = '$ServerURL' WHERE NIS = $nis";
 
    if(mysqli_query($id_mysql, $InsertSQL)){

        file_put_contents($ImagePath,base64_decode($ImageData));

        echo "Selamat, Anda berhasil mengganti foto";
    } else {
        echo "Mohon maaf, foto gagal diganti";
    }
 
    mysqli_close($id_mysql);

 } else {
        echo "Mohon maaf, foto gagal diganti";
 }

?>