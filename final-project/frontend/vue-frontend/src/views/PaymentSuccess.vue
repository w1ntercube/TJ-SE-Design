<template>
  <div class="payment-success">
    <h1>支付成功</h1>
    <p>感谢您的购买！</p>
    <p>订单号：{{ orderId }}</p>
    <p>支付方式：{{ paymentType }}</p>
    <p>支付金额：¥{{ amount }}</p>
    <button @click="goToOrderDetail">查看订单详情</button>
    <button @click="goToHome">返回首页</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "PaymentSuccess",
  data() {
    return {
      orderId: null,
      paymentType: null,
      amount: null,
      productId: null,
    };
  },
  created() {
    // 从 localStorage 加载订单信息
    const orderInfo = JSON.parse(localStorage.getItem("currentOrder"));

    if (!orderInfo) {
      alert("未找到订单信息，请返回首页！");
      this.$router.push({ name: "Home" });
      return;
    }

    this.orderId = orderInfo.orderId || "未知订单号";
    this.paymentType = orderInfo.paymentType || "未知支付方式";
    this.amount = orderInfo.price || "0.00";
    this.productId = orderInfo.productId || "未知产品ID";
    // 可选：验证订单支付状态
    this.checkPaymentStatus();
  },
  methods: {
    async checkPaymentStatus() {
      try {
        const response = await axios.get(`/api/orders/${this.orderId}/status`);
        if (response.data.status !== "PAID") {
          alert("支付状态验证失败，请联系客服！");
        }
      } catch (error) {
        console.error("支付状态验证失败:", error);
      }
    },
    goToOrderDetail() {
      this.$router.push({ name: "OrderDetail", params: { id: this.orderId } });
    },
    goToHome() {
      this.$router.push({ name: "Home" });
    },
  },
};
</script>

<style scoped>
.payment-success {
  text-align: center;
  margin: 50px auto;
}

button {
  margin: 10px;
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
