import { createRouter, createWebHistory } from 'vue-router';
import UserLogin from '../views/Login.vue'; // 登录页面
import UserRegister from '../views/Register.vue'; // 注册页面
import PasswordHandler from '../views/PasswordHandler.vue'; //修改密码页面
import ToHome from '../views/Home.vue'; //主页面
import UserProfile from '../views/Profile.vue';
import ProductDetail from '@/views/ProductDetail.vue'; // 商品的详情页面

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
  { path: '/product/:id', name: 'ProductDetail', component: ProductDetail}, // 商品详情页面路由
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

