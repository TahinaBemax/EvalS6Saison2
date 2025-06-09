function showLoading(){
    const body = $("body");
    body.prepend(loading())
}

function hideLoading(){
    $("#loading-animation").remove();

}

function LoadingButtonOn(btn) {
    btn.val("Loading...")
    btn.prop("disabled", true)
}

function LoadingButtonOff(btn, btnText) {
    btn.val(btnText)
    btn.prop("disabled", false)
}

function tableFetchDataLoadingAnimation(table) {
    const tbody = table.children("tbody");

    if (!tbody) return;

    const thead = table.children("thead");
    var totalColumnLength = thead ? thead.find("tr > th").length : 0;

    // If no columns are found, return early
    if (totalColumnLength === 0) {
        const td = thead.find("tr > td");
        totalColumnLength = td ? td.length : 0
    }

    if (totalColumnLength === 0) return ;

    // Clear the tbody and inject the loading spinner
    tbody.empty().append(`
        <tr>
            <td class="text-center" colspan="${totalColumnLength}">
                <div class="spinner-border text-light" role="status" id="loading-animation">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </td>
        </tr>
    `);
}



function loading(){
    return `
    <div id="loading-animation" class="d-flex align-items-center justify-content-center" style="width: 100vw; 
            height: 100vh; 
            position: fixed; top: 0; left: 0;
            background: rgba(255,255,255,0.63);
            z-index: 1000">
            <div class="spinner-border text-light" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
    </div>
    `
}