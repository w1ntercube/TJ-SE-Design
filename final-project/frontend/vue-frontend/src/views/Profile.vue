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
        <button @click="setActiveTab('consumerOrders')" :class="{ active: activeTab === 'consumerOrders' }">消费订单</button>
        <button @click="setActiveTab('storeOrders')" :class="{ active: activeTab === 'storeOrders' }">店铺订单</button>
        <button @click="setActiveTab('addProduct')" :class="{ active: activeTab === 'addProduct' }">上架商品</button>
        <button @click="setActiveTab('goAi')" :class="{ active: activeTab === 'goAi' }">乐器大师</button>
        <button @click="goBack">返回首页</button>


      </div>
    </div>

    <!-- 右侧内容区 -->
    <div class="right-panel">

        <!-- 个人中心选项卡 -->
        <div v-if="activeTab === 'profile'">
          <div class="avatar-upload">
            <h2>上传崭新的头像吧！</h2>
            <input type="file" id="avatar-image" ref="avatarImage" @change="handleAvatarUpload" />
            <button @click="submitAvatar">提交</button>
          </div>
        </div>

        <!-- 我的收藏选项卡 -->
        <div v-if="activeTab === 'favorites'">
          <h2>我的收藏</h2>
          <div v-if="favoriteProducts.length > 0" class="product-list">
            <div
              v-for="product in favoriteProducts"
              :key="product.id"
              class="product-item"
              @click="goToProductPage(product.id)"
            >
              <img :src="getProductImageUrl(product.imagePath)" alt="商品图片" class="product-image" />
              <div class="product-info">
                <h3>{{ product.name }}</h3>
                <p>出售价格：￥{{ product.price }}</p>
                <p>出租价格：￥{{ product.rentalPrice }}</p>
                <p>出售库存：{{ product.stock }}</p>
                <p>出租库存：{{ product.rentalStock }}</p>
              </div>
              <button @click.stop="removeFromFavorites(product.id)" class="remove-button">移除收藏</button>
            </div>
          </div>
          <div v-else>
            <p>您还没有收藏任何商品。</p>
          </div>
        </div>

        <!-- 我的商品选项卡 -->
        <div v-if="activeTab === 'products'">
          <h2>我的商品</h2>
            <div v-if="products.length > 0" class="product-list">
              <div v-for="product in products" :key="product.id" class="product-item" @click="goToProductPage(product.id)">
                <img :src="getProductImageUrl(product.imagePath)" alt="商品图片" class="product-image" />
                <div class="product-info">
                  <h3>{{ product.name }}</h3>
                  <p>出售价格：￥{{ product.price }}</p>
                  <p>出租价格：￥{{ product.rentalPrice }}</p>
                  <p>出售库存：{{ product.stock }}</p>
                  <p>出租库存：{{ product.rentalStock }}</p>
                </div>
                <div class="Stock-input" @click.stop>
                <!-- 添加库存数量输入框 -->
                <input 
                  type="number" 
                  v-model="product.addStocks" 
                  placeholder="调整库存数量" 
                />
                <!-- 减少库存数量输入框 -->
                <input 
                  type="number" 
                  v-model="product.lessStocks" 
                  placeholder="调整库存数量" 
                />
              </div>
              <div class="buttonInput" >
                <button @click.stop="adjustSellStock(product.id, product.addStocks)" class="Stock-button">调整出售库存</button>
                <button @click.stop="adjustRentStock(product.id, product.lessStocks)" class="Stock-button">调整出租库存</button>
              </div>
                
                <button @click.stop="removeProduct(product.id)" class="remove-button">下架</button>
              </div>
            </div>
          <div v-else>
            <p>您还没有上架任何商品。</p>
          </div>
        </div>


        <div>
          <!-- 消费订单选项卡 -->
          <div v-if="activeTab === 'consumerOrders'" class="order-section">
            <h2>消费订单</h2>

            <!-- 筛选框 -->
            <div class="filters">
              <label for="consumerOrderType">订单类型：</label>
              <select id="consumerOrderType" v-model="filters.orderType" class="filter-select">
                <option value="">全部</option>
                <option value="PURCHASE">购买订单</option>
                <option value="RENTAL">出租订单</option>
              </select>

              <label for="consumerOrderStatus">订单状态：</label>
              <select id="consumerOrderStatus" v-model="filters.orderStatus" class="filter-select">
                <option value="">全部</option>
                <option value="PENDING">待支付</option>
                <option value="PAID">已支付</option>
                <option value="SHIPPED">已发货</option>
                <option value="DELIVERED">已收货</option>
                <option value="CANCELLED">已取消</option>
                <option value="RETURNED">已回货</option>
                <option value="MERCHANT_CONFIRMED">商家确认</option>
              </select>

              <button @click="applyFilters" class="filter-button">筛选</button>
            </div>

            <!-- 订单列表 -->
            <div v-if="consumerOrders.length > 0" class="order-list">
              <div v-for="order in consumerOrders" :key="order.id" class="order-item">
                <img :src="getProductImageUrl(order.imagePath)" alt="商品图片" class="order-image" />
                <h3>订单编号：{{ order.id }}</h3>
                <p>商品编号：{{ order.productId }}</p>

                <!-- 动态显示字段 -->
                <template v-if="order.orderType === 'PURCHASE'">
                  <p>购买数量：{{ order.quantity }}</p>
                  <p>总价：￥{{ order.totalPrice }}</p>
                </template>
                <template v-else-if="order.orderType === 'RENTAL'">
                  <p>租借数量：{{ order.quantity }}</p>
                  <p>总价（含押金￥{{order.deposit}}）：￥{{ order.totalPrice + order.deposit }}</p>
                  <p>租借天数：{{ order.rentalDurationDays }} 天</p>
                  <p>
                    租借时期：
                    <span v-if="order.rentalStart && order.rentalEnd">
                      {{ order.rentalStart }} 至 {{ order.rentalEnd }}
                    </span>
                    <span v-else>
                      买家还未收货！
                    </span>
                  </p>
                </template>

                <p>订单状态：{{ getOrderStatusText(order.orderStatus) }}</p>
                <p>订单地址：{{ order.address }}</p>
                <p>创建时间：{{ order.createdAt }}</p>

                <button
                  v-if="order.orderStatus === 'SHIPPED'"
                  @click="confirmDelivery(order)"
                  class="order-action-button"
                >
                  确认收货
                </button>

                <button
                  v-if="order.orderStatus === 'DELIVERED'&& order.orderType === 'RENTAL'"
                  @click="returnOrder(order)"
                  class="action-button"
                >
                  我要退还
                </button>


              </div>
            </div>
            <div v-else>
              <p>您还没有任何消费订单。</p>
            </div>
          </div>

          <!-- 店铺订单选项卡 -->
          <div v-if="activeTab === 'storeOrders'" class="order-section">
            <h2>店铺订单</h2>

            <!-- 筛选框 -->
            <div class="filters">
              <label for="storeOrderType">订单类型：</label>
              <select id="storeOrderType" v-model="filter.orderType" class="filter-select">
                <option value="">全部</option>
                <option value="PURCHASE">购买订单</option>
                <option value="RENTAL">租赁订单</option>
              </select>

              <label for="storeOrderStatus">订单状态：</label>
              <select id="storeOrderStatus" v-model="filter.orderStatus" class="filter-select">
                <option value="">全部</option>
                <option value="PENDING">待支付</option>
                <option value="PAID">已支付</option>
                <option value="SHIPPED">已发货</option>
                <option value="DELIVERED">已收货</option>
                <option value="CANCELLED">已取消</option>
                <option value="RETURNED">已回货</option>
                <option value="MERCHANT_CONFIRMED">商家确认</option>
              </select>

              <button @click="fetchSellerOrders" class="filter-button">筛选</button>
            </div>

            <!-- 店铺订单列表 -->
            <div v-if="sellerOrders.length > 0" class="order-list">
              <div v-for="order in sellerOrders" :key="order.id" class="order-item">

                <img :src="getProductImageUrl(order.imagePath)" alt="商品图片" class="order-image" />
                <h3>订单编号：{{ order.id }}</h3>
                <p>商品编号：{{ order.productId || 'N/A' }}</p>

                <!-- 动态显示字段 -->
                <template v-if="order.orderType === 'PURCHASE'">
                  <p>购买数量：{{ order.quantity }}</p>
                  <p>总价：￥{{ order.totalPrice }}</p>
                </template>
                <template v-else-if="order.orderType === 'RENTAL'">
                  <p>租借数量：{{ order.quantity }}</p>
                  <p>总价（含押金￥{{order.deposit}}）：￥{{ order.totalPrice + order.deposit }}</p>
                  <p>租借天数：{{ order.rentalDurationDays }} 天</p>
                  <p>
                    租借时期：
                    <span v-if="order.rentalStart && order.rentalEnd">
                      {{ order.rentalStart }} 至 {{ order.rentalEnd }}
                    </span>
                    <span v-else>
                      买家还未收货！
                    </span>
                  </p>
                </template>

                <p>订单状态：{{getOrderStatusText(order.orderStatus)}}</p>
                <p>订单地址：{{ order.address }}</p>
                <p>创建时间：{{ order.createdAt }}</p>

                <button
                  v-if="order.orderStatus === 'PAID'"
                  @click="shipOrder(order)"
                  class="order-action-button"
                >
                  发货
                </button>

                <button
                  v-if="order.orderStatus === 'RETURNED'&& order.orderType === 'RENTAL'"
                  @click="confirmReturn(order)"
                  class="action-button"
                >
                  商家确认
                </button>

              </div>
            </div>
            <div v-else>
              <p>暂无店铺订单。</p>
            </div>
          </div>
        </div>




        <!-- 上架商品选项卡 -->
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

            <div class="form-container">
              <div class="form-group-price">
                <label for="price">出售价格：</label>
                <input type="number" id="price" v-model="newProduct.price" required />
              </div>
              <div class="form-group-price">
                <label for="price">出租价格：</label>
                <input type="number" id="price" step="0.01" v-model="newProduct.rental_price" required />
              </div>
            </div>

            <div class="form-container">
              <div class="form-group-price">
                <label for="stock">出售库存：</label>
                <input type="number" id="stock" v-model="newProduct.stock" required />
              </div>
              <div class="form-group-price">
                <label for="stock">出租库存：</label>
                <input type="number" id="stock" v-model="newProduct.rental_stock" required />
              </div>
            </div>
            <div class="form-group">
                <label for="description">商品描述：</label>
                <textarea id="description" v-model="newProduct.description" required></textarea>
            </div>

            <button type="submit" class="add-button">上架商品</button>
          </form>
        </div>


      </div>


             <!--               询问AI             -->       
             <div v-if="activeTab === 'goAi'" class="ai-chat-container">
            <h2>乐器大师强尼</h2>
           <div class="ai-chat-box">
           <div
               class="ai-chat-message"
               v-for="(message, index) in messages"
      :key="index"
      :class="{ 'ai-user-message': message.sender === 'user', 'ai-ai-message': message.sender === 'ai' }"
    >
      <div class="ai-message-content">
        <img
          v-if="message.sender === 'ai'"
          src="@/Resources/user.jpg"
          alt="AI头像"
          class="ai-avatar"
        />
        <div class="ai-text">{{ message.content }}</div>
        <img
          v-if="message.sender === 'user'"
          :src="user.avatarUrl"
          alt="用户头像"
          class="ai-avatar"
        />
      </div>
    </div>
  </div>
  <div class="ai-input-area">
    <textarea v-model="userInput" placeholder="请输入您的问题..."></textarea>
    <button @click="sendMessage">发送</button>
  </div>
