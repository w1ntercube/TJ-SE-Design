<template>
    <div class="Admin-user-panel">
      <!-- 左侧导航栏 -->
      <div class="Admin-left-panel">
        <div class="user-info">
          <img :src="user.avatarUrl" alt="用户头像" class="avatar" />
          <h3>{{ user.username }}</h3>
        </div>
        <div class="Admin-nav-tabs">
          <button @click="setActiveTab('Admin')" :class="{ active: activeTab === 'Admin' }">你好管理员</button>
          <button @click="setActiveTab('User')" :class="{ active: activeTab === 'User' }">用户管理</button>
          <button @click="setActiveTab('Product')" :class="{ active: activeTab === 'Product' }">商品管理</button>
          <button @click="setActiveTab('Review')" :class="{ active: activeTab === 'Review' }">评论管理</button>
        </div>
      </div>


    <div class="Admin-right-panel">

      <!-- 管理员欢迎内容 -->
      <div v-if="activeTab === 'Admin'" class="Admin-container">
        <h2 class="Admin-title">太好了！是管理员</h2>
        <h2 class="Admin-title">{{user.username}}</h2>
      </div>

      <div v-if="activeTab === 'User'">
          <h2 class ="Admin-user-title">用户列表</h2>
          <div v class="Admin-user-list">
          <div v-for="users in Users" :key="users.id" class="Admin-user-item">
            <img :src="getProductImageUrl(users.avatarUrl)" alt="用户头像" class="Admin-user-avatar" />
            <div class="Admin-user-info">
              <h3>{{ users.username }}</h3>
              <p>信誉积分{{ users.reputationScore }}</p>
              <div class ="Admin-user-button">
                <button 
                @click="toggleBan(users)" 
                :class="[users.isBanned ? 'unban-button' : 'ban-button']">
                {{ users.isBanned ? "解禁" : "封禁" }}
                </button>
                <button @click="editUser(users.id)">积分</button>
              </div>
            </div>
          </div>
          </div>
      </div>


    </div>


    </div>
    

</template>

<script>
import axios from "axios";

