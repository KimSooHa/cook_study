// Get 방식 요청
function methodGet(request, url) {
    let type = "GET";
    request.open(type, url);    // 요청 초기화
    request.send();
}

// Post 방식 요청
function methodPost(request, url) {
    let type = "POST";
    request.open(type, url);    // 요청 초기화
    request.send();
}