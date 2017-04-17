<?php
	
	//require "connect.php";

	$servername = "localhost";
	$dbName = "equiz";
	$conn = "mysql:host=$servername;dbname=$dbName";
	$options = array(
		// Use save vietnamese in db
		PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8',
		// Throw pdo error 
		PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
		);

	if (isset($_REQUEST["Token"])) {
		$token = $_REQUEST["Token"];
		echo $token;
		try {
			$db = new PDO($conn, 'root', '', $options);
			$query = "INSERT INTO users (token) values (:token) on DUPLICATE KEY UPDATE token = :token";
			$stmt = $db->prepare($query);
			$stmt->bindParam(':token', $token);
			$stmt->execute();
		} catch (PDOException $e) {
			echo $e->getMessage();
			exit();
		}
	}

?>