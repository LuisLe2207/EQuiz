<?php 
	$url = 'https://equiz-59c1f.firebaseio.com/.settings/rules.json?auth=JqLoD1NmSQzvyhMoIvXUHaL2u6R7D34i7T9uW5Kf';

   	$ch = curl_init();
   	curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 0);
	curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
 
    $result = curl_exec($ch);           
    if ($result === FALSE) {
        die('Curl failed: ' . curl_error($ch));
    }
    curl_close($ch);
    echo $result;
 ?>
