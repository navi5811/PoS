function getSalesUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report/sales";
}

function displaySalesList(data){
	var $tbody = $('#sales-table').find('tbody');
	$tbody.empty();
	var j=0;
	for(var i in data){
		j++;
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + j + '</td>'
		+ '<td>' + e.brand + '</td>'
		+ '<td>'  + e.category + '</td>'
        + '<td>'  + e.quantity + '</td>'
        + '<td>'  + e.totalAmount + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}
    function getList(){
        //Set the values to update
        var $form = $("#sales-form");
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
           success: function(response) {
                   displaySalesList(response);  
           },
           error: handleAjaxError
        });
    
        return false;
    }

    function init()
    {
        console.log("inside the function init");
        $("#get-sales").click(getList);
        
    }
    $(document).ready(init);
    $(document).ready(getList);

