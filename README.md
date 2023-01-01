# 개인프로젝트 게시판  / JPA를 이용한 RestApi 구현 / Spring Security (JWT 토큰)
<p align="center"><img width="750" alt="jenkins-docker-springboot-cicd" src="https://user-images.githubusercontent.com/104709432/209429451-36eac449-39d0-40a0-ac41-194c979c021d.png">
</p>
</br>

## RestApi Swagger 
### http://ec2-3-35-209-220.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html</br>
## Running Docker on AWS EC2(ec2 server🔽)
### http://ec2-3-35-209-220.ap-northeast-2.compute.amazonaws.com:8080
## Spring Security + JWT Token
### [Spring Security와 JWT Token 인증,인가 처리 (Velog 정리본)](https://velog.io/@guns95/Spring-Security%EC%99%80-JWT-%ED%86%A0%ED%81%B0%EC%9D%98-%EC%9D%B8%EC%A6%9D%EC%9D%B8%EA%B0%80-%EB%A1%9C%EA%B7%B8%EC%9D%B8)
## 해당 프로젝트 Test Code정리본
### [Controller TDD](https://velog.io/@guns95/Spring-Controller-Test)
### [Service TDD](https://velog.io/@guns95/Spring-Service-Test)
***

## 구현 완료
**Function** | **완료** | 
:------------ | :-------------| 
**Swagger** | :heavy_check_mark: |  
**Spring Security 인증 / 인가 필터 구현** | :heavy_check_mark: |  
**회원가입 / 로그인 구현 (JWT 토큰 발급)** | :heavy_check_mark: |  
**포스트 작성, 수정, 삭제, 리스트** | :heavy_check_mark: |  
**AWS EC2에 Docker 배포** | :heavy_check_mark: |  
**Gitlab CI & Crontab CD** | :heavy_check_mark: |  
**화면 UI 개발 (회원가입, 로그인, 글쓰기, 조회, 검색기능 , Validation , Thymeleaf 사용)** | :heavy_check_mark: |  
**ADMIN 회원으로 등급업하는 기능** | :heavy_check_mark: |  
**ADMIN 회원만이 일반 회원을 ADMIN으로 승격시키는 기능(일반회원 등급업 기능 불가)** | :heavy_check_mark: |  
**포스트 좋아요 (Like) / 해당 postId 좋아요 Count** | :heavy_check_mark: |  
**좋아요 취소** |  |  


# ERD 다이어그램
<p align="center">
<img width="360" alt="erd7" src="https://user-images.githubusercontent.com/104709432/209429249-e094ff1d-f979-4604-88fa-b60e72de62d1.PNG">
</p></br>


## 🔽 RestAPI EndPoint

| METHOD | URI                                | 기능               | RequestBody                                      |인증필요             |
| ------ | ---------------------------------- |---------------------------| ------------------------------------- |----------- |
| POST   | /api/v1/users/join                 | 회원가입                      | {"username": "string","password":"string"} |  | 
| POST   | /api/v1/users/login                | 로그인                       | {"username": "string","password":"string"} | | 
| POST   | /api/v1/users/{userId}/role/change | 회원 등급 변경(ADMIN 등급만 가능)    | { "role": "string" }                       |✔ | 
| GET    | /api/v1/users/{id}                      |회원 단건 조회(ADMIN 등급만 가능)  |                                           | ✔| 
| GET    | /api/v1/users/                      |회원 전체 조회(ADMIN 등급만 가능)  |                                           | ✔| 
| GET    | /api/v1/posts                      | 게시글 조회(최신 글 20개 페이징 처리)   |                                           | | 
| GET    | /api/v1/posts/{postId}             | 특정 게시글 상세 조회              |                                           | | 
| POST   | /api/v1/posts                      | 게시글 작성 (jwt 토큰 인증 필요) | { "title": "string" , "body": "string"}    |✔ | 
| PUT    | /api/v1/posts/{postId}             | 게시글 수정 (jwt 토큰 인증 필요) | { "title": "string" , "body": "string"}    |✔ | 
| DELETE | /api/v1/posts/{postId}             | 게시글 삭제 (jwt 토큰 인증 필요) |                                           | ✔| 
| POST | /api/v1/posts/{postId}/likes        | 게시글 좋아요 기능 (jwt 토큰 인증 필요) |                                           |✔ | 
| GET | /api/v1/posts/{postId}/likes            | 해당 게시글 좋아요 갯수 |                                           |✔ | 

## RestAPI Endpoint

### 1. 회원 가입 (POST) : /api/v1/users/join
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "userId": 1,
    "userName": "userName"
  }
}
```
#### 에러 발생📢 
**ErrorCode.DUPLICATED_USER_NAME** -> 이미 가입된 UserName시 CONFLICT(409상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "DUPLICATED_USER_NAME",
    "message": "string은 이미 가입된 이름 입니다."
  }
}
```
<br>

