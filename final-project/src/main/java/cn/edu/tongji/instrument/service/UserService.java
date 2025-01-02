package cn.edu.tongji.instrument.service;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 新增用户
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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

            // 更新密码时仅在提供新密码的情况下进行更新
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty() &&
                    !passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

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

        // 更新密码时仅在提供新密码的情况下进行更新
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty() &&
                !passwordEncoder.matches(updatedUser.getPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        user.setUsername(updatedUser.getUsername());
        user.setPhone(updatedUser.getPhone());
        user.setAvatarUrl(updatedUser.getAvatarUrl());
        user.setReputationScore(updatedUser.getReputationScore());
        user.setIsBanned(updatedUser.getIsBanned());
        return userRepository.save(user);
    }

    // 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    //查询指定名字用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 校验用户登录（校验用户名和密码是否匹配）
    public boolean validateUserLogin(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验密码是否匹配
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // 修改用户密码
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 校验原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

