function originDisplay(valid, invalid, exist, origin) {
    valid.classList.add("d-none");
    invalid.classList.add("d-none");
    if(exist != null)
        exist.classList.add("d-none");
    origin.classList.remove("d-none");
}

function invalidDisplay(valid, invalid, exist, origin) {
    if(exist != null)
        exist.classList.add("d-none");
    valid.classList.add("d-none");
    origin.classList.add("d-none");
    invalid.classList.remove("d-none");
}

function validDisplay(valid, invalid, exist, origin) {
    if(exist != null)
        exist.classList.add("d-none");
    invalid.classList.add("d-none");
    origin.classList.add("d-none");
    valid.classList.remove("d-none");
}

function existDisplay(valid, invalid, exist, origin) {
    origin.classList.add("d-none");
    valid.classList.add("d-none");
    invalid.classList.add("d-none");
    if(exist != null)
        exist.classList.remove("d-none");
}