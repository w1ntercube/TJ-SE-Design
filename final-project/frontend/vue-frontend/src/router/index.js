import { createRouter, createWebHistory } from 'vue-router';
import UserLogin from '../views/Login.vue'; // 登录页面
import UserRegister from '../views/Register.vue'; // 注册页面
import PasswordHandler from '../views/PasswordHandler.vue'; //修改密码页面
import ToHome from '../views/Home.vue'; //主页面
import UserProfile from '../views/Profile.vue';
import ProductDetail from '@/views/ProductDetail.vue'; // 商品的详情页面
import OrderDetail from '@/views/OrderDetail.vue'; // 订单详情页面
import PaymentSuccess from '@/views/PaymentSuccess.vue'; // 支付成功页面
import PaymentFailure from '@/views/PaymentFailure.vue'; // 支付失败页面


const routes = [
  { path: '/', redirect: '/login' }, // 默认跳转到 /login
  { path: '/login', name: 'UserLogin', component: UserLogin }, // 登录页面路由
  { path: '/register', name: 'UserRegister', component: UserRegister }, // 注册页面路由
  { 
    path: '/password/:action', 
    name: 'PasswordHandler', 
    component: PasswordHandler, 
    props: true // 将路由参数传递为组件的 props
  },
  { path: '/home', name: 'ToHome', component: ToHome }, // 主页面路由
  { path: '/profile', name: 'UserProfile', component: UserProfile }, // 个人页面路由
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail }, // 商品详情页面路由
  {
    path: "/order/:id", // 动态路由，用于访问订单详情
    name: "OrderDetail",
    component: OrderDetail,
  },
  {
    path: '/payment-success',
    name: 'PaymentSuccess',
    component: PaymentSuccess,
    props: (route) => ({
      orderId: route.query.orderId,
      paymentType: route.query.paymentType,
      amount: route.query.amount,
    }),
  },
  {
    path: '/payment-failure',
    name: 'PaymentFailure',
    component: PaymentFailure, // 支付失败页面
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

