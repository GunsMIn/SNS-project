# JPA를 이용한 RestApi 구현 / Spring Security (JWT 토큰 인증/인가) / CI-CD구축 / 비동기 좋아요,댓글 처리
<p align="center"><img width="750" alt="jenkins-docker-springboot-cicd" src="https://user-images.githubusercontent.com/104709432/209429451-36eac449-39d0-40a0-ac41-194c979c021d.png">
</p>
</br>

## RestApi Swagger 
### [http://ec2-52-79-151-163.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html](http://ec2-52-79-151-163.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html)</br>
## 화면 UI (AWS EC2 server🔽)
### [http://ec2-52-79-151-163.ap-northeast-2.compute.amazonaws.com:8080/](http://ec2-52-79-151-163.ap-northeast-2.compute.amazonaws.com:8080/)
## Spring Security + JWT Token
### [Spring Security와 JWT Token 인증,인가 처리 (Velog 정리본)](https://velog.io/@guns95/Spring-Security%EC%99%80-JWT-%ED%86%A0%ED%81%B0%EC%9D%98-%EC%9D%B8%EC%A6%9D%EC%9D%B8%EA%B0%80-%EB%A1%9C%EA%B7%B8%EC%9D%B8)
## 해당 프로젝트 Test Code정리본
### [Controller TDD](https://velog.io/@guns95/Spring-Controller-Test)
### [Service TDD](https://velog.io/@guns95/Spring-Service-Test)
## Axios를 이용한 비동기 서버 통신 정리본
### [Axios 비동기 서버통신](https://velog.io/@guns95/%EC%84%9C%EB%B2%84-%EB%B9%84%EB%8F%99%EA%B8%B0-%ED%86%B5%EC%8B%A0-Axios-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%8F%84%EC%9E%85)
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
**화면 UI 개발 (회원가입, 로그인, 글쓰기, 조회, 검색기능 ,댓글, 좋아요, 알림 Validation , Thymeleaf 사용)** | :heavy_check_mark: |  
**댓글 등록 비동기 처리 구현 (Ajax)** | :heavy_check_mark: |  
**ADMIN 회원만이 일반 회원을 ADMIN으로 승격시키는 기능(일반회원 등급업 기능 불가)** | :heavy_check_mark: |  
**포스트 좋아요 (Like) / 해당 postId 좋아요 Count** | :heavy_check_mark: |  
**좋아요 취소** |:heavy_check_mark:  |  
**댓글 작성/수정/삭제/리스트 조회(최신순,20개)** | :heavy_check_mark: |  
**포스트 / 댓글 / 좋아요 Soft Delete 적용** | :heavy_check_mark: |  
**Axios를 이용한 비동기 처리** | :heavy_check_mark: |  

# ERD 다이어그램
<p align="center">
<img width="530" alt="erd8" src="https://user-images.githubusercontent.com/104709432/210302879-a6acb17d-16cf-4732-aa2d-22f2de150070.PNG">
</p></br>

<hr>

### 테스트 전용 로그인 회원
**User**
> - ID : lion
>
> - PW : kk1234
<hr>

## 🔽 RestAPI EndPoint

