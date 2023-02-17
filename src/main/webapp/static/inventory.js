var role;
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}
async function uploadQuantity(inventoryObj, barcode){
    var url = getInventoryUrl() + "/" + barcode;
    await $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            qty = parseInt(response.productQuantity);
            inventoryObj.Quantity = parseInt(inventoryObj.Quantity) + parseInt(qty);
        },
        error: function(response){
			inventoryObj.error = JSON.parse(response.responseText).message;
	   		errorData.push(inventoryObj);
	   		uploadRows();
	   }
    });
    await console.log(inventoryObj.Quantity);
    return await inventoryObj;
}

var qty=0;
async function addInventory(event){
	event.preventDefault();
    //Set the values to update
    var barcode = $("#inventory-form input[name=inventoryProductBarcode]").val();
    var $form = $("#inventory-form");

    var json = toJson($form);


    var inventoryObj = JSON.parse(json, barcode);

	if(inventoryObj.productQuantity.includes("."))
	{
		sendAlert("Please enter a valid quantity");
		$("#add-inventory-modal").modal("show");
		return;
	}
    await updateQuantity(inventoryObj, barcode);
    var finalInvObj = await inventoryObj;
    json = await JSON.stringify(finalInvObj);
    await updateApiCall(json);
}
async function updateQuantity(inventoryObj, barcode){
	var url = getInventoryUrl() + "/" + barcode;
    await $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            qty = parseInt(response.productQuantity);
            inventoryObj.productQuantity = parseInt(inventoryObj.productQuantity) + parseInt(qty);
            inventoryObj.productQuantity = parseInt(inventoryObj.productQuantity);
        },
        error: function(response){
			handleAjaxError(response)
			
		   }
    });
    await console.log(inventoryObj.productQuantity);
    return await inventoryObj;
}
function updateApiCall(json){
    var url = getInventoryUrl();
    $.ajax({
        url: url,
        type: 'PUT',
        data: json,
        headers: {
			
            'Content-Type': 'application/json'
        },
        success: function(response) {
			$('#add-inventory-modal').modal('toggle');
			successAlert("Inventory added successfully");
            getInventoryList();
        },
        error: function(response){
			handleAjaxError(response)
		}
     });
     return false;
}


//Done//doubtfull
function updateInventory(event){
	event.preventDefault();
	$('#edit-inventory-modal').modal('show');
	//Get the ID
	var productId = $("#inventory-edit-form input[name=productId]").val();
	var url = getInventoryUrl() + "/" + productId;


	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);

	if(JSON.parse(json).productQuantity.includes(".")){
		$("#edit-inventory-modal").modal("show");
		sendAlert("Please enter a valid quantity");
		return;
	}
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
		successAlert("Inventory updated successfully");
		$("#edit-inventory-modal").modal("hide");
	   		getInventoryList();   
	   },
	   error: function(response){
		handleAjaxError(response)
		$("#edit-inventory-modal").modal("show");
	   }
	   
	});
	return false;
}

// Done
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
async function uploadRows(){
	//Update progress
	updateUploadDialog();

	//If everything processed then refresh data and close the modal
	if(processCount==fileData.length){
		if(errorData.length == 0){
			$("#upload-inventory-modal").modal('toggle');
		}
			getInventoryList();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;

	var fileObject = Object.keys(row);

	if((fileObject.length!=2) || (fileObject[0]!="Barcode") || (fileObject[1]!="Quantity")){
		handleJsError("File headers are not valid");
    }
	// tempObj = {
	// 	'Barcode': inventoryProductBarcode,
	// 	'Quantity': productQuantity
	// };

	//logic, loops, conditions

	// Object.keys(tempObj).reduce(()=>{

	// },{})


	let quan=parseInt(row.Quantity);
	

	if(row.Quantity!=quan)
	{
		row.error = "Quantity is not valid";
	   		errorData.push(row);
	   		uploadRows();
			return;
	}
	// if((qn.matches("[0-9]+")==false))
	// {

		
	// }

	var barcode=row.Barcode;

	var updatedFileObj = await uploadQuantity(row, barcode);


	modifiedObj = {
		inventoryProductBarcode: updatedFileObj.Barcode,
		productQuantity: updatedFileObj.Quantity
	}

    var json = await JSON.stringify(modifiedObj);

	// var json = JSON.stringify(row);
	var url = getInventoryUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
			// Recursive call on next row
	   		uploadRows();  
	   },
	   error: function(response){
			row.error = JSON.parse(response.responseText).message;
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

// Done
function displayInventoryList(data){
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	var serial=0;
	for (var i = data.length - 1; i >= 0; i--) {
		serial++;
		var e = data[i];
		var buttonHtml = ' <button class="btn btn-primary btn-sm" onclick="displayEditInventory(' + e.inventoryProductBarcode + ')"><i class="bi bi-pencil-square"></i> Edit</button>'
		var row = '<tr>'
		+ '<td>' + serial + '</td>'
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
	var url = getInventoryUrl() + "/" + barcode;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);   
	   },
	   error: function(data){
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
	var fileName = document.getElementById("inventoryFile").files[0].name;
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
	$('#upload-invetory-modal').trigger('reset');
	$("#download-errors").hide();
	$("#error-row").hide();
	$("#process-data").show();
 	resetUploadDialog(); 	
	$('#upload-inventory-modal').modal('toggle');
}

// Displays modal with given fields and detects id from displayEditInvnetory method
function displayInventory(data){
	$("#inventory-edit-form input[name=inventoryProductBarcode]").val(data.inventoryProductBarcode);		
	$("#inventory-edit-form input[name=productQuantity]").val(data.productQuantity);	
	$('#edit-inventory-modal').modal('toggle');
}

function addButton(){
	$("#inventory-form").trigger('reset');
  }

//INITIALIZATION CODE
function init(){
	getInventoryList();
	role = $("meta[name=userRole]").attr("content");
    if(role == "supervisor"){
        $("#top-buttons").show();
		$("#edit-column").show();
    }
	$('#add-button-inventory').click(addButton);
	$('#inventory-form').submit(addInventory);
	$('#inventory-edit-form').submit(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#inventoryFile').on('change', updateFileName)
}

$(document).ready(init);

