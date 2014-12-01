<?php
 
 
   
    mysql_connect("localhost","root",""); // host, username, password...
    mysql_select_db("android_api"); // db name...
     

    $lat_v = $_POST['lat'];
	$lng_v = $_POST['lng'];
	$bloodgroup = $_POST['bloodgroup'];
		 
    $q=mysql_query("SELECT name,phone, ( 6371 * acos( cos( radians($lat_v) ) * cos( radians( lat ) ) * cos( radians( lng ) - radians($lng_v) ) + sin( radians($lat_v) ) * sin( radians( lat ) ) ) ) AS distance FROM users WHERE bloodgroup ='$bloodgroup';");
    while($row=mysql_fetch_assoc($q))
            $json_output[]=$row;
      
    echo(json_encode($json_output));
      
    mysql_close();
    
	// HAVING distance < 25 ORDER BY distance LIMIT 0 , 20
?>


