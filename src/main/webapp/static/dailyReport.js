function getdailyReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/daily";
}

function getReportList(){
	var url = getdailyReportUrl();
	console.log(url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayReportList(data);  
	   },
	   error: handleAjaxError
	});
}

function displayReportList(data){
	console.log("inside display report");
	var $tbody = $('#dailyReport-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		console.log(e.date);
		console.log(e.numberOfOrders);
		console.log(e.numberOfItems);
		console.log(e.total);
		var row = '<tr>'
		+ '<td>' + e.date + '</td>'
		+ '<td>'  + e.numberOfOrders + '</td>'
        + '<td>'  + e.numberOfItems + '</td>'
        + '<td>'  + e.total + '</td>'
		// + '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
		console.log("Iteration...");
	}
}


$(document).ready(getReportList);