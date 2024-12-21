<template>
  <div class="user-panel">
    <!-- 左侧导航栏 -->
    <div class="left-panel">
      <div class="user-info">
        <img :src="user.avatarUrl" alt="用户头像" class="avatar" />
        <h3>{{ user.username }}</h3>
      </div>
      <div class="nav-tabs">
        <button @click="setActiveTab('profile')" :class="{ active: activeTab === 'profile' }">个人中心</button>
        <button @click="setActiveTab('favorites')" :class="{ active: activeTab === 'favorites' }">我的收藏</button>
        <button @click="setActiveTab('products')" :class="{ active: activeTab === 'products' }">我的商品</button>
        <button @click="setActiveTab('orders')" :class="{ active: activeTab === 'orders' }">我的订单</button>
        <button @click="setActiveTab('addProduct')" :class="{ active: activeTab === 'addProduct' }">上架商品</button>
        <button @click="goBack">返回首页</button>
      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="right-panel">
      <div v-if="activeTab === 'profile'">
      <div class="avatar-upload">
        <h2>上传崭新的头像吧！</h2>
        <input type="file" id="avatar-image" ref="avatarImage" @change="handleAvatarUpload" />
        <button @click="submitAvatar">提交</button>
      </div>
    </div>

      <div v-if="activeTab === 'favorites'">
        <h2>我的收藏</h2>
        <p>这里是我的收藏的内容。</p>
      </div>

      <div v-if="activeTab === 'products'">
        <div class="product-list">
          <div v-for="product in products" :key="product.id" class="product-item" @click="goToProductPage(product.id)">
            <img :src="getProductImageUrl(product.imagePath)" alt="商品图片" class="product-image" />
            <div class="product-info">
              <h3>{{ product.name }}</h3>
              <p>价格：￥{{ product.price }}</p>
              <p>库存：{{ product.stock }}</p>
            </div>
            <button @click.stop="removeProduct(product.id)" class="remove-button">下架</button>
          </div>
        </div>
      </div>

      <div v-if="activeTab === 'orders'">
        <h2>我的订单</h2>
        <p>这里是我的订单的内容。</p>
      </div>

      <div v-if="activeTab === 'addProduct'">
        <div class ="addform">
        <h2>上架商品</h2>
        <form @submit.prevent="addProduct">
          <div class="form-group">
            <label for="image">上传商品图片：</label>
            <input type="file" id="image" ref="image" @change="handleImageUpload" />
          </div>
          <div class="form-group">
            <label for="name">商品名称：</label>
            <input type="text" id="name" v-model="newProduct.name" required />
          </div>
          <div class="form-group">
            <label for="price">价格：</label>
            <input type="number" id="price" v-model="newProduct.price" required />
          </div>
          <div class="form-group">
            <label for="stock">库存：</label>
            <input type="number" id="stock" v-model="newProduct.stock" required />
          </div>
          <div class="form-group">
            <label for="description">商品描述：</label>
            <textarea id="description" v-model="newProduct.description" required></textarea>
          </div>
          <button type="submit" class="add-button">上架商品</button>
        </form>
      </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "UserProfile",
  data() {
    return {
      user: JSON.parse(localStorage.getItem("user")) || {
        username: "未登录用户",
        avatarUrl: "https://via.placeholder.com/50", // 默认头像
      },

      activeTab: "profile", // 默认选中"个人中心"选项卡
      products: [], // 初始化为空数组，动态加载商品数据
      newProduct: {
        name: "",
        price: 0,
        stock: 0,
        description: "",
        imageUrl: "",
      },
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
    // 跳转到商品详情页面
    goToProductPage(productId) {
      // 使用 Vue Router 进行跳转
      this.$router.push({
        name: "ProductDetail", // 路由名（在 router/index.js 中配置）
        params: { id: productId }, 
      });
    },
    // 下架商品
    async removeProduct(productId) {
      try {
        console.log(productId);
        const response = await axios.delete(`/api/products/delete/${productId}`);
        if (response.status === 200) {
          this.products = this.products.filter(product => product.id !== productId);  // 从列表中移除已删除商品
          alert("商品已下架");
        } else {
          alert("删除商品失败");
        }
      } catch (error) {
        console.error("删除商品时出错", error);
        alert("删除商品失败");
      }
    },



     // 用于存储选择的头像文件
    selectedAvatar: null,
     // 上传头像
     async submitAvatar() {
      if (!this.selectedAvatar) {
        alert("请先选择一个头像图片！");
        return;
      }

      const formData = new FormData();
      formData.append("username", this.user.username);
      formData.append("image", this.selectedAvatar);

      try {
        const response = await axios.post("/api/users/upload-avatar", formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        });

        if (response.status === 200) {
          alert("头像上传成功");
        } else {
          alert("头像上传失败");
        }
      } catch (error) {
        console.error("上传头像时出错", error);
        alert("头像上传失败，请稍后再试");
      }
    },

    // 处理头像选择
    handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (file) {
       this.selectedAvatar = file; // 保存文件到 selectedAvatar
     }
    },

    // 处理图片上传
    handleImageUpload(event) {
      const file = event.target.files[0];
      if (file) {
        this.newProduct.imageUrl = URL.createObjectURL(file); // 使用本地临时URL显示图片
      }
    },


    // 上架商品
    async addProduct() {
      if (this.newProduct.name && this.newProduct.price && this.newProduct.stock && this.newProduct.description && this.newProduct.imageUrl) {
        const formData = new FormData();

        // 添加商品信息到 FormData
        formData.append("name", this.newProduct.name);
        formData.append("price", this.newProduct.price);
        formData.append("stock", this.newProduct.stock);
        formData.append("description", this.newProduct.description);
        formData.append("seller_name", this.user.username);  // 传递卖家名称（假设已存储在 user 中）

        // 添加图片到 FormData
        const file = this.$refs.image.files[0]; // 使用 ref 获取文件
        if (file) {
          formData.append("image", file);
        } else {
          alert("请上传商品图片");
          return;
        }

        try {
          // 发送请求到后端
          const response = await axios.post('/api/products/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });

          if (response.status === 200) {
            alert(response.data);  // 商品上传成功提示
            this.resetNewProduct(); // 重置表单
            this.fetchProducts();  // 上传成功后刷新商品列表
          } else {
            alert("上传失败，请稍后再试");
          }
        } catch (error) {
          console.error("上传商品时出错", error);
          alert("发生错误，请重试");
        }
      } else {
        alert("请填写所有商品信息");
      }
    },

    // 重置上架商品表单
    resetNewProduct() {
      this.newProduct = {
        name: "",
        price: 0,
        stock: 0,
        description: "",
        imageUrl: "",
      };
      // 清空图片输入
      this.$refs.image.value = ""; 
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
    goBack() {
        // 返回上一页
        // this.$router.go(-1);
        // 返回首页
        this.$router.push({ name: 'ToHome' });
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
  
  .user-panel {
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
  .left-panel {
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
  .left-panel .user-info {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .left-panel .user-info .avatar {
    width: 200px;
    height:200px;
    border-radius: 50%;
    object-fit: cover;
    margin-bottom: 10px;
  }
  
  .left-panel .user-info h3 {
    font-size: 18px;
    color: #333;
  }
  
  /* 导航按钮 */
  .nav-tabs {
    display: flex;
    flex-direction: column;
  }
  
  .nav-tabs button {
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
  
  .nav-tabs button:hover {
    background-color: #04fffb;
  }
  
  .nav-tabs button.active {
    background-color: #0ee9c5;
  }
  
  /* 右侧内容面板 */
  .right-panel {
    background: url('@/Resources/profile.jpg') ; /* 背景图 */
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
  .right-panel h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .right-panel p {
    font-size: 16px;
    color: #666;
  }


/*  上传头像部分   */
.avatar-upload 
{
  background-color: #43e0e086;
  position: relative;
  left: 30%;
  width: 600px;
  height: 600px;
  text-align: center;
  display: flex;
  flex-direction: column; 
  justify-content: center;
  align-items: center;
  margin: 20px 0;
  gap: 20px; 
}

input[type="file"]
 {
  margin-bottom: 10px;
}

button {
  background-color: #6a2af5;
  color: white;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  width: 50%;
}

button:hover {
  background-color: #04fffb;
}



  
  /*  ----商品部分----- */
  
  /* 商品列表 */
  .product-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 100%;
  }
  
  /* 商品条目 */
  .product-item {
    display: flex;
    width: 100%;
    background-color: #16efff6f;
    padding: 15px;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    box-sizing: border-box;
  }
  
  .product-item .product-image {
    width: 120px;
    height: 120px;
    object-fit: contain;
    margin-right: 20px;
  }
  
  .product-item .product-info {
    flex-grow: 1;
  }
  
  .product-item .product-info h3 {
    font-size: 20px;
    margin: 0;
    color: #000000;
  }
  
  .product-item .product-info p {
    font-size: 15px;
    color: #080808;
  }
  
  .product-item .remove-button {
    background-color: #ff4d4f;
    color: white;
    border: none;
    padding: 10px;
    font-size: 14px;
    width: 100px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .product-item .remove-button:hover {
    background-color: #22f2f5;
  }
  
  .product-item:hover 
  {
    background-color: #8b4dff;
    transition: background-color 0.3s ease;
    cursor: pointer;
}


/*   上架商品   */
.addform
{
  position: relative;
  left:20%;
  width:900px;
  height: 800px;
  background-color: #4e40e99b;
  font-size: 20px;
  color:#ffffff;
}
  
  .add-button {
    background-color: #35f0fa;
    color: white;
    padding: 12px;
    border: none;
    cursor: pointer;
    font-size: 16px;
    width: 80%;

  }
  
  .add-button:hover {
    background-color: #13ff85;
  }
  
  .form-group {
    position: relative;
    left:10%;
    margin-bottom: 15px;
    width:80%;
  }
  
  input[type="text"],
  input[type="number"],
  textarea {
    width: 95%;
    padding: 8px;
    font-size: 14px;
    border-radius: 4px;
    border: 1px solid #ccc;
    margin-top: 5px;
  }
  
  textarea {
    resize: vertical;
    min-height: 100px;
  }
</style>