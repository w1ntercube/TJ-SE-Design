package com.example.final_project.controller;

import com.example.final_project.dto.ChangePasswordRequest;
import com.example.final_project.dto.LoginRequest;
import com.example.final_project.entity.User;
import com.example.final_project.service.UserService;
import com.example.final_project.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 注册接口
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = userService.findByUsername(loginRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户不存在");
        }
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("密码错误");
        }

        // 如果用户被封禁
        if (user.getIsBanned() != null && user.getIsBanned()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账户已被封禁");
        }
        return ResponseEntity.ok(user); // 登录成功返回用户信息
    }

    //修改密码接口
    // 修改密码接口
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        // 1. 验证用户名是否存在
        User user = userService.findByUsername(changePasswordRequest.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用户名不存在");
        }

        // 2. 验证原密码是否正确
        if (!user.getPassword().equals(changePasswordRequest.getOldPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("原密码错误");
        }

        // 3. 更新密码
        user.setPassword(changePasswordRequest.getNewPassword());
        userService.updateUser(user.getId(), user); // 假设 updateUser 方法支持修改密码

        return ResponseEntity.ok("密码修改成功");
    }

    // 查询所有用户
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 根据 ID 查询用户
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // 根据用户名获取用户信息
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 如果没有找到用户，返回 404
        }
        return ResponseEntity.ok(user);  // 如果找到用户，返回 200 和用户数据
    }

    // 更新用户
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    // 上传头像接口
    @PostMapping("/upload-avatar")
    public ResponseEntity<String> uploadAvatar(
            @RequestParam("username") String username,
            @RequestParam("image") MultipartFile imageFile) {

        try {
            // 获取用户信息，通过用户名查找用户
            User user = userService.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(400).body("User not found");
            }


            long fileId = System.currentTimeMillis();  // 使用时间戳生成唯一商品ID

            // 保存图片到指定目录
            String avatarPath = FileUploadUtil.saveFile(imageFile, fileId, "uploads/avatars");
            System.out.println("保存的路径为："+ avatarPath);

            //删除原来的头像.
            String deletePath =user.getAvatarUrl();
            System.out.println("删除的路径为： "+deletePath);
            if(deletePath!=null &&!deletePath.isEmpty())
              FileUploadUtil.deleteFile(deletePath);
            // 更新用户的头像路径
            user.setAvatarUrl(avatarPath);
            userService.updateUser(user.getId(), user);  // 假设这里有个更新用户信息的服务方法
            return ResponseEntity.ok("Avatar uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }
}
