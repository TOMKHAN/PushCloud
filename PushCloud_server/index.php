<!DOCTYPE html>
<html lang="en">

    <head>
        <title>PushCloud</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="style/style.css" type="text/css" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script src="javascript/javascript.js" type="text/javascript"></script>
        <link rel="shortcut icon" href="site_graphic/favicon.ico" type="image/x-icon">
        <link rel="icon" href="site_graphic/favicon.ico" type="image/x-icon">
    </head>

    <body class="body">
        <header class="logoPlace">
            <img src="site_graphic/pushCloudMain.png" />
        </header>

        <?php
        include_once 'database/db.php';
        include_once 'database/dbfunctions.php';
        $db_function = new DBFunction($db);
        $user_number = $db_function->getNumberOfUsersForSubscriber('PushCloud');
        ?>

        <div class="sponsorName">
            PushCloud - no. of users: <?php echo $user_number; ?>
        </div>
        <hr class="hr_style">

        <?php if ($user_number > 0) { ?>

            <div class="inputAndButton">
                <form id="pushCloudForm" name="pushCloudForm" onsubmit="return sendPushNotification()">
                    <input type="hidden" name="sponsorName" value="PushCloud"/>
                    <textarea rows="3" name="message" cols="25" class="txt_message" placeholder="Type message here"></textarea>
                    <input type="submit" class="btn_send" value="Send" onclick=""/>
                </form>
            </div>
            <div class="success_message" hidden="true">
                <span class="span_success_message">Message has been sent to all subscribers!</span>
            </div>
            <div class="img_loader_div" hidden="true">
                <img class="img_loader" src="site_graphic/loader.gif" />
            </div>
			<div class="fail_message" hidden="true">
                <span class="span_fail_message">Write a message!</span>
            </div>
        <?php } else { ?>
            <div class="inputAndButton">
                No registered users yet.
            </div>
        <?php } ?>

    </body>

</html>
