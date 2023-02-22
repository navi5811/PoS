var closeinv;
var closeid;
var edit = true;
var availableQuantity;
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

function editOrderRow(event) {
  $('#form-barcode').attr('readonly', false);
  event.preventDefault();
  var $form = $("#order-add-form");
  var json = toJson($form);
  var orderItem = JSON.parse(json);

  var barcode = orderItem.productBarcode;
  var status = false;

  var url = getInventoryUrl() + "/" + barcode;

  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      var orderedQuantity = orderItem.productQuantity;

      var availableQuantity = data.productQuantity;

      if (orderedQuantity > availableQuantity) {
        status = true;
        sendAlert("Order quantity cannot be greater than available quantity");
      }
      if (orderItem.productSellingPrice > data.mrp) {
        status = true;
        sendAlert("order selling price must be less than MRP");
      }
    },
    error: function (data) {
      status = true;
      sendAlert("barcode is not available");
    },
  }).then(function () {
    if (orderItem.productQuantity <= 0) {
      sendAlert("Quantity should be greater than 0");
      status = true;
    }
    if (orderItem.productSellingPrice < 0) {
      sendAlert("Selling price should be greater than or equal to 0");
      status = true;
    }
    var k = 0;
    for (var p in order) {

      if (order[k].productBarcode == orderItem.productBarcode && status == false) {

        order[k].productSellingPrice = parseInt(orderItem.productSellingPrice);

        order[k].productQuantity = parseInt(orderItem.productQuantity);

        cancelButton();
      }
      k++;
    }
    if (status == false) {
      $("#order-add-form").trigger("reset");
      displayOrderItemListAdd(order);
    }
  });
}


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
      var orderedQuantity = orderItem.productQuantity;
      availableQuantity = data.productQuantity;

      if (orderedQuantity > availableQuantity) {
        flag = true;
        sendAlert("Order quantity cannot be greater than available quantity :" + availableQuantity);
      }
      if (orderItem.productSellingPrice > data.mrp) {
        flag = true;
        sendAlert("order selling price must be less than MRP");
      }



    },
    error: function (data) {
      flag = true;
      sendAlert("barcode is not available");
    },
  }).then(function () {
    if (orderItem.productQuantity <= 0) {
      sendAlert("Quantity should be greater than 0");
      flag = true;
    }
    if (orderItem.productSellingPrice < 0) {
      sendAlert("Selling price should be greater than or equal to 0");
      flag = true;
    }
    var k = 0;
    for (var p in order) {
      if (order[k].productBarcode == orderItem.productBarcode && order[k].productSellingPrice != orderItem.productSellingPrice) {
        flag = true;
        sendAlert("same barcode product cannot be entered again");
      }

      else if (order[k].productBarcode == orderItem.productBarcode && order[k].productSellingPrice == orderItem.productSellingPrice) {
        order[k].productQuantity = parseInt(orderItem.productQuantity) + parseInt(order[k].productQuantity);

        $("#order-add-form").trigger("reset");

        if (order[k].productQuantity < availableQuantity) {

          displayOrderItemListAdd(order);
        }
        if (order[k].productQuantity > availableQuantity) { sendAlert("Order quantity exceeded the available quantity :" + availableQuantity); }
        flag = true;
      }
      k++;
    }

    if (flag == false) {
      order.push(orderItem);
      $("#order-add-form").trigger("reset");
      displayOrderItemListAdd(order);
    }

  });
}

function cancelOrder(event) {
  order = [];
  $("#order-add-form").trigger("reset");

}

function addOrder(event) {
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: "POST",
    data: JSON.stringify(order),
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      successAlert("Order placed successfully");
      order = [];
      getOrderList();
      $("#order-add-form").trigger("reset");
      $("#add-order-modal").modal("hide");
      $("#orderitemadd-table tbody").empty();
    },
    error: function (response) {
      handleAjaxError(response);
    },
  });
  return false;
}

