import Vue from 'vue';
import Router from 'vue-router';
import StudentUpload from '@/views/StudentUpload.vue';
import AdminDashboard from '@/views/AdminDashboard.vue';
import ClassManagement from '@/views/ClassManagement.vue'; // 新增班级管理页面
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue'; // 注册页面
import { decodeJwt } from '@/utils/auth.js'; // 解码 JWT 的工具

Vue.use(Router);

const router = new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: '/login', // 根路径重定向到登录页面
    },
    {
      path: '/login',
      name: 'Login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'Register',
      component: RegisterView,
    },
    {
      path: '/user',
      name: 'StudentUpload',
      component: StudentUpload,
      meta: { requiresAuth: true }, // 需要登录权限
    },
    {
      path: '/admin',
      name: 'AdminDashboard',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          name: 'AwardInfo', // 默认子路由显示内容
          component: () => import('@/views/AwardInfo.vue'), // 将获奖信息拆分成单独组件
        },
        {
           // 修改为动态路由，接收班级名称作为参数
          path: 'class-management/:className', // 动态参数 :className
          name: 'ClassManagement',
          component: ClassManagement,
          props: true,// 启用 props 传递 className
        },
      ],
    },
  ],
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  let user = null;

  if (token) {
    try {
      user = decodeJwt(token);

      // 检查 token 是否过期
      const currentTime = Math.floor(Date.now() / 1000);  // 获取当前时间戳
      if (user.exp && user.exp < currentTime) {
        console.log('Token 已过期');
        localStorage.removeItem('token');  // 清除过期 token
        return next('/login');  // 跳转到登录页
      }
    } catch (error) {
      console.error('Token 解码失败:', error);
      localStorage.removeItem('token');  // 清除无效 token
      return next('/login');  // 强制跳转到登录页面
    }
  }

  // 处理未登录的情况
  if (!user) {
    if (to.meta.requiresAuth) {
      return next('/login');
    }
    return next();
  }

  // 已登录用户访问登录页面时，直接跳转到对应主页
  if (to.name === 'Login') {
    return next(user.role === '2' ? '/admin' : '/user');
  }

  // 管理员权限检查
  if (to.meta.requiresAdmin && user.role !== '2') {
    return next('/user');  // 非管理员访问管理员页面时重定向
  }

  // 正常导航，继续执行
  next();
});

export default router;
