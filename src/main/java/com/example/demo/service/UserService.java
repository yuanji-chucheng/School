package com.example.demo.service;

import com.example.demo.common.BusinessException;
import com.example.demo.common.PageResult;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.PasswordUtil;
import com.example.demo.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/** 用户认证与管理服务 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    public UserService(UserMapper userMapper, JwtUtil jwtUtil, NotificationService notificationService) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
    }

    public void register(RegisterRequest req) {
        if (userMapper.findByStudentId(req.getStudentId()) != null) {
            throw new BusinessException("学号已注册");
        }
        User user = new User();
        user.setStudentId(req.getStudentId());
        user.setPassword(PasswordUtil.encrypt(req.getPassword()));
        user.setNickname(req.getNickname());
        user.setRole(0);
        user.setStatus(0); // 待审核
        user.setCreditScore(100);
        userMapper.insert(user);
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.findByStudentId(req.getStudentId());
        if (user == null || !PasswordUtil.matches(req.getPassword(), user.getPassword())) {
            throw new BusinessException("学号或密码错误");
        }
        if (user.getRole() == 0 && user.getStatus() == 0) {
            throw new BusinessException("账号待审核，请等待管理员通过");
        }
        if (user.getStatus() == 2) {
            throw new BusinessException("账号已被驳回，无法登录");
        }
        LoginResponse resp = new LoginResponse();
        resp.setToken(jwtUtil.generateToken(user.getId(), user.getStudentId(), user.getRole()));
        resp.setUserId(user.getId());
        resp.setStudentId(user.getStudentId());
        resp.setNickname(user.getNickname());
        resp.setRole(user.getRole());
        resp.setStatus(user.getStatus());
        return resp;
    }

    public User getProfile() {
        User user = userMapper.findById(UserContext.getUserId());
        user.setPassword(null);
        return user;
    }

    public User updateProfile(User user) {
        user.setId(UserContext.getUserId());
        userMapper.update(user);
        return getProfile();
    }

    /** 管理员审核用户 */
    public void auditUser(Long userId, boolean approved, String reason) {
        checkAdmin();
        User user = userMapper.findById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        user.setStatus(approved ? 1 : 2);
        userMapper.update(user);
        notificationService.send(userId,
                approved ? "注册审核通过" : "注册审核驳回",
                approved ? "您的账号已通过审核，可以正常使用" : "驳回原因：" + (reason != null ? reason : "不符合要求"),
                "USER_AUDIT");
    }

    public PageResult<User> pendingUsers(int page, int size) {
        checkAdmin();
        int offset = (page - 1) * size;
        List<User> rows = userMapper.findPending(offset, size);
        rows.forEach(u -> u.setPassword(null));
        return new PageResult<>(rows, userMapper.countPending());
    }

    public PageResult<User> allUsers(int page, int size) {
        checkAdmin();
        int offset = (page - 1) * size;
        List<User> rows = userMapper.findAll(offset, size);
        rows.forEach(u -> u.setPassword(null));
        return new PageResult<>(rows, userMapper.countAll());
    }

    public User getById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) user.setPassword(null);
        return user;
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "需要管理员权限");
        }
    }
}
