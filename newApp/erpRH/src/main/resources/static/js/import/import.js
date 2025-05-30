$(document).ready(function () {
    $supplierBtn = $("#supplierBtn");
    $reqBtn = $("#reqBtn");

    handleEvent()

    function handleEvent() {
        $("#delete-data").submit(function (e) {
            e.preventDefault()
            deleteData()
        });

        $supplierBtn.click(function () {
            removeActiveTable();
            $tableSupplier = $("#table-supplier");
            setActive($tableSupplier);
        });

        $reqBtn.click(function () {
            removeActiveTable();
            $tableReqForQuotation = $("#table-req-for-quotation");
            setActive($tableReqForQuotation);
        });
    }

    function removeActiveTable() {
        $(".table").removeClass("active");
    }

    function setActive(table) {
        table.addClass("active");
    }

    function deleteData() {
        $deleteDataBtn = $("#delete")
        const userResponse = confirm("Would you like to delete all existing data?")

        if (userResponse) {
            $.ajax({
                url: "/delete-data",
                method: "POST",
                beforeSend: function () {
                    LoadingButtonOn($deleteDataBtn)
                },
                complete: function () {
                    LoadingButtonOff($deleteDataBtn, "Delete Data")
                },
                success: function (data) {
                    console.log(data)
                    if (data.status === "success") {
                        showSuccessMessage(data.message)
                        return null;
                    }
                    showErrorMessage(data.message);
                },
                error: function (xhr) {
                    console.log(xhr)
                    const response = JSON.parse(xhr.responseText);
                    var errorList = "<ul class='p-0 m-0'>"

                    for (const message of response) {
                        errorList += `<li>${message.message}</li>`
                    }
                    errorList += "</ul>"

                    showErrorMessage(errorList);
                }
            })
        }

    }

    function showSuccessMessage(message) {
        removeMessages()
        $("#form-container").prepend(`<div class="my-2 alert alert-success">${message}</div>`)
    }

    function showErrorMessage(message) {
        removeMessages()
        $("#form-container").prepend(`<div class="my-2 alert alert-danger">${message}</div>`)
    }

    function removeMessages() {
        $("div.alert-success").remove();
        $("div.alert-danger").remove();
    }

    function LoadingButtonOn(btn) {
        btn.val("Loading...")
        btn.prop("disabled", true)
    }

    function LoadingButtonOff(btn, btnText) {
        btn.val(btnText)
        btn.prop("disabled", false)
    }

})