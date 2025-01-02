<template>
    <div class="product-detail-page">
      <div class="left-box">
        <h2>商品详情</h2>
    
        <!-- 若商品信息仍在请求中时，先显示“加载中” -->
        <div v-if="isLoading">加载中...</div>
        <div v-else>
          <h3>{{ product.name }}</h3>
          <img :src="fullImagePath" alt="商品图片" class="product-detail-image" />
          <p>价格：¥{{ product.price }}</p>
          <p>库存：{{ product.stock }}</p>
    
          <!-- 卖家信息 -->
          <div class="seller-info">
            <p>商家名称：{{ product.seller?.username }}</p>
            <p>商家信誉分：{{ product.seller?.reputationScore }}</p>
          </div>
    
          <p>描述：{{ product.description }}</p>
        </div>
        
      </div>
      <div class="action-buttons">
          <button class="action-button" @click="handleLike">加入喜欢</button>
          <button class="action-button" @click="handleBuy">立即购买</button>
          <button class="action-button" @click="handleRent">租借试试</button>
      </div>
      <!-- 返回或其它操作按钮 -->
      <button class="back-button" @click="goBack">返回</button>
      <!-- 右侧评论区 -->
      <div class="right-box">
        <!-- 评论列表 -->
        <div class="comment-list">
          <h2>评论区</h2>
          <div
            class="comment-item"
            v-for="review in reviews"
            :key="review.id"
          >
            <div class="comment-header">
              <img :src="review.avatarUrl" alt="用户头像" class="user-avatar" />
              <h4 class="comment-username">{{ review.username }}</h4>
              <span class="comment-rating">评分：({{ review.rating }}⭐)</span>
              <button class="delete-comment-button" @click="deleteComment(review.id)">删除</button>
            </div>
            <p class="comment-content">{{ review.comment }}</p>
            <div class="comment-bottom">
              <span class="timestamp">{{ review.createdAt }}</span>
            </div>
          </div>
        </div>

        <!-- 评论表单 -->
        <div class="comment-form">
          <h3>发表评论</h3>
          <textarea v-model="newReview.comment" placeholder="输入您的评论"></textarea>
          <div>
            <label for="rating">评分：</label>
            <div class="rating-options">
              <label v-for="star in 5" :key="star" class="rating-label">
                <input
                  type="radio"
                  :value="star"
                  v-model="newReview.rating"
                  class="rating-input"
                />
                <span>{{ star }} 星</span> <!-- 确保文字显示 -->
              </label>
            </div>
          </div>
          <button @click="submitReview" class="comment-button">提交评论</button>
        </div>
      </div>
    </div>

    <!-- 购买弹窗 -->
    <div v-if="showPurchaseDialog" class="purchase-dialog">
      <div class="dialog-overlay" @click="closeDialog"></div>
      <div class="dialog-content">
        <h3>购买商品</h3>
        <div class="dialog-form">
          <label for="quantity">购买数量：</label>
          <input
            id="quantity"
            type="number"
            v-model="purchaseDetails.quantity"
            min="1"
            max="product.stock"
            placeholder="请输入购买数量"
          />

          <p>总价格：<span>￥{{ formattedPurchasePrice }}</span></p>
          
          <label for="address">配送地址：</label>
          <input
            id="address"
            type="text"
            v-model="purchaseDetails.address"
            placeholder="请输入配送地址"
          />

          <label for="payment-type">选择支付方式：</label>
          <div class="payment-options">
            <label>
              <input
                type="radio"
                value="1"
                v-model="purchaseDetails.type"
              />
              微信支付
            </label>
            <label>
              <input
                type="radio"
                value="2"
                v-model="purchaseDetails.type"
              />
              支付宝支付
            </label>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="action-button" @click="confirmPurchase">
            确认购买
          </button>
          <button class="action-button cancel-button" @click="closeDialog">
            取消
          </button>
        </div>
      </div>
    </div>


  <!-- 租借弹窗 -->
  <div v-if="showRentalDialog" class="rental-dialog">
    <div class="dialog-overlay" @click="closeRentalDialog"></div>
    <div class="dialog-content">
      <h3>租借商品</h3>
      <div class="dialog-form">
        <!-- 输入租借天数 -->
        <label for="rentalDays">租借天数：</label>
        <input
          id="rentalDays"
          type="number"
          v-model="rentalDetails.days"
          min="1"
          placeholder="请输入租借天数"
        />

        <!-- 输入租借数量 -->
        <label for="quantity">租借数量：</label>
        <input
          id="quantity"
          type="number"
          v-model="rentalDetails.quantity"
          min="1"
        />

        <p>总价格：<span>￥{{ formattedRentalPrice }}（含押金）</span></p>

        <label for="address">配送地址：</label>
        <input
          id="address"
          type="text"
          v-model="rentalDetails.address"
          placeholder="请输入配送地址"
        />

        <!-- 支付方式 -->
        <label for="payment-type">选择支付方式：</label>
        <div class="payment-options">
          <label>
            <input type="radio" value="1" v-model="rentalDetails.type" />
            微信支付
          </label>
          <label>
            <input type="radio" value="2" v-model="rentalDetails.type" />
            支付宝支付
          </label>
        </div>
      </div>
      <div class="dialog-actions">
        <button class="action-button" @click="confirmRental">确认租借</button>
        <button class="action-button cancel-button" @click="closeRentalDialog">取消</button>
      </div>
    </div>
  </div>


