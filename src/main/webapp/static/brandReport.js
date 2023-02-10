function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/brand";
}

function getBrandList() {
	var url = getBrandUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayBrandList(data);
		},
		error: handleAjaxError
	});
}


function displayBrandList(data) {
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	var serial = 0;
	data.sort(compare);
	for (var i in data) {
		serial++;
		var e = data[i];
		// var buttonHtml = '<button onclick="deleteBrand(' + e.brandId + ')">Delete Brand</button>'
		// var buttonHtml = ' <button class="btn btn-primary" onclick="displayEditBrand(' + e.brandId + ')"><i class="bi bi-pencil-square"></i> </button>'
		var row = '<tr>'
			+ '<td>' + serial + '</td>'
			+ '<td>' + e.brandName + '</td>'
			+ '<td>' + e.brandCategory + '</td>'
			// + '<td>' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
	}
}


$(document).ready(getBrandList);