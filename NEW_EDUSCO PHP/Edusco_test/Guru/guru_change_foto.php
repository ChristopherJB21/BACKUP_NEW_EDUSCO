<?php

    require_once('koneksi.php');
 
 if($_SERVER['REQUEST_METHOD'] == 'POST') {
     
    $nig = base64_decode($_POST['nig']);
    $ImageNama = $_POST['nama'];
    $ImageData = $_POST['image_path'];

    $nig_encode = base64_encode($nig);
    $nama_encode = base64_encode($ImageNama);
    
    $imagenama_encode = $nig_encode . $nama_encode;

    $ImagePath = "Photo/$imagenama_encode.png";
 
    $ServerURL = "https://eduscotest.educationscoring.com/Guru/$ImagePath";
 
    $InsertSQL = "UPDATE `daftar_guru` SET `Foto` = '$ServerURL' WHERE NIG = $nig";
 
    if(mysqli_query($id_mysql, $InsertSQL)){

        file_put_contents($ImagePath,base64_decode($ImageData));

        echo "Selamat, Anda berhasil merubah foto";
    } else {
        echo "Mohon maaf, foto gagal diganti";
    }
 
    mysqli_close($id_mysql);

 } else {
        echo "Mohon maaf, foto gagal diganti";
 }

?>