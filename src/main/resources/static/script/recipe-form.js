// window.addEventListener("load", function () {
const form = document.querySelector("form");
const addBtn = form.querySelector(".add-form");
const cookingOrder = form.querySelector(".cooking-order");
let orderBoxList;

const html =
    `<div class="info explanation input-content order-box">
            <div class="img-input-box">
                <span class="order-num">3.</span>
                <label class="detail-title" for="image"></label>
                <input type="file" name="" id="image">
            </div>
            <img class="d-none food-detail-img" src="" alt="요리사진">
            <textarea name="" id="" cols="30" rows="5" placeholder="내용을 입력해주세요." required></textarea>
        </div>`;

addBtn.addEventListener("click", () => {
    let textarea = cookingOrder.lastElementChild.querySelector("textarea");

    if (textarea.value == null || textarea.value == "" || textarea.value == " ")
        return;

    cookingOrder.insertAdjacentHTML("beforeend", html);
    addNum();
});


function addNum() {
    orderBoxList = cookingOrder.querySelectorAll(".order-box");
    let orderIdx = orderBoxList.length;
    console.log(orderIdx);
    let orderBox = cookingOrder.lastElementChild;
    let orderNum = orderBox.querySelector(".order-num");
    orderNum.innerText = orderIdx + ".";
}


orderBoxList = cookingOrder.querySelectorAll(".order-box");

cookingOrder.addEventListener("click", (e) => {
    for (let i = 0; i < orderBoxList.length; i++) {
        const image = orderBoxList[i].querySelector("img");
        const fileInput = orderBoxList[i].querySelector("input[type=file]");
        let cloneArr;

        if (e.target == fileInput) {

            if (image.src != "")
                init();


            // 이미지 변경
            fileInput.onchange = (e) => {

                let file = fileInput.files[0];  // files: File 객체의 컬렉션

                if (file) {
                    // FileReader 객체 생성
                    var reader = new FileReader();

                    reader.onload = (e) => {
                        // memberPhoto.style.backgroundImage = `url(${e.target.result})`;
                        image.src = e.target.result;
                        image.classList.remove("d-none");
                    }
                    // reader가 이미지 읽도록 하기
                    reader.readAsDataURL(file);
                } else {

                    image.classList.add("d-none");

                }

                // 브라우저상에서만 변경
                // memberPhoto.style.backgroundImage = `url(${URL.createObjectURL(file)})`;  // 바이너리(업로드)된 이미지 데이터(로드된 결과물의 이벤트 객체를 전달)
                // URL.revokeObjectURL(file);  // 이벤트 발생마다 생성되는 객체 URL 해제
            };
        }
        function init() {
            // fileInput.value = memberImg.src;
            cloneArr = [fileInput.cloneNode(true)];
        }
    }
});

const imgBox = document.querySelector(".img-input-box");

cookingOrder.addEventListener("click", (e) => {

    const image = imgBox.querySelector("img");
    const fileInput = imgBox.querySelector("input[type=file]");
    let cloneArr;

    if (e.target == fileInput) {

        if (image.src != "")
            init();




        // 이미지 변경
        fileInput.onchange = (e) => {

            let file = fileInput.files[0];  // files: File 객체의 컬렉션

            if (file) {
                // FileReader 객체 생성
                var reader = new FileReader();

                reader.onload = (e) => {
                    // memberPhoto.style.backgroundImage = `url(${e.target.result})`;
                    image.src = e.target.result;
                    image.classList.remove("d-none");
                }
                // reader가 이미지 읽도록 하기
                reader.readAsDataURL(file);
            } else {

                image.classList.add("d-none");

            }

            // 브라우저상에서만 변경
            // memberPhoto.style.backgroundImage = `url(${URL.createObjectURL(file)})`;  // 바이너리(업로드)된 이미지 데이터(로드된 결과물의 이벤트 객체를 전달)
            // URL.revokeObjectURL(file);  // 이벤트 발생마다 생성되는 객체 URL 해제
        };
    }
    function init() {
        // fileInput.value = memberImg.src;
        cloneArr = [fileInput.cloneNode(true)];
    }


});


function preventText(e) {
    e.preventDefault();

}

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
// });