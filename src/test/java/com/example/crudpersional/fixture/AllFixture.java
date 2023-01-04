package com.example.crudpersional.fixture;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import lombok.Data;

@Data
public  class AllFixture {
    private Long postId;
    private Long userId;
    private String userName;
    private String password;
    private String title;
    private String body;
    /**알람**/
    private AlarmType alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;
    /**댓글**/
    private Long commentId;
    private String comment;
    private boolean deleted;
    /**좋아요**/
    private Long likeId;
    private Integer count;

    public static AllFixture getDto() {
        AllFixture p = new AllFixture();
        p.setPostId(1L);
        p.setUserId(1L);
        p.setUserName("test");
        p.setPassword("1234");
        p.setTitle("테스트 제목");
        p.setBody("테스트 내용");
        p.setAlarmType(AlarmType.NEW_COMMENT_ON_POST);
        p.setFromUserId(2L);
        p.setTargetId(10L);
        p.setText("New Comment!");
        p.setCommentId(1L);
        p.setComment("댓글씁니다");
        p.setDeleted(false);
        p.setLikeId(1L);
        p.setCount(10);
        return p;
    }
}