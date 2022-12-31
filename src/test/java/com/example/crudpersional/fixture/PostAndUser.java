package com.example.crudpersional.fixture;
import lombok.Data;

@Data
public  class PostAndUser{
    private Long postId;
    private Long userId;
    private String userName;
    private String password;
    private String title;
    private String body;

    public static PostAndUser getDto() {
        PostAndUser p = new PostAndUser();
        p.setPostId(1L);
        p.setUserId(1L);
        p.setUserName("test");
        p.setPassword("1234");
        p.setTitle("테스트 제목");
        p.setBody("테스트 내용");
        return p;
    }
}