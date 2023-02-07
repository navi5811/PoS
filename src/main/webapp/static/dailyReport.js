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
	var p=0;
	for(var i in data){
		p++;
		var e = data[i];
		var date = new Date(e.date);
    	date = date.toLocaleString();
		console.log(e.date);
		console.log(e.numberOfOrders);
		console.log(e.numberOfItems);
		console.log(e.total);
		var row = '<tr>'
		+ '<td>' + p + '</td>'
		+ '<td>' + date + '</td>'
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