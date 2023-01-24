function getOrderUrl() {
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/order";
}

// BUTTON ACTIONS
var order = [];

function addOrderItem(event) {
	event.preventDefault();
	var $form = $("#order-add-form");
	var json = toJson($form);
	var orderItem = JSON.parse(json);
	
	if(orderItem.productQuantity > 0 && orderItem.productSellingPrice >= 0)
	{
		order.push(orderItem);
		$("#order-add-form").trigger("reset");
		console.log(order);
		displayOrderItemListAdd(order);
	}

	else if(orderItem.ProductQuantity <= 0) {
		console.log("Quantity should be greater than 0");
		}
	else if(orderItem.productSellingPrice < 0) {
		console.log("Selling price should be greater than or equal to 0");
	}
}
function cancelOrder(event) {
	order = [];
	$("#order-add-form").trigger("reset");
	$("#orderitemadd-table tbody").remove();
}

function addOrder(event) {
	var url = getOrderUrl();
	console.log("submit order");
	console.log(JSON.stringify(order));
	console.log("order : ", order);
	
	$.ajax({
		url : url,
		type : 'POST',
		data : JSON.stringify(order),
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			order = [];
			getOrderList();
			$("#order-add-form").trigger("reset");
			$('#add-order-modal').modal('hide');
			$("#orderitemadd-table tbody").empty();
		},
		error : function(response) {
			console.log("entered error");
			handleAjaxError(response)
		}
	});
	return false;
}

function getInvoice(id) {
    var url = getOrderUrl() + "/invoice/" + id;
    console.log(url);
    $.ajax({
        url: url,
        type: 'GET',
        responseType: 'arraybuffer',
        success: function (response) {
            var arrayBuffer = base64ToArrayBuffer(response);
            function base64ToArrayBuffer(base64) {
                var binaryString = window.atob(base64);
                var binaryLen = binaryString.length;
                var bytes = new Uint8Array(binaryLen);
                for (var i = 0; i < binaryLen; i++) {
                    bytes[i] = binaryString.charCodeAt(i);
                }
                return bytes;
            }

            var blob = new Blob([arrayBuffer], {type: "application/pdf"});
            var link = window.URL.createObjectURL(blob);
            window.open(link, '');
        },
        error: handleAjaxError
    })
}

function updateOrderItem(event) {
	// Get the ID
	var id = $("#order-edit-form input[name=id]").val();
	// var orderId = $("#order-edit-form input[name=orderId]").val();
	var url = getOrderUrl() + "/item/" + id;
	// Set the values to update
	var $form = $("#order-edit-form");
	var json = toJson($form);

	$.ajax({
		url : url,
		type : 'PUT',
		data : json,
		headers : {
			'Content-Type' : 'application/json'
		},
		success : function(response) {
			$('#edit-orderitem-modal').modal('hide');
			// getOrderItemList(orderId);
			getOrderList();	
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});

	return false;
}
function getOrderList() {
	console.log("getting order list");
	var url = getOrderUrl();
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderList(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

function getOrderItemList(id) {
	console.log("getting order item list");
	var url = getOrderUrl() + "/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderItem(data);
			console.log(data);
		},
		error : handleAjaxError
	});
}

function deleteOrderItem(id) {
	var url = getOrderUrl() + "/item/" + id;

	$.ajax({
		url : url,
		type : 'DELETE',
		success : function(data) {
			getOrderItemList(id);
			getOrderList(id);
		},
		error : function(response) {
			handleAjaxError(response)
		}
	});
}

function deleteRow(data) {
	console.log(data);
	order = order.filter((item) => {
		return item.productBarcode !== data;
	});
	displayOrderItemListAdd(order);
}


function displayOrderList(data) {
	var $tbody = $('#order-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var date = new Date(e.datetime);
        date = date.toLocaleString();
		var buttonHtml = ' <button type="button" class="btn btn-secondary" onclick="getOrderItemList('
				+ e.orderId + ')">View</button>'
		buttonHtml += '<button type="button" class="btn btn-secondary" onclick="getInvoice('
                            				+ e.orderId + ')">Invoice</button>'
		var row = '<tr>' + '<td>' + e.orderId + '</td>' + '<td>' + date + '</td>'+ '<td>' + e.billAmount + '</td>' + '<td>' + buttonHtml
				+ '</td>' + '</tr>';
		$tbody.append(row);
	}
}
//edited the button edit// class="btn text-bodye" data-toggle="tooltip" title="Edit" 
function displayOrderItem(data) {
	var $tbody = $('#orderitem-table').find('tbody');
	$tbody.empty();
	console.log("dataaa : ", data);
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = ' <button type="button"onclick="displayEditOrderItem('
				+ e.orderItemId + ')">Edit</button>'
		var row = '<tr>' + '<td>' + e.name + '</td>' + '<td>' + e.productBarcode
				+ '</td>' + '<td>' + e.productQuantity + '</td>' + '<td>'
				+ e.productSellingPrice + '</td>' + '<td>' + buttonHtml + '</td>'
				+ '</tr>';
		$tbody.append(row);
	}
	$('#order-display-modal').modal('toggle');
}

function displayOrderItemListAdd(data) {
	var $tbody = $('#orderitemadd-table').find('tbody');
	$tbody.empty();
	for ( var i in data) {
		var e = data[i];
		var buttonHtml = '<button type="button" class="btn btn-danger" title="Delete" onclick="deleteRow(\'' + e.productBarcode + '\')">delete</button>'
		var row = '<tr>' + '<td>' + e.productBarcode + '</td>' + '<td>' + e.productQuantity
				+ '</td>' + '<td>' + e.productSellingPrice + '</td>'+ '<td>' + buttonHtml + '</td>' + '</tr>';
		$tbody.append(row);
	}
}

function displayEditOrderItem(id) {
	$('#order-display-modal').modal('hide');
	var url = getOrderUrl() + "/item/" + id;
	$.ajax({
		url : url,
		type : 'GET',
		success : function(data) {
			displayOrderItemEdit(data);
		},
		error : handleAjaxError
	});
}

function displayOrderItemEdit(data) {
	console.log("dataa : ",data);
	$("#order-edit-form input[name=productBarcode]").val(data.productBarcode);
	$("#productBarcode").html("" + data.productBarcode);
	$("#mrp").html("" + data.mrp);
	$("#availableQuantity").html("" + data.availableQuantity);
	$("#order-edit-form input[name=productQuantity]").val(data.productQuantity);
	$("#order-edit-form input[name=productSellingPrice]").val(data.productSellingPrice);
	$("#order-edit-form input[name=id]").val(data.orderItemId);
	$("#order-edit-form input[name=orderId]").val(data.id);
	$('#edit-orderitem-modal').modal('toggle');
	
}

// INITIALIZATION CODE
function init() {
	$('#order-add-form').submit(addOrderItem);
	$('#order-edit-form').submit(updateOrderItem);
	$('#cancel-order').click(cancelOrder);
	$('#submit-order').click(addOrder);
	$('#refresh-data').click(getOrderList);
}

$(document).ready(init);
$(document).ready(getOrderList);