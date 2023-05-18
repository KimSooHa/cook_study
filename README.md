# 🍽 Springboot Project - Yorimoyeo

## 📝개요
- 프로젝트 명: Yorimoyeo
- 개발 인원: 1명
- 개발 기간: 2023.01.31 ~
- 개발 목적
  - 요리 레시피를 공유하고 함께 요리하는 공간을 제공해 스터디를 모집하는 커뮤니티 사이트 제작
  - Spring Boot Framework와 JPA를 잘 이해하고 적용하여 REST API 방식으로 구현
  - AWS를 통해 운영서버에 배포 및 도메인 연결
- 개발 환경: IntelliJ IDE 2022.2.4(Community Edition), SpringBoot 2.7.7, JDK 11, Mariadb 3.0.9, Gradle, Lombok 1.18.24, JPA 5.3.24, Spring Data JPA 2.7.7, Querydsl 5.0.0, JUnit5, HTML, CSS, Javascript, Thymeleaf, AWS

## 아키텍처
  ![architecture](https://github.com/KimSooHa/cook_study/assets/81688625/087df5ed-1c2e-4b3e-9554-b9a3fde26fc7)


## 내용


### 구현기능

- 회원가입, 로그인/로그아웃
- 레시피 CRUD
- 레시피 조리양식 CRUD
- 요리실 CRUD(UI상에서 Read만 가능)
- 스케줄 CRUD(UI상에서 Read만 가능)
- 요리실 예약 CRUD
- 쿡스터디 CRUD
- 쿡스터디 참여 CRD
- 카테고리 CRUD(UI상에서 Read만 가능)
- 레시피 댓글 CRUD


### DB 설계


#### 개념설계 Diagram


![diagram](https://github.com/KimSooHa/cook_study/assets/81688625/c9b37deb-8a0a-4bfd-b4d2-47f6c40819b6)


#### ERD


![ERD](https://github.com/KimSooHa/cook_study/assets/81688625/2a71dbfa-9ba7-4bcf-b257-5fdd21241008)

> member: 회원 테이블. 로그인, 레시피 등록, 요리실 예약, 쿡스터디 등록, 참여할 수 있습니다.
> 
> recipe: 레시피 테이블
> 
> recipe_field: 레시피 설명 양식 테이블. 레시피를 등록할 때 조리 순서에 대한 내용을 함께 등록합니다.
> 
> photo: 사진 테이블. 레시피나 레시피 조리 순서에 등록하는 이미지를 관리합니다.
> 
> comment: 레시피 댓글 테이블. 레시피에 대한 댓글을 작성할 수 있습니다.
> 
> club: 요리그룹 테이블. 쿡스터디를 등록하고 참여할 수 있습니다.
> 
> participation: 참여 테이블. 등록된 쿡스터디에 참여, 탈퇴할 수 있습니다.
> 
> category: 카테고리 테이블. 레시피, 쿡스터디를 등록할 때 카테고리를 지정합니다.
> 
> reservation: 예약 테이블. 요리실을 예약할 수 있습니다.
> 
> cooking_room: 요리실 테이블. 예약할 요리실을 조회합니다.
> 
> schedule: 일정 테이블. 각 요리실 당 예약가능한 시간들을 조회합니다.



## Features & Screens


### 메인 페이지


<img width="1344" alt="index" src="https://github.com/KimSooHa/cook_study/assets/81688625/a84fd65c-6b5d-41c0-aa31-d7acdaeb1d42">

사이트에 들어가면 가장 먼저 보이는 메인 페이지입니다.

- 구현 기능 설명
    - 사용자 전체가 보는 메인 화면
    - Best 레시피 리스트(댓글 많은 순) 4개 조회
    - Best 쿡스터디 리스트(참여자 많은 순) 4개 조회
    - 회원가입 외에 다른 화면으로 이동하기 위해서는 로그인을 해야 합니다.
    
---
    

### 회원가입

![signup](https://github.com/KimSooHa/cook_study/assets/81688625/ac919b09-cd4d-4e2d-a262-849c870c1993)

- 구현 기능 설명
    - 프론트와 서버단에서 각각 유효성 검사를 합니다.
    - 중복확인: ajax를 통해 비동기로 기존 회원의 아이디나 이메일과 일치하는지 비교합니다.


### 로그인

![login](https://github.com/KimSooHa/cook_study/assets/81688625/08ad24a5-e7e7-47fe-91c9-999062bf44cc)

- 구현 기능 설명
    - 가입한 회원의 계정으로 로그인을 합니다.
    - 로그인에 성공하면 메인 화면으로 이동하고 헤더에 회원의 이름이 나타납니다.


### 프로필 수정

![member_update](https://github.com/KimSooHa/cook_study/assets/81688625/f4ea82fc-ddd9-46b0-ae81-6fa07760a01f)

- 구현 기능 설명
    - 마이페이지로 이동 후 프로필 수정 클릭
    - 회원가입과 동일하게 유효성 검사 및 중복확인을 합니다.
    - 원하는 항목을 수정 후 수정하기를 누르면 마이페이지로 다시 이동합니다.
    - 로그아웃 후 다시 로그인을 할 때 수정한 아이디로 로그인을 성공합니다.


### 회원 탈퇴

![member_remove](https://github.com/KimSooHa/cook_study/assets/81688625/1a9c1a0e-2f53-4a45-b45d-c7651ef1800c)

- 구현 기능 설명
    - 마이 페이지에서 회원 탈퇴 클릭 시 탈퇴할 것인지 확인하는 창을 띄우도록 하였습니다.
    - 확인 버튼을 클릭하면 DB에서 해당 회원의 정보가 삭제되고 탈퇴되었다는 alert창을 띄웁니다.
    - 다시 일반 유저 메인 페이지로 이동하게 되며, 기존 서비스를 이용하기 위해서는 다시 로그인을 해야 합니다.

---

### 레시피 조회


- 레시피 목록 페이지
![recipe-list_read](https://github.com/KimSooHa/cook_study/assets/81688625/39f2b977-b4d1-44e3-a371-cff9a294f30b)


    - 구현 기능 설명
        - 레시피 목록 조회
        - 카테고리별로 목록 조회
        - 페이징을 통해 레시피 목록 넘기기


- 레시피 검색 및 상세 페이지
![recipe-detail_read](https://github.com/KimSooHa/cook_study/assets/81688625/1c49e128-ecf7-41c7-ac47-d657bf57a23c)


    - 구현 기능 설명
        - 검색창에서 레시피 이름으로 조회가능합니다.
        - 레시피 상세 조회(제목, 작성자, 설명글, 인분, 요리시간, 재료, 조리순서)
        - 작성자만 수정/삭제 가능합니다.


### 레시피 등록


- 레시피 등록 내용 작성
![recipe_create1](https://github.com/KimSooHa/cook_study/assets/81688625/d8d252d8-616d-48da-b07a-608f37288562)


- 레시피 조리 순서 내용 작성 및 등록
![recipe_create2](https://github.com/KimSooHa/cook_study/assets/81688625/67b7a46f-1645-4519-b205-4c6899e68e40)


    - 구현 기능 설명
        - 등록할 레시피 상세 내용(제목, 이미지, 카테고리, 설명글, 재료 등)을 작성합니다.
        - 조리 순서 상세 내용(이미지, 설명글)
            - 상세내용을 추가로 작성하고 싶다면 하단에 + 버튼을 클릭하면 양식이 나타납니다.
            - 반대로 - 버튼을 누르면 가장 하단의 내용이 사라집니다.

                ![recipe_create3](https://github.com/KimSooHa/cook_study/assets/81688625/127ae5e0-4b15-42be-8c56-bcc12a4c318b)

                등록 후 저장된 레시피의 상세 페이지로 이동

        - 등록 버튼을 클릭하면 등록이 되고 등록한 레시피의 상세 페이지로 이동하게 됩니다.


### 레시피 수정


![recipe_update1](https://github.com/KimSooHa/cook_study/assets/81688625/a2cd4242-2c4e-486f-abb3-42cf4ec5cbc7)


- 레시피 수정 클릭 후 수정 페이지로 이동

![recipe_update2](https://github.com/KimSooHa/cook_study/assets/81688625/39bf9daf-7f6e-4502-bc87-22192170000d)

  - 구현 기능 설명
      - 수정 시, DB에 등록된 레시피 내용, 조리 순서(recipe_field) 내용 불러옴
      - 수정하기 위해 레시피 조리 순서 내용을 추가한 후 저장을 합니다.
      - 이미지 수정할 시 함께 수정 가능.
      - 저장 클릭 시, 해당 내용으로 DB에 등록되고 추가된 내용 또한 등록.

- 저장 후 상세 페이지로 이동한 화면
![recipe_update3](https://github.com/KimSooHa/cook_study/assets/81688625/ec4bd0fb-b65c-4a7f-93cd-73c7ac73e003)

  - 저장된 후에 레시피 상세 조회 화면으로 이동하게 됩니다. 수정한 내용 및 추가한 내용이 들어간 것을 확인할 수 있습니다.


### 레시피 삭제

![recipe_delete](https://github.com/KimSooHa/cook_study/assets/81688625/7268e795-7bf8-435a-a1d1-3304c1ce42d4)


- 구현 기능 설명
    - 레시피 상세 페이지에서 삭제하기 클릭
    - 삭제하기를 클릭 시, 실수로 삭제 버튼 클릭한 경우를 방지하기 위해 삭제할 것인지 한번 더 확인하는 confirm창이 뜨게 됩니다.
    - 확인을 클릭하면 DB에서 해당 레시피를 삭제 후 레시피 목록 페이지로 이동하게 됩니다.


### 등록한 레시피 조회

![saved_recipe](https://github.com/KimSooHa/cook_study/assets/81688625/e5ec68c2-b79c-4991-87a4-713837486516)


- 구현 기능 설명
    - 마이페이지로 이동 후 등록한 레시피 클릭하면 등록한 레시피로 이동합니다.
    - 등록한 레시피 목록 조회
    - 카테고리 별로 조회
    - 페이징을 통해 레시피 목록 넘기기
    - 검색창에서 레시피 이름으로 조회가능합니다.


---

### 레시피 댓글 조회


![comment_read](https://github.com/KimSooHa/cook_study/assets/81688625/919edbdb-76c0-486f-bb2d-34b25c41fe38)


- 구현 기능 설명
    - 레시피 상세 페이지로 이동 후 하단에서 댓글(아이디, 댓글내용, 등록일자)을 확인할 수 있습니다.
    - 버튼을 클릭하면 비동기 호출로 페이징을 통해 댓글 목록을 추가로 더 조회할 수 있습니다.


### 레시피 댓글 등록


![comment_create](https://github.com/KimSooHa/cook_study/assets/81688625/270fe4d3-d6bd-4bb7-b05f-fa25f925fbd8)


- 구현 기능 설명
    - 댓글을 작성 후 등록하기 누르면 하단에 목록 상단에 작성한 댓글이 추가됩니다.
    

### 레시피 댓글 수정


![comment_update](https://github.com/KimSooHa/cook_study/assets/81688625/6e9e5e34-c645-4e84-a854-292bd8e1d57a)


- 구현 기능 설명
    - 본인이 작성한 댓글에만 버튼이 있습니다.
    - 수정하기 버튼을 누르고 작성 후 수정을 클릭하면 수정된 댓글내용으로 변경됩니다.


### 레시피 댓글 삭제


![comment_delete](https://github.com/KimSooHa/cook_study/assets/81688625/1369fdd6-4c22-49a4-8e24-7f47849bebb1)


- 구현 기능 설명
    - 삭제하기를 클릭 시, 실수로 삭제 버튼 클릭한 경우를 방지하기 위해 삭제할 것인지 한번 더 확인하는 confirm창이 뜨게 됩니다.
    - 확인 버튼을 누르면 작성했던 ajax를 통해 비동기로 해당 댓글을 삭제하고, 댓글이 목록에서 사라지게 됩니다.


---

### 요리실 예약 목록 조회


![reservation_read](https://github.com/KimSooHa/cook_study/assets/81688625/4d1d05d9-ac44-4aa7-b5b9-6cb12aca7bbf)


- 구현 기능 설명
    - 메인 페이지에서 요리실로 이동
    - 요리실 예약일의 최신순으로 나열
    - 페이징을 통해 예약한 요리실 목록 넘길 수 있습니다.


### 요리실 예약


![reservation](https://github.com/KimSooHa/cook_study/assets/81688625/34ead2d3-11f2-4055-8c22-5e909fb12a84)


- 구현 기능 설명
    - 요리실 대여로 이동
    - 날짜와 요리실을 선택하면 예약가능한 시간에 체크박스가 풀립니다.
    - 예약 한번 할 때 시간은 최대 3개 선택 가능합니다.
    - 예약하기를 누르면 예약이 완료되고 요리실 목록으로 이동합니다.
    - 다시 예약하러 이동하면 방금 예약한 시간은 선택이 불가능한 것을 확인할 수 있습니다.

- 예약 불가능한 경우

![reservation_fail](https://github.com/KimSooHa/cook_study/assets/81688625/6b02ab7b-1e27-440a-9373-42dfd4f512df)


  - 현재 날짜 기준 이후로 예약일을 비교했을 때, 예약한 수가 10개가 되었을 때는 예약이 불가능합니다.
  - 예약을 진행하기 위해서는 기존 예약을 취소하거나 날짜가 지나야 가능합니다.


### 요리실 예약 수정


![reservation_update](https://github.com/KimSooHa/cook_study/assets/81688625/64ab165b-3546-427a-a82a-17fe3f76d4c5)


- 구현 기능 설명
    - 메인 페이지에서 요리실로 이동
    - 요리실 목록에서 수정하기 클릭
        - 현재 날짜를 포함한 지난 날짜는 수정이 불가합니다.
        - 수정하기 페이지로 이동
        - 현재 예약했던 시간은 체크박스에 체크가 되어있습니다.
        - 수정을 원하는 날짜, 요리실, 시간을 선택하고 저장 클릭 시 alert창을 통해 수정되었음을 알리도록 하였습니다.
        - 확인을 누르면 다시 요리실 목록으로 이동하며, 수정된 것을 확인할 수 있습니다.


### 요리실 예약 취소


![reservation_cancel](https://github.com/KimSooHa/cook_study/assets/81688625/b13c709f-92b4-4cae-bbbc-8d92c1f533a8)


- 구현 기능 설명
    - 요리실 예약 목록에서 취소하기를 누르면 예약목록에서 사라집니다.
    - 요리실 예약으로 이동후 확인해보면 취소한 시간이 예약가능한 것을 확인할 수 있습니다.


---


### 쿡스터디 조회


![club_read](https://github.com/KimSooHa/cook_study/assets/81688625/3452d73d-2b77-47d1-9181-309e30536469)


- 구현 기능 설명
    - 메인페이지에서 쿡스터디로 이동
    - 쿡스터디 목록 조회
        - 최근에 등록된 순을 기준으로 나열
        - 카테고리 별로 목록(스터디명, 운영자) 조회
        - 페이징을 통해 스터디 목록을 넘길 수 있습니다.
        - 검색창에 스터디명 검색을 통해 조회할 수 있습니다.
    - 스터디 상세 페이지 조회
        - 스터디 상세 내용(제목, 운영자, 카테고리, 그룹 설명글, 모집인원 등)을 포함한 참여/탈퇴하기 버튼을 볼 수 있습니다.


### 쿡스터디 등록


![club_create](https://github.com/KimSooHa/cook_study/assets/81688625/e9190e3c-b721-4ade-9125-ebcd879b901b)


- 구현 기능 설명
    - 헤더의 쿡스터디 등록을 클릭 시, 쿡스터디 등록으로 이동
    - 쿡스터디에 대한 내용(제목, 설명글, 카테고리, 모집 인원 등)을 입력 및 선택합니다.
    - 예약된 요리실이 존재할 경우, 목록 조회를 합니다.
    - 예약한 요리실 선택을 통해 스터디 진행을 할 요리실과 날짜를 기재할 수 있습니다.
    - 등록하기를 클릭하면 alert 창이 뜬 후에 쿡스터디 상세 페이지로 이동합니다.
    

### 쿡스터디 수정


![club_update](https://github.com/KimSooHa/cook_study/assets/81688625/10c8dddc-b5ed-483e-956a-bd8b7c8761a4)


- 구현 기능 설명
    - 수정하기를 클릭 시 수정 페이지로 이동합니다.
    - 모집인원 수정 3 → 4
    - 수정 후 저장하기를 클릭하면 alert 창이 뜬 후에 쿡스터디 상세 페이지로 이동합니다.
    - 모집인원이 4명으로 수정된 것을 볼 수 있습니다.


### 쿡스터디 삭제


![club_delete](https://github.com/KimSooHa/cook_study/assets/81688625/3ce6ea89-632f-43eb-bfed-a0af7d40eb7e)


- 구현 기능 설명
    - 삭제하기 클릭시 confirm 창을 통해 삭제할 것인지 한번 더 확인하도록 하였습니다.
    - 확인을 클릭하면 DB에서 해당 쿡스터디가 삭제되고 alert창으로 삭제되었음을 알립니다. 이후 쿡스터디 목록 페이지로 이동합니다.
    

### 쿡스터디 참여/탈퇴


![participate:drop_out](https://github.com/KimSooHa/cook_study/assets/81688625/1dc53f13-76f9-41cd-95e5-98d48e5d47b7)


- 구현 기능 설명
    - 쿡스터디 참여
        - 상세 페이지 하단에 쿡스터디 참여하기를 클릭하면 해당 스터디에 참여가 가능합니다.
        - 스터디를 참여하면 남은인원이 줄어든 것을 확인할 수 있습니다.
        - 참여 후 탈퇴하기 버튼으로 변경되어있습니다.
        - 정원이 마감된 스터디면 참여하기 버튼 클릭이 불가능합니다.
    - 쿡스터디 탈퇴
        - 참여 중인 스터디라면 하단에 탈퇴하기 버튼을 통해 탈퇴가 가능합니다.
        - 탈퇴 후 남은 인원 수와 버튼이 변경된 것을 확인할 수 있습니다.


### 등록한 쿡스터디 조회


![saved_club](https://github.com/KimSooHa/cook_study/assets/81688625/8949adf4-3855-4a65-aefe-8ad8f72d26d6)


- 구현 기능 설명
    - 마이 페이지에서 등록한 쿡스터디 클릭 시, 등록한 쿡스터디 목록 페이지로 이동합니다.
    - 등록한 쿡스터디 목록 조회
        - 회원이 등록한 쿡스터디의 목록들만 조회합니다.
        - 최근에 등록된 순을 기준으로 나열
        - 카테고리 별로 목록(스터디명, 운영자) 조회
        - 페이징을 통해 스터디 목록을 넘길 수 있습니다.
        - 검색창에 스터디명 검색을 통해 조회할 수 있습니다.


### 참여하는 쿡스터디 조회


![joined_club](https://github.com/KimSooHa/cook_study/assets/81688625/068c8123-618d-447f-908c-401cdc307850)


- 구현 기능 설명
    - 마이 페이지에서 참여하는 쿡스터디 클릭 시, 참여하는 쿡스터디 목록 페이지로 이동합니다.
    - 참여하는 쿡스터디 목록 조회
        - 회원이 참여하는 쿡스터디의 목록들만 조회합니다.
        - 최근에 등록된 순을 기준으로 나열
        - 카테고리 별로 목록(스터디명, 운영자) 조회
        - 페이징을 통해 스터디 목록을 넘길 수 있습니다.
        - 검색창에 스터디명 검색을 통해 조회할 수 있습니다.

---


## 서비스 도메인 URL


[🍽 요리모여 - 레시피를 공유하고 함께 요리하는 공간](http://yorimoyeo.com)

> AWS를 통해 운영서버에 배포하였습니다. 위의 링크로 접속하면 실제 서비스 시연을 해볼 수 있습니다.
>