function getInvoice(id, invoice) {
  var url = getOrderUrl() + "/invoice/" + id;
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
      if (invoice == false) {
        successAlert("Invocie generated successfully");
      }

      if (invoice == true) {
        var blob = new Blob([arrayBuffer], { type: "application/pdf" });
        var link = window.URL.createObjectURL(blob);
        var downloadLink = document.createElement("a");
        downloadLink.href = link;
        var name = "invoice" + id;
        downloadLink.download = name;
        downloadLink.click();
        downloadLink.remove();
      }
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
  let invoice = false;

  $.ajax({
    url: url,
    type: "PUT",
    data: json,
    headers: {
      "Content-Type": "application/json",
    },
    success: function (response) {
      successAlert("OrderItem edited successfully");
      $("#edit-orderitem-modal").modal("hide");
      // getOrderItemList(orderId);

      getOrderItemList(orderId, invoice);
      getOrderList();

    },
    error: function (response) {
      handleAjaxError(response);
      $("#edit-orderitem-modal").modal("hide");
      getOrderItemList(orderId, invoice);
    },
  });

  return false;
}
function getOrderList() {
  var url = getOrderUrl();
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderList(data);
    },
    error: handleAjaxError,
  });
}

function getOrderItemList(id, invoice) {
  var url = getOrderUrl() + "/" + id;
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderItem(data, invoice);
    },
    error: handleAjaxError,
  });
}

function deleteOrderItem(id, orderId, invoice) {
  var url = getOrderUrl() + "/item/" + id;

  $.ajax({
    url: url,
    type: "DELETE",
    success: function (data) {
      sendAlert("OrderItem deleted successfully");
      // $("#order-display-modal").modal("toggle");
      // $("#order-display-modal").modal("toggle");
      getOrderItemList(orderId, invoice);
      $("#order-display-modal").show();

      // getOrderList();
    },
    error: function (response) {
      handleAjaxError(response);
    },
  });
}

function deleteRow(data) {
  order = order.filter((item) => {
    return item.productBarcode !== data;
  });
  displayOrderItemListAdd(order);
}