</div>
       <!--               询问AI             -->  
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
      favoriteProducts: [], // 存储用户收藏的商品列表
      newProduct: {
        name: "",
        price: 0,
        rental_price: 0,
        stock: 0,
        rental_stock: 0,
        description: "",
      },
      consumerOrders: [], // 存储消费订单
      sellerOrders: [], // 店铺订单列表
      orderStatusOptions: [
        "PENDING",
        "PAID",
        "SHIPPED",
        "DELIVERED",
        "CANCELLED",
        "RETURNED",
        "MERCHANT_CONFIRMED",
      ],
      filters: {
        orderType: "", // 消费订单筛选类型
        orderStatus: "", // 消费订单筛选状态
      },

      filter: {
        orderType: "", // 店铺订单筛选类型
        orderStatus: "", // 店铺订单筛选状态
      },

      messages: [],
      userInput:"",
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      if (tab === "products") {
        this.fetchProducts(); // 点击"我的商品"时获取商品数据
      } else if (tab === "favorites") {
        this.fetchFavorites(); // 点击"我的收藏"时获取收藏商品数据
      } else if (tab === "consumerOrders") {
          this.fetchConsumerOrders(); // 点击消费订单时加载数据
      } else if (tab === "storeOrders") {
          this.fetchSellerOrders(); // 点击店铺订单时加载数据
      }
      },
    // 获取收藏商品数据
    async fetchFavorites() {
      try {
        const response = await axios.get(`/api/carts/find/${this.user.id}`);
        if (response.status === 200) {
          this.favoriteProducts = response.data; 
        } else {
          alert("获取收藏列表失败");
        }
      } catch (error) {
        console.error("获取收藏列表时出错", error);
        alert("获取收藏列表时出错，请稍后再试");
      }
    },

    // 移除收藏
    async removeFromFavorites(productId) {
      try {
        const response = await axios.put(`/api/carts/toggle/${this.user.id}/${productId}`);
        if (response.status === 200) {
          this.favoriteProducts = this.favoriteProducts.filter(product => product.id !== productId);
          alert("已移除收藏");
        } else {
          alert("移除收藏失败");
        }
      } catch (error) {
        console.error("移除收藏时出错", error);
        alert("移除收藏失败，请稍后再试");
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
    // 增加库存
    async adjustSellStock(productId, quantity) {
      try {
        console.log(productId, quantity);
        const response = await axios.post(`/api/products/adjustSellStock`, {
          productId: productId,
          quantity: quantity
        });
        if (response.status === 200) {
          const updatedProduct = response.data;
          this.products = this.products.map(product =>
            product.id === productId ? updatedProduct : product
          ); // 更新产品列表中的库存信息
          alert("出售的库存已调整");
          this.fetchProducts();
          this.setActiveTab('products');
        } else {
          alert("调整库存失败");
        }
      } catch (error) {
        console.error("调整库存时出错", error);
        alert("调整库存失败");
      }
    },
    // 减少库存
    async adjustRentStock(productId, quantity) {
      try {
        console.log(productId, quantity);
        const response = await axios.post(`/api/products/adjustRentStock`, {
          productId: productId,
          quantity: quantity
        });
        if (response.status === 200) {
          const updatedProduct = response.data;
          this.products = this.products.map(product =>
            product.id === productId ? updatedProduct : product
          ); // 更新产品列表中的库存信息
          alert("出租库存已调整");
          this.fetchProducts();
          this.setActiveTab('products');
        } else {
          alert("调整库存失败");
        }
      } catch (error) {
        console.error("调整库存时出错", error);
        alert("调整库存失败");
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
      if (this.newProduct.name && this.newProduct.price && this.newProduct.rental_price 
          && (this.newProduct.stock || this.newProduct.rental_stock) 
          && this.newProduct.description) {
        const formData = new FormData();
        formData.append('name', this.newProduct.name);
        formData.append('price', this.newProduct.price); 
        formData.append('rental_price', this.newProduct.rental_price);
        formData.append('stock', this.newProduct.stock);
        formData.append('rental_stock', this.newProduct.rental_stock);
        formData.append('description', this.newProduct.description);
        formData.append('seller_id', this.user.id);
        formData.append('is_active', true); 
        

        const file = this.$refs.image.files[0];
        if (file) {
          formData.append("file", file);
        } else {
          alert("请上传商品图片");
          return;
        }

        try {
          for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
          }
          const response = await axios.post('/api/products/addProduct', formData);


          if (response.status === 200) {
            alert("商品上传成功");
            this.resetNewProduct(); 
            this.fetchProducts();  
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
        rental_price: 0,
        rental_stock: 0,
        description: "",
        imageUrl: "",
      };
      // 清空图片输入
      this.$refs.image.value = ""; 
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

    async fetchConsumerOrders() {
        try {
          const response = await axios.get("/api/orders/filter", {
            params: {
              userId: this.user.id,
            },
          });
          if (response.status === 200) {
            console.log("消费订单数据：", response.data);
            this.consumerOrders = response.data;
            // 提取订单ID，按降序排列并取前10个
            const recentOrderIds = this.consumerOrders
              .map(order => order.id)
              .sort((a, b) => b - a) // 按订单号降序排序
              .slice(0, 10); // 取前10个订单号

            // 检查每个订单状态并更新
            await Promise.all(recentOrderIds.map(orderId => this.updateOrderStatus(orderId)));

          } else {
            alert("获取消费订单失败！");
          }
        } catch (error) {
          console.error("获取消费订单时出错：", error);
          alert("获取消费订单时出错，请稍后再试！");
        }
    },

    async applyFilters() {
      try {
        const params = {
          userId: this.user.id,
        };

        // 添加筛选条件（仅当不为空时）
        if (this.filters.orderType) params.orderType = this.filters.orderType;
        if (this.filters.orderStatus) params.orderStatus = this.filters.orderStatus;

        const response = await axios.get("/api/orders/filter", { params });

        if (response.status === 200) {
          this.consumerOrders = response.data;
        } else {
          alert("筛选订单失败！");
        }
      } catch (error) {
        console.error("筛选订单时出错：", error);
        alert("筛选订单时出错，请稍后再试！");
      }
    },

    async fetchSellerOrders() {
      try {
        const params = {
          sellerId: this.user.id, // 当前用户ID
        };

        // 添加筛选条件（仅当不为空时）
        if (this.filter.orderType) params.orderType = this.filter.orderType;
        if (this.filter.orderStatus) params.orderStatus = this.filter.orderStatus;

        const response = await axios.get("/api/orders/seller/filter", { params });
        this.sellerOrders = response.data;

        // 提取订单ID，按降序排列并取前10个
        const recentOrderIds = this.sellerOrders
          .map(order => order.id)
          .sort((a, b) => b - a) // 按订单号降序排序
          .slice(0, 10); // 取前10个订单号

        // 检查每个订单状态并更新
        await Promise.all(recentOrderIds.map(orderId => this.updateOrderStatus(orderId)));

      } catch (error) {
        console.error("获取店铺订单失败", error);
        alert("获取店铺订单失败，请稍后再试");
      }
    },
    
    // 将订单状态转换为中文
    getOrderStatusText(status) {
      const statusMap = {
        PENDING: "待支付",
        PAID: "已支付",
        SHIPPED: "已发货",
        DELIVERED: "已收货",
        CANCELLED: "已取消",
        RETURNED: "已回货",
        MERCHANT_CONFIRMED: "商家确认",
      };
      return statusMap[status] || "未知状态";
    },

    async confirmDelivery(order) {
      try {
        const response = await axios.patch(`/api/orders/${order.id}/confirm-delivery`);
        if (response.status === 200) {
          alert("确认收货成功！");
          // 更新订单状态为 DELIVERED
          order.orderStatus = "DELIVERED";
        } else {
          alert("确认收货失败，请稍后再试！");
        }
      } catch (error) {
        console.error("确认收货时出错：", error);
        alert("确认收货失败，请稍后再试！");
      }
    },

    async returnOrder(order) {
      try {
        const response = await axios.patch(`/api/orders/${order.id}/return`);
        if (response.status === 200) {
          alert("退还订单成功！");
          order.orderStatus = "RETURNED";
        } else {
          alert("退还订单失败，请稍后再试！");
        }
      } catch (error) {
        console.error("退还订单时出错：", error);
        alert("退还订单失败，请稍后再试！");
      }
    },

    // 发货逻辑
    async shipOrder(order) {
      try {
        const response = await axios.patch(`/api/orders/seller/${order.id}/ship`);
        if (response.status === 200) {
          alert("发货成功！");
          order.orderStatus = "SHIPPED";
        } else {
          alert("发货失败，请稍后再试！");
        }
      } catch (error) {
        console.error("发货时出错：", error);
        alert("发货失败，请稍后再试！");
      }
    },

    // 商家确认逻辑
    async confirmReturn(order) {
      try {
        const response = await axios.patch(`/api/orders/seller/${order.id}/merchant-confirm`);
        if (response.status === 200) {
          alert("确认成功！");
          order.orderStatus = "MERCHANT_CONFIRMED";
        } else {
          alert("确认失败，请稍后再试！");
        }
      } catch (error) {
        console.error("确认时出错：", error);
        alert("确认失败，请稍后再试！");
      }
    },

    async updateOrderStatus(orderId) {
      try {
        const response = await axios.get("/api/orders/queryStatus", {
          params: {
            orderId: orderId,
          },
        });

        if (response.data.status === "success") {
          console.log(`订单 ${orderId} 状态更新成功: ${response.data.message}`);
        } else {
          console.warn(`订单 ${orderId} 状态更新失败: ${response.data.message}`);
        }
      } catch (error) {
        console.error(`更新订单 ${orderId} 状态时出错:`, error);
      }
    },

    async sendMessage() {
      if (this.userInput.trim() === "") {
        alert("请输入内容后发送！");
        return;
      }

      // 添加用户消息到消息列表
      this.messages.push({
        sender: "user",
        content: this.userInput,
      });

      // 暂存用户输入并清空输入框
      const inputMessage = this.userInput;
      this.userInput = "";

      try {
        // 向后端发送请求
        const response = await axios.post("http://localhost:8080/api/chat", null, {
          params: {
            message: inputMessage,
          },
        });

        // 提取后端返回的 content
        const content = response.data.choices[0].message.content;

        // 添加 AI 的回复到消息列表
        this.messages.push({
          sender: "ai",
          content: content,
        });
      } catch (error) {
        console.error("请求失败:", error);
        this.messages.push({
          sender: "ai",
          content: "抱歉，我无法连接到服务器，请稍后重试。",
        });
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
  
  /* Stock-input 样式 */
  .Stock-input {
    display: flex;
    flex-direction: column; /* 垂直排列 */
    justify-content: center; /* 内容在容器中间 */
    gap: 10px; /* 控制输入框之间的间距 */
  }

  /* 输入框通用样式 */
  .Stock-input input {
    width: 100%; /* 占满父容器宽度 */
    padding: 20px; /* 增加内边距 */
    font-size: 16px; /* 调整字体大小 */
    border: 1px solid #ccc; /* 边框样式 */
    border-radius: 5px; /* 圆角 */
    box-sizing: border-box; /* 包括内边距 */
  }

  /* buttonInput 样式 */
  .buttonInput {
    display: flex;
    flex-direction: column; /* 垂直排列 */
    justify-content: center; /* 内容在容器中间 */
    gap: 10px; /* 控制按钮之间的间距 */
  }

  .product-item .Stock-button {
    background-color: #ff4d4f;
    color: white;
    border: none;
    padding: 10px;
    font-size: 14px;
    width: 100px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    margin-right: 10px;
  }
  .product-item .Stock-button:hover {
    background-color: #22f2f5;
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
    margin-right: 10px;
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

  .form-container {
    display: flex; /* 水平排列 */
    gap: 20px; /* 子元素之间的间距 */
  }

  .form-group-price {
    flex: 1; /* 每个子元素占一半宽度 */
    display: flex;
    flex-direction: column; /* 垂直排列子元素 */
    justify-content: center;
    margin-left: 20px; /* 左边距 */
    margin-right: 20px;
  }

  .form-group input {
    width: 70%; /* 输入框占满父容器宽度 */
    padding: 8px;
    font-size: 16px;
    border: 1px solid #ccc;
    border-radius: 5px;
    box-sizing: border-box; /* 确保 padding 不影响宽度 */
  }

  .form-group label {
    margin-bottom: 5px; /* 标签与输入框之间的间距 */
    font-weight: bold; /* 加粗标签 */
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



    .order-section {
    margin-top: 20px;
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  }

  .filters {
    display: flex;
    gap: 10px;
    align-items: center;
    margin-bottom: 20px;
  }

  .filter-select {
    padding: 8px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 5px;
  }

  .filter-button {
    padding: 8px 12px;
    background-color: #6a2af5;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
    transition: background-color 0.3s ease;
  }

  .filter-button:hover {
    background-color: #4c13fa;
  }

  .order-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .order-item {
    background-color: #ffffff;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .order-item h3 {
    font-size: 18px;
    margin-bottom: 10px;
  }

  .order-item p {
    font-size: 14px;
    margin: 5px 0;
  }

  .order-image {
    width: 140px;
    height: 80px;
    object-fit: cover; 
    border-radius: 8px; 
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    margin-right: 0px;
  }

  .order-action-button {
    margin-top: 10px;
    padding: 8px 16px;
    background-color: #6a2af5;
    color: white;
    font-size: 14px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }

  .order-action-button:hover {
    background-color: #4c13fa;
  }

  /* ai界面 */
.ai-chat-container {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 1200px;
  height: 80vh; /* 界面高度 */
  margin: 0 auto;
  padding: 10px;
  background-color: #f7f9fc;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.ai-chat-box {
  flex-grow: 1; /* 填充剩余空间 */
  max-height: calc(100% - 60px); /* 留出底部输入框空间 */
  overflow-y: auto; /* 超出部分滚动 */
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #ffffff;
}

.ai-chat-message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  max-width: 100%;
}

.ai-chat-message.ai-user-message {
  justify-content: flex-end; /* 用户消息靠右 */
}

.ai-chat-message.ai-ai-message {
  justify-content: flex-start; /* AI 消息靠左 */
}

.ai-message-content {
  display: flex;
  flex-direction: row; /* 确保头像和消息并排 */
  align-items: flex-start; /* 顶部对齐 */
  gap: 10px;
}

.ai-avatar {
  flex-shrink: 0;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
}

.ai-text {
  padding: 15px;
  font-size: 16px;
  background-color: #e8e8e8;
  border-radius: 10px;
  word-wrap: break-word;
  box-sizing: border-box; /* 包括padding在内的宽度限制 */
  text-align: left;
}

.ai-user-message .ai-text {
  background-color: #d1f7c4;
}

.ai-ai-message .ai-text {
  background-color: #f1f1f1;
}

.ai-input-area {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-top: 1px solid #ddd;
  background-color: #ffffff;
}

.ai-input-area textarea {
  flex-grow: 1;
  resize: none;
  height: 40px; /* 调整输入框高度 */
  padding: 8px;
  font-size: 14px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
}

.ai-input-area button {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 8px 15px;
  font-size: 14px;
  border-radius: 5px;
  width: 50px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.ai-input-area button:hover {
  background-color: #45a049;
}

/* 滚动条样式 */
.ai-chat-box::-webkit-scrollbar {
  width: 8px;
}

.ai-chat-box::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 4px;
}

.ai-chat-box::-webkit-scrollbar-thumb:hover {
  background: #999;
}

.ai-chat-box::-webkit-scrollbar-track {
  background: #f1f1f1;
}


</style>