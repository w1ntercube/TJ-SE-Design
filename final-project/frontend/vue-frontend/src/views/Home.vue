<template>
  <div class="home-page">
    <!-- 用户信息栏 -->
    <UserBar :user="user" />

    <!-- 搜索框 -->
    <div class="search-bar">
      <input
        type="text"
        placeholder="搜索商品..."
        v-model="searchQuery"
        @keyup.enter="handleSearch"
      />
      <button @click="handleSearch">搜索</button>
    </div>

    <!-- 商品展示区 -->
    <div class="product-container">
      <div
        class="product-card"
        v-for="productData in paginatedProducts"
        :key="productData.id"
        @click="goToProductPage(productData.product.id)"
      >
      <img :src="baseURL + productData.product.imagePath" alt="商品图片" class="product-image" />
        <h3 class="product-name">{{ productData.product.name }}</h3>
        <p class="product-price">出售价格：¥{{ productData.product.price }}</p>
        <p class="product-price">出租价格：¥{{ productData.product.rentalPrice }}</p>
        <!-- 商家信息 -->
        <p class="seller-info">商家：{{ productData.seller?.username || "加载中..." }}</p>
        <p class="seller-reputation">信誉积分：{{ productData.seller?.reputationScore || "加载中..." }}</p>
      </div>
    </div>

    <!-- 分页功能 -->
    <div class="pagination">
      <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 1">
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button
        @click="goToPage(currentPage + 1)"
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import UserBar from "@/components/UserBar.vue";

export default {
  components: {
    UserBar,
  },
  name: "ToHome",
  data() {
    return {
    baseURL:'http://localhost:8080', //由于是本地，所以源目录为这个

      user: JSON.parse(localStorage.getItem("user")) || {
        username: "未登录用户",
        avatarUrl: require('@/Resources/user.jpg'), // 默认头像
      },
      searchQuery: "",
      productWithSellerData: [], // 商品及卖家信息
      currentPage: 1, // 当前页
      itemsPerPage: 5, // 每页商品数（4*2）
    };
  },
  computed: {
    userAvatar() {
      return this.user.avatarUrl && this.user.avatarUrl !== 'null'
        ? this.user.avatarUrl
        : require('@/Resources/user.jpg');
    },
    filteredProducts() {
      if (!this.searchQuery) return this.productWithSellerData;
      return this.productWithSellerData.filter((data) =>
        data.product.name.includes(this.searchQuery)
      );
    },
    totalPages() {
      return Math.ceil(this.filteredProducts.length / this.itemsPerPage); // 计算总页数
    },
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      const end = start + this.itemsPerPage;
      return this.filteredProducts.slice(start, end); // 获取当前页的商品
    },
  },
  methods: {

    // 获取商品数据
    fetchProductsWithSellers() {
      axios
        .get("/api/products/all-with-sellers")
        .then(({ data }) => {
          this.productWithSellerData = data;
        })
        .catch((error) => console.error("获取商品及卖家信息失败:", error));
    },

        // 获取用户信息
        fetchUserInfo(username) {
      axios
        .get(`/api/users/username/${username}`)
        .then((response) => {
          this.user = response.data; // 设置获取到的用户数据
          console.log(this.user.avatarUrl);
          this.user.avatarUrl='http://localhost:8080'+this.user.avatarUrl;
        })
        .catch((error) => {
          console.error("获取用户信息失败:", error);
          this.user = {
            username: "获取用户信息失败",
            avatarUrl: require('@/Resources/user.jpg'),
          };
        });
    },

    //搜索功能
    handleSearch() {
      alert(`搜索关键词：${this.searchQuery}`);
    },
    goToPage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page; // 更新当前页
    },

    // 跳转到商品详情页面
    goToProductPage(productId) {
      // 使用 Vue Router 进行跳转
      this.$router.push({
        name: "ProductDetail", // 路由名（在 router/index.js 中配置）
        params: { id: productId }, 
      });
    },
  },
  created() {
    this.fetchProductsWithSellers();
    const storedUser = JSON.parse(localStorage.getItem("user"));
    if (storedUser?.username) {
      this.fetchUserInfo(storedUser.username);
    }
  },
};
</script>
  
  <style scoped>
  /* 全局样式 */
  html,
  body {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow: hidden;
  }
  
  /* 主页面样式 */
  .home-page {
    background: url('@/Resources/Login.jpg') no-repeat center center;
    background-size: cover;
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    display: flex;
    flex-direction: column;
    font-family: Arial, sans-serif;
    color: white;
    overflow-y: auto;
  }
  
  /* 搜索栏样式 */
  .search-bar {
    margin: 20px auto;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
  }
  
  .search-bar input {
    width: 1000px;
    padding: 10px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }
  
  .search-bar button {
    padding: 10px 20px;
    background-color: #0ec1e9;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
  }
  
  .search-bar button:hover {
    background-color: #6e15ea;
  }
  
  /* 商品容器样式 */
  .product-container {
    display: grid;
    grid-template-columns: repeat(5, 1fr); /* 每行4个商品 */
    gap: 20px;
    margin: 30px auto;
    width: 90%;
    padding: 20px;
    background-color: rgba(155, 20, 245, 0.7); /* 背景色 */
    border-radius: 10px; /* 边框圆角 */
    border: 2px solid #12b3f8; 
    flex-grow: 1;
    overflow-y: auto;
  }
  
  .product-card {
  background: rgb(255, 255, 255);
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  text-align: center;
  padding: 10px;
  cursor: pointer; /* 鼠标悬停时显示手型 */
  transition: transform 0.2s, box-shadow 0.2s; /* 平滑过渡效果 */
}

.product-card:hover {
  transform: scale(1.05); /* 悬停时轻微放大 */
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); /* 悬停时更显眼的阴影 */
}
  
  .product-image {
    width: 250px;
    height: 250px;
    object-fit: contain;
  }
  
  .product-name {
    font-size: 20px;
    margin: 10px 0;
    color: #000000;
  }
  
  .product-price {
    font-size: 20px;
    color: #3a23e4;
  }
  
  /* 分页样式 */
  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
  }
  
  .pagination button {
    padding: 10px 20px;
    background-color: #0ec1e9;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;
    margin: 0 10px;
  }
  
  .pagination button:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
  /* 商家信息样式 */
.seller-info,
.seller-reputation
 {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.seller-info 
{
    font-size: 20px;
    color: #2cef2f; 
}

.seller-reputation
{
  font-size: 20px;
  color: #c0cb24; 
}
  </style>
  
  
  
  
  
  