var role;
// Done
function getProductUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}
function getBrandUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
//BUTTON ACTIONS
// Done
function addProduct(event) {
	//Set the values to update
	var $form = $("#product-form");
	var json = toJson($form);
	var url = getProductUrl();

	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			successAlert("Product added successfully");
			$('#add-product-modal').modal('toggle');
			getProductList();
		},
		error: handleAjaxError
	});

	return false;
}

//Done
function updateProduct(event) {
	$('#edit-product-modal').modal('toggle');
	getBrandList();
	//Get the ID
	var id = $("#product-edit-form input[name=productId]").val();
	var url = getProductUrl() + "/" + id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			successAlert("Product Updated successfully");
			getProductList();
		},
		error: handleAjaxError
	});

	return false;
}

function getProductList() {
	var url = getProductUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayProductList(data);
		},
		error: handleAjaxError
	});
}

function deleteProduct(id) {
	var url = getProductUrl() + "/" + id;
	$.ajax({
		url: url,
		type: 'DELETE',
		success: function (data) {
			getProductList();
		},
		error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;

// Initial fn called
function processData() {
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

// To set value of fileData variable to the actual file values
function readFileDataCallback(results) {
	fileData = results.data;
	uploadRows();
}

// Recursive fn, calls row by row 
function uploadRows() {
	//Update progress
	updateUploadDialog();

	//If everything processed then refresh data and close the modal
	if (processCount == fileData.length) {
		if (errorData.length == 0) {
			$("#upload-product-modal").modal('toggle');
		}
		getProductList();
		return;
	}

	//Process next row
	var row = fileData[processCount];
	processCount++;

	var fileObject = Object.keys(row);

	if ((fileObject.length != 5) || (fileObject[0] != "ProductName") || (fileObject[1] != "Brand") || (fileObject[2] != "Category") || (fileObject[3] != "MRP") || (fileObject[4] != "Barcode")) {
		handleJsError("File headers are not valid");
	}

	modifiedObj = {
		productName: row.ProductName,
		productBrandName: row.Brand,
		productBrandCategoryName: row.Category,
		productMrp: row.MRP,
		productBarcode: row.Barcode
	}

	var json = JSON.stringify(modifiedObj);
	var url = getProductUrl();

	//Make ajax call
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			// Recursive call on next row
			uploadRows();
		},
		error: function (response) {
			row.error = JSON.parse(response.responseText).message;
			errorData.push(row);
			uploadRows();
		}
	});

}

function downloadErrors() {
	writeFileData(errorData);
}

//UI DISPLAY METHODS

// Done
function displayProductList(data) {

	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	var serial = 0;
	for (var i = data.length - 1; i >= 0; i--) {
		serial++;

		var e = data[i];
		var buttonHtml = ' <button class="btn btn-primary btn-sm" onclick="displayEditProduct(' + e.productId + ')"><i class="bi bi-pencil-square"></i> Edit</button>'
		var row = '<tr>'
			+ '<td>' + serial + '</td>'
			+ '<td>' + e.productName + '</td>'
			+ '<td>' + e.productBrandName + '</td>'
			+ '<td>' + e.productBrandCategoryName + '</td>'
			+ '<td>' + e.productBarcode + '</td>'
			+ '<td>' + parseFloat(e.productMrp).toFixed(2) + '</td>'
			+ '<td class="edit-button" style="display:none">' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
	}
	if (role == "supervisor") {
		$(".edit-button").show();
	}
}

// Displays Modal
function displayEditProduct(id) {

	editcategoryOption();
	var url = getProductUrl() + "/" + id;
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayProduct(data);
		},
		error: handleAjaxError
	});
}

