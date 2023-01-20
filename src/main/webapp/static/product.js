// Done
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
//BUTTON ACTIONS
// Done
function addProduct(event){
	console.log("Add Product");
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
	   success: function(response) {
	   		getProductList();  
	   },
	   error: handleAjaxError
	});

	return false;
}

//Done
function updateProduct(event){
	console.log("inside update");
	$('#edit-product-modal').modal('toggle');
	getBrandList();
	//Get the ID
	var id = $("#product-edit-form input[name=productId]").val();	
	var url = getProductUrl() + "/" + id;
	console.log(id, url);

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
	   success: function(response) {
	   		getProductList();   
	   },
	   error: handleAjaxError
	});

	return false;
}

function getProductList(){
	console.log("Get Product List");
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProductList(data);  
	   },
	   error: handleAjaxError
	});
}

function deleteProduct(id){
	var url = getProductUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
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
function processData(){
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

// To set value of fileData variable to the actual file values
function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

// Recursive fn, calls row by row 
function uploadRows(){
	//Update progress
	updateUploadDialog();

	//If everything processed then refresh data and close the modal
	if(processCount==fileData.length){
		if(errorData.length == 0){
			$("#upload-product-modal").modal('toggle');
			getProductList();
		}
		else{
			getProductList();
		}
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getProductUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			// Recursive call on next row
	   		uploadRows();  
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

// Done
function displayProductList(data){
	
	console.log("Display Product List");
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button class="btn btn-primary" onclick="displayEditProduct(' + e.productId + ')">Edit</button>'
		var row = '<tr>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>'  + e.productBrandName + '</td>'
		+ '<td>'  + e.productBrandCategoryName + '</td>'
		+ '<td>'  + e.productBarcode + '</td>'
		+ '<td>'  + e.productMrp + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

// Displays Modal
function displayEditProduct(id){
	var url = getProductUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   
	   },
	   error: handleAjaxError
	});	
}

// To reset the modal values
function resetUploadDialog(){
	//Reset file name
	console.log("inside reset upload dialog");
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
function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

// To replace choose file with Upload File name
function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
	console.log("entered upload modal");
 	resetUploadDialog();
	$('#upload-product-modal').modal('toggle');
}

// To update options 
function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
			updateBrandOptions(data); 
			updateEditBrandOptions(data); 
	   },
	   error: handleAjaxError
	});
}

// Display options in the dropdown menu
function updateBrandOptions(data){
	var $selectBrandCategoryName = $("#inputProductBrandCategoryName");
	$selectBrandCategoryName.empty();

	var $selectBrandName = $("#inputProductBrandName");
	$selectBrandName.empty();

	const names = new Set();
	const categories = new Set();

	// Add Brand name and category to sets
	for(var i in data){
		var brandDetails = data[i];
		names.add(brandDetails.brandName);
		categories.add(brandDetails.brandCategory);

	}
	
	console.log(names);
	console.log(categories);

	for(category of categories.values()){
		var option1 = $('<option></option>').attr("value", category).text(category);
        $selectBrandCategoryName.append(option1);
	}
	
	for(brandName of names.values()){
		var option2 = $('<option></option>').attr("value", brandName).text(brandName);
		$selectBrandName.append(option2);
	}
}

function updateEditBrandOptions(data){
	var $selectBrandCategoryName = $("#editProductBrandCategoryName");
	$selectBrandCategoryName.empty();

	var $selectBrandName = $("#editProductBrandName");
	$selectBrandName.empty();

	const names = new Set();
	const categories = new Set();

	// Add Brand name and category to sets
	for(var i in data){
		var brandDetails = data[i];
		names.add(brandDetails.brandName);
		categories.add(brandDetails.brandCategory);

	}
	
	console.log(names);
	console.log(categories);

	for(category of categories.values()){
		var option1 = $('<option></option>').attr("value", category).text(category);
        $selectBrandCategoryName.append(option1);
	}
	
	for(brandName of names.values()){
		var option2 = $('<option></option>').attr("value", brandName).text(brandName);
		$selectBrandName.append(option2);
	}
}

// Displays modal with given fields and detects id from displayEditBrand method
function displayProduct(data){
	$("#product-edit-form input[name=productName]").val(data.productName);	
	$("#editProductBrandName").val(data.productBrandName);		
	$("#editProductBrandCategoryName").val(data.productBrandCategoryName);
	$("#product-edit-form input[name=productMrp]").val(data.productMrp);
	$("#product-edit-form input[name=productBarcode]").val(data.productBarcode);
	$("#product-edit-form input[name=productId]").val(data.productId);	
	$('#edit-product-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
}

$(document).ready(init);
$(document).ready(getProductList);
$(document).ready(getBrandList);


