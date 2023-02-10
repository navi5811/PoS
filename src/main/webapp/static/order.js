var closeinv;
var closeid;
function getOrderUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/order";
}
function getInventoryUrl() {
  var baseUrl = $("meta[name=baseUrl]").attr("content");
  return baseUrl + "/api/inventory";
}

// BUTTON ACTIONS
var order = [];

function addOrderItem(event) {
  event.preventDefault();
  var $form = $("#order-add-form");
  var json = toJson($form);
  var orderItem = JSON.parse(json);

  var barcode = orderItem.productBarcode;
  var flag = false;

  var url = getInventoryUrl() + "/" + barcode;



  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      console.log("entering display inventory");

      var orderedQuantity = orderItem.productQuantity;

      var availableQuantity = data.productQuantity;

      if (orderedQuantity > availableQuantity) {
        flag = true;
        alert("Order quantity cannot be greater than available quantity");
      }
      if (orderItem.productSellingPrice > data.mrp) {
        flag = true;
        alert("order selling price must be less than MRP");
      }



    },
    error: function (data) {
      console.log("why this error is occuring");
      flag = true;
      alert("barcode is not available");
    },
  }).then(function () {
    if (orderItem.productQuantity < 0 || orderItem.productSellingPrice < 0) {
      flag = true;
    }
    var k = 0;
    for (var p in order) {
      console.log("producrtbarcode is ", order[k].productBarcode);
      console.log("orderitem barcode", orderItem.productBarcode);
      if (order[k].productBarcode == orderItem.productBarcode && order[k].productSellingPrice != orderItem.productSellingPrice) {
        flag = true;
        alert("same barcode product cannot be entered again");
      }
      else if (order[k].productBarcode == orderItem.productBarcode && order[k].productSellingPrice == orderItem.productSellingPrice) {
        order[k].productQuantity = parseInt(orderItem.productQuantity) + parseInt(order[k].productQuantity);
        flag = true;
        $("#order-add-form").trigger("reset");
        displayOrderItemListAdd(order);
      }
      k++;
    }
    if (flag == false) {
      order.push(orderItem);
      $("#order-add-form").trigger("reset");
      console.log(order);
      displayOrderItemListAdd(order);
    } else if (orderItem.ProductQuantity <= 0) {
      alert("Quantity should be greater than 0");
      console.log("Quantity should be greater than 0");
    } else if (orderItem.productSellingPrice < 0) {
      alert("Selling price should be greater than or equal to 0");
      console.log("Selling price should be greater than or equal to 0");
    }
  });
}

function cancelOrder(event) {
  order = [];
  $("#order-add-form").trigger("reset");
  
}

function addOrder(event) {
  var url = getOrderUrl();
  console.log("submit order");
  console.log(JSON.stringify(order));
  console.log("order : ", order);

  $.ajax({
    url: url,
    type: "POST",
    data: JSON.stringify(order),
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      order = [];
      getOrderList();
      $("#order-add-form").trigger("reset");
      $("#add-order-modal").modal("hide");
      $("#orderitemadd-table tbody").empty();
    },
    error: function (response) {
      console.log("entered error");
      handleAjaxError(response);
    },
  });
  return false;
}

function getInvoice(id) {

  console.log("entered invoice");
  var url = getOrderUrl() + "/invoice/" + id;
  console.log(url);
  $.ajax({
    url: url,
    type: "GET",
    responseType: "arraybuffer",
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

      var blob = new Blob([arrayBuffer], { type: "application/pdf" });
      var link = window.URL.createObjectURL(blob);
      window.open(link, "");
      getOrderList();

    },
    error: handleAjaxError,
  });
}




function updateOrderItem(event) {
  // Get the ID
  var id = $("#order-edit-form input[name=id]").val();
  var orderId = $("#order-edit-form input[name=orderId]").val();
  // var orderId = $("#order-edit-form input[name=orderId]").val();

  var url = getOrderUrl() + "/item/" + id;
  // Set the values to update
  var $form = $("#order-edit-form");
  var json = toJson($form);
  let invoice=false;

  
  

  

  $.ajax({
    url: url,
    type: "PUT",
    data: json,
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      $("#edit-orderitem-modal").modal("hide");
      // getOrderItemList(orderId);

      getOrderItemList(orderId,invoice);
      getOrderList();
      
    },
    error: function (response) {
      handleAjaxError(response);
      $("#edit-orderitem-modal").modal("hide");
      getOrderItemList(orderId,invoice);
    },
  });

  return false;
}
function getOrderList() {
  console.log("getting order list");
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderList(data);
      console.log(data);
    },
    error: handleAjaxError,
  });
}

function getOrderItemList(id, invoice) {
  console.log("invoice is", invoice);
  console.log("getting order item list");
  var url = getOrderUrl() + "/" + id;
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderItem(data, invoice);
      console.log(data);
    },
    error: handleAjaxError,
  });
}

function deleteOrderItem(id) {
  var url = getOrderUrl() + "/item/" + id;

  $.ajax({
    url: url,
    type: "DELETE",
    success: function (data) {
      getOrderItemList(id);
      getOrderList(id);
    },
    error: function (response) {
      handleAjaxError(response);
    },
  });
}

