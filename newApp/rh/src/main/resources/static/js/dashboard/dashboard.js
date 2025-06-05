$(document).ready(function () {
    const $tbody = $("#dashboard tbody");
    const tbodyForStatPerMonth = $("#statistic-per-month tbody");
    const selectMonth = $("#select-month");
    const selectedTotalSalarySlipYear = $("#year");

    init();

    function init() {
        fetchTotalSalarySlipPerMonth();
        filterResults();
        bindEvents();
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
    function bindEvents() {
        selectMonth.change(filterResults);
        selectedTotalSalarySlipYear.change(fetchTotalSalarySlipPerMonth)
    }
    function filterResults() {
        const month = $("#select-month").val();

        $.ajax({
            url: "/salary-slips/details",
            method: "GET",
            data: {month : month || null},
            dataType: "json",
            beforeSend: function() {
                showLoading()
            },
            success: function(data) {
                if (data){
                    salaryEmployeeTableContent(data);
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
                    $tbody.empty();
                    $tbody.append(`<tr><td colspan='6' class='text-center text-danger'>${message}</td></tr>`);
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
    function fetchTotalSalarySlipPerMonth() {
        const year = selectedTotalSalarySlipYear.val();

        $.ajax({
            url: "/salary-slips/total-per-month",
            method: "GET",
            data: {year : year || null},
            dataType: "json",
            beforeSend: function() {
                showLoading()
            },
            success: function(data) {
                if (data){
                    monthlySalaryEmployeeSummaryTableContent(data);
                    return true;
                }
                alert("An error occured when fetching data!")
            },
            error: function(xhr) {
                if (xhr){
                    const responseText = xhr.responseText;
                    const response = (responseText != null) ? JSON.parse(responseText) : "Internal Server Error";
                    const message = (responseText != null) ? response.message : response

                    tbodyForStatPerMonth.empty();
                    tbodyForStatPerMonth.append(`<tr><td colspan='3' class='text-center text-danger'>${message}</td></tr>`);
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
    function salaryEmployeeTableContent(data) {
        $tbody.empty();

        if (!data || data.length === 0)
            return $tbody.append("<tr><td colspan='6' class='text-center text-warning'>Empty</td></tr>");

        data.forEach(function(salarySlip) {
            $tbody.append(`
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
    function monthlySalaryEmployeeSummaryTableContent(data) {
        tbodyForStatPerMonth.empty();

        if (!data || data.length === 0)
            return tbodyForStatPerMonth.append("<tr><td colspan='6' class='text-center text-warning'>Empty</td></tr>");

        data.forEach(function(stat) {
            tbodyForStatPerMonth.append(`
                <tr>
                  <td>${getMonthName(stat.month)}</td>
                  <td class="text-end">${currencyFormat(stat.totalAmount)}</td>
                  <td>
                        ${salaryEmployeeDetailsHtmlContent(stat.details)}
                  </td>
                </tr>
            `);
        });
    }
    function salarySlipDetailsHtmlListContent(details, total){
        if(details == null){
            return ""
        }

        var list = "<ul class='salary-detail-container'>"
        details.forEach(function(detail) {
            list += `<li><span>${detail.salaryComponent}:</span> <span>${currencyFormat(detail.amount)}</span></li>`
        })

        list += `<li><span><b>Total</b>:</span> <span>${currencyFormat(total)}</span></li></ul>`;
        return list;
    }
    function salaryEmployeeDetailsHtmlContent(details){
        if(details == null){
            return ""
        }

        var list = "<ul class='salary-detail-container'>"
        details.forEach(function(detail) {
            list += `<li><span>${detail.salary_component}:</span> <span>${currencyFormat(detail.total_amount)}</span></li>`
        })
        return list;
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

    function getMonthName(monthNumber) {
        const months = [
            'January', 'February', 'March', 'April', 'May', 'June',
            'July', 'August', 'September', 'October', 'November', 'December'
        ];
        return months[monthNumber - 1] || '';
    }
});
