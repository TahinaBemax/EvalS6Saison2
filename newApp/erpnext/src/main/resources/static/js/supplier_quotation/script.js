let itemCounter = 1;
let focusedWarehouse;
let focusedItem;
let warehouses ;
let items;

$(document).ready(function () {
    //set default date
    $('#date').val(new Date());

    //fetch data
    fetchWarehouses()
    fetchItems()

    $('#sq_form').on('submit', (e) => {
        e.preventDefault();
        const btnSubmit = $(this).find("[type='submit']");
        send(btnSubmit);
    })

    $('#items-table').on({
        focus: function () {
            focusedWarehouse = $(this);
            // Remove any existing list before appending a new one
            $("#warehouse-container").remove();
            $(this).after(warehouseList());
        },
        focusout: function () {
            setTimeout(() => $("#warehouse-container").remove(), 1000); // slight delay to allow li click
        }
    }, "[name='warehouse']");

    $('#items-table').on({
        focus: function (){
            focusedItem = $(this);
            // Remove any existing list before appending a new one
            $("#item-container").remove();
            $(this).after(itemsHtmlList());
        },
        focusout: function (){
            setTimeout(() => $("#item-container").remove(), 900);
        }
    }, "[name='itemCode']");

    // Use event delegation for dynamically added <li> items
    $(document).on("click", "#warehouse-list > li", function (e) {
        e.preventDefault();
        if (focusedWarehouse) {
            focusedWarehouse.val($(this).text());
            $("#warehouse-container").remove(200)
        }
        $("#warehouse-list > li").removeClass("active");
        $(this).addClass("active");
    });

    $(document).on("click", "#item-list > li", function (e) {
        e.preventDefault();
        if (focusedItem) {
            focusedItem.val($(this).text());
            $("#item-container").remove(200)
        }
        $("#item-list > li").removeClass("active");
        $(this).addClass("active");
    });

    // Select All Items
    $("#add-btn").on("click", function () {
        $("tbody").append(addNewItemLine());
    });

    $("tbody").on("click", ".delete-btn", function () {
        const deleteBtnCounter = $(".delete-btn").length;

        if (deleteBtnCounter > 1) {
            $(this).closest("tr").remove();
        }
    });

    function addNewItemLine() {
        const newLine =  `
            <tr>
                <td class="position-relative">
                  <input class="form-control" type="text" name="itemCode" >
                </td>
                <td>
                  <input class="form-control" type="number" step="any" name="quantity">
                </td>
                <td><input class="form-control" type="number" name="rate"></td>
                <td><input class="form-control" type="number" name="amountRate"></td>
                <td><input class="form-control" type="text" name="uom"></td>
                <td class="position-relative">
                  <input class="form-control" type="text" name="warehouse">
                </td>
            </tr>`;

        itemCounter++;
        return newLine;
    }
    function warehouseList() {
        let warehousesList = '';

        for (let w of warehouses) {
            warehousesList += `<li>${w.name}</li>`
        }

        return `
            <div class="position-absolute end-0 z-3 bg-white shadow" id="warehouse-container">
                <ul id="warehouse-list">
                     ${warehousesList}
                </ul>
            </div>`;
    }
    function itemsHtmlList() {
        let itemList = '';

        for (let item of items) {
            itemList += `<li>${item.name}</li>`
        }

        return `
            <div class="position-absolute end-0 z-3 bg-white shadow" id="item-container">
                <ul id="item-list">
                     ${itemList}
                </ul>
            </div>`;
    }
})

function showLoadingOnSubmitButton(btnSubmit){
    btnSubmit.html(`
           <div class="spinner-border text-light" role="status">
             <span class="visually-hidden">Loading...</span>
           </div>
`       )
    btnSubmit.attr("disabled", true);
    btnSubmit.css('cursor','not-allowed')
}
function removeLoading(btnSubmit){
    btnSubmit.attr("disabled", false);
    btnSubmit.html(`Save`)
    btnSubmit.css('cursor','pointer')
}
function send(btnSubmit){
    showLoadingOnSubmitButton(btnSubmit);
    $.ajax({
        url: "/supplier-quotations/save",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(prepareFormData()),
        success: function (data)  {
            if (data.status === "success"){
                window.location.href = "/supplier-quotations";
            } else {
                showError(data.message);
            }
            removeLoading(btnSubmit);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            showError(errorThrown);
            removeLoading(btnSubmit);
        }
    })
}

function showError(message){
    $('#sq_form').append(`<div class="alert alert-danger my-2" role="alert">${message}</div>`)
}
function prepareFormData() {
    const series = $("[name='series']").val();
    const supplier = $("[name='supplier']").val();
    const transactionDate = $("[name='transaction_date']").val();
    const validTill = $("[name='valid_till']").val();

    const items = [];

    // Parcours de chaque ligne du tableau
    $("#items-table tbody tr").each(function () {
        const item = {
            item_code: $(this).find("[name='itemCode']").val(),
            qty: parseFloat($(this).find("[name='quantity']").val()),
            rate: parseFloat($(this).find("[name='rate']").val()),
            amount: parseFloat($(this).find("[name='amountRate']").val()),
            uom: $(this).find("[name='uom']").val(),
            warehouse: $(this).find("[name='warehouse']").val()
        };
        items.push(item);
    });

    return {
        series: series,
        supplier: supplier,
        transaction_date: transactionDate,
        valid_till: validTill,
        items: items
    };
}
function fetchWarehouses(){
    const url = "/warehouses";
    $.ajax({
        url: url,
        method: 'GET',
        success: (data) => {
            if (data.status === "success"){
                warehouses = data.data;
            } else {
                alert(data.message)
            }
        },
        error: (jqXHR, textStatus, errorThrown) => {
            console.log(`textStatus: ${textStatus}, jqXHR:${jqXHR}, error:${errorThrown}`)
        }
    })
}
function fetchItems(){
    const url = "/items";
    $.ajax({
        url: url,
        method: 'GET',
        success: (data) => {
            if (data.status === "success"){
                items = data.data;
            } else {
                alert(data.message)
            }
        },
        error: (jqXHR, textStatus, errorThrown) => {
            console.log(`textStatus: ${textStatus}, jqXHR:${jqXHR}, error:${errorThrown}`)
        }
    })
}