// To reset the modal values
function resetUploadDialog() {
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

// To update file parameters
function updateUploadDialog() {
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

// To replace choose file with Upload File name
function updateFileName() {
	var $file = $('#productFile');
	var fileName = document.getElementById("productFile").files[0].name;
	$('#productFileName').html(fileName);
}

function displayUploadData() {
	$('#upload-product-modal').trigger('reset');
	$("#download-errors").hide();
	$("#error-row").hide();
	$("#process-data").show();
	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}


var brandsAndCategory = {};
var brands = new Set();
var editbrands = new Set();
var editbrandsAndCategory = {};
// To update options 
function getBrandList() {
	var url = getBrandUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			brandOption(data);
			editbrandOption(data);
		},
		error: handleAjaxError
	});
}

function sortBrand(a, b) {
	if (a.brandName > b.brandName) {
		return 1;
	}
	else if (a.brandName < b.brandName) {
		return -1;
	}
	return 0;
}


function brandOption(data) {

	data.sort(sortBrand);

	var selectTag = $('#inputProductBrandName');
	selectTag.empty();


	for (var i in data) {
		var e = data[i];
		brands.add(e.brandName);
		if (brandsAndCategory[e.brandName])
			brandsAndCategory[e.brandName].add(e.brandCategory);
		else
			brandsAndCategory[e.brandName] = new Set([e.brandCategory]);
	}




	var brandOption = '<option selected disabled value="">' + "Select Brand" + '</option>'
	selectTag.append(brandOption);

	for (brandName of brands.values()) {
		let option = $('<option></option>').attr("value", brandName).text(brandName);
		selectTag.append(option);
	}

	categoryOption();
}
function categoryOption() {
	var brd = $('#inputProductBrandName')[0].value;
	var selectTag = $("#inputProductBrandCategoryName");
	selectTag.empty();


	var categoryOption = '<option selected disabled value> Select Category</option>';
	selectTag.append(categoryOption);



	if (brd.length != 0) {
		for (categoryName of brandsAndCategory[brd].values()) {
			var option1 = $('<option></option>').attr("value", categoryName).text(categoryName);
			selectTag.append(option1);
		}
	}
}


function editbrandOption(data) {
	data.sort(sortBrand);
	
	var editSelectTag = $('#editProductBrandName');
	editSelectTag.empty();
	for (var i in data) {
		var e = data[i];
		editbrands.add(e.brandName);

		if (editbrandsAndCategory[e.brandName])
			editbrandsAndCategory[e.brandName].add(e.brandCategory);
		else
			editbrandsAndCategory[e.brandName] = new Set([e.brandCategory]);
	}

	for (brandName of editbrands.values()) {
		let option = $('<option></option>').attr("value", brandName).text(brandName);
		editSelectTag.append(option);
	}

	editcategoryOption();
}
function editcategoryOption() {
	let bd = $('#editProductBrandName')[0].value;
	var editSelectTag = $("#editProductBrandCategoryName");
	editSelectTag.empty();

	for (categoryName of editbrandsAndCategory[bd].values()) {
		let option1 = $('<option></option>').attr("value", categoryName).text(categoryName);
		editSelectTag.append(option1);
	}

}


// Displays modal with given fields and detects id from displayEditBrand method
function displayProduct(data) {
	editcategoryOption();
	$("#product-edit-form input[name=productName]").val(data.productName);
	$("#editProductBrandName").val(data.productBrandName);
	$("#editProductBrandCategoryName").val(data.productBrandCategoryName);
	$("#product-edit-form input[name=productMrp]").val(data.productMrp);
	$("#product-edit-form input[name=productBarcode]").val(data.productBarcode);
	$("#product-edit-form input[name=productId]").val(data.productId);
	editcategoryOption();
	$('#edit-product-modal').modal('toggle');
}

function addButton() {
	$("#product-form").trigger('reset');
}
//INITIALIZATION CODE
function init() {
	getProductList();
	getBrandList();
	role = $("meta[name=userRole]").attr("content");
	if (role == "supervisor") {
		$("#top-buttons").show();
		$("#edit-column").show();
	}
	$("#add-button").click(addButton);
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#productFile').on('change', updateFileName);
	$('#inputProductBrandName').change(categoryOption);
	$('#editProductBrandName').change(editcategoryOption);
}

$(document).ready(init);


