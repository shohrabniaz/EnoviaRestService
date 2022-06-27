$(document).ready(function () {
 
    $("#btnSubmit").click(function (event) {

        //stop submit the form, we will post it manually.
        event.preventDefault();

        fire_ajax_submit();

    });
    
//    $("#btnDownload").click(function (event) {
//
////         event.preventDefault();
//        fire_ajax_download();
//
//    });

});
function createCORSRequest(method, url) {
  var xhr = new XMLHttpRequest();
  if ("withCredentials" in xhr) {

    // Check if the XMLHttpRequest object has a "withCredentials" property.
    // "withCredentials" only exists on XMLHTTPRequest2 objects.
    xhr.open(method, url, true);

  } else if (typeof XDomainRequest != "undefined") {

    // Otherwise, check if XDomainRequest.
    // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
    xhr = new XDomainRequest();
    xhr.open(method, url);

  } else {

    // Otherwise, CORS is not supported by the browser.
    xhr = null;

  }
  return xhr;
}
function fire_ajax_submit() {
    // Get form
    var form = $('#fileUploadForm')[0];

    var data = new FormData(form);
    data.append("CustomField", "This is some extra data, testing");
//    alert('ajax to upload');
    $("#btnSubmit").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: "./api/upload/multi",
        data: data,
        //http://api.jquery.com/jQuery.ajax/
        //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        timeout: 600000,
        success: function (data) {

            $("#result").text(data);
            console.log("SUCCESS : ", data);
//            alert("Success");
            $("#btnSubmit").prop("disabled", false);
            document.getElementById("resultHead").style.display = "block";
        },
        error: function (e) {

            $("#result").text(e.responseText);
            console.log("ERROR : ", e);
//            alert("ERROR");
            $("#btnSubmit").prop("disabled", false);
            

        }
    });
}
//    function fire_ajax_download() {
//        var fileName = $("#fileName2").val();
//        var location = $("#location2").val();
//    $.ajax({
//        url: "http://localhost:8080/file-upload-download/downloadFileFromUI",
//        cache: false,
//        timeout: 600000,
//        data: {
//                fileName: fileName,
//                location: location
//              },
//        success: function (data) {
//           alert("SUCCECC2" );
//
//
//        },
//        error: function (e) {
//                alert('ERROR');
//
//        }
//    });
//
//}



















//    <h1>THROUGH AJAX CALL</h1>
//        
//        Enter Download FileName: <br/>
//        <input type="text" name="fileName2" id="fileName2"/>
//        <select name="fileName2" id="fileName2">
//            <option value=""> -- </option>
//            <option th:each="file : ${//listOfFileName}"
//                    th:utext="${//file}"
//                    th:value="${//file}"
//               />
//        </select>
        
//        <br/><br/>
//        Enter Save Location: <br/>
//        <input type="text" name="location2" id="location2"/><br/><br/>
//        <button type="submit">//Download</button>
//        <input type="submit" value="Download" id="btnDownload"/>