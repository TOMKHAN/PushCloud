<?php

// response json
$json = array();

/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["regId"]) && isset($_POST["nameOfSponsor"])) {
    include_once '../database/db.php';
    include_once '../database/dbfunctions.php';
    $gcm_regid = $_POST["regId"]; // GCM Registration ID
    $name_of_sponsor = $_POST["nameOfSponsor"];
    $db_function = new DBFunction($db);
    $response = $db_function->insertOrDeleteNewSubscriber($gcm_regid, $name_of_sponsor);
    echo $response;
    exit;

    /* pošalje poruku kod preplačivanja
      $gcm = new GCM();
      $registatoin_ids = array($gcm_regid);
      $message = array("gcmMessage" => "Pretplatili ste se na: ".$name_of_sponsor);

      $result = $gcm->send_notification($registatoin_ids, $message);
     */
} else {
    // user details missing
    echo "NON SHALL PASS!";
    exit;
}
?>