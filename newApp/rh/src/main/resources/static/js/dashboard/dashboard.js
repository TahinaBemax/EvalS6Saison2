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
                  <td class="text-start">
                        ${salarySlipDetailsList(salarySlip.salaryDetailEarnings, salarySlip.totalEarnings, salarySlip.currency)}
                  </td>
                  <td class="text-start">
                        ${salarySlipDetailsList(salarySlip.salaryDetailDeductions, salarySlip.totalDeduction, salarySlip.currency)}
                  </td>
                  <td class="text-end">
                        ${salarySlip.netPay + '' + salarySlip.currency}
                  </td>
                </tr>
            `);
        });
    }

    function salarySlipDetailsList(details, total, currency){
        if(details == null){
            return ""
        }

        var list = "<ul>"
        details.forEach(function(detail) {
            list += `<li>${detail.salaryComponent}: ${detail.amount + '' + currency}</li>`
        })

        list += `<li><b>Total</b>:${total + "" + currency}</li></ul>`;
        return list;
    }
});
