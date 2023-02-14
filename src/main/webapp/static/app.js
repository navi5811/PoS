
//HELPER METHOD
function toJson($form) {
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for (s in serialized) {
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}


function handleAjaxError(response) {
    var response = JSON.parse(response.responseText);
    sendAlert(response.message);
}

function compare(a, b) {
    if (a.brandName > b.brandName) {
        return 1;
    }
    else if (a.brandName < b.brandName) {
        return -1;
    }
    return 0;
}

function handleJsError(message)
{
    sendAlert(message);
    throw new Error(message);
    
}

function readFileData(file, callback) {
    var config = {
        header: true,
        delimiter: "\t",
        skipEmptyLines: "greedy",
        complete: function (results) {
            callback(results);
            $("#process-data").hide();
            $("#download-errors").show();
            $("#error-row").show();
        }
    }
    Papa.parse(file, config);
}
function sendAlert(message) {
    Toastify({
        text: message,
        duration: 5000,
        close: true,
        gravity: "top", // `top` or `bottom`
        position: "right", // `left`, `center` or `right`
        stopOnFocus: true, // Prevents dismissing of toast on hover
        style: {
            background: "linear-gradient(to right, #B00052, #C93D3D)",
        },
        onClick: function () { } // Callback after click
    }).showToast();

}
function writeFileData(arr) {
    var config = {
        quoteChar: '',
        escapeChar: '',
        delimiter: "\t"
    };

    var data = Papa.unparse(arr, config);
    var blob = new Blob([data], { type: 'text/tsv;charset=utf-8;' });
    var fileUrl = null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click();
}

function hoverActive() {
    $('.nav-item active').removeClass('active').removeAttr('aria-current');
    $('a[href="' + location.pathname + '"]').closest('li').addClass('active').attr('aria-current', 'page');
}

function init() {

    hoverActive();
    var role = $("meta[name=userRole]").attr("content");
    if (role == "supervisor") {
        $("#report_dropdown").show();
    }
}

$(document).ready(init);
