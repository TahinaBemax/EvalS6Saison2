function showLoading(){
    const body = $("body");
    body.prepend(loading())
}

function hideLoading(){
    $("#loading-animation").remove();

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