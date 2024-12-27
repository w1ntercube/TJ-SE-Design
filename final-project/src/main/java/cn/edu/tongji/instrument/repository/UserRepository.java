package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // 根据用户名查询用户
    User findByPhone(String phone); // 根据手机号查询用户
}
