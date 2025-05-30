$(document).ready(function () {
    $supplierBtn = $("#supplierBtn");
    $reqBtn = $("#reqBtn");

    handleEvent()

    function handleEvent(){
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
    function removeActiveTable(){
        $(".table").removeClass("active");
    }

    function setActive(table) {
        table.addClass("active");
    }
})