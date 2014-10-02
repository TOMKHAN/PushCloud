<?php

// response json
$json = array();

/**
 * Registering a user device
 * Store reg id in users table
 */
if (isset($_POST["regId"])) {
    include_once '../database/db.php';
    include_once '../database/dbfunctions.php';
    include_once '../google/gcm.php';
    $gcm_regid = $_POST["regId"]; // GCM Registration ID

    $db_function = new DBFunction($db);
    $gcm = new GCM();

    $res = $db_function->storeUser($gcm_regid);

    /* slanje poruke nakon registracije!
      $registatoin_ids = array($gcm_regid);
      $message = array("gcmMessage" => "Uspješno registriran na server!");

      $result = $gcm->send_notification($registatoin_ids, $message); */

    echo $res;
    exit;
} else {
    // user details missing
}
?>