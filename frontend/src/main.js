import Vue from 'vue';
import App from './App.vue';
import router from './router';
import axios from 'axios';
import VueAxios from 'vue-axios';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';


Vue.use(ElementUI);
Vue.config.productionTip = false;

Vue.use(VueAxios, axios);
axios.defaults.baseURL = 'http://localhost:7777'; // 后端API地址

new Vue({
    router,
    render: h => h(App),
}).$mount('#app');