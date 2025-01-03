package cn.edu.tongji.instrument.service;
import cn.edu.tongji.instrument.entity.User;
import cn.edu.tongji.instrument.repository.UserRepository;
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

        user.setUsername(updatedUser.getUsername());
        user.setPassword(updatedUser.getPassword());

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
/*

    // 校验用户登录（校验用户名和密码是否匹配）
    public boolean validateUserLogin(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 校验密码是否匹配
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
*/


/*    // 修改用户密码
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
    }*/

    //封禁用户
    public User banUser(Long id)
    {
        return userRepository.findById(id).map(user -> {
            user.setIsBanned(true); // 设置封禁状态
            System.out.println("已解封用户id为："+id);
            return userRepository.save(user); // 保存用户
        }).orElseThrow(() -> new RuntimeException("用户未找到"));
    }

    //解除封禁用户
    public User unbanUser(Long id)
    {
        return userRepository.findById(id).map(user -> {
            user.setIsBanned(false); // 解除封禁状态
            System.out.println("已解封用户id为："+id);
            return userRepository.save(user); // 保存用户
        }).orElseThrow(() -> new RuntimeException("用户未找到"));
    }

    //调整信誉积分
    public User updateReputationScore(Long id, Integer delta) {
        return userRepository.findById(id).map(user -> {
            // 初始化信誉积分为 0（如果为 null）
            if (user.getReputationScore() == null) {
                user.setReputationScore(0);
            }

            // 计算新的信誉积分
            int newScore = user.getReputationScore() + delta;
            if (newScore < 0) {
                newScore = 0; // 确保最低为 0
            }
            user.setReputationScore(newScore);
            System.out.println("已更新用户 ID：" + id + " 的信誉积分，新积分为：" + newScore);

            // 判断信誉积分是否低于 60
            if (newScore < 60) {
                user.setIsBanned(true); // 自动封禁
                System.out.println("用户 ID：" + id + " 的信誉积分低于 60，已自动封禁");
            } else {
                user.setIsBanned(false); // 如果积分恢复到 60 以上，则解除封禁
                System.out.println("用户 ID：" + id + " 的信誉积分高于 60，已自动解禁");
            }

            // 保存用户并返回
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("用户未找到"));

    }

    public User findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + phoneNumber));
    }

    public void updatePassword(String phoneNumber, String newPassword) {
        User user = findByPhoneNumber(phoneNumber);
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}

