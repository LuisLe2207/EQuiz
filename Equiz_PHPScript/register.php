<?php
	
	include "connect.php";

	if (isset($_REQUEST["Token"]) || isset($_REQUEST["UserID"])) {
		$token = $_REQUEST["Token"];
		$userID = $_REQUEST["UserID"];
		try {
			$selectQuery = "SELECT * from users where userID = :userID";
			$stmt = $db->prepare($selectQuery);
			$stmt->bindParam(':userID', $userID);
			$stmt->setFetchMode(PDO::FETCH_ASSOC);
			$stmt->execute();
			$query = "";
			if ($stmt->fetchAll()) {
				$query = "UPDATE users SET token = :token where userID = :userID";
			} else {
				$query = "INSERT INTO users (userID, token) values (:userID, :token) on DUPLICATE KEY UPDATE userID = :userID, token = :token";
			}
			echo $query;
			$stmt = $db->prepare($query);
			$stmt->bindParam(':userID', $userID);
			$stmt->bindParam(':token', $token);
			$stmt->execute();
		} catch (PDOException $e) {
			echo $e->getMessage();
			exit();
		}
	}

?>