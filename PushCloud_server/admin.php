<html>

    <head>
        <title>PushCloud - admin</title>
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

        <div class="inputAndButton">
            <form id="pushCloudFormAdmin" action="newSponsor.php" method="post"
                  enctype="multipart/form-data">
                <label for="file">Sponsor name:</label>
                <input type="text" name="sponsor_name" class="sponsor_name"><br>
                <label for="file">Sponsor picture:</label>
                <input type="file" name="file" class="file"><br>
                <input class="btn_send" type="submit" value="Submit" onclick="">
            </form>
        </div>
		
		<?php
			include_once 'database/db.php';
			include_once 'database/dbfunctions.php';
			$db_function = new DBFunction($db);
			$sponsor_list = $db_function->getAllSponsors();
        ?>
		
		<div class="selectorToDeleteSponsor">
			<form id="form_delete_sponsor" action="deleteSponsor.php" method="post">
				<select id="sponsor_options" name="sponsor_options">
					<?php foreach ($sponsor_list as $sponsor) { 
					echo "<option value='".$sponsor['name']."'> ".$sponsor['name']."</option>";
					} ?>
				</select>
				<input class="btn_send" type="submit" value="Delete!?" onclick="">
			</form>
		</div>

    </body>
</html> 