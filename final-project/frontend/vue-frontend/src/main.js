import { createApp } from 'vue';
import App from './App.vue';
import axios from 'axios';
import router from './router'; 
axios.defaults.baseURL = 'http://localhost:8080'; // 后端 Spring Boot 服务地址

const app = createApp(App);
app.config.globalProperties.$axios = axios; // 全局注册 Axios
app.use(router);
app.mount('#app');

