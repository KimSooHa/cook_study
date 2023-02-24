window.addEventListener("load", function () {
    function getTimeList() {
        let date = document.querySelector("input[type=date]").value;
        let cookingRoomId = document.querySelector("select").value;
        // let checkboxContainer = document.querySelector(".checkbox-container");
        let checkboxes = document.querySelectorAll(".checkbox");

        if (date == null || date == "" || cookingRoomId == null || cookingRoomId == "")
            return;

        const request = new XMLHttpRequest();
        let type = "GET";
        let url = "/reserved-time?date=" + date + "&cookingRoomId=" + cookingRoomId;

        request.onreadystatechange = () => {
            if (request.readyState != XMLHttpRequest.DONE)
                return;
            let results = request.response;
            // let obj = JSON.parse(result);
            console.log(results);

            for (let i = 0; i < checkboxes.length; i++) {
                checkboxes[i].disabled = true;
                checkboxes[i].checked = false;
            }


            console.log("read");


            for (let i = 0; i < checkboxes.length; i++) {

                if (results.length == 0) {
                    checkboxes[i].disabled = false;
                    continue;
                }

                for (const result of results) {
                    if (result == checkboxes[i].value) {
                        checkboxes[i].disabled = true;
                    }

                }
            }


        }

        /* Post 방식으로 요청 */
        request.open(type, url);    // 요청 초기화
        /* Response Type을 Json으로 사전 정의 */
        request.responseType = "json";
        /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
        // request.setRequestHeader('content-type', 'application/json');
        /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
        request.send();

    }


    let now_utc = Date.now(); //지금 날짜를 밀리초로
    let timeOff = new Date().getTimezoneOffset() / (60 * 24) + (1000 * 60 * 60 * 24 * 7); // 로캘 시간과의 차이를 분단위로 반환
    let laterWeek = new Date(now_utc + timeOff).toISOString().split("T")[0];  // new Date(now_utc-timeOff).toISOString()은 '2022-05-11T18:09:38.134Z'를 반환
    document.querySelector(".date").setAttribute("min", laterWeek);

    /* <![CDATA[ */
    window.onload = () => {

        let message = [[${ msg }]];
        let url = [[${ url }]];
        if (message == null)
            return;

        alert(message);
        location.href = url;
    }

    /* ]]> */




    const submitBtn = document.querySelector("input[type=submit]");

    submitBtn.onclick = (e) => {
        let checkboxes = document.querySelectorAll(".checkbox");
        let cnt = 0;
        for (let i = 0; i < checkboxes.length; i++) {
            if (checkboxes[i].checked) {
                cnt++;
            }
        }

        if (cnt > 3) {
            e.preventDefault();
            alert("시간은 최대 3개까지만 가능합니다.");
        } else if (cnt == 0) {
            e.preventDefault();
            alert("예약하실 시간을 선택해주세요.");
        }


        //     const request = new XMLHttpRequest();
        //     let type = "POST";
        //     let url = "/reservation";
        //     let formData = new FormData();


        //     // let document.querySelector(".")
        //     let data = {
        //         "date": [[${ reservationForm.date }]];
        //         "cookingRoom": [[${ reservationForm.cookingRoomId }]];

        //     }

        //     for (let i = 0; i < checkboxes.length; i++) {
        //         if (checkboxes[i].checked) {
        //             formData.append('list', checkboxes[i].value);
        //         }
        //     }

        //     formData.append('form', [JSON.stringify(data)]);

        //     request.onreadystatechange = () => {
        //         if (request.readyState != XMLHttpRequest.DONE)
        //             return;
        //         let results = request.response;
        //         // let obj = JSON.parse(result);
        //         console.log(results);

        //         if(results.result == false)
        //             console.log("전송 실패!");
        //         else
        //         console.log("전송 성공!");

        //         console.log("read");

        //     }

        //     /* Post 방식으로 요청 */
        //     request.open(type, url);    // 요청 초기화
        //     /* Response Type을 Json으로 사전 정의 */
        //     request.responseType = "json";
        //     /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
        //     // request.setRequestHeader('content-type', 'application/json');
        //     request.setRequestHeader('content-type', 'multipart/formed-data');

        //     /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
        //     request.send(data);

    };
});