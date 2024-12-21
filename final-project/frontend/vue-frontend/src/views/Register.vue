<template>
    <div class="register-page">
      <div class="overlay">
        <!-- 艺术字 -->
        <h1 class="artistic-text">欢迎注册</h1>
  
        <!-- 注册框 -->
        <div class="login-box">
          <form @submit.prevent="handleRegister">
            <div class="form-group">
              <input
                id="username"
                v-model="username"
                type="text"
                placeholder="请输入用户名"
              />
            </div>
  
            <div class="form-group">
              <input
                id="password"
                v-model="password"
                type="password"
                placeholder="请输入密码"
              />
            </div>
  
            <div class="form-group">
              <input
                id="phone"
                v-model="phone"
                type="text"
                placeholder="请输入手机号"
              />
            </div>
  
            <button type="submit" class="login-button">注册</button>
            <button type="button" class="back-button" @click="goBack">返回登录</button>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  export default {
    name: "UserRegister",
    data() {
      return {
        username: "",
        password: "",
        phone: "",
      };
    },
    methods: {

        //与后端对接的函数
        async handleRegister() {
    if (!this.username || !this.password || !this.phone) {
      alert("请完整填写所有信息！");
      return;
    }

    try {
      const response = await axios.post("/api/users", {
        username: this.username,
        password: this.password,
        phone: this.phone,
        avatarUrl: null,
        reputationScore: 100,
        isBanned: false,
      });
      alert(`注册成功！用户 ID: ${response.data.id}`);
      this.$router.push("/login");
    } catch (error) {
      console.error("注册失败", error);
      alert("注册失败，请检查后端服务！");
    }
  },

      goBack() {
        this.$router.push('/login');

      },
    },
  };
  </script>
  
  <style scoped>
  html,
  body {
    margin: 0;
    padding: 0;
    height: 100%; /* html 和 body 高度填满视口 */
    overflow: hidden; /* 禁止滚动条 */
  }
  
  .register-page {
    background: url('@/Resources/register.jpg') no-repeat center center; /* 背景图 */
    background-size: cover; /* 确保背景完全填充 */
    position: fixed; /* 锁定背景 */
    top: 0;
    left: 0;
    width: 100vw; /* 填满视口宽度 */
    height: 100vh; /* 填满视口高度 */
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: Arial, sans-serif;
    color: white;
  }
  
  .overlay {
    background-color: rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
  }
  
  .artistic-text {
    font-size: 40px;
    font-weight: bold;
    color: #12c2f3;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
    margin-bottom: 20px;
  }
  
  .login-box {
    background: white;
    border-radius: 12px;
    width: 400px;
    padding: 30px;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  }
  
  .form-group {
    margin-bottom: 20px;
  }
  
  .form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 16px;
    color: #333;
    background-color: #f9f9f9;
  }
  
  .form-group input::placeholder {
    color: #aaa;
  }
  
  .login-button,
  .back-button {
    width: 100%;
    padding: 12px;
    margin-top: 10px; /* 增加按钮之间的间距 */
    background: #3498db;
    border: none;
    border-radius: 6px;
    color: white;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .login-button:hover,
  .back-button:hover {
    background: #2980b9;
  }
  
  .back-button {
    background: #e67e22; /* 返回按钮颜色 */
  }
  
  .back-button:hover {
    background: #d35400; /* 返回按钮悬停颜色 */
  }
  </style>
  