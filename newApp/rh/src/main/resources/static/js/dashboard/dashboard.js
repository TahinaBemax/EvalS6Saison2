$(document).ready(function () {
    const $tbody = $("tbody");
    const selectMonth = $("#select-month");

    init();

    function init() {
        //fetchDepartments();
        filterResults();
        bindEvents();
        dataTable();
    }

    function dataTable(){
        $("#dashboard").DataTable({
            "columnDefs": [
                {
                    "orderable": false, "targets": [0]
                }
            ]
        });
    }

    function bindEvents() {
        selectMonth.change(filterResults);
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
                    dashboardList(data);
                    return true;
                }
                alert("An error occured when fetching data!")
            },
            error: function(xhr) {
                if (xhr){
                    const responseText = xhr.responseText;
                    const response = (responseText != null) ? JSON.parse(responseText) : "Internal Server Error";

                    alert((responseText != null) ? response.message : response)
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

    function dashboardList(data) {
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
                        ${salarySlipDetailsList(salarySlip.salaryDetailEarnings, salarySlip.grossPay)}
                  </td>
                  <td>
                        ${salarySlipDetailsList(salarySlip.salaryDetailDeductions, salarySlip.totalDeduction)}
                  </td>
                  <td class="text-end">
                        ${currencyFormat(salarySlip.netPay)}
                  </td>
                </tr>
            `);
        });
    }

    function salarySlipDetailsList(details, total){
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

    function currencyFormat(amount){
        var currenyFormatter = new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'EUR'
        })

        return currenyFormatter.format(amount);
    }
});
