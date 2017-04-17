<?php 

	function send_notification ($tokens, $message)
	{
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			 'registration_ids' => $tokens,
			 'data' => $message
			);

		$headers = array(
			'Authorization:key = AAAAcj2rpfw:APA91bF4dFwMYTOwYJhh-FG37lFnyH5z3kMXjsGdz1J2vjFDogt2yEkH40p8vA_I1wkqYRhvHGHnCiOPH-LKh_s5Nedop9-jNuM5oKTi3t57VR2gqjmL58LasB3HV4rMt9Z3i7xZ-1ey',
			'Content-Type: application/json'
			);

	   $ch = curl_init();
       curl_setopt($ch, CURLOPT_URL, $url);
       curl_setopt($ch, CURLOPT_POST, true);
       curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
       curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
       curl_setopt ($ch, CURLOPT_SSL_VERIFYHOST, 0);  
       curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
       curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
       $result = curl_exec($ch);           
       if ($result === FALSE) {
           die('Curl failed: ' . curl_error($ch));
       }
       curl_close($ch);
       return $result;
	}
	
	$message = $_REQUEST['Message'];

	$conn = mysqli_connect("localhost","root","","equiz");

	$sql = " Select token From users";

	$result = mysqli_query($conn,$sql);
	$tokens = array();

	if(mysqli_num_rows($result) > 0 ){

		while ($row = mysqli_fetch_assoc($result)) {
			$tokens[] = $row["token"];
		}
	}

	mysqli_close($conn);

	$message = array("message" => $message);
	$message_status = send_notification($tokens, $message);
	echo $message_status;



 ?>
