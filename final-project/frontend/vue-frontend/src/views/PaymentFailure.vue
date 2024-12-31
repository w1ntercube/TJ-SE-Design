<template>
  <div class="payment-failure">
    <h1>支付失败</h1>
    <p>很抱歉，您的支付未成功。</p>
    <p>您可以尝试以下操作：</p>
    <ul>
      <li>检查您的网络连接是否正常。</li>
      <li>确保支付账户余额充足。</li>
      <li>稍后重试。</li>
    </ul>
    <div>
      <button @click="retryPayment">重试支付</button>
      <button @click="goToHome">返回首页</button>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    retryPayment() {
      this.$router.push({ name: "ProductDetail", params: { id: this.$route.query.productId } });
    },
    goToHome() {
      this.$router.push({ name: "ToHome" });
    },
  },
};
</script>

<style scoped>
.payment-failure {
  text-align: center;
  margin: 50px auto;
}

button {
  margin: 10px;
  padding: 10px 20px;
  background-color: #e53935;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #d32f2f;
}
</style>
