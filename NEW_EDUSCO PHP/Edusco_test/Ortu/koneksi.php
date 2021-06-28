<?php

 define('HOST','localhost');
 define('USER','cyihspjz_edusco_test');
 define('PASS','ranggajelek');
 define('DB','cyihspjz_db_edusco_test');
 
 $id_mysql = mysqli_connect(HOST,USER,PASS,DB) or die('Koneksi gagal');
 ?>