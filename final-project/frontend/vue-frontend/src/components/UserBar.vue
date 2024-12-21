<template>
  <div class="user-bar">
    <!-- 左侧的用户信息 -->
    <div class="user-info" @click="navigateToProfile">
      <div class="avatar-container">
        <img :src="user.avatarUrl" alt="用户头像" class="avatar" />
      </div>
      <span class="username">{{ user.username }}</span>
    </div>

    <!-- 中间的欢迎语句 -->
    <div class="welcome-message" :class="{ 'fade-in': isMessageVisible }">
      欢迎回家！
    </div>
  </div>
</template>

<script>


export default {
  name: "UserBar",
  props: {
    user: {
      type: Object,
      required: true,
      default: () => ({
        username: "未登录用户",
        avatarUrl: "https://via.placeholder.com/50", // 默认头像
      }),
    },
  },
  data() {
    return {
      baseURL: "http://localhost:8080", // 服务器源地址
      isMessageVisible: false, // 控制动画
      localUser: { ...this.user }, // 创建一个 localUser 变量来存储用户信息副本
    };
  },
  mounted() {
    // 延迟显示欢迎语句
    setTimeout(() => {
      this.isMessageVisible = true;
    }, 50); // 延迟50ms

    // 调用接口获取用户信息（包括头像URL）

  },
  methods: {

    // 跳转到个人中心
    navigateToProfile() {
      this.$router.push("/profile"); // 跳转到个人中心页面
    },
  },
};
</script>
  
  <style scoped>
  .user-bar {
    display: flex;
    align-items: center;
    position: relative; /* 为 welcome-message 提供参考定位 */
    padding: 10px 20px;
    background-color: #4d1be6;
    box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
    width: 100%; /* 确保横条占满宽度 */
  }
  
  .user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: background-color 0.2s ease;
  }
  
  .user-info:hover {
    background-color: #1fe3f1;
    border-radius: 5px;
  }
  
  .avatar-container {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 10px;
    box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
  }
  
  .avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .username {
    font-size: 16px;
    color: #ffffff;
    font-weight: bold;
  }
  
  /* 中间语句样式 */
  .welcome-message {
    position: absolute; /* 绝对定位 */
    left: 50%; /* 左边距离父容器 50% */
    transform: translateX(-50%); /* 偏移自身宽度的一半，水平居中 */
    font-size: 40px;
    font-weight: bold;
    color: #ffffff;
    text-align: center;
    opacity: 0; /* 初始不可见 */
    animation: fadeIn 0.5s ease-in-out forwards, colorChange 3s infinite; /* 淡入和颜色变化 */
  }
  
  /* 定义淡入动画 */
  @keyframes fadeIn {
    0% {
      opacity: 0;
    }
    100% {
      opacity: 1;
    }
  }
  
  /* 定义颜色渐变动画 */
  @keyframes colorChange {
    0% {
      color: #33fff5; 
    }
    10% {
      color: #33adff; 
    }
    20% {
      color: #3357ff; 
    }
    30% {
      color: #512bf9; 
    }
    40% {
      color: #33a3ff; 
    }
    50% {
      color: #3352ff; 
    }
    60% {
      color: #33c2ff; 
    }
    70% {
      color: #33c2ff; 
    }
    80% {
      color: #33c5ff; 
    }
    90% {
      color: #33e4ff; 
    }
    100% {
      color: #33ffc2; 
    }
  }
  </style>
  
  
  

  