let itemCheckedCounter = 0;

$(document).ready(function () {

    $("#item-list").DataTable({
        "columnDefs": [
            {
                "orderable": false, "targets": [0]
            }
        ]
    });

    $("#item-group").on({
        focusout: function () {
            $("#item-group-container").fadeOut();
        },
        focus: function () {
            $("#item-group-container").fadeIn();
        }
    });

    $("#item-group-list > li").click(function (e) {
        e.preventDefault();
        $("#item-group").val(e.target.textContent)
        $("#item-group-list > li").removeClass("active");
        e.target.className = "active";
    });
    // Select All Items
    $('#item-list input:checkbox').on('click', function () {
        const $this = $(this);
        const isAllCheckbox = $this.attr('id') === 'all-checkbox';
        const isChecked = $this.prop('checked');

        if (isAllCheckbox) {
            const $checkboxes = $('#item-list input:checkbox').not('#all-checkbox');
            itemCheckedCounter = isChecked ? $checkboxes.length : 0;

            $checkboxes.each(function () {
                $(this).prop('checked', isChecked);
                if (isChecked) {
                    addButtons($(this));
                } else {
                    removeButtons($(this));
                }
            });

            $('.item-name').text(
                isChecked ? `${itemCheckedCounter} element(s) selected.` : 'Item Name'
            );
        } else {
            $this.prop('checked', isChecked);
            itemCheckedCounter += isChecked ? 1 : -1;
            itemCheckedCounter = Math.max(itemCheckedCounter, 0);

            $('.item-name').text(
                itemCheckedCounter > 0 ? `${itemCheckedCounter} element(s) selected.` : 'Item Name'
            );

            if (isChecked) {
                addButtons($this);
            } else {
                removeButtons($this);
            }
        }
    });

    $("#item-form").validate({
        rules: {
            item_code: {required: true},
            item_group: { required: true},
            stock_uom: { required: true}
        },
        messages: {
            item_code: {required: "Item code is mendatory"},
            item_group: { required: "Item group is mendatory"},
            stock_uom: { required: "Default iom is mendatory"}
        },
        errorElement: "div",
        errorClass: "alert alert-danger",
        highlight: function (element) {
            $(element).addClass("is-invalid")
        },
        unhighlight: function (element) {
            $(element).removeClass("is-invalid")
        },
        submitHandler: function (form) {
            $(form).submit(function (e) {
                e.preventDefault();
                sendFormData($(this));
            });
        }
    });

    function updateAndDeleteBtns() {
        return `
            <span class="btns">
                <button type="button" class="delete-btn">
                    <i class="bi bi-trash"></i>
                </button>
                <button type="button" class="update-btn">
                    <i class="bi bi-pencil-square"></i>
                </button>
            </span>`;
    }

    function addButtons($checkbox) {
        // Add buttons only if not already present
        if ($checkbox.next('.btns').length === 0) {
            $checkbox.after(updateAndDeleteBtns());
        }
    }

    function removeButtons($checkbox) {
        // Remove buttons if present after this checkbox
        if ($checkbox.next('.btns').length) {
            $checkbox.next('.btns').remove();
        }
    }

    //
    function sendFormData(form){
        const jsonData = JSON.stringify(formToObject(form));
        const url = "/items";

        $.ajax({
            url: url,
            method: 'Post',
            contentType: 'application/json',
            data: jsonData,
            beforeSend: () => $("#save-btn").prop('disabled', true),
            success: function (resp) {
                if (resp && resp.status === "success") {
                    window.location.href = "/items/list";
                }
            },
            error: function (xhr) {
                alert(xhr.responseText)
            },
            complete: () => $("#save-btn").prop('disabled', false)
        });
    }
})

function formToObject($form) {
    const unindexed_array = $form.serializeArray();
    const indexed_object = {};
    $.map(unindexed_array, function (n) {
        indexed_object[n['name']] = n['value'];
    });
    return indexed_object;
}