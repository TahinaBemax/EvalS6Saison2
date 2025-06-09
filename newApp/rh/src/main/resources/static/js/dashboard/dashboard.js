$(document).ready(function () {
    const tableMonthlyEmployeeSalary = $("#monthly-employee-salary")
    const tableForStatPerMonth = $("#statistic-per-month");

    let monthlyDataTable;
    let monthlySummaryDataTable;

    const tbodyMonthlyEmployeeSalary = tableMonthlyEmployeeSalary.children("tbody");
    const tbodyForStatPerMonth = tableForStatPerMonth.children("tbody");

    const selectedMonth = $("#select-month");
    const selectedTotalSalarySlipYear = $("#year");

    init();

    function init() {
        fetchTotalSalarySlipPerMonth();
        fetchMonthlyEmployeeSalary();
        bindEvents();
    }


    function bindEvents() {
        selectedMonth.change(fetchMonthlyEmployeeSalary);
        selectedTotalSalarySlipYear.change(fetchTotalSalarySlipPerMonth)
    }

    function monthlyEmployeeSalaryDataTable(){
        monthlyDataTable = tableMonthlyEmployeeSalary.DataTable({
            destroy: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50, 100]
        });
    }
    function monthlyEmployeeSalarySummaryDataTable(){
        // Initialize the statistics table
        monthlySummaryDataTable = tableForStatPerMonth.DataTable({
            destroy: true,
            pageLength: 3,
            lengthMenu: [3, 6, 9, 12]
        });
    }
    function fetchMonthlyEmployeeSalary() {
        const month = $("#select-month").val();

        $.ajax({
            url: "/salary-slips/details",
            method: "GET",
            data: {month : month || null},
            dataType: "json",
            beforeSend: function() {
                tableFetchDataLoadingAnimation(tableMonthlyEmployeeSalary)
            },
            success: function(data) {
                if (data){
                    // First destroy the existing DataTable
                    if ($.fn.DataTable.isDataTable(tableMonthlyEmployeeSalary)) {
                        monthlyDataTable.destroy();
                    }
                    // Clear the table content
                    tbodyMonthlyEmployeeSalary.empty();

                    const hasData = salaryEmployeeTableContent(data);
                    if (hasData) {
                        monthlyEmployeeSalaryDataTable()
                    }
                }
            },
            error: function(xhr) {
                xhrErrorHandler(xhr, tbodyMonthlyEmployeeSalary, 6)
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
                tableFetchDataLoadingAnimation(tableForStatPerMonth)
            },
            success: function(data) {
                if (data){
                    // First destroy the existing DataTable
                    if ($.fn.DataTable.isDataTable(tableForStatPerMonth)) {
                        monthlySummaryDataTable.destroy();
                    }
                    // Clear the table content
                    tbodyForStatPerMonth.empty();
                    
                    const hasData = monthlySalaryEmployeeSummaryTableContent(data);
                    if (hasData) {
                        monthlyEmployeeSalarySummaryDataTable();
                    }
                }
            },
            error: function(xhr) {
                xhrErrorHandler(xhr, tbodyForStatPerMonth, 3);
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
        tbodyMonthlyEmployeeSalary.empty();

        if (!data || data.length === 0) {
            tbodyMonthlyEmployeeSalary.append("<tr><td colspan='6' class='text-center text-warning'>Empty</td></tr>");
            return false;
        }

        data.forEach(function(salarySlip) {
            tbodyMonthlyEmployeeSalary.append(`
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

        return true;
    }
    function monthlySalaryEmployeeSummaryTableContent(data) {
        tbodyForStatPerMonth.empty();

        if (!data || data.length === 0) {
            tbodyForStatPerMonth.append("<tr><td colspan='6' class='text-center text-warning'>Empty</td></tr>");
            return false;
        }

        data.forEach(function(stat) {
            tbodyForStatPerMonth.append(`
                <tr>
                  <td>${getMonthName(stat.month)}</td>
                  <td class="text-end">
                    <a href="/detail-per-employee?month=${stat.month}&year=${stat.year}">${currencyFormat(stat.totalAmount)}</a>
                  </td>
                  <td>
                        ${salaryEmployeeDetailsHtmlContent(stat.details)}
                  </td>
                </tr>
            `);
        });

        return true;
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
