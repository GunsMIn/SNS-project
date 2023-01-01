
package com.example.crudpersional.mvc.controller;

import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.PostForm;
import com.example.crudpersional.mvc.dto.SessionConst;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import com.example.crudpersional.service.PostService;
import com.sun.xml.bind.v2.model.core.ID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.List;
@ApiIgnore
@Controller
@RequiredArgsConstructor
@Slf4j
public class PostMvcController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @GetMapping("/posts/form")
    public String goWriteForm(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @ModelAttribute PostForm postForm, HttpServletResponse response) throws Exception{

        String url = "";
        //아래의 코드는 로그인을 하지 않았다면 alert를 띄우고 로그인 안내
        if (loginMember != null) {
            //세션에 저장된 user의 정보
            String userName = loginMember.getUsername();
            postForm.setUserName(userName);
            url = "writePost";
        }else{// -> 로그인 안되어 있을 시 알림창 후 메인화면으로
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('글 작성은 로그인 후에 진행해주세요🤗'); history.go(-1);</script>");
            out.flush();
        }
        return url;
        //
    }

    //validated 적용
    @PostMapping("/posts/doForm")
    public String doWriteForm(@Validated @ModelAttribute PostForm postForm, BindingResult result, String userName,HttpServletResponse response,Model model) throws Exception {
        //postForm dto에 설정한 validation에 걸릴 시 글 쓰기 폼으로 view 이동
        if(result.hasErrors()){
            return "writePost";
        }

        String url = "";
        //if문안에 조건은 제목 또는 내용이 없을 시 경고창을 띄우고 /members/loginIndex로 리다이렉트 전송
        if (postForm.getTitle()!=null && postForm.getBody()!=null) {
            postService.addMvcPost(postForm, userName);
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('글 작성이 완료되었습니다.🤗'); window.location.href = '/posts/list';</script>");
            out.flush();
        }else{
            url = "redirect:/posts/form";
        }
        return url;
    }

    @GetMapping("/posts/list")
    public String getPostList(@PageableDefault(page = 0 ,size = 10, sort ="registeredAt",
            direction = Sort.Direction.DESC) Pageable pageable, Model model,String title) throws Exception {
        //비 로그인 사용자 시 로그인 유도
        Page<Post> posts = null;
        //title 있을 시 검색조건 페이징 처리 작동
        if (title == null) {
            posts = postService.getViewPosts(pageable);
        }else{
            posts = postService.searchByTitle(pageable, title);
        }

        //new PostSelectResponse();
        //페이지블럭 처리
        //1을 더해주는 이유는 pageable은 0부터라 1을 처리하려면 1을 더해서 시작해주어야 한다.
        int nowPage = posts.getPageable().getPageNumber() + 1;
        //-1값이 들어가는 것을 막기 위해서 max값으로 두 개의 값을 넣고 더 큰 값을 넣어주게 된다.
        int startPage =  Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage+9, posts.getTotalPages());
        log.info("list:{}",posts);
        model.addAttribute("title", title);
        model.addAttribute("posts", posts);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "post/postList";
    }


    //포스트 상세보기
    @GetMapping("/post/getOne/{id}")
    public String getPost(@PathVariable Long id,Model model) {
        log.info("id :{}" ,id);
        PostSelectResponse post = postService.getPost(id);
        model.addAttribute("post", post);
        return "post/postDetail";
    }


    @GetMapping("/post/{id}/edit")
    public String updatePost(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @PathVariable Long id,Model model,HttpServletResponse response) throws Exception{
        log.info("id :{}" ,id);

        if (loginMember == null) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('해당 글을 작성한 회원만 수정 할 권한이 있습니다.🤗'); history.go(-1);</script>");
            out.flush();
        }

        PostSelectResponse post = postService.getPost(id);
        if (!loginMember.getUsername().equals(post.getUserName())) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, "해당 글을 작성한 회원만 수정 할 권한이 있습니다");
        }

        model.addAttribute("postForm", post);
        model.addAttribute("postId", id);
        log.info("타임리프에 넘길 id:{}", id);
        return "updatePost";
    }

    // 글 수정
    @PostMapping("/post/{id}/edit")
    public String doUpdatePost(@PathVariable Long id,@Validated @ModelAttribute PostForm postForm,BindingResult result,HttpServletResponse response) throws Exception{
        //postForm dto에 설정한 validation에 걸릴 시 글 쓰기 폼으로 view 이동
        if(result.hasErrors()){
            return "updatePost";
        }

        String url = "";
        //if문안에 조건은 제목 또는 내용이 없을 시 경고창을 띄우고 /members/loginIndex로 리다이렉트 전송
        if (postForm.getTitle()!=null && postForm.getBody()!=null) {
            postService.updateMvcPost(id,postForm);
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('글 수정이 완료되었습니다.🤗');location.assign('/members/loginIndex');</script>");
            out.flush();
        }else{
            url = "redirect:/post/{id}/edit";
        }
        return url;
    }

    @PostMapping("post/{id}/delete")
    public String delete(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) User loginMember, @PathVariable Long id, HttpServletResponse response, HttpSession session) throws Exception {

        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 post는 존재하지 않습니다."));
        log.info("session id:{}",loginMember.getId());
        log.info("posted user id:{}",post.getUser().getId());
        if (loginMember.getId() != post.getUser().getId() || session.getAttribute("loginMember") == null) {
            throw new PostException(ErrorCode.INVALID_PERMISSION, "글을 작성한 본인만 글을 삭제할 수 있습니다");
        }else{
            postService.deleteMvcPost(id);
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('글 삭제가 완료되었습니다.🤗');location.assign('/posts/list');</script>");
            out.flush();

        }
        return "/";
    }


}

