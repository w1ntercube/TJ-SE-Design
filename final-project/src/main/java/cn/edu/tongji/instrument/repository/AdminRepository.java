package cn.edu.tongji.instrument.repository;

import cn.edu.tongji.instrument.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsById(Long id);
    Admin findByUsername(String username);
}
