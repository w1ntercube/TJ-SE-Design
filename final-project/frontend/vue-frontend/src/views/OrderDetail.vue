<template>
  <div class="order-detail">
    <h1>订单详情</h1>

    <!-- 加载中状态 -->
    <div v-if="loading">加载中...</div>

    <!-- 订单不存在状态 -->
    <div v-else-if="orderNotFound">
      <p>未找到订单信息，请返回。</p>
      <button @click="goToHome">返回首页</button>
    </div>

    <!-- 订单详情显示 -->
    <div v-else>
      <p><strong>订单号：</strong>{{ order.id }}</p>
      <p><strong>用户ID：</strong>{{ order.userId }}</p>
      <p><strong>支付金额：</strong>¥{{ order.totalPrice }}</p>
      <p><strong>订单状态：</strong>{{ order.orderStatus }}</p>
      <p><strong>创建时间：</strong>{{ formattedCreatedAt }}</p>

      <!-- 商品信息 -->
      <h2>商品信息</h2>
      <p><strong>商品ID：</strong>{{ order.productId }}</p>
      <p><strong>购买数量：</strong>{{ order.quantity }}</p>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <button @click="goToProductDetail(order.productId)">查看商品详情</button>
        <button @click="goToHome">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import dayjs from "dayjs";

export default {
  name: "OrderDetail",
  data() {
    return {
      order: null, // 订单数据
      loading: true, // 加载状态
      orderNotFound: false, // 订单是否不存在
    };
  },
  computed: {
    // 格式化创建时间
    formattedCreatedAt() {
      return this.order?.createdAt ? dayjs(this.order.createdAt).format("YYYY-MM-DD HH:mm:ss") : "";
    },
  },
  created() {
    // 从路由参数中获取订单ID
    const orderId = this.$route.params.id;
    if (!orderId) {
      alert("订单ID缺失，请返回首页！");
      this.$router.push({ name: "Home" });
      return;
    }

    // 请求订单详情
    this.fetchOrderDetails(orderId);
  },
  methods: {
    async fetchOrderDetails(orderId) {
      try {
        const response = await axios.get(`/api/orders/${orderId}`);
        
        const data = response.data;
        if (!data) {
            throw new Error("订单数据为空");
        }
        
        this.order = response.data;

        if (!data.productId) {
          alert("商品信息缺失，无法跳转到商品详情！");
        }
      } catch (error) {
        console.error("获取订单详情失败:", error);
        this.orderNotFound = true;
        alert("无法加载订单详情，请稍后再试！");
      } finally {
        this.loading = false;
      }
    },
    goToProductDetail(productId) {
      if (!productId) {
        alert("商品信息缺失，无法跳转！");
        return;
      }
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
    goToHome() {
      // 返回首页
      this.$router.push({ name: "Home" });
    },
  },
};
</script>

<style scoped>
.order-detail {
  max-width: 800px;
  margin: 50px auto;
  text-align: left;
  font-family: Arial, sans-serif;
}

.order-detail h1 {
  text-align: center;
  margin-bottom: 20px;
}

.action-buttons {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  justify-content: center;
}

button {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>
