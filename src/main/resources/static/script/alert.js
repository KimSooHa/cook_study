function setMessageAndUrl(message, url) {
    if (message == null || message == "")
        return;
    alert(message);

    if (url == null || url == "")
        return;
    location.href = url;
}

function setMessage(message) {
    if (message == null || message == "")
        return;
    alert(message);
}