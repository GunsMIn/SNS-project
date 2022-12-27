package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.Response;
import com.example.crudpersional.domain.dto.Result;
import com.example.crudpersional.domain.dto.user.UserAdminResponse;
import com.example.crudpersional.domain.dto.user.UserListResponse;
import com.example.crudpersional.domain.dto.user.UserRoleDto;
import com.example.crudpersional.domain.dto.user.UserSelectResponse;
import com.example.crudpersional.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class AdminController {

    private final UserService userService;

    //회원 조회
    @ApiOperation(value = "회원 단건 조회(admin)", notes = "userId로 회원 단건 조회")
    @GetMapping("/{userId}")
    public Response<UserSelectResponse> getOne(@PathVariable Long userId) {

        UserSelectResponse userSelectResponse = userService.getUser(userId);
        return Response.success(userSelectResponse);

    }
    //회원 전체 조회
    @ApiOperation(value = "회원 전체 조회(admin)", notes = "회원 전체 조회")
    @GetMapping
    public Result<List<UserListResponse>> getOne() {
        List<UserListResponse> responseList = userService.getUsers();
        return new Result(responseList.size(),responseList);
    }

    @ApiOperation(value = "회원 UserRole 전환", notes = "회원 UserRole(USER,ADMIN) 전환 API ")
    @ApiImplicitParams({
                    @ApiImplicitParam(
                            name = "id"
                            , value = "회원 ID"
                            , required = true
                            , dataType = "Long"
                            , paramType = "path"
                            , defaultValue = "None")
            })
    @PostMapping("/{id}/role/change")
    public Response<UserAdminResponse> updateUserRole(@PathVariable Long id, @RequestBody UserRoleDto userRoleDto,Authentication authentication) {
        log.info("유저 권한 변경 userId:{}", id);
        UserAdminResponse changeRoleResponse = userService.changeRole(authentication.getName(), id, userRoleDto);
        return Response.success(changeRoleResponse);
    }


}
