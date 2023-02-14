
function getSalesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report/sales";
}

function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

//function for sorting the table
function comp(a, b) {
    if (a.brand > b.brand) {
        return 1;
    }
    else if (a.brand < b.brand) {
        return -1;
    }
    return 0;
}

function displaySalesList(data) {
    var $tbody = $('#sales-table').find('tbody');
    $tbody.empty();
    var serial = 0;
    data.sort(comp);
    for (var i in data) {
        serial++;
        var e = data[i];
        var row = '<tr>'
            + '<td>' + serial + '</td>'
            + '<td>' + e.brand + '</td>'
            + '<td>' + e.category + '</td>'
            + '<td>' + e.quantity + '</td>'
            + '<td>' + e.totalAmount + '</td>'
            + '</tr>';
        $tbody.append(row);
    }
}
function getList() {
    //Set the values to update
    var $form = $("#sales-form");
    console.log($form)
    var json = toJson($form);
    var url = getSalesUrl();
    console.log(url);
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (response) {
            displaySalesList(response);
            
        },
        error: handleAjaxError
    });

    return false;
}


function updateBrandOptions(data) {
    console.log("data is ",data);
    var $selectBrandCategoryName = $("#inputBrandCategory");
    $selectBrandCategoryName.empty();

    var $selectBrandName = $("#inputBrandName");
    $selectBrandName.empty();

    const names = new Set();
    const categories = new Set();

    // Add Brand name and category to sets
    var option = $('<option></option>').attr("value", "").text("All");
    $selectBrandCategoryName.append(option);
    var boption = $('<option></option>').attr("value", "").text("All");
    $selectBrandName.append(boption);
    for (var i in data) {
        var brandDetails = data[i];
        names.add(brandDetails.brandName);
        categories.add(brandDetails.brandCategory);
    }
    

    for (brandCategory of categories.values()) {
        var option1 = $('<option></option>').attr("value", brandCategory).text(brandCategory);
        $selectBrandCategoryName.append(option1);
    }

    for (brandName of names.values()) {
        var option2 = $('<option></option>').attr("value", brandName).text(brandName);
        $selectBrandName.append(option2);
    }
}

function getBrandList() {
	var url = getBrandUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			updateBrandOptions(data);
		},
		error: handleAjaxError
	});
}
function datefunction(){
    var $form = $("#sales-form");
    var json = toJson($form);
    document.getElementById("inputEndDate").min=JSON.parse(json).startDate;
}
function endDatefunction()
{
    var $form = $("#sales-form");
    var json = toJson($form);
    document.getElementById("inputStartDate").max=JSON.parse(json).endDate;
}

function init() {
    getList();
    getBrandList();
    $("#get-sales").click();
    $("#get-sales").click(getList);
    // inputEndDate.max=new Date().toISOString().split('T')[0]
    $("#inputStartDate").change(datefunction);
    $("#inputEndDate").change(endDatefunction);
}

$(document).ready(init);

