$(document).ready(function () {
    const $tbody = $("tbody");
    const tableListEmployee = $("#employee-list");
    const filterBtn = $("#filterButton");

    init();

    function init() {
        //fetchDepartments();
        fetchEmployeeData();
        bindEvents();
    }

    function dataTable(){
        $("#employee-list").DataTable({destroy : true});
    }

    function bindEvents() {
        filterBtn.click(fetchEmployeeData);
    }

    function fetchEmployeeData() {
        const fullName = $("#fullNameFilter").val();
        const company = $("#companyFilter").val();

        $.ajax({
            url: "/employees/filter",
            method: "GET",
            data: { fullName: fullName, company: company },
            dataType: "json",
            beforeSend: function() {
                tableFetchDataLoadingAnimation(tableListEmployee)
            },
            success: function(data) {
                if (data && data.status === "success"){
                    updateEmployeeList(data.data);
                    dataTable()
                    return true;
                }
                alert("An error occured when fetching employeee data!")
            },
            error: function(xhr) {
                xhrErrorHandler(xhr, $tbody, 5)
            },
            complete: () =>{
                hideLoading()
            }
        });
    }

    function updateEmployeeList(data) {
        $tbody.empty();
        if (data.length > 0){
            data.forEach(function(employee) {
                $tbody.append(`
                    <tr>
                        <td class="col-actions">
                            <input type="checkbox" class="w-auto">
                            <span>${employee.employeID}</span>
                        </td>
                        <td>${employee.fullName}</td>
                        <td>${employee.company}</td>
                        <td>${employee.gender}</td>
                        <td>${employee.status}</td>
                    </tr>
                `);
            });
        } else {
            $tbody.append(`<tr><td colspan="6" class="text-info">Empty</td></tr>`)
        }
    }
});
