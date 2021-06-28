<?php

include 'DatabaseConfig.php';

// Create connection
$conn = new mysqli($HostName, $HostUser, $HostPass, $DatabaseName);
 
    $DefaultId = 0;
    $ImageData = $_GET['path'];
    $ImageName = $_GET['name'];

    $GetOldIdSQL = "SELECT id FROM UploadImageToServer ORDER BY id ASC";
 
    $Query = mysqli_query($conn,$GetOldIdSQL);
 
    while($row = mysqli_fetch_array($Query)){
        $DefaultId = $row['id'];
    }
 
    $ImagePath = "Photo/$DefaultId.png";
 
    $ServerURL = "https://eduscotest.educationscoring.com/Photo/$ImagePath";
 
    $InsertSQL = "insert into UploadImageToServer (image_path,image_name) values ('$ServerURL','$ImageName')";
 
    if(mysqli_query($conn, $InsertSQL)){

        file_put_contents($ImagePath,base64_decode($ImageData));

        echo "Your Image Has Been Uploaded.";
    }
 
    mysqli_close($conn);

?>