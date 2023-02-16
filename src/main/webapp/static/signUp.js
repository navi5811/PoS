function getUserUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/site/signUp";
}

//user add function
function addUser(event) {
	//Set the values to update
	var $form = $("#init-form");
	var json = toJson($form);
	var url = getUserUrl();

	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			sendAlert("user sign up done sucessfully you can login now");
			location.href="http://localhost:9000/pos/site/login";
		},
		error: handleAjaxError
	});

	return false;
}
//function for show password feature
function myFunction() {
	var x = document.getElementById("password");
	if (x.type === "password") {
		x.type = "text";
	} else {
		x.type = "password";
	}
}

function init() {
	$('#login').click(addUser);
}
$(document).ready(init);





