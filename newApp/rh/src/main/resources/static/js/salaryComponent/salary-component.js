$(document).ready(function () {
    const tableListEmployee = $("#table-salary-component");
    let salaryComponentDataTable;

    init();

    function init() {
        dataTable();
    }

    function dataTable() {
        salaryComponentDataTable = tableListEmployee.DataTable({destroy: true});
    }
});
