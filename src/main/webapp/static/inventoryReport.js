function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/inventory";
}

function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}
function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	var serial=0;
	data.sort(compare);
	for(var i in data){
		serial++;
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + serial + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td>' + e.reportInventoryQuantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
$(document).ready(getInventoryList);