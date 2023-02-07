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
	var p=0;
	for(var i in data){
		p++;
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + p + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td>' + e.reportInventoryQuantity + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
$(document).ready(getInventoryList);