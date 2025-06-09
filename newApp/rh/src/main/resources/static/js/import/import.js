$(document).ready(function () {
    const btns = $(".btn-group").children("button");

    const employeeBtn = $("#employeeBtn");
    const salaryComponentBtn = $("#salaryComponentBtn");
    const salarySlipBtn = $("#salarySlipBtn");

    const tableEmployee = $("#table-employee")
    const tableSalaryComponent = $("#table-salary-component")
    const tableSalarySlip = $("#table-salary-slip")

    const employeeResultContainer = $("#info-employee")
    const salaryComponentResultContainer = $("#info-salary-component")
    const salarySlipResultContainer = $("#info-salary-slip")

    init()

    function init(){
        handleEvent()
        initDataTable()
    }

    function initDataTable(){
        applyDataTableNoFilterNoSoringSetting(tableEmployee);
        applyDataTableNoFilterNoSoringSetting(tableSalaryComponent);
        applyDataTableNoFilterNoSoringSetting(tableSalarySlip);
    }

    function applyDataTableNoFilterNoSoringSetting(table){
        table.DataTable({
            "ordering": false,   // Disable sorting
            "searching": false,  // Disable searching/filtering
            "paging": true,      // Optionally keep paging enabled
            "info": true
        })
    }

    function handleEvent() {
        $("#delete-data").submit(function (e) {
            e.preventDefault()
            deleteData()
        });

        btns.click(function () {
            btns.removeClass("active");
            $(this).addClass("active")
        })

        employeeBtn.click(function () {
            setActive(employeeResultContainer);
        });

        salaryComponentBtn.click(function () {
            setActive(salaryComponentResultContainer);
        });

        salarySlipBtn.click(function () {
            setActive(salarySlipResultContainer);
        });
    }

    function removeActiveContainer() {
        employeeResultContainer.removeClass("active");
        salaryComponentResultContainer.removeClass("active");
        salarySlipResultContainer.removeClass("active");
    }

    function setActive(resultContainer) {
        removeActiveContainer()
        resultContainer.addClass("active");
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

                    var errorList = `
                        <ul class='p-0 m-0'>
                            <li>${response.message}</li>
                        </ul>`
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
})