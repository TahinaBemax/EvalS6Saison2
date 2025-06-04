$(document).ready(function() {
    const toPdfBtns = $(".toPdfButton");

    init()
    eventHandler()


    function init(){
        initializeDataTable()
        initializeCurrentDate()
    }

    function initializeDataTable(){
        $('#salary-slip-list').DataTable();
    }
    function handleSelectAllCheckbox(){
        $('#select-all').change(function() {
            $('tbody input[type="checkbox"]').prop('checked', $(this).prop('checked'));
        });
    }
    function handleFilterChange(){
        $('#employee, #month, #year, #status').change(function() {
            // Add your filter logic here
        });
    }
    function formSubmissionHandler(){
        $('#salary-slip-list').click(function() {
            if ($('#salary-slip-form')[0].checkValidity()) {
                // Add your save logic here
                $('#modal-salary-slip-form').modal('hide');
            } else {
                $('#salary-slip-form')[0].reportValidity();
            }
        });
    }
    function initializeCurrentDate(){
        // Initialize date inputs with current month
        const now = new Date();
        const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
        const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

        $('#posting-date').val(now.toISOString().split('T')[0]);
        $('#start-date').val(firstDay.toISOString().split('T')[0]);
        $('#end-date').val(lastDay.toISOString().split('T')[0]);

    }

    function eventHandler(){
        formSubmissionHandler();
        handleFilterChange();
        handleSelectAllCheckbox();

        toPdfBtns.click(function () {
            const salarySlipId = $(this).attr("data-id");
            toPdf(salarySlipId)
        });
    }

    function toPdf(salarySlipName){
        if (!salarySlipName || salarySlipName.trim() === "") {
            alert("Salary ID is required!")
            return false;
        }

        const formData = new FormData();
        formData.append("id", salarySlipName);

        $.ajax({
            url: `/salary-slips/toPdf`,
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,
            xhrFields: {
                responseType: 'blob'
            },
            beforeSend: () => { showLoading() },
            success: function (data) {
                const blob = new Blob([data], { type: 'application/pdf' });
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = `salary-slip-${salarySlipName}-${new Date()}.pdf`;
                link.click();
                window.URL.revokeObjectURL(link.href);
            },
            error: function (xhr) {
                const resp = JSON.parse(xhr.responseText);
                alert(resp.message || "Error downloading PDF");
            },
            complete: () => { hideLoading() }
        })
    }
});


/*                const blob = new Blob([data], { type: 'application/pdf' });
                const link = document.createElement('a');
                link.href = window.URL.createObjectURL(blob);
                link.download = `salary-slip-${salarySlipName}.pdf`;
                link.click();
                window.URL.revokeObjectURL(link.href);*/