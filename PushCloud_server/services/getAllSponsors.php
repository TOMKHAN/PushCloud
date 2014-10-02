<?php

	include_once '../database/db.php';
	include_once '../database/dbfunctions.php';
	$db_function = new DBFunction($db);
	$allSponsors = $db_function->getAllSponsors();
	$i = 1;
	$namesOfSponsors = "";
	foreach ($allSponsors as $oneSponsorName) {
		if ($oneSponsorName['sponsor_image'] == NULL) {
			$oneSponsorName['sponsor_image'] = " ";
		}
		$arr = array("č","ć","ž","đ","š","Č","Ć","Ž","Š","Đ","?"," ");
		$sponsor_name_image = str_replace($arr, "", $oneSponsorName['sponsor_image']);
		if ($i == 2) {
			$namesOfSponsors .= "," . $oneSponsorName['name'] . ";" . $sponsor_name_image;
		} else {
			$namesOfSponsors = "3ss21a--" .$oneSponsorName['name'] . ";" . $sponsor_name_image; //"3ss21a?" is a control number
			$i = 2;
		}
	}
	echo $namesOfSponsors;
	exit; //treba zbog 00webhosta jer ubacuje neki brojač u svaki php fajl! rješenje: http://stackoverflow.com/questions/2268868/webhoster-inserts-a-javascript-which-brokes-my-code-how-to-remove-it
?>