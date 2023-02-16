function getdailyReportUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/daily";
}

function getReportList(){
	var url = getdailyReportUrl();
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
	var $tbody = $('#dailyReport-table').find('tbody');
	$tbody.empty();
	var serial=0;
	for(var i in data){
		serial++;
		var e = data[i];
		let date = new Date(e.date).toLocaleDateString();
    	date = date.toLocaleString();
		var row = '<tr>'
		+ '<td>' + serial + '</td>'
		+ '<td>' + date + '</td>'
		+ '<td>'  + e.numberOfOrders + '</td>'
        + '<td>'  + e.numberOfItems + '</td>'
        + '<td>'  + parseFloat(e.total).toFixed(2) + '</td>'
		// + '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}


$(document).ready(getReportList);