$(document).ready(function () {
    let items = [];
    let warehouses = [];
    let rowCount = 1;

    const $btnContainer = $(".btn-container");
    const $addRowBtn = $("#add-row-btn");
    const $tbody = $("tbody");
    const $table = $("table");
    const submitBtn = $("#save-btn");


    init();

    function init() {
        fetchItems();
        fetchWarehouses();
        bindEvents();
    }

    function bindEvents() {
        $addRowBtn.on("click", handleAddRow);
        $btnContainer.on("click", "#delete-row-btn", handleDeleteRows);
        $table.on("click", ":checkbox", handleCheckboxClick);
        submitBtn.click(save);
    }

    function handleAddRow(e) {
        e.preventDefault();
        $tbody.append(createNewRow(rowCount++));
        initAutocomplete(); // Rebinds autocomplete on new inputs
    }

    function handleDeleteRows() {
        const $checkboxes = $tbody.find("input[type='checkbox']").not("#all-checkbox");
        $checkboxes.each(function () {
            if ($(this).is(":checked")) {
                $(this).closest("tr").remove();
                rowCount--;
            }
        });
        resetCheckboxes();
        removeDeleteButton();
    }

    function handleCheckboxClick() {
        const $this = $(this);
        if ($this.is(":checked") && $this.attr("id") === "all-checkbox") {
            $("input[type='checkbox']").prop("checked", true);
        }
        showDeleteButton();
    }

    function createNewRow(rowNumber) {
        return `
            <tr>
                <td><input type="checkbox"></td>
                <td class="text-center">${rowNumber}</td>
                <td><input type="text" name="item_code"></td>
                <td><input type="date" name="required_by"></td>
                <td><input type="text" name="quantity"></td>
                <td><input type="text" name="warehouse"></td>
                <td><input type="text" name="uom"></td>
            </tr>
        `;
    }

    function showDeleteButton() {
        if (!$("#delete-row-btn").length) {
            $btnContainer.append(`
                <button type="button" id="delete-row-btn" class="px-3 py-1 rounded-2" style="font-size: 0.95em;">Delete Row</button>
            `);
        }
    }

    function removeDeleteButton() {
        $("#delete-row-btn").remove();
    }

    function resetCheckboxes() {
        $("input[type='checkbox']").prop("checked", false);
    }

    function initAutocomplete() {
        $("input[name='item_code']").autocomplete({ source: items });
        $("input[name='warehouse']").autocomplete({ source: warehouses });
    }

    function fetchItems() {
        $.ajax({
            url: "/items",
            method: "GET",
            dataType: "json",
            success(data) {
                if (data.status === "success") {
                    items = data.data.map(item => item.name);
                    initAutocomplete();
                } else {
                    alert("Error when fetching items");
                }
            },
            error(xhr) {
                console.error("Items Error:", xhr.responseText);
            }
        });
    }

    function fetchWarehouses() {
        $.ajax({
            url: "/warehouses",
            method: "GET",
            dataType: "json",
            success(data) {
                if (data.status === "success") {
                    warehouses = data.data.map(w => w.name);
                    initAutocomplete();
                } else {
                    alert("Error when fetching warehouses");
                }
            },
            error(xhr) {
                console.error("Warehouses Error:", xhr.responseText);
            }
        });
    }

    function prepareFormData(){
        const series = $("[name='series']").val();
        const purpose = $("[name='purpose']").val();
        const transaction_date = $("[name='transaction_date']").val();
        const required_by = $("[name='required_by']").val();

        const items = []

        $("table > tbody > tr").each(function () {
            const item = {
                item_code: $(this).find($("[name='item_code']")).val(),
                warehouse: $(this).find($("[name='warehouse']")).val(),
                qty: $(this).find($("[name='quantity']")).val(),
                stock_uom: $(this).find($("[name='uom']")).val(),
                schedule_date: $(this).find($("[name='item_required_by']")).val(),
            }

            items.push(item);
        })

        return {
            series: series,
            required_by: required_by,
            transaction_date: transaction_date,
            material_request_type: purpose,
            items: items
        }
    }

    function save() {
        $.ajax({
            url: "/material_requests",
            method: "Post",
            contentType: "application/Json",
            dataType: "json",
            data: JSON.stringify(prepareFormData()),
            beforeSend: () => {
                submitBtn.prop("disabled", true);
                submitBtn.text("Submitting...");
            },
            success: function (data) {
                if (data && data.status === "success"){
                    window.location.href = "/material_requests";
                } else {
                    alert(data.message);
                }
            },
            error: function (xhr) {
                alert(xhr.responseText)
            },
            complete: function () {
                submitBtn.prop("disabled", false);
                submitBtn.text("Save");
            }
        })
    }
});
