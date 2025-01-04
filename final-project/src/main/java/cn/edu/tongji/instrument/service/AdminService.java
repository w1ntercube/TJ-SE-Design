package cn.edu.tongji.instrument.service;

import cn.edu.tongji.instrument.entity.Admin;
import cn.edu.tongji.instrument.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // 检查管理员是否存在
    public boolean adminExistsById(Long id) {
        return adminRepository.existsById(id);
    }
    // 根据用户名查找管理员
    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
}
