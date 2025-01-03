<template>
    <div class="banned-user-panel">
      <!-- 左侧导航栏 -->
      <div class="banned-left-panel">
        <div class="user-info">
          <img :src="user.avatarUrl" alt="用户头像" class="avatar" />
          <h3>{{ user.username }}</h3>
        </div>
        <div class="banned-nav-tabs">
          <button @click="setActiveTab('Ban')" :class="{ active: activeTab === 'Ban' }">小黑屋</button>
          <button @click="setActiveTab('buys')" :class="{ active: activeTab === 'buys' }">我的购入</button>
          <button @click="setActiveTab('sells')" :class="{ active: activeTab === 'sells' }">我的出售</button>
        </div>
      </div>


    <div class="banned-right-panel">


      <!-- 小黑屋内容 -->
      <div v-if="activeTab === 'Ban'" class="ban-container">
        <h2 class="ban-title">你被关小黑屋了！</h2>
        <div class="ban-image-container">
          <img :src="banImage" alt="小黑屋图片" class="ban-image" />
        </div>
        <h2>请联系管理员：15159581396</h2>
      </div>

      <div v-if="activeTab === 'orders'">
        <h2>我的订单</h2>
        <p>这里是我的订单的内容。</p>
      </div>


    </div>
    
    </div>


</template>

<script>
import axios from "axios";

export default {
  name: "BannedProfile",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")) || {
        username: "未登录用户",
        avatarUrl: "https://via.placeholder.com/50", // 默认头像
      },

      activeTab: "orders", // 默认选中"个人中心"选项卡
      banImage:require("@/Resources/Ban-message.jpg"), //小黑屋图片
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      if (tab === "products") {
        this.fetchProducts(); // 点击"我的商品"时获取商品数据
      } 
    },


    // 拼接路径
    getProductImageUrl(imagePath) {
      const fullUrl = `http://localhost:8080${imagePath}`; // 拼接完整的路径
      console.log(fullUrl); // 打印出来看看路径是否正确
      return fullUrl;
    },

    // 调用接口获取商品列表
    async fetchProducts() {
      try {
        const response = await axios.post("/api/products/by-seller", {
          sellerName: this.user.username,
        });
        if (response.status === 200) {
          this.products = response.data; // 更新商品列表
        } else {
          alert("获取商品列表失败");
        }
      } catch (error) {
        console.error("获取商品时出错", error);
        alert("获取商品时出错，请稍后再试");
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
  
  .banned-user-panel {
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
  .banned-left-panel {
    width: 250px;
    background-color: #3f88d6d0;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding: 20px 0;
    box-sizing: border-box;
    height: 100%;
  }
  
  /* 用户信息部分 */
  .banned-left-panel .user-info {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .banned-left-panel .user-info .avatar {
    width: 200px;
    height:200px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 10px;
  }
  
  .banned-left-panel .user-info h3 {
    font-size: 18px;
    color: #333;
  }
  
  /* 导航按钮 */
  .banned-nav-tabs {
    display: flex;
    flex-direction: column;
  }
  
  .banned-nav-tabs button {
    width: 100%;
    padding: 12px;
    background-color: #6a2af5;
    border: 1px solid #3452fa;
    text-align: left;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    color: white;
  }
  
  .banned-nav-tabs button:hover {
    background-color: #04fffb;
  }
  
  .banned-nav-tabs button.active {
    background-color: #0ee9c5;
  }
  
  /* 右侧内容面板 */
  .banned-right-panel {
    background: url('@/Resources/banned.jpg') ; /* 背景图 */
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
  .banned-right-panel h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .banned-right-panel p {
    font-size: 16px;
    color: #666;
  }


  /*  小黑屋的样式 */
  .ban-container
 {
  text-align: center;
  position:relative;
  top:10%;
}

.ban-title {
  font-size: 70px !important;
  color: #ebfefe;
  margin-bottom: 20px;
}

.ban-image-container {
  display: flex;
  justify-content: center;
}

.ban-image {
  width: 400px;
  height: auto;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
  /*  小黑屋的样式 */
</style>