export default {
  name: "AdminProfile",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")) || {
        username: "未登录用户",
        avatarUrl: "https://via.placeholder.com/50", // 默认头像
      },

      activeTab: "Admin", // 默认选中"个人中心"选项卡

      Users: [], //用户列表
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      if (tab === "User") {
        this.fetchUsers(); // 获取用户列表
      } 
    },


    // 拼接路径
    getProductImageUrl(imagePath) {
      const fullUrl = `http://localhost:8080${imagePath}`; // 拼接完整的路径
      console.log(fullUrl); // 打印出来看看路径是否正确
      return fullUrl;
    },

    // 调用接口获取用户列表
  async fetchUsers() {
      try {
        const response = await axios.get("/api/users/allusers");
        if (response.status === 200) {
          this.Users = response.data; // 更新用户列表
        } else {
          alert("获取用户列表失败");
        }
      } catch (error) {
        console.error("获取用户时出错", error);
        alert("获取用户列表时出错，请稍后再试");
      }
    },



    // 点击商品时执行的函数
    showProductId(productId) {
      console.log("点击的商品ID:", productId);  // 输出商品ID
    },

    // 调用接口获取用户信息
    async fetchUserInfo() {
      try {
        const response = await axios.get(`/api/users/username/${this.user.username}`);
        if (response.status === 200) {
          // 更新头像和用户名
          this.user = response.data;
          console.log(this.user.avatarUrl);
          this.user.avatarUrl='http://localhost:8080'+this.user.avatarUrl;
        } else { 
          console.error("获取用户信息失败");
        }
      } catch (error) {
        console.error("获取用户信息时出错", error);
        alert("获取用户信息失败，请稍后再试");
      }
    },

    //封禁||解禁用户
    async toggleBan(user)
    {
        const action = user.isBanned ? "unban" : "ban"; // 根据状态确定操作
        axios.post(`/api/users/${user.id}/${action}`)
        .then(response => {
        user.isBanned = response.data.isBanned; // 更新本地状态
        alert(user.isBanned ? "用户已被封禁" : "用户已被解禁");
      })
      .catch(error => {
        console.error("操作失败", error);
        alert("操作失败，请稍后重试");
      });
    },
   async editUser(userId)
    {
        const inputScore = prompt("请输入积分改变量 (-100 到 100)：");

      // 检查输入的分数是否有效
      const score = parseInt(inputScore, 10);
      if (isNaN(score) || score < -100 || score > 100) {
        alert("请输入有效的分数 (-100 到 100)");
        return;
    }

    try {
      // 调用后端接口更新积分
      const response = await axios.post(`/api/users/${userId}/${score}/reputation`);

      // 更新成功的提示
      alert(`用户ID ${userId} 的积分更新成功，新积分为：${response.data.reputationScore}`);
    } catch (error) {
      // 处理错误
      console.error("更新积分时出错：", error);
      alert("积分更新失败，请稍后重试");
    }
  },
  },

  //初始化方法
  created()
  {
    // 在页面加载时获取用户信息
    this.fetchUserInfo();
  }
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
  
  .Admin-user-panel {
    position: fixed;
    top: 0;
    left: 0;
    display: flex;
    flex-direction: row;
    height: 100%;
    min-height: 100vh;
    overflow: hidden;
    width: 100%;
  }
  
  /* 左侧面板 */
  .Admin-left-panel {
    width: 250px;
    flex-shrink: 0;
    background-color: #9c08ffd0;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding: 20px 0;
    box-sizing: border-box;
    height: 100%;
  }
  
  /* 用户信息部分 */
  .Admin-left-panel .user-info {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .Admin-left-panel .user-info .avatar {
    width: 200px;
    height:200px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 10px;
  }
  
  .Admin-left-panel .user-info h3 {
    font-size: 30px;
    color: #fbe1fc;
  }
  
  /* 导航按钮 */
  .Admin-nav-tabs {
    display: flex;
    flex-direction: column;
  }
  
  .Admin-nav-tabs button {
    width: 100%;
    padding: 12px;
    background-color: #d72af5;
    border: 1px solid #9434fa;
    text-align: left;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    color: rgb(216, 248, 250);
  }
  
  .Admin-nav-tabs button:hover {
    background-color: #6627db;
  }
  
  .Admin-nav-tabs button.active {
    background-color: #5b0ee9;
  }
  
  /* 右侧内容面板 */
  .Admin-right-panel {
    background: url('@/Resources/Admin.jpg') ; /* 背景图 */
    background-size: cover;
    background-position: center;
    flex-grow: 1;
    padding: 20px;
    background-color: #fff;
    overflow-y: auto;
    height: 100%;
    box-sizing: border-box;
  }
  
  /* 右侧面板的标题和内容 */
  .Admin-right-panel h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .Admin-right-panel p {
    font-size: 16px;
    color: #666;
  }


  /*  你好界面  */
  .Admin-container
 {
  text-align: center;
  position:relative;
  top:10%;
}

.Admin-title {
  font-size: 70px !important;
  color: #8800ff;
  margin-bottom: 20px;
}
  /*  欢迎界面 */

  /*用户列表界面 */

  .Admin-user-title
  {
    font-size: 30px !important;
    color:#7d03ff;
  }
  .Admin-user-list {
    display: flex;
    flex-wrap: wrap; 
    gap: 20px;
    width: 100%;
  }
  
  .Admin-user-item {
    display: flex;
    background-color: #8252f16f;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    flex: 1 1 calc(25% - 20px); 
    max-width: calc(25% - 20px); 
    box-sizing: border-box;
  }
  
  .Admin-user-item .Admin-user-avatar {
    width: 120px;
    height: 120px;
    object-fit: contain;
    margin-right: 20px;
  }
  
  .Admin-user-item .Admin-user-info {
    flex-grow: 1;
  }
  
  .Admin-user-item .Admin-user-info h3 {
    font-size: 20px;
    margin: 0;
    color: #ffffff;
  }
  
  .Admin-user-item .Admin-user-info p {
    font-size: 15px;
    color: #ffd500;
  }

 .Admin-user-button >>> .ban-button {
  background-color: #5b0ee9; 
  color: white;
}
.Admin-user-button >>> .unban-button {
  background-color: #5cdfe6;
  color: white;
}

.Admin-user-button >>>.ban-button:hover {
  background-color: #7c42f5;
}

/* 解禁按钮样式 */
.Admin-user-button >>>.unban-button:hover {
  background-color: #38beea; 
}



  .Admin-user-button 
  {
    display: flex; /* 横向排列按钮 */
    gap: 15px;
    margin-top: 30px; /* 按钮容器与用户信息之间的距离 */ 
    margin-left: 15px;
  }

.Admin-user-button button {
  padding: 8px 15px;
  font-size: 14px;
  cursor: pointer;
  border: none;
  border-radius: 5px;
  background-color: #5b0ee9; /* 按钮背景颜色 */
  color: white; /* 按钮文字颜色 */
  transition: background-color 0.3s ease;
}

.Admin-user-button button:hover {
  background-color: #7c42f5; /* 悬停时背景颜色 */
}
  /*用户列表界面 */
</style>