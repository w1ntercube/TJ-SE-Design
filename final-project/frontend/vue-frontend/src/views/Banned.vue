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
          <button @click="setActiveTab('consumerOrders')" :class="{ active: activeTab === 'consumerOrders' }">消费订单</button>
          <button @click="setActiveTab('storeOrders')" :class="{ active: activeTab === 'storeOrders' }">店铺订单</button>
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



      <!--芜湖-->
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
                <option value="RENTAL">租借订单</option>
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

            <!-- 消费订单列表 -->
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

      /*订单初始化 */
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
    };
  },
  methods: {
    setActiveTab(tab) {
      this.activeTab = tab;
      if (tab === "products") {
        this.fetchProducts(); // 点击"我的商品"时获取商品数据
      }else if (tab === "consumerOrders") {
          this.fetchConsumerOrders(); // 点击消费订单时加载数据
      } else if (tab === "storeOrders") {
          this.fetchSellerOrders(); // 点击店铺订单时加载数据
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

    /*全部的订单逻辑 */
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
    /*全部的订单逻辑 */

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


  /* 订单样式 */
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

    /* 订单样式 */
</style>