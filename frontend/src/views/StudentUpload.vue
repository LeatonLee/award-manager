<template>
  <el-container>
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="cloud-user-info" style="display: flex; align-items: center; padding: 20px; justify-content: center;">
        <div style="text-align: center;">
          <i class="el-icon-cloud" style="font-size: 36px; color: #5cadff;"></i>
          <p style="margin-top: 10px; color: #040404; font-size: 20px;">你好，<strong>{{ userName }}</strong></p>
        </div>
      </div>
      <el-menu :default-active="activeMenu" @select="handleMenuSelect">
        <el-menu-item index="1">提交获奖</el-menu-item>
        <el-menu-item index="2">历史记录</el-menu-item>
        <el-menu-item index="3">个人信息</el-menu-item>
        <el-menu-item index="4" class="logout-menu">退出登录</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体部分 -->
    <el-container>
      <el-header>
        <div class="header-container">
          <h1>获奖信息管理助手</h1>
        </div>
      </el-header>
      <el-main>
        <!-- 路由显示区域 -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { jwtDecode } from 'jwt-decode';

export default {
  data() {
    return {
      userName: '',
      activeMenu: '1',
      pageTitle: '提交获奖',
    };
  },
  mounted() {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const decoded = jwtDecode(token);
      if (decoded.name) {
        this.userName = decoded.name; // 恢复姓名获取逻辑
      }
    } catch (error) {
      console.error('Token 解码失败', error);
    }
  }
  this.updateMenuAndTitle(this.$router.currentRoute.path);

  this.$router.afterEach((to) => {
    this.updateMenuAndTitle(to.path);
  });
},
  methods: {
    handleMenuSelect(index) {
      const routeMap = {
        '1': '/user/submit',
        '2': '/user/history',
        '3': '/user/profile',
        '4': '/login',
      };
      if (index === '4') {
        this.logout();
        return;
      }
      const targetRoute = routeMap[index];
      if (this.$router.currentRoute.path !== targetRoute) {
        this.$router.push(targetRoute);
      }
    },
    updateActiveMenu(path) {
      if (path.includes('submit')) {
        this.activeMenu = '1';
        this.pageTitle = '提交获奖';
      } else if (path.includes('history')) {
        this.activeMenu = '2';
        this.pageTitle = '历史记录';
      } else if (path.includes('profile')) {
        this.activeMenu = '3';
        this.pageTitle = '个人信息';
      } else {
        this.activeMenu = '1';
        this.pageTitle = '提交获奖';
      }
    },
    logout() {
    this.$confirm('确定退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token');
      this.$router.push('/login');
      this.$message.success('退出登录成功');
    }).catch(() => {
      this.$message.info('已取消退出登录');
    });
  }
  },
};
</script>

<style scoped>
.el-container {
  background-color: #f5f7fa;
  height: 100vh;
}

.el-aside {
  background-color: #ffffff;
  padding: 20px;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.el-header {
  background-color: #409EFF;
  color: #ffffff;
  text-align: center;
  padding: 20px;
}

.header-container h1 {
  margin: 0;
  font-size: 24px;
}

.cloud-user-info {
  background: linear-gradient(135deg, #e0f7ff, #cceeff);
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.logout-menu {
  color: #606266;
}

.logout-menu:hover {
  color: #ffffff;
  background-color: #f56c6c !important;
}
</style>
