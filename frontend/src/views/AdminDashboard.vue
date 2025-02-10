<template>
  <el-container>
    <el-aside width="200px">
      <!-- 顶部用户信息 -->
      <div class="cloud-user-info" style="display: flex; align-items: center; padding: 20px; justify-content: center;">
        <div style="text-align: center;">
          <!-- 云朵图标 -->
          <i class="el-icon-cloud" style="font-size: 36px; color: #5cadff;"></i>
          <p style="margin-top: 10px;color: #040404; font-size: 20px;">你好，<strong>{{ userName }}</strong></p>
        </div>
      </div>
      <el-menu :default-active="activeMenu">
        <el-menu-item index="1" @click="navigateToAwardInfo">获奖信息</el-menu-item>
        <el-menu-item index="2" @click="navigateToClassManagement">管理班级</el-menu-item>
        <el-menu-item index="3" @click="logout">退出登录</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h1>获 奖 信 息 管 理</h1>
        </div>
      </el-header>
      <el-main>
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
      className: "", 
      activeMenu: '1', // 默认选中的菜单项为“获奖信息”
      pageTitle: '获奖信息管理',
      awardList: [], // 班级所有学生的获奖信息列表
      awardForm: {
        awardName: '',
        awardDate: '',
        remarks: '',
        certificate: null
      },
      fileList: [],
      dialogVisible: false, // 控制新增获奖信息的弹窗
      dateRange: [], // 日期范围选择器
      userName: '', // 登录者姓名
    };
  },
  mounted() {
  // 从 localStorage 获取存储的 JWT Token
  const token = localStorage.getItem('token');
  
  if (token) {
    try {
      // 解析 JWT 获取用户名
      const decoded = jwtDecode(token);
      localStorage.setItem('className', decoded.className);  // 将班级名称存储到 localStorage
      console.log('解码后的 Token:', decoded); // 调试用
      if (decoded.name) {
        this.userName = decoded.name; // 获取用户名
        this.className = decoded.className; // 班级名称
      } else {
        console.error('Token 中未包含 name 字段');
      }
    } catch (error) {
      console.error('Token 解码失败', error);
    }
  } else {
    console.error('未找到 token');
  }
}
,
  methods: {
    navigateToClassManagement() {
    const className = localStorage.getItem('className');  // 获取班级信息
    if (className) {
      const currentRoute = this.$router.currentRoute.fullPath;
      const targetRoute = `/admin/class-management/${className}`;
      
      if (currentRoute !== targetRoute) {  // 只有当当前路由不等于目标路由时才跳转
        this.$router.push(targetRoute);
        this.activeMenu = '2'; // 设置为“班级管理”菜单高亮
        this.pageTitle = '班级成员管理'; // 设置标题为班级管理
      } else {
        this.$message.warning('您已经在班级管理页面');
      }
    } else {
      this.$message.error('无法获取班级信息');
    }
  },

  navigateToAwardInfo() {
    const currentRoute = this.$router.currentRoute.fullPath;
    if (currentRoute !== '/admin/award-info') {  // 只有当当前路由不等于目标路由时才跳转
      this.$router.push('/admin'); // 跳转到管理员页面，默认显示获奖信息
      this.activeMenu = '1'; // 设置为“获奖信息”菜单高亮
      this.pageTitle = '获奖信息管理'; // 设置标题为获奖信息管理
    } else {
      this.$message.warning('您已经在获奖信息页面');
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
}
};
</script>

<style scoped>
/* 整体容器的布局 */
.el-container {
  position: relative;
  background-color: #f5f7fa;
  padding: 20px;
  z-index: 10; 
  height: 100vh; /* 使用全屏高度 */
}

/* 侧边栏样式 */
.el-aside {
  background-color: #ffffff;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px 20px;
  border-radius: 10px;
  margin-top: 20px;
  width: 200px; /* 固定宽度 */
  min-height: 100%; /* 使侧边栏至少填满整个页面高度 */
}

/* 顶部标题样式 */
.el-header {
  background-color: #409EFF;
  color: #ffffff;
  padding: 0; /* 去掉内边距 */
  display: flex; /* 启用 flex 布局 */
  justify-content: center; /* 水平居中对齐 */
  align-items: center; /* 垂直居中对齐 */
  height: 100px; /* 设置固定的高度，确保标题居中 */
  border-radius: 20px;
}

/* 标题 h1 样式 */
.el-header h1 {
  font-size: 24px;
  margin: 0; /* 去掉默认的外边距 */
}


/* 用户信息 */
.cloud-user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #e0f7ff, #cceeff);
  border-radius: 10px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.cloud-user-info i {
  font-size: 48px;
  color: #5cadff;
}

.el-card {
  position: relative;
  overflow: visible;  /* 确保子元素不会被裁剪 */
  z-index: 10;
  height: auto;    
}


.cloud-user-info p {
  margin-top: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

/* 操作按钮样式 */
.el-button {
  margin-right: 10px;
}

/* 弹窗样式 */
.el-dialog {
  border-radius: 8px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
}

.el-dialog__header {
  background-color: #409EFF;
  color: white;
  padding: 15px 20px;
  font-size: 18px;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}



/* 表单样式 */
.el-form-item {
  margin-bottom: 20px;
}

.el-input,
.el-date-picker,
.el-upload {
  border-radius: 8px;
  background-color: #f9f9f9;
}

.el-button.primary {
  background-color: #409EFF;
  border-color: #409EFF;
  color: white;
}

.el-button.primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

/* 退出登录按钮悬停效果 */
.el-menu-item:last-child {
  transition: background-color 0.3s ease;
}

.el-menu-item:last-child:hover {
  background-color: #f56c6c;
  color: white;
}
</style>
