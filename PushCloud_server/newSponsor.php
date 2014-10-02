<?php

if (isset($_POST["sponsor_name"]) && isset($_FILES["file"]["name"])) {
    include_once 'database/db.php';
    include_once 'database/dbfunctions.php';
    $allowedExts = array("gif", "jpeg", "jpg", "png");
    $temp = explode(".", $_FILES["file"]["name"]);
    $extension = end($temp);
    $sponsor_name = $_POST["sponsor_name"];
    $db_function = new DBFunction($db);

//picture upload
    if ((($_FILES["file"]["type"] == "image/gif") || ($_FILES["file"]["type"] == "image/jpeg") || ($_FILES["file"]["type"] == "image/jpg") || ($_FILES["file"]["type"] == "image/pjpeg") || ($_FILES["file"]["type"] == "image/x-png") || ($_FILES["file"]["type"] == "image/png")) && ($_FILES["file"]["size"] < 2000000) && in_array($extension, $allowedExts)) {
        if (!$_FILES["file"]["error"] > 0) {
            /* echo "Upload: " . $_FILES["file"]["name"] . "<br>";
              echo "Type: " . $_FILES["file"]["type"] . "<br>";
              echo "Size: " . ($_FILES["file"]["size"] / 1024) . " kB<br>";
              echo "Temp file: " . $_FILES["file"]["tmp_name"] . "<br>"; */
            if (!file_exists("sponsor_image/" . $sponsor_name . ".png")) {
				$arr = array("č","ć","ž","đ","š","Č","Ć","Ž","Š","Đ","?"," ");
				$sponsor_name_image = str_replace($arr, "", $sponsor_name);
                move_uploaded_file($_FILES["file"]["tmp_name"], "sponsor_image/" . $sponsor_name_image . ".png");
            }
        }

        //insert sponsor in DB
        $db_function->insertNewSponsor($sponsor_name, "http://tomislav-sulc.iz.hr/PushCloud_server/sponsor_image/" . $sponsor_name . ".png");

        //creating .php file for Sponsor
        $phpString = "<!DOCTYPE html>
				<html lang='en'>

				<head>
					<title>" . $sponsor_name . "</title>
					<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
					<link rel='stylesheet' href='../style/style.css' type='text/css' />
					<meta name='viewport' content='width=device-width, initial-scale=1.0'/>
					<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js'></script>
					<script src='../javascript/javascript.js' type='text/javascript'></script>
					<link rel='shortcut icon' href='../site_graphic/favicon.ico' type='image/x-icon'>
					<link rel='icon' href='../site_graphic/favicon.ico' type='image/x-icon'>
				</head>

				<body class='body'>
					<header class='logoPlace'>
						<img src='../site_graphic/pushCloudMain.png' />
					</header>
					
					<?php
						include_once '../database/db.php';
						include_once '../database/dbfunctions.php';
						$" . db_function . " = new DBFunction($" . db . ");
						$" . user_number . " = $" . db_function . "->getNumberOfUsersForSubscriber('" . $sponsor_name . "');
					?>

					<div class='sponsorName'>
						" . $sponsor_name . " - no. of users: <?php echo $" . user_number . "; ?>
					</div>
					<hr class='hr_style'>
					
					<?php if ($" . user_number . " > 0) { ?>

						<div class='inputAndButton'>
							<form id='pushCloudForm' name='pushCloudForm' onsubmit='return sendPushNotification(true)'>
								<input type='hidden' name='sponsorName' value='" . $sponsor_name . "'/>
								<textarea rows='3' name='message' cols='25' class='txt_message' placeholder='Type message here'></textarea>
								<input type='submit' class='btn_send' value='Send' onclick=''/>
							</form>
						</div>
						<div class='success_message' hidden='true'>
							<span class='span_success_message'>Message has been sent to all subscribers!</span>
						</div>
						<div class='img_loader_div' hidden='true'>
							<img class='img_loader' src='../site_graphic/loader.gif' />
						</div>
						<div class='fail_message' hidden='true'>
							<span class='span_fail_message'>Write a message!</span>
						</div>
					<?php } else { ?>
						<div class='inputAndButton'>
							No registered users yet.
						</div>
					<?php } ?>
					
				</body>

				</html>";
        $fp = fopen('sponsors/'. $sponsor_name . '.php', 'w');
        fwrite($fp, $phpString);
        fclose($fp);
        header('Location: echoViews/successEchoSponsor.php');
    } else {
        echo "Invalid file";
    }
} else {
    echo "There is no sponsor name!";
}
?> 