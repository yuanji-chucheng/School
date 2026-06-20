package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.AuditRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.*;

/** 用户接口 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Result<User> profile() {
        return Result.ok(userService.getProfile());
    }

    @PutMapping("/profile")
    public Result<User> updateProfile(@RequestBody User user) {
        return Result.ok(userService.updateProfile(user));
    }

    @GetMapping("/{id:\\d+}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /** 管理员：待审核用户列表 */
    @GetMapping("/pending")
    public PageResult<User> pending(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return userService.pendingUsers(page, size);
    }

    /** 管理员：全部用户 */
    @GetMapping("/admin/list")
    public PageResult<User> all(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return userService.allUsers(page, size);
    }

    /** 管理员：审核用户 */
    @PostMapping("/{id:\\d+}/audit")
    public Result<Void> audit(@PathVariable Long id, @RequestBody AuditRequest req) {
        userService.auditUser(id, Boolean.TRUE.equals(req.getApproved()), req.getReason());
        return Result.ok(null);
    }
}
