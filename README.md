## 🛠️ SE-Design for Software Testing

### 🌐 前端运行步骤

1. 安装依赖

   ```bash
   npm install
   ```
2. 启动前端服务

   ```bash
   npm run serve
   ```

---

### 🧩 后端配置与运行指南

1. ⚙️ **配置数据库连接**
   在 `application.properties` 中填写你本地的数据库配置（如 URL、用户名、密码）。

2. 🔑 **运行sql脚本**
    执行 `./sql_for_create_table/merged_all.sql` 脚本，导入表结构
    
3. 🛠️ **构建项目**
   使用 IntelliJ IDEA 打开后端项目，采用 Maven 进行构建。

4. 📘 **API 文档访问**
   项目已集成 Swagger，运行后可通过以下地址访问 API 文档：
   [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

5. 💳 **支付流程优化**
   为了便于测试，项目在原始逻辑基础上对支付流程做了简化和修正：

   * 支付操作现在为模拟完成（无需跳转实际支付）
   * 创建订单后立即标记为已支付，并同步更新库存
