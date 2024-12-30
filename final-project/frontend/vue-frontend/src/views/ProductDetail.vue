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
        <div class="action-buttons">
          <button class="action-button" @click="handleLike">加入喜欢</button>
          <button class="action-button" @click="handleBuy">立即购买</button>
          <button class="action-button" @click="handleRent">租借试试</button>
        </div>
      </div>
      <!-- 返回或其它操作按钮 -->
      <button class="back-button" @click="goBack">返回</button>
      <!-- 右侧评论区 -->
      <div class="right-box">
        <h2>评论区</h2>
        <div
          class="comment-item"
          v-for="review in reviews"
          :key="review.id"
        >
        <div class="comment-header">
          <!-- 左上角用户名 -->
          <h4 class="comment-username">{{ review.username }}</h4>
          <!-- 右上角评分 -->
          <span class="comment-rating">评分：{{ review.rating }}</span>
        </div>
        <!-- 评论内容（左对齐） -->
        <p class="comment-content">{{ review.comment }}</p>
        <div class="comment-bottom">
          <!-- 右下角时间戳 -->
          <span class="timestamp">{{ review.created_at }}</span>
        </div>
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
        isLoading: true, // 是否正在加载数据
        reviews: [
          // 您可以在组件创建或请求后端接口获取评论列表
          { id: 1, username: "Alice", rating: 5, comment: "这个商品不错，物美价廉！", created_at: "2024-07-01" },
          { id: 2, username: "Bob", rating: 4, comment: "价格实惠，值得购买！", created_at: "2024-07-02" },
          { id: 3, username: "Charlie", rating: 3, comment: "一般般，不太满意。", created_at: "2024-07-03" },
          { id: 4, username: "Alice", rating: 5, comment: "这个商品不错，物美价廉！", created_at: "2024-07-01" },
          { id: 5, username: "Bob", rating: 4, comment: "价格实惠，值得购买！", created_at: "2024-07-02" },
          { id: 6, username: "Charlie", rating: 3, comment: "一般般，不太满意。", created_at: "2024-07-03" },
          { id: 7, username: "Alice", rating: 5, comment: "这个商品不错，物美价廉！", created_at: "2024-07-01" },
          { id: 8, username: "Bob", rating: 4, comment: "价格实惠，值得购买！", created_at: "2024-07-02" },
          { id: 9, username: "Charlie", rating: 3, comment: "一般般，不太满意。", created_at: "2024-07-03" },
          { id: 10, username: "Charlie", rating: 3, comment: "一般般，不太满意。", created_at: "2024-07-03" },
        ],
        userId: 1, // 假设当前用户的 ID 为 1，实际情况可从用户登录信息中获取
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
    },
    created() {
      // 获取路由参数中的商品ID
      const productId = this.$route.params.id;
      // 发起请求获取商品详细信息
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
    methods: {
      goBack() {
        // 返回上一页
        this.$router.go(-1);
        // 直接跳转到首页
        // this.$router.push({ name: 'ToHome' });
      },
      async handleBuy() { 
        try {
          // 请求参数
          const payload = new URLSearchParams();
          payload.append("userId", this.userId); // 当前用户 ID
          payload.append("productId", this.product.id); // 商品 ID
          payload.append("quantity", 1); // 默认购买数量为 1
          payload.append("type", 1); // 假设支付方式为支付宝（2）。微信可改为 1
          payload.append("price", this.product.price); // 商品价格


          // 向后端发送请求
          const response = await axios.post("/api/payment/purchase", payload, {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded", // 设置请求头
            },
          });

          // 获取返回的 HTML 脚本
          const htmlResponse  = response.data;

          // 使用正则从返回的 HTML 中提取跳转 URL
          const urlMatch = htmlResponse.match(/window\.location\.href\s*=\s*'([^']+)'/);

          if (urlMatch && urlMatch[1]) {

            const relativeUrl = urlMatch[1];

            // 拼接完整 URL
            const baseUrl = "https://2218466.pay.lanjingzf.com";
            const redirectUrl = baseUrl + relativeUrl;

            console.log("Redirecting to:", redirectUrl);
            
            // 跳转
            window.location.href = redirectUrl;
          } else {
            console.error("未找到跳转 URL:", htmlResponse);
            alert("购买失败，请稍后再试。");
          }
        }catch (error) {
          console.error("购买失败:", error);
          alert("购买失败，请稍后再试。");
        }
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
      overflow-y: auto;         /* 只在右侧出现垂直滚动条 */
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
    .right-box .comment-header {
      display: flex;
      justify-content: space-between; /* 左右分布 */
      align-items: flex-start;        /* 顶部对齐 */
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

    /* 按钮容器：绝对定位在 left-box 底部，水平排列 */
    .action-buttons {
      position: fixed;       /* 固定在屏幕视窗 */
      bottom: 80px;          /* 距离屏幕底部 80px，您可根据需求微调为 50px、100px 等 */
      left: 50%;             /* 居中对齐 */
      transform: translateX(-180%); 
      display: flex;
      gap: 20px;             /* 按钮之间的水平间距 */
      z-index: 9999;         /* 保持在最前，以免被其他元素覆盖 */
    }

    /* 三个按钮统一样式 */
    .action-button {
      padding: 10px 20px;
      background-color: #0ec1e9;
      color: white;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
      transition: all 0.3s ease;
    }

    .action-button:hover {
      background-color: #6e15ea;
      transform: scale(1.05);
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
    }

  </style>
  