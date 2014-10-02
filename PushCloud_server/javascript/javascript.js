$(document).ready(function() {
	document.getElementById("sponsor_options").selectedIndex = -1;
});
function sendPushNotification($comingFromSponsor) {
	$url = "google/send_message.php";
	if ($comingFromSponsor == true) {
		$url = "../google/send_message.php";
	}
    if ($('.success_message').is(":visible")) {
        $('.success_message').hide(300);
        $('.img_loader_div').show(400);
    } else if ($('.fail_message').is(":visible")){
		$('.fail_message').hide(300);
		$('.img_loader_div').show(400);
	} else {
        $('.img_loader_div').show(400);
    }
    var data = $('#pushCloudForm').serialize();
    $('#pushCloudForm').unbind('submit');
    $.ajax({
        url: $url,
        type: 'GET',
        data: data,
        beforeSend: function() {
        },
        success: function(data, textStatus, xhr) {
			if (data=="no_message"){
				$('.fail_message').show(400);
				$('.img_loader_div').hide(300);
			} else {
				$('.txt_message').val("");
				$('.img_loader_div').hide(300);
				$('.success_message').show(400);
			}
        },
        error: function(xhr, textStatus, errorThrown) {
			$('.txt_message').val("uispidaps");
        }
    });
    return false;
}