</template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "ProductDetail",
    data() {
      return {
        product: {},    // 保存获取到的商品信息
        reviews: [],    // 保存获取到的评论列表
        newReview: {
          userId: null, // 从用户登录信息中获取
          productId: null, // 当前商品 ID
          comment: "",
          rating: 5,
        },
        isLoading: true, // 是否正在加载数据

        // 购买弹窗相关
        showPurchaseDialog: false, // 控制弹窗显示
        purchaseDetails: {
          quantity: 1, // 默认购买数量
          type: 1, // 默认支付方式，1表示微信
        },

        // 租借弹窗相关
        showRentalDialog: false, // 控制租借弹窗显示
        rentalDetails: {
          days: 1, // 默认租借天数
          quantity: 1,
          type: 1, // 默认支付方式
          deposit: 0.1, // 默认押金
        },
      };
    },
    computed: {
      // 拼接完整的图片地址
      fullImagePath() {

        if (this.product.imagePath) {
          return "http://localhost:8080" + this.product.imagePath;
        }
        // 如果无图片或路径为空，可返回默认图
        return require("@/Resources/default-product.jpg");
      },

      formattedPurchasePrice() {
        const totalPrice = this.product.price * this.purchaseDetails.quantity;
        return totalPrice.toFixed(2);
      },

      formattedRentalPrice() {
        const rentalFee = this.rentalDetails.days * this.rentalDetails.quantity * 0.1;
        const deposit = 50;
        const totalPrice = rentalFee + deposit;
        return totalPrice.toFixed(2);
      },
    },
    created() {
      // 获取路由参数中的商品ID
      const productId = this.$route.params.id;
      // 发起请求获取商品详细信息
      this.fetchProductDetails(productId);
      // 获取评论信息
      this.fetchProductReviews(productId);
    },
    methods: {
      // 获取商品详细信息
      fetchProductDetails(productId) {
        axios
        .get(`/api/products/${productId}`)
        .then((response) => {
          this.product = response.data;
        })
        .catch((error) => {
          console.error("获取商品详情失败:", error);
        })
        .finally(() => {
          this.isLoading = false;
        });
      },
      // 获取商品评论信息
      fetchProductReviews(productId) {
        axios
          .get(`/api/reviews/product/${productId}`)
          .then((response) => {
            this.reviews = response.data.map((review) => ({
              ...review,
              avatarUrl: review.avatarUrl
                ? `http://localhost:8080${review.avatarUrl}`
                : require('@/Resources/default-avatar.jpg'), // 默认头像路径
            }));
          })
          .catch((error) => {
            console.error("获取商品评论失败:", error);
          });
      },
      // 返回
      goBack() {
        // 返回上一页
        this.$router.go(-1);
        // 直接跳转到首页
        // this.$router.push({ name: 'ToHome' });
      },
      handleLike() {
        const storedUser = JSON.parse(localStorage.getItem("user"));

        if (!storedUser || !storedUser.id) {
          alert("用户未登录，请先登录！");
          return;
        }

        const userId = storedUser.id;

        if (!this.product.id) {
          alert("商品信息加载失败，请稍后再试！");
          return;
        }

        // 构建请求数据
        const params = new URLSearchParams();
        params.append("userId", userId);
        params.append("productId", this.product.id);
        params.append("quantity", 1); 

        // 发起 POST 请求
        axios
          .post(`/api/carts`, params, {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
          })
          .then(() => {
            alert("已成功加入喜欢列表！");
          })
          .catch((error) => {
            console.error("加入喜欢失败:", error);
            alert("加入喜欢失败，请稍后重试！");
          });
      },
      // 提交评论
      submitReview() {
        const storedUser = JSON.parse(localStorage.getItem("user"));

        if (!storedUser || !storedUser.id) {
          alert("用户未登录，请先登录！");
          return;
        }

        const userId = storedUser.id;
        const productId = this.$route.params.id;

        // 构建评论数据
        const newReviewData = {
          userId,
          productId,
          comment: this.newReview.comment,
          rating: this.newReview.rating,
        };

        if (!newReviewData.comment) {
          alert("评论内容不能为空！");
          return;
        }

        axios
          .post(`/api/reviews/add`, newReviewData)
          .then((response) => {
            alert("评论提交成功！");
            // 将新评论添加到评论列表中（无需刷新页面）
            this.reviews.push(response.data);
            // 清空评论表单
            this.newReview.comment = "";
            this.newReview.rating = 5;
            window.location.reload();
          })
          .catch((error) => {
            console.error("评论提交失败:", error);
            alert("评论提交失败，请稍后重试！");
          });
        },
        // 删除评论
        async deleteComment(commentId) {
          if (!confirm("确定要删除这条评论吗？")) {
            return; // 如果用户取消操作，直接退出
          }

          try {
            // 获取本地用户信息
            const storedUser = JSON.parse(localStorage.getItem("user"));

            if (!storedUser || !storedUser.id) {
              alert("用户未登录，请先登录！");
              return;
            }

            const userId = storedUser.id;

            // 向后端发送删除请求，包含 userId 和 commentId
            const response = await axios.post(`/api/reviews/delete`, {
              userId: userId,
              reviewId: commentId,
            });

            if (response.status === 200) {
              // 从评论列表中移除已删除的评论
              this.reviews = this.reviews.filter(comment => comment.id !== commentId);
              alert("评论已成功删除");
              window.location.reload();
            } else {
              alert("删除评论失败，请稍后再试");
            }
          } catch (error) {
              console.error("完整错误信息: ", error);

              if (error.response) {
                console.log("错误响应信息: ", error.response);
                const status = error.response.status;

                if (status === 401) {
                  alert("未授权，请重新登录！");
                } else if (status === 404) {
                  alert("评论不存在或已被删除！");
                } else {
                  alert("删除评论失败，请稍后再试！");
                }
              } else {
                console.error("删除评论时出错，可能是网络问题: ", error);
                alert("网络错误，请稍后再试！");
              }
            }
        },

        // 确认购买
      async confirmPurchase() {
        const storedUser = JSON.parse(localStorage.getItem("user"));

        if (!storedUser || !storedUser.id) {
          alert("用户未登录，请先登录！");
          return;
        }

        if (!this.purchaseDetails.quantity || this.purchaseDetails.quantity <= 0) {
          alert("购买数量必须是一个正整数！");
          return;
        }
        if (this.purchaseDetails.quantity > this.product.stock) {
          alert("购买数量不能超过商品库存！");
          return;
        }

        if (!this.purchaseDetails.address || this.purchaseDetails.address.trim() === "") {
          alert("请输入有效的配送地址！");
          return;
        }

        const userId = storedUser.id;
        try {
          const payload = new URLSearchParams();
          payload.append("userId", userId); // 默认用户 ID
          payload.append("productId", this.product.id); // 商品 ID
          payload.append("quantity", this.purchaseDetails.quantity); // 用户选择的购买数量
          payload.append("type", this.purchaseDetails.type); // 用户选择的支付方式
          payload.append("price", this.formattedPurchasePrice); // 总价格
          payload.append("address", this.purchaseDetails.address);

          const response = await axios.post("/api/payment/purchase", payload, {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
          });

          const { orderId, redirectUrl } = response.data;

          // 保存订单信息到 localStorage
          localStorage.setItem(
            "currentOrder",
            JSON.stringify({
              orderId,
              productId: this.product.id,
              price: this.product.price * this.purchaseDetails.quantity,
              paymentType: this.purchaseDetails.type === 1 ? "微信" : "支付宝",
            })
          );

          window.location.href = redirectUrl;
        } catch (error) {
          console.error("购买失败:", error);
          alert("购买失败，请稍后再试。");
        } finally {
          this.closeDialog(); // 无论成功与否，都关闭弹窗
        }
      },

      async confirmRental() {
        const storedUser = JSON.parse(localStorage.getItem("user"));
        if (!storedUser || !storedUser.id) {
          alert("用户未登录，请先登录！");
          this.closeRentalDialog();
          return;
        }

        const userId = storedUser.id;

        // 验证用户输入
        if (!this.rentalDetails.days || this.rentalDetails.days <= 0) {
          alert("租借天数必须是一个正整数！");
          return;
        }

        // 验证租借数量
        if (!this.rentalDetails.quantity || this.rentalDetails.quantity <= 0) {
          alert("租借数量必须是一个正整数！");
          return;
        }

        // 验证地址
        if (!this.rentalDetails.address || this.rentalDetails.address.trim() === "") {
          alert("请输入有效的配送地址！");
          return;
        }

        if (!this.product || !this.product.id) {
          alert("商品信息加载失败，请稍后再试！");
          this.closeRentalDialog();
          return;
        }

        const productId = this.product.id;

        try {
          const payload = new URLSearchParams();
          payload.append("userId", userId);
          payload.append("productId", productId);
          payload.append("days", this.rentalDetails.days); // 租借天数
          payload.append("deposit", this.rentalDetails.deposit.toFixed(2));
          payload.append("price", this.formattedRentalPrice);
          payload.append("quantity", this.rentalDetails.quantity);
          payload.append("type", this.rentalDetails.type);
          payload.append("address", this.rentalDetails.address);

          console.log(Object.fromEntries(payload.entries()));

          const response = await axios.post("/api/payment/rental", payload, {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
          });

          const { orderId, redirectUrl } = response.data;

          localStorage.setItem(
            "currentRentalOrder",
            JSON.stringify({
              orderId,
              productId,
              rentalDays: this.rentalDetails.days,
              deposit: this.rentalDetails.deposit,
              price: this.product.price,
              paymentType: this.rentalDetails.type === 1 ? "微信" : "支付宝",
            })
          );

          window.location.href = redirectUrl;
        } catch (error) {
          console.error("租借失败:", error);
          alert("租借失败，请稍后再试。");
        } finally {
          this.closeRentalDialog();
        }
      },


      async handleBuy() { 
        this.showPurchaseDialog = true;
      },
      closeDialog() {
        this.showPurchaseDialog = false;
      },

      // 打开租借弹窗
      handleRent() {
        this.showRentalDialog = true;
      },

      // 关闭租借弹窗
      closeRentalDialog() {
        this.showRentalDialog = false;
      },
    },
  };
  </script>
  
  <style scoped>
    /* 与 Home 页面保持一致的全局设置 */
    html,
    body {
    margin: 0;
    padding: 0;
    height: 100%;
    overflow: hidden;
    }

    /* 主容器，背景等与 Home 相似 */
    .product-detail-page {
      background: url('@/Resources/Login.jpg') no-repeat center center; 
      background-size: cover;
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100vh;
      display: flex;
      flex-direction: row;
      font-family: Arial, sans-serif;
      color: white;
      overflow-y: auto; /* 可滚动 */
    }

    /* 左侧商品信息方框 */
    .left-box {
      position: relative;
      width: 50%;              /* 左侧宽度可根据实际需求调整 */
      margin: 10px;            /* 与右侧留一点间距 */
      padding: 20px;
      box-sizing: border-box;
      border-radius: 8px;
      overflow: auto;          /* 如果内容超过区域，可在左侧单独滚动 */

      /* 新增背景与文字色 */
      background-color: rgba(255, 255, 255, 0.8); /* 半透明白色 */
      color: #000;                               /* 黑色字体 */
    }

    /* 商品标题、图片等 */
    .left-box h2 {
      margin-top: 0; 
    }

    /* 右侧评论方框 */
    .right-box {
      flex: 1;                  /* 占据剩余宽度 */
      margin: 10px;
      padding: 20px;
      box-sizing: border-box;
      border-radius: 8px;
      border: 2px solid #ccc;
      display: flex;
      flex-direction: column; /* 上下布局 */
      /*overflow-y: auto;         /* 只在右侧出现垂直滚动条 */
      overflow: hidden;
    }
    /* 评论列表部分 */
    .comment-list {
      flex: 3; /* 占据 3/4 的高度 */
      overflow-y: auto; /* 支持滚动 */
      padding-right: 10px; /* 防止滚动条覆盖内容 */
      margin-bottom: 10px; /* 与表单分隔 */
      border-bottom: 1px solid #ddd; /* 添加分隔线 */
    }

    /* 评论表单部分 */
    .comment-form {
      display: flex;
      flex-direction: column; /* 垂直排列 */
      justify-content: flex-start; /* 从上到下排列 */
      gap: 20px; /* 每部分之间的间距 */
      padding: 20px;
      background-color: #ffffff; /* 背景颜色 */
      border-radius: 10px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2); /* 增加阴影效果 */
      max-width: 600px; /* 表单最大宽度 */
      width: 100%; /* 表单宽度占满父容器 */
      margin: 20px auto; /* 居中对齐并留出顶部空间 */
      box-sizing: border-box;
    }

    .comment-form h3 {
      font-size: 20px; /* 设置标题大小 */
      font-weight: bold;
      color: #333; /* 确保标题颜色清晰可见 */
      text-align: center;
      margin-bottom: 10px; /* 与下一部分保持距离 */
    }

    .textarea-container {
      width: 100%; /* 输入框占满容器宽度 */
    }

    .comment-textarea {
      resize: none; /* 禁止调整大小 */
      width: 100%;
      height: 100px; /* 输入框高度 */
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 20px;
      box-sizing: border-box;
    }

    .rating-container {
      display: flex;
      flex-direction: column; /* 垂直排列评分标题和选项 */
      gap: 10px; /* 标题和选项之间的间距 */
    }

    .rating-title {
      font-size: 16px;
      font-weight: bold;
      color: #333; /* 确保标题颜色清晰 */
    }

    .rating-options {
      display: flex;
      gap: 15px; /* 每个评分选项之间的间距 */
      justify-content: center; /* 水平居中 */
    }

    .rating-label {
      display: flex;
      align-items: center; /* 垂直居中 */
      gap: 5px; /* 按钮和文字之间的间距 */
      cursor: pointer; /* 鼠标变为手型 */
      font-size: 14px; /* 设置文字大小 */
      color: #333; /* 确保文字颜色清晰 */
    }

    .rating-input {
      cursor: pointer;
    }


    .button-container {
      display: flex;
      justify-content: center; /* 提交按钮居中 */
    }

    .comment-button {
      padding: 12px 40px; /* 调整按钮宽度 */
      background-color: #0ec1e9;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
      font-weight: bold;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
      transition: all 0.3s ease;
    }

    .comment-button:hover {
      background-color: #6e15ea;
      transform: scale(1.05); /* 悬停时放大效果 */
    }


    .right-box .comment-item {
      /* 每个评论框的外观 */
      background-color: #fff;
      border-radius: 8px;
      border-bottom: 1px solid #ddd;
      padding: 10px;
      margin-bottom: 10px; /* 相邻评论间距 */
      
      /* 让内部结构可以灵活摆放 */
      display: flex;
      flex-direction: column;
    }

    /* 去掉最后一个评论的下边框和间距 */
    .right-box .comment-item:last-child {
      border-bottom: none;
      margin-bottom: 0;
    }

    /* 头部区域：左侧用户名 + 右侧评分 */
    .comment-header {
      display: flex;
      align-items: center; /* 头像与用户名垂直居中 */
      gap: 5px; /* 控制头像与名字之间的间距 */
      position: relative; /* 为了绝对定位按钮 */
    }
    .user-avatar {
      width: 40px; /* 调整头像宽度 */
      height: 40px; /* 调整头像高度 */
      border-radius: 50%; /* 确保头像圆形 */
      margin-right: 5px; /* 减小与用户名的间距 */
      object-fit: cover; /* 确保图片裁剪比例正确 */
    }
    .right-box .comment-bottom {
      display: flex;
      justify-content: flex-end; /* 内容靠右对齐 */
      align-items: center;
      margin-top: 10px;          /* 与上方内容保持一点间距 */
    }
    /* 时间戳样式 */
    .timestamp {
      font-size: 14px;
      color: #999;              /* 比正文更浅一些，突出时间为次要信息 */
      /* 如果想绝对定位到右下角，可以改成：
        position: absolute;
        right: 10px;
        bottom: 10px;
        但需确保父元素 .comment-item 有 position: relative; 
      */
    }
    /* 用户名（左上角） */
    .right-box .comment-username {
      color: #333;
      font-size: 16px;
      font-weight: bold;
      margin: 0; /* 去掉默认 margin */
    }

    /* 评分（右上角） */
    .right-box .comment-rating {
      color: #333;
      font-size: 20px;
    }

    /* 评论内容（左对齐，放在头部下方） */
    .right-box .comment-content {
      margin-top: 10px;
      color: #666;
      text-align: left;
      line-height: 1.5;
    }


    /* 用一个容器来包裹商品详情内容，类似 product-container 的风格 */
    .product-detail-container {
      width: 90%;
      margin: 30px auto;
      padding: 20px;
      background-color: rgba(155, 20, 245, 0.7); /* 参考 Home 页面 */
      border-radius: 10px;
      border: 2px solid #12b3f8;
      display: flex;
      flex-direction: column;
      align-items: center; 
    }

    /* 商品图片 */
    .product-detail-image {
      width: 300px;
      height: 300px;
      object-fit: contain;
      background-color: #fff; /* 如果想突出图片区域，也可保留白背景 */
      border-radius: 10px;
      margin-bottom: 20px;
    }

    /* 商品标题、价格、卖家信息等，可参考原有文字风格 */
    .product-detail-name {
      font-size: 24px;
      color: #000; /* 与 Home 页的 .product-name 类似 */
      margin: 10px 0;
    }

    .product-detail-price {
      font-size: 20px;
      color: #3a23e4;
      margin: 10px 0;
    }

    /* 卖家信息，可参考 .seller-info, .seller-reputation */
    .seller-info {
      font-size: 20px;
      color: #2cef2f; 
    }

    .seller-reputation {
      font-size: 20px;
      color: #c0cb24; 
      margin-bottom: 10px;
    }

    /* 商品描述等文字 */
    .product-detail-description {
      font-size: 16px;
      color: #000;
      background-color: rgba(255, 255, 255, 0.8);
      padding: 10px;
      border-radius: 5px;
      margin: 20px auto;
      max-width: 800px; /* 控制一下宽度 */
    }

    /* 返回按钮或其他操作按钮的风格，可参考搜索按钮 */
    .back-button {
      /* 基础样式 */
      padding: 10px 20px;
      background-color: #0ec1e9;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
      
      /* 新增定位及视觉效果 */
      position: absolute;     /* 使用绝对定位 */
      top: 20px;             /* 距离页面顶部 20px */
      left: 20px;            /* 距离页面左侧 20px */
      z-index: 9999;         /* 保持在最上层，防止被其它元素覆盖 */
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
      transition: all 0.3s ease; /* 为悬停动画和其它状态变化提供平滑过渡 */
    }

    .back-button:hover {
      background-color: #6e15ea;
      transform: scale(1.05);               /* 悬停时略微放大 */
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3); /* 悬停时加深阴影 */
    }

    .action-buttons {
      position: fixed; /* 固定位置 */
      top: 30px; /* 距离顶部 */
      left: 39%; /* 距离左侧 */
      width: 200px; /* 调整宽度，确保足够容纳按钮 */
      padding: 20px; /* 添加内边距，留出子容器与边框的距离 */
      display: flex;
      flex-direction: column; /* 垂直排列 */
      justify-content: space-evenly; /* 子元素均匀分布 */
      align-items: center; /* 水平居中 */
      gap: 15px; /* 按钮之间的间距 */
      background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
      border-radius: 10px; /* 圆角 */
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 阴影效果 */
      z-index: 10; /* 优先显示 */
      box-sizing: border-box; /* 确保宽度包括内边距 */
    }

    /* 按钮样式 */
    .action-button {
      padding: 10px 20px; /* 内边距 */
      background-color: #0ec1e9;
      color: white;
      border: none;
      border-radius: 5px; /* 按钮圆角 */
      cursor: pointer;
      font-size: 16px; /* 字体大小 */
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3); /* 按钮阴影 */
      transition: all 0.3s ease; /* 添加平滑过渡 */
      width: calc(100% - 40px); /* 减去内边距的宽度 */
      text-align: center; /* 按钮文字居中 */
    }


    .action-button:hover {
      background-color: #6e15ea;
      transform: scale(1.05);               /* 悬停时略微放大 */
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3); /* 悬停时加深阴影 */
    }
    .delete-comment-button {
      background-color: transparent; /* 背景透明 */
      color: red; /* 删除按钮颜色 */
      border: none; /* 去掉边框 */
      font-size: 14px; /* 调整字体大小 */
      cursor: pointer; /* 鼠标变为手型 */
      top: 0; /* 距离顶部对齐 */
      position: absolute; /* 绝对定位 */
      right: 10px; /* 距离右侧对齐 */
    }

    .delete-comment-button:hover {
      color: darkred; /* 悬停时颜色变化 */
    }

  /* 弹窗的背景遮罩 */
  .dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999; /* 遮罩层在最上层 */
  }

  /* 弹窗的内容容器 */
  .dialog-content {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 400px;
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
    z-index: 1000; /* 弹窗内容高于遮罩层 */
    text-align: center;
  }

  /* 弹窗标题 */
  .dialog-content h3 {
    font-size: 18px;
    margin-bottom: 20px;
  }

  /* 弹窗表单 */
  .dialog-form {
    margin-bottom: 20px;
  }

  .dialog-form label {
    display: block;
    font-size: 14px;
    margin-bottom: 5px;
  }

  .dialog-form input[type="number"] {
    width: calc(100% - 20px);
    padding: 5px 10px;
    font-size: 14px;
    margin-bottom: 15px;
    border: 1px solid #ddd;
    border-radius: 5px;
    text-align: center;
  }

  .payment-options {
    display: flex;
    justify-content: center;
    gap: 15px;
    margin-top: 10px;
  }

  .payment-options label {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 14px;
    cursor: pointer;
  }

  .payment-options input[type="radio"] {
    cursor: pointer;
  }

  /* 弹窗按钮组 */
  .dialog-actions {
    display: flex;
    justify-content: space-between;
    gap: 15px;
  }

  .dialog-actions .action-button {
    flex: 1;
    padding: 10px;
    background-color: #0ec1e9;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s ease;
  }

  .dialog-actions .cancel-button {
    background-color: #f44336;
  }

  .dialog-actions .action-button:hover {
    transform: scale(1.05);
  }

  </style>
  