var role;
// Done
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

//BUTTON ACTIONS
// Done
function addBrand(event){
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);
	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
		$('#add-brand-modal').modal('toggle');
	   		getBrandList();  
	   },
	   error: handleAjaxError
	});

	return false;
}

//Done
function updateBrand(event){
	$('#edit-brand-modal').modal('toggle');
	//Get the ID
	var id = $("#brand-edit-form input[name=brandId]").val();	
	console.log("Heyy"+ id);
	var url = getBrandUrl() + "/" + id;
	console.log(id, url);

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		getBrandList();   
	   },
	   error: handleAjaxError
	});

	return false;
}

// Done
function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);  
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
	var file = $('#brandFile')[0].files[0];
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
			$("#upload-brand-modal").modal('toggle');
			getBrandList();
		}
		else{
			getBrandList();
		}
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getBrandUrl();

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
function displayBrandList(data){
	var $tbody = $('#brand-table').find('tbody');
	$tbody.empty();
	var j=0;
	for(var i in data){
		j++;
		var e = data[i];
		var buttonHtml = ' <button class="btn btn-primary"  onclick="displayEditBrand(' + e.brandId + ')">Edit <i class="bi bi-pencil-square"></i></button>'
		var row = '<tr>'
		+ '<td>' + j + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>'  + e.brandCategory + '</td>'
		+ '<td class="edit-button" style="display:none">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}

	if(role == "supervisor"){
     	$(".edit-button").show();
    }
}

// Displays Modal
function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);   
	   },
	   error: handleAjaxError
	});	
}

// To reset the modal values
function resetUploadDialog(){
	//Reset file name
	var $file = $('#brandFile');
	$file.val('');
	$('#brandFileName').html("Choose File");
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
	var $file = $('#brandFile');
	var fileName = $file.val();
	$('#brandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-brand-modal').modal('toggle');
}

// Displays modal with given fields and detects id from displayEditBrand method
function displayBrand(data){
	$("#brand-edit-form input[name=brandName]").val(data.brandName);	
	$("#brand-edit-form input[name=brandCategory]").val(data.brandCategory);		
	$("#brand-edit-form input[name=brandId]").val(data.brandId);	
	$('#edit-brand-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){


	role = $("meta[name=userRole]").attr("content");
    if(role == "supervisor"){
        $("#top-buttons").show();
		$("#edit-column").show();
    }
	$('#add-brand').click(addBrand);
	$('#update-brand').click(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#brandFile').on('change', updateFileName);
}
$(document).ready(init);
$(document).ready(getBrandList);

