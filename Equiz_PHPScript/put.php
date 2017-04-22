<?php
	$data = [
		'rules' => [
			'.read' => true,
			'.write' => true
		]
	];
	$data_json = json_encode($data);
	$url = 'https://equiz-59c1f.firebaseio.com/.settings/rules.json?auth=JqLoD1NmSQzvyhMoIvXUHaL2u6R7D34i7T9uW5Kf';
	$ch = curl_init();
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json','Content-Length: ' . strlen($data_json)));
	curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'PUT');
	curl_setopt($ch, CURLOPT_POSTFIELDS, $data_json);
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