function deleteRow(data) {
  console.log(data);
  order = order.filter((item) => {
    return item.productBarcode !== data;
  });
  displayOrderItemListAdd(order);
}
function editRow(data) {
  var k = 0;
  for (var p in order) {

    if (order[k].productBarcode == orderItem.productBarcode) {

    }
    k++;
  }
}

function displayOrderList(data) {
  var $tbody = $("#order-table").find("tbody");
  $tbody.empty();
  for (var i = data.length - 1; i >= 0; i--) {
    var e = data[i];
    var date = new Date(e.datetime);
    date = date.toLocaleString();
    var invoice = e.invoiced;
    value = '<button type="button" id="button-invoice" class="btn btn-dark" onclick="getInvoice(' + e.orderId + ')"><i class="bi bi-download"></i> Download Invoice</button>';
    if (invoice == false) {
      var value = '<button type="button" id="button-invoice" class="btn btn-secondary" onclick="getInvoice(' + e.orderId + ')"><i class="bi bi-receipt"></i> Generate Invoice</button>';
    }
    var buttonHtml =
      ' <button type="button" class="btn btn-dark mr-2" onclick="getOrderItemList(' + e.orderId + "," + invoice + ')"><i class="bi bi-eye"></i> View</button>';
    buttonHtml += value;
    var row =
      "<tr>" +
      "<td>" +
      e.orderId +
      "</td>" +
      "<td>" +
      date +
      "</td>" +
      "<td>" +
      e.billAmount +
      "</td>" +
      "<td>" +
      buttonHtml +
      "</td>" +
      "</tr>";
    $tbody.append(row);
    console.log("invoice is ", invoice);
  }
}
//edited the button edit// class="btn text-bodye" data-toggle="tooltip" title="Edit"
function displayOrderItem(data, invoice) {
  $("#view-order-id").html(data[0].id);
  var $tbody = $("#orderitem-table").find("tbody");
  $tbody.empty();
  if (invoice == true) {
    $("#action-button").hide();
  }
  console.log("dataaa : ", data);
  for (var i = data.length - 1; i >= 0; i--) {
    var e = data[i];

    var buttonHtml =
      ' <button class="edit-button btn btn-dark" style="display:none" type="button"onclick="displayEditOrderItem(' + e.orderItemId + "," + invoice + ')"><i class="bi bi-pencil-square"></i> Edit</button>';
    var row =
      "<tr>" +
      "<td>" +
      e.name +
      "</td>" +
      "<td>" +
      e.productBarcode +
      "</td>" +
      "<td>" +
      e.productQuantity +
      "</td>" +
      "<td>" +
      e.productSellingPrice +
      "</td>" +
      "<td>" +
      buttonHtml +"</td>" + "</tr>";
    $tbody.append(row);
  }
  if (invoice == false) {
    $("#action-button").show();
    $(".edit-button").show();
    // $(".edit-button").html("Generate Invoice");
  }
  $("#order-display-modal").modal("toggle");
}

function displayOrderItemListAdd(data) {
  var $tbody = $("#orderitemadd-table").find("tbody");
  $tbody.empty();
  let serial=0;
  for (var i = data.length - 1; i >= 0; i--) {
    serial++;
    var e = data[i];
    var buttonHtml =
      '<button type="button" class="btn btn-danger" title="Delete" onclick="deleteRow(\'' +
      e.productBarcode +
      "')\">delete</button>";
    var row ="<tr>" +
      "<td>" + serial + "</td>" +
      "<td>" + e.productBarcode + "</td>" +
      "<td>" + e.productQuantity + "</td>" +
      "<td>" + e.productSellingPrice + "</td>" +
      "<td>" + buttonHtml + "</td>" +
      "</tr>";
    $tbody.append(row);
  } 
}

function displayEditOrderItem(id , invoice) {
  $("#order-display-modal").modal("hide");
  var url = getOrderUrl() + "/item/" + id;
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderItemEdit(data , invoice);
    },
    error: handleAjaxError,
  });
}

function displayOrderItemEdit(data ,invoice) {
  console.log("dataa : ", data);
  $("#order-edit-form input[name=productBarcode]").val(data.productBarcode);
  $("#productBarcode").html("" + data.productBarcode);
  $("#mrp").html("" + data.mrp);
  $("#availableQuantity").html("" + data.availableQuantity);
  $("#order-edit-form input[name=productQuantity]").val(data.productQuantity);
  $("#order-edit-form input[name=productSellingPrice]").val(data.productSellingPrice);
  $("#order-edit-form input[name=id]").val(data.orderItemId);
  $("#order-edit-form input[name=orderId]").val(data.id);
  $("#edit-orderitem-modal").modal("toggle");
  closeinv=invoice;
  closeid=data.id;
}

function addButton(){
  $("#add-order-modal").modal("toggle");
  $("#order-add-form").trigger("reset");
  $("#orderitemadd-table tbody").empty();
}
function close(){
  console.log("Closer hona chahie");
  $("#edit-orderitem-modal").modal("hide");
  getOrderItemList(closeid,closeinv);
}
// INITIALIZATION CODE
function init() {
  $("#add-button").click(addButton);
  $("#order-add-form").submit(addOrderItem);
  $("#order-edit-form").submit(updateOrderItem);
  $("#cancel-order").click(cancelOrder);
  $("#submit-order").click(addOrder);
  $("#refresh-data").click(getOrderList);
  $("#edit-close-button").click(close);
}

$(document).ready(init);
$(document).ready(getOrderList);