function displayOrderList(data) {
  var $tbody = $("#order-table").find("tbody");
  $tbody.empty();
  for (var i = data.length - 1; i >= 0; i--) {
    var e = data[i];


    var date = new Date(e.datetime);
    date = date.toLocaleString();
    var invoice = e.invoiced;
    value = '<button type="button" id="button-invoice" class="btn btn-info btn-sm" onclick="getInvoice(' + e.orderId + "," + invoice + ')"><i class="bi bi-download"></i> Download Invoice</button>';
    if (invoice == false) {
      var value = '<button type="button" id="button-invoice" class="btn btn-success btn-sm" onclick="getInvoice(' + e.orderId + "," + invoice + ')"><i class="bi bi-receipt"></i> Generate Invoice</button>';
    }
    var buttonHtml =
      ' <button type="button" class="btn btn-primary btn-sm mr-2" onclick="getOrderItemList(' + e.orderId + "," + invoice + ')"><i class="bi bi-eye"></i> View</button>';
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
      parseFloat(e.billAmount).toFixed(2) +
      "</td>" +
      "<td>" +
      buttonHtml +
      "</td>" +
      "</tr>";
    $tbody.append(row);
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
  let serial=0;
  for (var i = data.length - 1; i >= 0; i--) {
    var e = data[i];

    serial++;
    var buttonHtml =
      ' <button class="edit-button btn btn-primary btn-sm mr-2" style="display:none" type="button"onclick="displayEditOrderItem(' + e.orderItemId + "," + invoice + ')">Edit</button>'

    buttonHtml += '<button type="button" class="delete-orderItem btn btn-danger btn-sm"title="Delete" style="display:none" onclick="deleteOrderItem(' + e.orderItemId + "," + e.id + "," + invoice + ")\">Delete</button>";
    var row =  "<tr>" +
      "<td>" +  serial +  "</td>" +
      "<td>" +  e.name +  "</td>" +
      "<td>" +  e.productBarcode +  "</td>" +
      "<td>" +  e.productQuantity +  "</td>" +
      "<td>" +  parseFloat(e.productSellingPrice).toFixed(2)  +  "</td>" +
      "<td>" +  buttonHtml + "</td>" +
      "</tr>";
    $tbody.append(row);
  }
  if (invoice == false) {
    $("#action-button").show();
    $(".edit-button").show();
    $(".delete-orderItem").show();

    // $(".edit-button").html("Generate Invoice");
  }
  $("#order-display-modal").modal('show');

}

function displayOrderItemListAdd(data) {
  var $tbody = $("#orderitemadd-table").find("tbody");
  $tbody.empty();
  let serial = 0;
  for (var i = data.length - 1; i >= 0; i--) {
    serial++;
    var e = data[i];
    var buttonHtml =
      '<button type="button" class="btn btn-danger btn-sm mr-2" title="Delete" onclick="deleteRow(\'' +
      e.productBarcode +
      "')\">Delete</button>"
    buttonHtml += '<button type="button" class="btn btn-primary btn-sm" title="Delete" onclick="editRow(\'' + e.productBarcode + "')\">Edit</button>";
    var row = "<tr>" +
      "<td>" + serial + "</td>" +
      "<td>" + e.productBarcode + "</td>" +
      "<td>" + e.productQuantity + "</td>" +
      "<td>" + parseFloat(e.productSellingPrice).toFixed(2) + "</td>" +
      "<td>" + buttonHtml + "</td>" +
      "</tr>";
    $tbody.append(row);
  }
}

function editRow(barcode, data) {

  $('#form-barcode').attr('readonly', true);
  $("#order-add-form").trigger("reset");
  $("#add-order").hide();
  $("#submit-order").hide();
  $("#edit-cart-button").removeClass('d-none');
  $("#cancel-orderItem").removeClass('d-none');
  var quantity;
  var sp;
  var k = 0;
  for (var p in order) {
    if (order[k].productBarcode == barcode) {
      quantity = order[k].productQuantity;
      sp = order[k].productSellingPrice;
    }
    k++;
  }
  sendData(barcode, quantity, sp)
}
function sendData(barcode, quantity, sp) {
  $("#edit-cart-button").show();
  $("#cancel-orderItem").show();
  $("#order-add-form input[name=productBarcode]").val(barcode);
  $("#order-add-form input[name=productQuantity]").val(quantity);
  $("#order-add-form input[name=productSellingPrice]").val(sp);
}


function displayEditOrderItem(id, invoice) {
  $("#order-display-modal").modal("hide");
  var url = getOrderUrl() + "/item/" + id;
  $.ajax({
    url: url,
    type: "GET",
    success: function (data) {
      displayOrderItemEdit(data, invoice);
    },
    error: handleAjaxError,
  });
}

function displayOrderItemEdit(data, invoice) {
  $("#order-edit-form input[name=productBarcode]").val(data.productBarcode);
  $("#productBarcode").html("" + data.productBarcode);
  $("#mrp").html("" + data.mrp);
  $("#availableQuantity").html("" + data.availableQuantity);
  $("#order-edit-form input[name=productQuantity]").val(data.productQuantity);
  $("#order-edit-form input[name=productSellingPrice]").val(data.productSellingPrice);
  $("#order-edit-form input[name=id]").val(data.orderItemId);
  $("#order-edit-form input[name=orderId]").val(data.id);
  $("#edit-orderitem-modal").modal("toggle");
  closeinv = invoice;
  closeid = data.id;
}

function addButton() {
  $('#form-barcode').attr('readonly', false);
  $("#add-order-modal").modal("toggle");
  $("#order-add-form").trigger("reset");
  $("#orderitemadd-table tbody").empty();

  $("#add-order").show();
  $("#submit-order").show();
  document.getElementById("edit-cart-button").classList.add("d-none");
  document.getElementById("cancel-orderItem").classList.add("d-none");
  for (var member in order) { delete order[member]; }
}
function close() {
  $("#edit-orderitem-modal").modal("hide");
  getOrderItemList(closeid, closeinv);
}
function cancelButton() {
  $('#form-barcode').attr('readonly', false);
  $("#order-add-form").trigger("reset");
  document.getElementById("edit-cart-button").classList.add("d-none");
  document.getElementById("cancel-orderItem").classList.add("d-none");
  $("#add-order").show();
  $("#submit-order").show();
}
// INITIALIZATION CODE
function init() {
  getOrderList();
  $("#cancel-orderItem").click(cancelButton);

  $("#edit-cart-button").click(editOrderRow);

  $("#add-button").click(addButton);
  $("#order-add-form").submit(addOrderItem);
  $("#order-edit-form").submit(updateOrderItem);
  $("#cancel-order").click(cancelOrder);
  $("#submit-order").click(addOrder);
  $("#refresh-data").click(getOrderList);
  $("#edit-close-button").click(close);
}

$(document).ready(init);
