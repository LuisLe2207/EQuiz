<?php 

	include "connect.php";

	function send_notification ($tokens, $data)
	{
		$url = 'https://fcm.googleapis.com/fcm/send';
		$fields = array(
			 'registration_ids' => $tokens,
			 'data' => $data
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

	if (isset($_REQUEST['Title']) || isset($REQUEST['Message']) || isset($_REQUEST['UserID'])) {
		$title = $_REQUEST['Title'];
		$message = $_REQUEST['Message'];
		$userID = $_REQUEST['UserID'];
		try {
			$query =  "Select token From users";
			$stmt = $db->prepare($query);
			$stmt->setFetchMode(PDO::FETCH_ASSOC);
			$stmt->execute();
			$resultSet = $stmt->fetchAll();
			$tokens = array();
			foreach ($resultSet as $row) {
				$tokens[] = $row["token"];
			}
			foreach ($tokens as $token) {
				echo $token;
			}
			$data = array("userID" => userID, "title" => $title, "message" => $message);
			$message_status = send_notification($tokens, $data);

		} catch (PDOException $e) {
			echo $e->getMessage();
			exit();
		} 

	}
 ?>