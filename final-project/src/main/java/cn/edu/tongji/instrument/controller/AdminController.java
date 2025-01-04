package cn.edu.tongji.instrument.controller;

import cn.edu.tongji.instrument.dto.LoginRequest;
import cn.edu.tongji.instrument.entity.Admin;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // 管理员登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 通过用户名查找管理员
        Admin admin = adminService.findByUsername(loginRequest.getUsername());
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("管理员不存在");
        }

        // 验证密码
        if (!admin.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
        }

        return ResponseEntity.ok(admin);
    }

    // 检查管理员是否存在
    @GetMapping("/login")
    public ResponseEntity<Boolean> checkAdminExists(@PathVariable Long id) {
        // 调用服务层方法检查管理员是否存在
        boolean exists = adminService.adminExistsById(id);

        // 返回结果
        if (exists) {
            return ResponseEntity.ok(true); // 存在时返回 200 和 true
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false); // 不存在时返回 404 和 false
        }
    }
}
