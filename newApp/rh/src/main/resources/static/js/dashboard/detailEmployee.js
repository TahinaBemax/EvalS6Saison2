$(document).ready(function () {
    init();
    const tableDetailSalaryEmployee = $("#details-employee")
    const tbodyDetailSalaryEmployee = tableDetailSalaryEmployee.children("tbody");

    function init() {
        dataTable();
    }
    function dataTable(){
        $(".table").DataTable({
            "columnDefs": [
                {
                    "orderable": false, "targets": [0]
                }
            ]
        });
    }

    /* +++++ +++++ ++++++
        FETCH DATA
       ++++ +++++ +++++*/
    function fetchEmployeeDetails() {
        $.ajax({
            url: "/salary-slips/details",
            method: "GET",
            data: {month : null, year: null},
            dataType: "json",
            beforeSend: function() {
                tableFetchDataLoadingAnimation(tableDetailSalaryEmployee)
            },
            success: function(data) {
                if (data){
                    EmployeeDetailsTableContent(data);
                    return true;
                }
                alert("An error occured when fetching data!")
            },
            error: function(xhr) {
                if (xhr){
                    const responseText = xhr.responseText;
                    const response = (responseText != null) ? JSON.parse(responseText) : "Internal Server Error";
                    const message = (responseText != null) ? response.message : response

                    alert(message)
                    tbodyDetailSalaryEmployee.empty();
                    tbodyDetailSalaryEmployee.append(`<tr><td colspan='6' class='text-center text-danger'>${message}</td></tr>`);
                    console.error("Filter Error:", response);
                    return false;
                }
                alert("Internal Server Error")
            },
            complete: function(){
                hideLoading()
            }
        });
    }



    /* ++++++ +++++ ++++++++
        HTML CONTENT
       ++++++ +++++ +++++++*/
    function EmployeeDetailsTableContent(data) {
        tbodyDetailSalaryEmployee.empty();

        if (!data || data.length === 0)
            return tbodyDetailSalaryEmployee.append("<tr><td colspan='6' class='text-center text-warning'>Empty</td></tr>");

        data.forEach(function(salarySlip) {
            tbodyDetailSalaryEmployee.append(`
                <tr>
                  <td>${salarySlip.employeeName}</td>
                  <td>${salarySlip.startDate}</td>
                  <td>${salarySlip.endDate}</td>
                  <td>
                        ${salarySlipDetailsHtmlListContent(salarySlip.salaryDetailEarnings, salarySlip.grossPay)}
                  </td>
                  <td>
                        ${salarySlipDetailsHtmlListContent(salarySlip.salaryDetailDeductions, salarySlip.totalDeduction)}
                  </td>
                  <td class="text-end">
                        ${currencyFormat(salarySlip.netPay)}
                  </td>
                </tr>
            `);
        });
    }

    /* ++++++ +++++ ++++++++
        HELPER
        ++++++ +++++ +++++++*/

    function currencyFormat(amount){
        var currenyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR'
        })

        return currenyFormatter.format(amount);
    }
});
