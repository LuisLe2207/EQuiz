<?php
	$servername = "localhost";
	$dbName = "equiz";
	$conn = "mysql:host=$servername;dbname=$dbName";
	$options = [
		// Use save vietnamese in db
		PDO::MYSQL_ATTR_INIT_COMMAND => 'SET NAMES utf8',
		// Throw pdo error 
		PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
		];

	$db = new PDO($conn, 'root', '', $options);
?>