<?php

if (isset($_GET["message"]) && isset($_GET['sponsorName'])) {
    include_once '../google/gcm.php';
    include_once '../database/db.php';
    include_once '../database/dbfunctions.php';
    //$regId = $_GET["regId"];
    $message = $_GET["message"];
	if ($message == ""){
		echo "no_message";
		return;
	}
    $sponsorName = $_GET["sponsorName"];

    $gcm = new GCM();
    $db_function = new DBFunction($db);

    /* if ($regId != ""){
      $registatoin_ids = array($regId);
      } else {
      $reIdFromDatabase = $db_function->getAllUsers();
      $registatoin_ids = array();
      foreach ($reIdFromDatabase as $oneRegId) {
      $registatoin_ids[] = $oneRegId['gcm_regid'];
      }
      } */

    $reIdFromDatabase = $db_function->getAllUsersForSubscriber($sponsorName);
    $registatoin_ids = array();
    foreach ($reIdFromDatabase as $oneRegId) {
        $registatoin_ids[] = $oneRegId['gcm_regid'];
    }

    $message = array("gcmMessage" => $message, "sponsorName" => $sponsorName);

    $result = $gcm->send_notification($registatoin_ids, $message);

    echo $result;
}
?>