### 2. 회원 로그인 (POST) : /api/v1/users/login
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "jwt": "eyJhbGciOiJIU"
  }
}
```
#### 에러 발생📢 
**ErrorCode.USERNAME_NOT_FOUNDE** -> 해당 회원 DB존재하지 않을 시 NOT_FOUND(404상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "USERNAME_NOT_FOUND",
    "message": "해당 user를 찾을 수 없습니다."
  }
}
```

<br>

### 3. 회원 권한 변경 (POST) : /api/v1/users/{userId}/role/change -> **권한(Role) ADMIN 회원만 가능**

#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "userId": 0,
    "message": "ADMIN"
  }
}
```
#### 에러 발생📢 
**ErrorCode.INVALID_PERMISSION** -> 관리자가 아닌 일반회원으로 인증 시 UNAUTHORIZED(401상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```
<br>

### 4. 게시글  조회 (GET) : /api/v1/posts
#### 성공 JSON 응답✔
```json
{
 {
    "resultCode": "SUCCESS",
    "result": {
        "content": [
            {
                "id": 10,
                "title": "글이 들어온다아아아",
                "body": "글들어온다아앙",
                "userName": "손흥민",
                "createdAt": "2022/12/22 10:43:25",
                "lastModifiedAt": "2022/12/22 10:43:25"
            },
            {
                "id": 8,
                "title": "오늘은 더 추워",
                "body": "집이 최고",
                "userName": "손흥민",
                "createdAt": "2022/12/22 10:08:10",
                "lastModifiedAt": "2022/12/22 10:08:10"
            },
            {
                "id": 6,
                "title": "오늘 춥네요",
                "body": "눈이 엄청왔어요",
                "userName": "string",
                "createdAt": "2022/12/21 13:29:02",
                "lastModifiedAt": "2022/12/21 13:29:02"
            }
        ],
        "pageable": "INSTANCE",
        "last": true,
        "totalPages": 1,
        "totalElements": 3,
        "size": 3,
        "number": 0,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "first": true,
        "numberOfElements": 3,
        "empty": false
    }
}
```

<br>

### 5. 게시글 상세 조회 (GET) :  /api/v1/posts/{postId}
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "id": 1,
    "title": "title1",
    "body": "body",
    "userName": "user1",
    "createdAt": "yyyy/mm/dd hh:mm:ss",
    "lastModifiedAt": "yyyy/mm/dd hh:mm:ss"
  }
}
```
#### 에러 발생📢 
**ErrorCode.POST_NOT_FOUND** -> 존재 하지 않는 글 조회 시 NOT_FOUND(404상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```

<br>

### 6. 게시글 작성 (POST) : /api/v1/posts
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 등록 완료",
    "postId": 0
  }
}
```
```
#### 에러 발생📢 
**ErrorCode.USERNAME_NOT_FOUND** -> 로그인 하지 않은 회원 글 작성 시 NOT_FOUND(404상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "USER_NOT_FOUND",
    "message": "해당 해당 유저는 존재하지 않습니다 없습니다."
  }
}
```
<br>

### 7. 게시글 수정 (PUT) : /api/v1/posts/{postId}
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 수정 완료",
    "postId": 0
  }
}
```
#### 에러 발생📢 
**ErrorCode.POST_NOT_FOUND** -> 존재 하지 않는 글 수정 API 적용한 경우 NOT_FOUND(404상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 포스트가 없습니다."
  }
}
```
<br>

### 8. 게시글 삭제 (DELETE) : /api/v1/posts/{postId}
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": {
    "message": "포스트 삭제 완료",
    "postId": 0
  }
}
```
#### 에러 발생📢 
**ErrorCode.INVALID_PERMISSION** -> 삭제할 권한이 없는 회원 삭제 API 적용한 경우 UNAUTHORIZED(401상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "INVALID_PERMISSION",
    "message": "사용자가 권한이 없습니다."
  }
}
```
### 9. 게시글 좋아요 (POST) : /api/v1/posts/{postId}/likes
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result": "2번의 글 좋아요(Like) 성공"
}
```
#### 에러 발생📢 
**ErrorCode.ALREADY_LIKED** -> 이미 좋아요를 눌렀을 경우 CONFLICT(409상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "ALREADY_LIKED",
    "message": "이미 %d번 글의 좋아요를 눌렀습니다."
  }
}
```

### 10. 해당 게시글 좋아요 갯수 (GET) : /api/v1/posts/{postId}/likes
#### 성공 JSON 응답✔
```json
	
{
  "resultCode": "SUCCESS",
  "result": "2번 게시글의 좋아요 개수 : 1"
}
```
#### 에러 발생📢 
**ErrorCode.POST_NOT_FOUND** -> 존재 하지 않는 글 좋아요 갯수 조회 시 NOT_FOUND(404상태코드) 
```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "POST_NOT_FOUND",
    "message": "해당 글은 존재하지 않습니다."
  }
}
```
<br>



---

<br>


<br>

## MY VIEW 
![Screenshot 2022-12-27 at 17 08 36](https://user-images.githubusercontent.com/104709432/209633961-f3a94bb2-09c7-4e34-ba06-0b31e89068a3.JPG)



<br>
