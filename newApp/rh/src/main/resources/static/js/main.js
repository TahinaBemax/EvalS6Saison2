function xhrErrorHandler(xhr, tbody, colspan){
    if (xhr){
        const responseText = xhr.responseText;
        const response = (responseText != null) ? JSON.parse(responseText) : "Internal Server Error";
        const message = (responseText != null) ? response.message : response

        tbody.empty();
        tbody.append(`<tr><td colspan='${colspan}' class='text-center text-danger'>${message}</td></tr>`);
        console.error("Filter Error:", response);
        return false;
    }
}

function showXhrErrorMessage(xhr){
    if (xhr){
        const responseText = xhr.responseText;
        const response = (responseText != null) ? JSON.parse(responseText) : "Internal Server Error";
        const message = (responseText != null) ? response.message : response

        alert("Error: " + message)
        console.error("Filter Error:", response);
        return false;
    }
}