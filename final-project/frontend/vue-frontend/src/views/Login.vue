<template>
    <div class="login-page">
      <div class="overlay">
        <!-- 艺术字 -->
        <h1 class="artistic-text">{{ line1 }}</h1>
        <h2 class="artistic-text subtitle">{{ line2 }}</h2>
  
        <!-- 登录框 -->
        <div class="login-box">
          <form @submit.prevent="handleLogin">
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
  
            <div class="options">
              <span @click="forgotPassword" class="clickable">找回密码</span>
              <span @click="modifyPassword" class="clickable">修改密码</span>
            </div>
  
            <button type="submit" class="login-button">登录</button>
            <button type="button" class="register-button" @click="register">注册</button>
          </form>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios"; 
  export default {
    name: "UserLogin",
    data() {
      return {
        username: "",
        password: "",
        fullLine1: "琴音袅袅", // 第一行文字
        fullLine2: "——爱乐者的交易集市——", // 第二行文字
        line1: "", // 当前显示的第一行
        line2: "", // 当前显示的第二行
      };
    },
    mounted() {
    this.typeEffect(this.fullLine1, "line1", () => {
      this.typeEffect(this.fullLine2, "line2"); // 第二行文字
    });
    },
    methods: {
        typeEffect(text, target, callback) {
      let index = 0;
      const interval = setInterval(() => {
        if (index < text.length) {
          this[target] += text[index];
          index++;
        } else {
          clearInterval(interval);
          if (callback) callback(); // 如果有回调，执行它
        }
      }, 50); // 每个字显示的时间间隔
    },

      
    //对接后端，进行用户的登录
    async handleLogin() {
    if (!this.username || !this.password) {
      alert("请填写用户名和密码！");
      return;
    }
    try {
      // 尝试管理员登录
      const adminResponse = await axios.post("/api/admin/login", {
        username: this.username,
        password: this.password,
      });

      const userResponse = await axios.post("/api/users/login", {
        username: this.username,
        password: this.password,
      });

      const adminData = adminResponse.data; 
      const userData = userResponse.data; 

         
      localStorage.setItem("user", JSON.stringify(userData));

      // 登录成功提示
      alert(`欢迎回来，管理员 ${adminData.username}！`);

      // 跳转到管理员主页
      this.$router.push("/Admin");
    } catch (adminError) {
      // 如果管理员登录失败，检查是否是普通用户
      if (adminError.response && adminError.response.status === 401) {
        try {
          // 尝试普通用户登录
          const userResponse = await axios.post("/api/users/login", {
            username: this.username,
            password: this.password,
          });

          // 如果成功，处理普通用户登录逻辑
          const userData = userResponse.data; // 假设返回的数据中有 `id`、`username` 等用户信息

          // 将用户信息存储到 localStorage
          localStorage.setItem("user", JSON.stringify(userData));

          // 登录成功提示
          alert(`欢迎回来，${userData.username}！`);

          // 跳转到普通用户主页
          this.$router.push("/home");
        } catch (userError) {
          // 如果用户登录失败，处理错误逻辑
          if (userError.response && userError.response.status === 403) {
            this.errorMessage = "账户已被封禁";
          } else if (userError.response && userError.response.status === 401) {
            alert("用户名或密码错误！");
          } else {
            alert("登录失败，请稍后重试！");
          }
        }
      } else {
        // 如果管理员的其他错误（非 401），处理错误逻辑
        alert("登录失败，请稍后重试！");
      }
    }

  },
      forgotPassword() {
        this.$router.push('/password/recover');
      },
      modifyPassword() {
        this.$router.push('/password/modify');
      },
      register() {
        this.$router.push('/register'); // 跳转到注册页面
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

.login-page {
  background: url('@/Resources/Login.jpg') no-repeat center center; /* 背景图 */
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
  

  
  .artistic-text {
  font-size: 60px;
  font-weight: bold;
  color: #12c2f3;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
  margin-bottom: 10px; /* 减小行间距 */
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
  
  .options {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px; /* 减小间距 */
  }
  
  .options .clickable {
    font-size: 14px;
    color: #007bff;
    cursor: pointer;
    text-decoration: underline;
  }
  
  .options .clickable:hover {
    color: #0056b3;
  }
  
  .login-button,
  .register-button {
    width: 100%;
    padding: 12px;
    margin-top: 10px; /* 增加按钮之间的间距 */
    background: #12f3e0;
    border: none;
    border-radius: 6px;
    color: white;
    font-size: 18px;
    font-weight: bold;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .login-button:hover,
  .register-button:hover {
    background: #3922e6;
  }
  
  .register-button {
    background: #3498db; /* 注册按钮的颜色 */
  }
  

  </style>
  