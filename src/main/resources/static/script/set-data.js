// Get 방식 요청
function methodGet(request, url) {
    let type = "GET";
    request.open(type, url);    // 요청 초기화
    request.send();
}

function methodGetJson(request, url) {
    let type = "GET";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";
    request.send();
}

// Post 방식 요청
function methodPost(request, url, data) {
    let type = "POST";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";
    /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
    request.setRequestHeader('content-type', 'application/json');
     /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
    request.send(data);
}

function methodPostMp(request, url, formData) {
    let type = "POST";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";
    
     /* 정의된 서버에 FormData 형식의 요청 Data를 포함하여 요청을 전송 */
    request.send(formData);
}

// Put 방식 요청
function methodPut(request, url, data) {
    let type = "PUT";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";
    /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
    request.setRequestHeader('content-type', 'application/json');
     /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
    request.send(data);
}

function methodPutMp(request, url, formData) {
    let type = "PUT";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";    
     /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
    request.send(formData);
}
// Remove 방식 요청
function methodRemove(request, url) {
    let type = "DELETE";
    request.open(type, url);    // 요청 초기화
    /* Response Type을 Json으로 사전 정의 */
    request.responseType = "json";
    request.send();
}