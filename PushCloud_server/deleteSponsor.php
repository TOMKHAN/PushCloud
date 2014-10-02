<?php
$option = isset($_POST['sponsor_options']) ? $_POST['sponsor_options'] : false;
if($option) {
	include_once 'database/db.php';
	include_once 'database/dbfunctions.php';
	$db_function = new DBFunction($db);
	$valueForDelete = $db_function->deleteSponsor($option);
	if ($valueForDelete == "true"){
		$arr = array("č","ć","ž","đ","š","Č","Ć","Ž","Š","Đ","?"," ");
		$sponsor_name_image = str_replace($arr, "", $option);
		if (unlink("sponsors/".$option.".php") && unlink("sponsor_image/".$sponsor_name_image.".png")){
			header('Location: echoViews/successEchoDeleteSponsor.php');
		} else {
			echo "delete ERORORROR!";
		}
	} else {
		echo "No such sponsor!";
	}
	
} else {
	echo "task option is required";
	exit; 
}