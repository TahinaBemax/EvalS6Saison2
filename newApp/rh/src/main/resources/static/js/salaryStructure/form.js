$(document).ready(function () {
    const form = $("#salary-structure-form");
    let focusedComponent = null;
    let counterEarning = 1;
    let counterDeduction = 1;
    let components = [];

    const detailBtn = $("#detail-btn");
    const earningDeductionBtn = $("#earning-deduction-btn");
    const accountBtn = $("#account-btn");
    const addRowEarningBtn = $("#add-row-earning");
    const addRowDeductionBtn = $("#add-row-deduction");
    const deleteRowEarningBtn = $("#delete-row-earning");
    const deleteRowDeductionBtn = $("#delete-row-deduction");

    const detailContainer = $('.details')
    const earningDeductionContainer = $('.earning-deduction')
    const accountContainer = $('.account')
    const inputContainers = $(".input-container");

    const earningTbody = $("#earning-table > tbody");
    const deductionTbody = $("#deduction-table > tbody");

    init();

    function init(){
        fetchComponents();
        eventHandler();
    }
    function eventHandler() {
        addAndDeleteRowEarningHandler();
        addAndDeleteRowDeductionHandler();
        componentInputNameFocusHandler()

        form.submit((e) => {
            e.preventDefault();
            submitForm()
        });

        detailBtn.click(() => {
            removeActiveBtns()
            detailBtn.addClass("active")
            showContainer(detailContainer);
        });
        earningDeductionBtn.click(() => {
            removeActiveBtns();
            earningDeductionBtn.addClass("active")
            showContainer(earningDeductionContainer);
        });
        accountBtn.click(() => {
            removeActiveBtns();
            accountBtn.addClass("active")
            showContainer(accountContainer);
        });
    }



    /* TABLE HANDLER */
    function addAndDeleteRowEarningHandler(){
        addRowEarningBtn.click(() => {
            counterEarning++;
            addRow(earningTbody, counterEarning);
        })

        deleteRowEarningBtn.click( () => {
            if (counterEarning === 1)
                return false;
            removeRow(earningTbody);
            counterEarning = (counterEarning > 1) ? counterEarning--: 1;
        })
    }
    function addAndDeleteRowDeductionHandler(){
        addRowDeductionBtn.click(() => {
            counterDeduction++;
            addRow(deductionTbody, counterDeduction);
        })

        deleteRowDeductionBtn.click( () => {
            if (counterDeduction === 1)
                return false;

            removeRow(deductionTbody);
            counterDeduction = (counterDeduction > 1) ? counterDeduction--: 1;
        })
    }
    function addRow(tbody, counter) {
        tbody.append(prepareRowToAdd(counter));
    }
    function removeRow(tbody){
        const $checkboxs = tbody.find(".delete-checkbox");

        $checkboxs.each(function () {
            const isChecked = $(this).is(":checked");
            if (isChecked){
                $(this).closest("tr").remove();
            }
        })

    }
    function prepareRowToAdd(counter) {
        return `
                            <tr>
                                <td><input type="checkbox" class="form-check-input delete-checkbox"></td>
                                <td>${counter}</td>
                                <td class="position-relative">
                                    <input type="text" name="name" class="form-control">
                                </td>
                                <td>
                                    <input type="text" name="abbr" class="form-control">
                                </td>
                                <td>
                                    <input type="text" name="amount" class="form-control">
                                </td>
                                <td class="checkbox">
                                    <input type="checkbox" name="depedendOnPaymentDays" class="form-check">
                                </td>
                                <td class="checkbox">
                                    <input type="checkbox" name="isTaxApplicable" class="form-check">
                                </td>
                                <td class="checkbox">
                                    <input type="checkbox" name="amountBasedOnFormula" class="form-check">
                                </td>
                                <td>
                                    <input type="text" name="formula" class="form-control">
                                </td>
                            </tr>
        `
    }
    function componentInputNameFocusHandler(){
        $('table').on({
            focus: function (){
                focusedComponent = $(this);
                const isEarning = focusedComponent.closest("table.table").prop("id") === "earning-table";
                let filterdComponents = [];

                if (isEarning){
                     filterdComponents = components.filter(c => c.type === "Earning");
                } else {
                     filterdComponents = components.filter(c => c.type === "Deduction");
                }
                // Remove any existing list before appending a new one
                $("#component-container").remove();
                $(this).after(componentsPopUpList(filterdComponents));
            },
            focusout: function (){
                setTimeout(() => $("#component-container").remove(), 900);
            }
        }, "[name='name']");

        setComponentNameValue();
    }
    function setComponentNameValue(){
        $(document).on("click", "#componentlist > li", function (e) {
            e.preventDefault();
            if (focusedComponent) {
                const componentName = $(this).text();
                const component = components.filter(c => c.name === componentName).at(0);
                const tr = focusedComponent.closest("tr");

                tr.find("[name='name']").val(component.name);
                tr.find("[name='abbr']").val(component.salaryComponentAbbr);
                tr.find("[name='amount']").val(component.amount);
                tr.find("[name='depedendOnPaymentDays']").prop("checked",component.dependsOnPaymentDays === 1);
                tr.find("[name='isTaxApplicable']").prop("checked", component.isTaxApplicable === 1);
                tr.find("[name='amountBasedOnFormula']").prop("checked",component.amountBasedOnFormula === 1);
                tr.find("[name='formula']").val(component.formula);

                $("#component-container").remove();
            }
        });
    }
    /* ----- -------- --------- */



    function showContainer(container) {
        hideContainer();
        container.addClass("active");
    }
    function hideContainer() {
        inputContainers.removeClass("active");
    }
    function removeActiveBtns() {
        detailBtn.removeClass("active");
        earningDeductionBtn.removeClass("active");
        accountBtn.removeClass("active");
    }
    function componentsPopUpList(filterdComponents) {
        let componentList = '';

        for (let c of filterdComponents) {
            componentList += `<li>${c.name}</li>`
        }

        return `
            <div class="position-absolute end-0 z-3 bg-white shadow" id="component-container">
                <ul id="componentlist">
                     ${componentList}
                </ul>
            </div>`;
    }



    /* INTERACTION TO SERVER SPRING BOOT */
    function submitForm(){
        $.ajax({
            url: "/salary-structures/save",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(data()),
            beforeSend: () => {
                loading()
            },
            success: function(data) {
                if (data.status === "success"){
                    window.location.href = "/salary-structures";
                } else {
                    alert(data.message);
                }
                hideLoading();
            },
            error: (jqXHR, textStatus, errorThrown) => {
                alert("Internal Server error!");
                console.error(jqXHR)
            }
        })
    }
    function fetchComponents(){
        const url = "/api/salary-components";
        $.ajax({
            url: url,
            method: 'GET',
            success: (data) => {
                if (data.status === "success"){
                    components = data.data;
                } else {
                    alert(data.message)
                }
            },
            error: (jqXHR, textStatus, errorThrown) => {
                alert("Internal Server Error")
                console.log(`textStatus: ${textStatus}, jqXHR:${jqXHR}, error:${errorThrown}`)
            }
        })
    }
    /* ----- ------ ---- */



    /* FORM DATA PREPARATION */
    function data(){
        return {
            name: $("details #name").val(),
            company: $("#company").val(),
            isActive: $("#is-active").val(),
            currency: $("#currency").val(),
            payrollFrequency: $("#payroll-frequency").val(),
            letterHead: $("#letter-head").val(),
            leaveEncashment: $("#leave-encashment").val(),
            modeOfPayment: $("#modeOfPayment").val(),
            paymentAccount:$("#paymentAccount").val(),
            salarySlipBasedOnTimesheet: $("#based-on-timesheet").val(),
            earnings: earningsData(),
            deductions: deductionsData()
        }
    }
    function earningsData(){
        const earnings = [];
        const $tableRows = $("#earning-table tbody tr");

        $tableRows.each(() => {
            earnings.push({
                name: $(this).find("[name='name']").val(),
                salaryComponent: $(this).find("[name='name']").val(),
                salaryComponentAbbr: $(this).find("[name='abbr']").val(),
                amount: $(this).find("[name='amount']").val(),
                type: "Earning",
                dependsOnPaymentDays: ($(this).find("[name='depedendOnPaymentDays']").val() === true) ? 1: 0,
                isTaxApplicable: ($(this).find("[name='isTaxApplicable']").val() === true) ? 1: 0,
                amountBasedOnFormula: ($(this).find("[name='amountBasedOnFormula']").val() === true) ? 1: 0,
                formula: $(this).find("[name='formula']").val(),
            })
        })

        return earnings;
    }
    function deductionsData(){
        const deductions = [];
        const $tableRows = $("#deduction-table tbody tr");

        $tableRows.each(() => {
            deductions.push({
                name: $(this).find("[name='name']").val(),
                salaryComponent: $(this).find("[name='name']").val(),
                salaryComponentAbbr: $(this).find("[name='abbr']").val(),
                amount: $(this).find("[name='amount']").val(),
                type: "Deduction",
                dependsOnPaymentDays: ($(this).find("[name='depedendOnPaymentDays']").val() === true) ? 1: 0,
                isTaxApplicable: ($(this).find("[name='isTaxApplicable']").val() === true) ? 1: 0,
                amountBasedOnFormula: ($(this).find("[name='amountBasedOnFormula']").val() === true) ? 1: 0,
                formula: $(this).find("[name='formula']").val(),
            })
        })

        return deductions;
    }
    /* --- ---- ---- */
});