| METHOD | URI                                | 기능               | RequestBody                                      |인증필요             |
| ------ | ---------------------------------- |---------------------------| ------------------------------------- |----------- |
| POST   | /api/v1/**users**/join                 | 회원가입                      | {"username": "string","password":"string"} |  | 
| POST   | /api/v1/**users**/login                | 로그인                       | {"username": "string","password":"string"} | | 
| POST   | /api/v1/**users**/{userId}/role/change | 회원 등급 변경(ADMIN 등급만 가능)    | { "role": "string" }                       |✔ | 
| GET    | /api/v1/**users**/{id}                      |회원 단건 조회(ADMIN 등급만 가능)  |                                           | ✔| 
| GET    | /api/v1/**users**/                      |회원 전체 조회(ADMIN 등급만 가능)  |                                           | ✔| 
| GET    | /api/v1/**posts**                      | 게시글 조회(최신 글 20개 페이징 처리)   |                                           | | 
| GET    | /api/v1/**posts**/{postId}             | 특정 게시글 상세 조회              |                                           | | 
| POST   | /api/v1/**posts**                      | 게시글 작성 (jwt 토큰 인증 필요) | { "title": "string" , "body": "string"}    |✔ | 
| PUT    | /api/v1/**posts**/{postId}             | 게시글 수정 (jwt 토큰 인증 필요) | { "title": "string" , "body": "string"}    |✔ | 
| DELETE | /api/v1/**posts**/{postId}             | 게시글 삭제 (Soft Delete 적용) |                                           | ✔| 
| GET | /api/v1/**posts**/my           | 내가 쓴 포스트 보기(최신순,20개) |                                           |✔ | 
| GET | /api/v1/**alarms**          | 알림 보기(최신순,20개) |                                           | | 
| POST | /api/v1/**posts**/{postId}/likes        | 게시글 좋아요 기능 (jwt 토큰 인증 필요) |                                           |✔ | 
| POST | /api/v1/{id}/**comments**            | 해당 게시글 댓글 달기 |  { "comment": "string" }                                          |✔ | 
| PUT | /api/v1/{id}/**comments**             | 해당 게시글 댓글 수정 |           { "comment": "string" }                                 |✔ | 
| DELETE | /api/v1/{id}/**comments**             | 해당 게시글 댓글 삭제 (Soft Delete 적용) |                                           |✔ | 
| GET | /api/v1/{id}/**comments**            | 해당 게시글 댓글 조회(페이징,최신순) |                                           | | 


<br>

## ✔ Controller / Service Test Result✔ </BR>
###  ✅Controller Test Result
<img width="630" alt="테스트 1" src="https://user-images.githubusercontent.com/104709432/210554589-65f433ef-b40f-42da-ac1e-1057056436d8.PNG"></BR>

###  ✅Service Test Result
<img width="630" alt="XP4" src="https://user-images.githubusercontent.com/104709432/210555591-d2d8458a-650f-4375-9535-69ad0e573ea6.PNG">


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
### 11. 마이 피드 보기 (GET) : /api/v1/posts/my
#### 성공 JSON 응답✔
```json
{
  "resultCode": "SUCCESS",
  "result":{
    "content":[
			{
			"id": 4,
			"title": "test",
			"body": "body",
			"userName": "test",
			"createdAt": "2022-12-16T16:50:37.515952"
			}
		],
	"pageable":{
			"sort":{"empty": true, "sorted": false, "unsorted": true }, "offset": 0,…},
			"last": true,
			"totalPages": 1,
			"totalElements": 1,
			"size": 20,
			"number": 0,
			"sort":{
			"empty": true,
			"sorted": false,
			"unsorted": true
			},
			"numberOfElements": 1,
	"first": true,
	"empty": false
}
```
### 12. 댓글 조회 (GET) : /api/v1/posts/{id}/comments
#### 성공 JSON 응답✔
```json
{
	"resultCode": "SUCCESS",
	"result":{
	"content":[
		{
		"id": 3,
		"comment": "comment test3",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:07:25.699346"
		},
		{
		"id": 2,
		"comment": "comment test2",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:03:30.670768"
		}
	],
	"pageable":{"sort":{"empty": false, "sorted": true, "unsorted": false }, 
	"offset": 0,…},
	"last": true,
	"totalPages": 1,
	"totalElements": 2,
	"size": 10,
	"number": 0,
	"sort":{
	"empty": false,
	"sorted": true,
	"unsorted": false
	},
	"numberOfElements": 2,
	"first": true,
	"empty": false
	}
}
```

### 13. 댓글 작성 (POST) : /api/v1/posts/{id}/comments
#### 성공 JSON 응답✔
```json
{
	"resultCode": "SUCCESS",
	"result":{
		"id": 4,
		"comment": "comment test4",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:15:04.270741"
	}
}
```
### 14. 댓글 수정 (PUT) : /api/v1/posts/{id}/comments
#### 성공 JSON 응답✔
```json
{
	"resultCode": "SUCCESS",
	"result":{
		"id": 4,
		"comment": "modify comment",
		"userName": "test",
		"postId": 2,
		"createdAt": "2022-12-20T16:15:04.270741"
		}
}
```
### 15. 댓글 삭제 (DELETE) : /api/v1/posts/{id}/comments
#### 성공 JSON 응답✔
```json
{
	"resultCode": "SUCCESS",
	"result":{
		"message": "댓글 삭제 완료",
		"id": 4
		}
}
```
### 16. 알람 리스트 (GET) : /api/v1/alarms
- 특정 사용자의 글에 대한 알림 조회
#### 성공 JSON 응답✔
```json
{
	"resultCode":"SUCCESS",
  "result": {
	"content":
	[
		{
	      "id": 1,
	      "alarmType": "NEW_LIKE_ON_POST",
        "fromUserId": 1,
        "targetId": 1,
	      "text": "new like!",
	      "createdAt": "2022-12-25T14:53:28.209+00:00",
	  }
	]
	}
}
```
<br>



---

<br>




## MAIN
![메인페이지](https://user-images.githubusercontent.com/104709432/211267784-418628d8-3908-4514-80df-bd0e7a27fe44.JPG)
## Login(Validation 적용)
![Screenshot 2023-01-09 at 11 57 12](https://user-images.githubusercontent.com/104709432/211234914-b17eafdf-9110-41b7-b740-edf4866b8ace.JPG)
## WritePost(Validation 적용)
![글쓰기](https://user-images.githubusercontent.com/104709432/211235183-9b7af6f0-4330-43f9-b58b-58aad474b376.JPG)
## PostList(최신순 /댓글순 / 좋아요순 / 검색기능 / 페이징처리)
![Screenshot 2023-01-09 at 11 58 16](https://user-images.githubusercontent.com/104709432/211235007-c29b676e-4b41-4946-af57-6f3fa79fab1d.JPG)
## PostDetail(Axios 댓글 , 좋아요 비동기 처리 / 게시글 수정, 삭제 / 댓글 등록 , 삭제 )
![Screenshot 2023-01-09 at 11 58 46](https://user-images.githubusercontent.com/104709432/211235217-68e93921-7c99-4651-8ec5-a0a6babec096.JPG)
## Alarms(내가 쓴 글의 댓글,좋아요 알람)
![Screenshot 2023-01-09 at 17 21 09](https://user-images.githubusercontent.com/104709432/211265771-b3b93c74-ac62-49a5-92ed-f9140c52b9fc.JPG)

<br>
