<template>
    <div class="login-page">
      <div class="overlay">
        <!-- 动态艺术字标题 -->
        <h1 class="artistic-text">{{ title }}</h1>
        <h2 class="artistic-text subtitle">请完成以下操作</h2>
  
        <!-- 根据action展示不同的表单 -->
        <div class="login-box" v-if="action === 'modify'">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <input
                id="username"
                v-model="username"
                type="text"
                placeholder="请输入用户名"
                maxlength="50"
              />
            </div>
  
            <div class="form-group">
              <input
                id="oldPassword"
                v-model="oldPassword"
                type="password"
                placeholder="请输入旧密码"
              />
            </div>
  
            <div class="form-group">
              <input
                id="newPassword"
                v-model="newPassword"
                type="password"
                placeholder="请输入新密码"
              />
            </div>
  
            <button type="submit" class="login-button">确认修改</button>
          </form>
        </div>
  
        <div class="login-box" v-if="action === 'recover'">
          <form @submit.prevent="handleSubmit">
            <div class="form-group">
              <input
                id="phone"
                v-model="phone"
                type="text"
                placeholder="请输入手机号"
                maxlength="11"
              />
            </div>
  
            <div class="form-group">
              <input
                id="code"
                v-model="code"
                type="text"
                placeholder="请输入验证码"
              />
              <button type="button" class="code-button" @click="sendCode" :disabled="codeSent">
                {{ codeButtonText }}
              </button>
            </div>
  
            <div class="form-group">
              <input
                id="newPassword"
                v-model="newPassword"
                type="password"
                placeholder="请输入新密码"
              />
            </div>
  
            <button type="submit" class="login-button">确认找回</button>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  export default {
    name: "PasswordHandler",
    props: ["action"], // 接收路由参数，用于区分找回密码和修改密码
    data() {
      return {
        username: "", // 用于修改密码时的用户名
        oldPassword: "", // 用于修改密码时的旧密码
        phone: "", // 用于找回密码时的手机号
        code: "", // 用于找回密码时的验证码
        newPassword: "", // 新密码
        codeSent: false, // 验证码是否已发送
        codeButtonText: "发送验证码",
      };
    },
    computed: {
      title() {
        return this.action === "modify" ? "修改密码" : "找回密码";
      },
    },
    methods: {
      sendCode() {
        if (!this.phone || this.phone.length !== 11) {
          alert("请输入正确的手机号！");
          return;
        }
        this.codeSent = true;
        this.codeButtonText = "验证码已发送";
        setTimeout(() => {
          this.codeSent = false;
          this.codeButtonText = "重新发送验证码";
        }, 60000); // 60 秒后允许重新发送
  
        // 这里调用第三方短信接口，例如阿里云、腾讯云
        alert("验证码已发送到手机：" + this.phone);
      },

      //与后端对接的逻辑
      handleSubmit() {
        if (this.action === "modify") {
          if (!this.username || !this.oldPassword || !this.newPassword) {
            alert("请填写完整信息！");
            return;
          }
  
      // 调用后端修改密码 API
      axios
        .put("http://localhost:8080/api/users/change-password", {
          username: this.username,
          oldPassword: this.oldPassword,
          newPassword: this.newPassword,
        })
        .then((response) => {
          alert(response.data); // 返回成功信息
          // 跳转登录页面
      this.$router.push("/login");
        })
        .catch((error) => {
          if (error.response) {
            alert(error.response.data); // 错误信息
          } else {
            alert("请求失败：" + error.message);
          }
        });

    }   
        else if (this.action === "recover")
         {
          if (!this.phone || !this.code || !this.newPassword) {
            alert("请填写完整信息！");
            return;
          }
          console.log("找回密码逻辑");
        }
      },
    },
  };
  </script>
  
  <style scoped>
  html,
  body {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow: hidden;
  }
  
  .login-page {
    background: url('@/Resources/Login.jpg') no-repeat center center;
    background-size: cover;
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    font-family: Arial, sans-serif;
    color: white;
  }
  
  .artistic-text {
    font-size: 60px;
    font-weight: bold;
    color: #12c2f3;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
    margin-bottom: 10px;
    text-align: center;
  }
  
  .subtitle {
    font-size: 28px;
    color: #12c2f3;
    margin-top: 5px;
  }
  
  .login-box {
    background: white;
    border-radius: 12px;
    width: 400px;
    padding: 30px;
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
  }
  
  h3 {
    font-size: 24px;
    margin-bottom: 20px;
    color: #333;
    text-align: center;
  }
  
  .form-group {
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
  }
  
  .form-group input {
    width: 70%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 16px;
  }
  
  .code-button {
    width: 25%;
    padding: 10px;
    background: #3498db;
    border: none;
    border-radius: 6px;
    color: white;
    font-size: 12px;
    cursor: pointer;
  }
  
  .code-button:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
  
  .login-button {
    width: 100%;
    padding: 12px;
    background: #12c2f3;
    border: none;
    border-radius: 6px;
    color: white;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
  }
  
  .login-button:hover {
    background: #2980b9;
  }
  </style>
  
  
  
  
  