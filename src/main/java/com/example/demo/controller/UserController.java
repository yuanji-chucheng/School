package com.example.demo.controller;

import com.example.demo.common.PageResult;
import com.example.demo.common.Result;
import com.example.demo.dto.AuditRequest;
import com.example.demo.dto.PasswordResetRequest;
import com.example.demo.entity.User;
import java.util.List;
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

    /** 管理员：已通过注册的学生列表 */
    @GetMapping("/admin/students")
    public PageResult<User> approvedStudents(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return userService.approvedStudents(page, size);
    }

    /** 管理员：删除学生 */
    @DeleteMapping("/{id:\\d+}/admin")
    public Result<Void> deleteStudent(@PathVariable Long id) {
        userService.deleteStudent(id);
        return Result.ok("删除成功", null);
    }

    /** 管理员：批量删除学生 */
    @DeleteMapping("/admin/batch")
    public Result<Void> deleteStudentsBatch(@RequestBody List<Long> ids) {
        userService.deleteStudentsBatch(ids);
        return Result.ok("批量删除成功", null);
    }

    /** 管理员：重置学生密码 */
    @PostMapping("/{id:\\d+}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody PasswordResetRequest req) {
        userService.resetPassword(id, req);
        return Result.ok("密码已重置", null);
    }
}
