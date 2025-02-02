import Vue from 'vue';
import Router from 'vue-router';
import StudentUpload from '@/views/StudentUpload.vue';
import AdminDashboard from '@/views/AdminDashboard.vue';
import ClassManagement from '@/views/ClassManagement.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import { decodeJwt } from '@/utils/auth.js';

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'Login', component: LoginView },
    { path: '/register', name: 'Register', component: RegisterView },
    {
      path: '/user',
      name: 'UserDashboard',
      redirect: '/user/submit',
      component: StudentUpload,
      meta: { requiresAuth: true },
      children: [
        { path: 'submit', name: 'SubmitAward', component: () => import('@/views/SubmitAward.vue') },
        { path: 'history', name: 'AwardHistory', component: () => import('@/views/AwardHistory.vue') },
        { path: 'profile', name: 'UserProfile', component: () => import('@/views/UserProfile.vue') },
      ],
    },
    {
      path: '/admin',
      name: 'AdminDashboard',
      redirect: '/admin/award-info',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: 'award-info', name: 'AwardInfo', component: () => import('@/views/AwardInfo.vue') },
        { path: 'class-management/:className', name: 'ClassManagement', component: ClassManagement, props: true },
      ],
    },
    { path: '*', name: 'NotFound', component: () => import('@/views/NotFound.vue') },
  ],
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  let user = null;

  if (token) {
    try {
      user = decodeJwt(token);
      if (!user || isTokenExpired(token)) {
        localStorage.removeItem('token');
        // 弹出提示
        Vue.prototype.$message.error('登录信息已过期，请重新登录');
        return next('/login');
      }
    } catch (error) {
      console.error('Token 解码失败:', error);
      localStorage.removeItem('token');
      Vue.prototype.$message.error('登录信息无效，请重新登录');
      return next('/login');
    }
  }

  if (!user) {
    if (to.meta.requiresAuth) {
      Vue.prototype.$message.error('请先登录');
      return next('/login');
    }
    return next();
  }

  if (to.name === 'Login') {
    return next(user.role === '2' ? '/admin' : '/user');
  }

  if (to.meta.requiresAdmin && user.role !== '2') {
    Vue.prototype.$message.error('无权限访问');
    return next('/user');
  }

  next();
});

function isTokenExpired(token) {
  const decoded = decodeJwt(token);
  if (!decoded || !decoded.exp) return true;
  return decoded.exp < Math.floor(Date.now() / 1000);
}

export default router;
