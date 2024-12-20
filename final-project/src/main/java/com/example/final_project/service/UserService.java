package com.example.final_project.service;
import com.example.final_project.entity.User;
import com.example.final_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 新增用户
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 查询所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 根据 ID 查询用户
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // 更新用户
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            user.setPhone(updatedUser.getPhone());
            user.setAvatarUrl(updatedUser.getAvatarUrl());
            user.setReputationScore(updatedUser.getReputationScore());
            user.setIsBanned(updatedUser.getIsBanned());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("用户未找到"));
    }

    // 更新用户信息，根据用户名
    public User updateUserByUsername(String username, User updatedUser) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 更新用户信息
        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());
        user.setPhone(updatedUser.getPhone());
        user.setAvatarUrl(updatedUser.getAvatarUrl());
        user.setReputationScore(updatedUser.getReputationScore());
        user.setIsBanned(updatedUser.getIsBanned());
        return userRepository.save(user);  // 保存更新后的用户
    }

    // 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    //查询指定名字用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

