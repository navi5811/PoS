var role;

// Done
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

//BUTTON ACTIONS
// Done
// function addInventory(event){
// 	//Set the values to update
// 	var $form = $("#inventory-form");
// 	var json = toJson($form);
// 	var url = getInventoryUrl();

// 	$.ajax({
// 	   url: url,
// 	   type: 'PUT',
// 	   data: json,
// 	   headers: {
//        	'Content-Type': 'application/json'
//        },	   
// 	   success: function(response) {
// 	   		getInventoryList();  
// 	   },
// 	   error: handleAjaxError
// 	});

// 	return false;  getInventoryUrl() + "/" + productId;
// }
var qty=0;
async function addInventory(event){
    //Set the values to update
    var barcode = $("#inventory-form input[name=inventoryProductBarcode]").val();
	console.log("product barcode is",barcode);
    var $form = $("#inventory-form");
    var json = toJson($form);
    var inventoryObj = JSON.parse(json, barcode);
	console.log("invetory obj values",inventoryObj);
    await updateQuantity(inventoryObj, barcode);
    var finalInvObj = await inventoryObj;
    json = await JSON.stringify(finalInvObj);
    await updateApiCall(json);
}
async function updateQuantity(inventoryObj, barcode){
	var url = getInventoryUrl() + "/" + barcode;
	console.log(url);
	console.log("product quantity",inventoryObj.productQuantity);
    await $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
			$('#add-inventory-modal').modal('toggle');
            qty = parseInt(response.productQuantity);
            inventoryObj.productQuantity = parseInt(inventoryObj.productQuantity) + parseInt(qty);
            inventoryObj.productQuantity = parseInt(inventoryObj.productQuantity);
            console.log("Value in update qty fn " + inventoryObj.productQuantity);
        },
        error: handleAjaxError
    });
    await console.log(inventoryObj.productQuantity);
    return await inventoryObj;
}
function updateApiCall(json){
    var url = getInventoryUrl();
	console.log("entered update api call");
    $.ajax({
        url: url,
        type: 'PUT',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            getInventoryList();
        },
        error: handleAjaxError
     });
     return false;
}


//Done//doubtfull
function updateInventory(event){
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var productId = $("#inventory-edit-form input[name=productId]").val();	
	console.log("Heyy"+ productId);
	var url = getInventoryUrl() + "/" + productId;
	console.log(productId, url);

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getInventoryList();   
	   },
	   error: handleAjaxError
	});
	return false;
}

// Done
function getInventoryList(){
    console.log("just entered get inventory");
	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {


        console.log("just entring the display inventory");
	   		displayInventoryList(data);

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
	var file = $('#inventoryFile')[0].files[0];
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
			$("#upload-inventory-modal").modal('toggle');
			getInventoryList();
		}
		else{
			getInventoryList();
		}
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getInventoryUrl();

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
function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	var j=0;
	for(var i in data){
		j++;
		var e = data[i];
		// var buttonHtml = '<button onclick="deleteBrand(' + e.brandId + ')">Delete Brand</button>'
		var buttonHtml = ' <button class="btn btn-secondary" onclick="displayEditInventory(' + e.inventoryProductBarcode + ')"><i class="bi bi-pencil-square"></i> Edit</button>'
		var row = '<tr>'
		+ '<td>' + j + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>' + e.inventoryProductBarcode + '</td>'
		+ '<td>' + e.productQuantity + '</td>'
		+ '<td class="edit-button" style="display:none">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
    if(role == "supervisor"){
		$(".edit-button").show();
   }
}

// Displays Modal
function displayEditInventory(barcode){
    console.log("entered edit inventory");
	var url = getInventoryUrl() + "/" + barcode;
    console.log("url is " ,url);
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
        console.log("entring display inventory");
	   		displayInventory(data);   
	   },
	   error: function(data){
        console.log("why this error is occuring");
        handleAjaxError
       }
       
	});	
}

// To reset the modal values
function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-inventory-modal').modal('toggle');
}

// Displays modal with given fields and detects id from displayEditInvnetory method
function displayInventory(data){
	$("#inventory-edit-form input[name=inventoryProductBarcode]").val(data.inventoryProductBarcode);		
	$("#inventory-edit-form input[name=productQuantity]").val(data.productQuantity);	
	$('#edit-inventory-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
	role = $("meta[name=userRole]").attr("content");
    if(role == "supervisor"){
        $("#top-buttons").show();
		$("#edit-column").show();
    }
	$('#add-inventory').click(addInventory);
	$('#update-inventory').click(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);
$(document).ready(getInventoryList);

