# 개인프로젝트 게시판  / JPA를 이용한 RestApi 구현 / Spring Security (JWT 토큰)
<p align="center"><img width="750" alt="jenkins-docker-springboot-cicd" src="https://user-images.githubusercontent.com/104709432/209429451-36eac449-39d0-40a0-ac41-194c979c021d.png">
</p>

## Running Docker on AWS EC2(Hospital Project🔽)
### http://ec2-3-35-209-220.ap-northeast-2.compute.amazonaws.com:8080
#### 메인화면 
![Screenshot 2022-12-24 at 18 39 43](https://user-images.githubusercontent.com/104709432/209433782-d7684bf1-7ff5-4c7b-bbf6-135f84551d53.JPG)
- 1.로그인 회원만 글 작성 가능 - controller 내에서 alert 구현
- 2.글 작성시 Validation 도입. 제목,내용 하나라도 안쓸 시 validate작동
- 3.글 목록 페이징 처리 -> 한 페이지의 10개의 글 / 글 최신 순 정렬
- 4.해당 글 작성 회원만 글 수정/삭제할 수 있는 기능
- 5.view 단 Thymeleaf 사용
## RestApi Swagger 
### http://ec2-3-35-209-220.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html
# ERD 다이어그램
<p align="center">
<img width="360" alt="erd7" src="https://user-images.githubusercontent.com/104709432/209429249-e094ff1d-f979-4604-88fa-b60e72de62d1.PNG">
</p></br>

# Spring Security + JWT Token
### [Spring Security와 JWT Token 인증,인가 처리 (Velog 정리본)](https://velog.io/@guns95/Spring-Security%EC%99%80-JWT-%ED%86%A0%ED%81%B0%EC%9D%98-%EC%9D%B8%EC%A6%9D%EC%9D%B8%EA%B0%80-%EB%A1%9C%EA%B7%B8%EC%9D%B8)
## User RestApi(회원) - JWT 로그인 구현
### @PostMapping("/api/users/join") : 회원가입 api -> BCryptPasswordEncoder를 사용하여 비밀번호 암호화 후 DB저장
#### 🆗회원가입 성공 시
<img width="400" alt="캡처2" src="https://user-images.githubusercontent.com/104709432/204977035-f6ff4669-d473-4d0e-923b-80fcc24129db.PNG"> <br>
- **BCryptPasswordEncoder 암호화된 PassWord로 저장⬇**<br><br>
<img width="779" alt="캡처9" src="https://user-images.githubusercontent.com/104709432/204985692-b9c6ed6a-5616-47b8-a6ba-6ec36b8aa69f.PNG"> <br>
#### 🆖회원가입 실패 시<br>
- **중복되는 userName존재 할 때 ExceptionHandeler에서 HttpStatus.CONFLICT(409), message = "User name is duplicated errorCode"처리**
<img width="550" alt="캡처4" src="https://user-images.githubusercontent.com/104709432/204979030-d5385eec-7a57-45ec-90ae-c561b77e3598.PNG">

### @PostMapping("/api/users/login") : 로그인 -> 로그인 성공 시 Jwt토큰 반환
#### 🆗로그인 성공 시 - > JWT토큰 (인증,인가)
<img width="750" alt="캡처5" src="https://user-images.githubusercontent.com/104709432/204987426-c4ea9a39-a668-4ff1-b16c-a66af691a7a3.PNG"><br>
#### 🆖로그인 실패 시<br>
- **1.userName 존재 X 로그인 실패->HttpStatus.NOT_FOUND(404), message = "Not Found errorCode"처리**
<img width="550" alt="캡처7" src="https://user-images.githubusercontent.com/104709432/204981292-1375306f-966a-4b19-8f80-c15d5b97d5a0.PNG"><br>
- **2.비밀번호 틀릴 시 로그인 실패->HttpStatus.BAD_REQUEST(400), message = "Not correct password errorCode"처리**
<img width="550" alt="캡처6" src="https://user-images.githubusercontent.com/104709432/204980562-6777a266-6046-480a-bed1-be44673ac462.PNG"><br>
### @GetMapping("/api/v1/users/{userId}") : 회원 단건 조회
### @GettMapping("/api/v1/users") : 회원 전체 조회
***

## Post RestApi(게시글)
### @GetMapping("/api/v1/posts/{postId}") : 해당 ID로 게시글 조회
### @PostMapping("/api/v1/posts") : 게시글 등록 
### @PutMapping("/api/v1/posts/{postId}") : 게시글 수정(변경감지 dirty cash 사용)
### @DeleteMapping("/api/v1/posts/{postId}") : 게시글 삭제
### @GetMapping("/api/v1/posts") : 게시글 전체 조회(제네릭타입을 응답 타입으로 사용하여 회원 count 기능 추가)
### @PostMapping("/{postId}/likes") : 해당 게시글 좋아요 누르기 기능 -> 회원이 이미 좋아요를 눌렀다면 Exception(LikeException) 발생 => 상태코드 <CONFLICT 409>
### @GetMapping("/{postId}/likes") : 해당 게시글 좋아요 COUNT 반환 
<br><br>
***
## Admin RestApi(관리자)
### @PostMapping("/api/v1/admins/{id}") : Id에 해당하는 회원의 UserRole(USER,ADMIN) 전환 API
