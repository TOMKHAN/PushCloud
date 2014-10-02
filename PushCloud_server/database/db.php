<?php

$config = array('host' => '', 'username' => '', 'password' => '', 'dbname' => '');
$db = new PDO('mysql:host=' . $config['host'] . ';dbname=' . $config['dbname'], $config['username'], $config['password'], array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES 'UTF8'"));
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_WARNING); // warning for development, later PDO::ERRMODE_EXCEPTION
$google_api_key = "";
