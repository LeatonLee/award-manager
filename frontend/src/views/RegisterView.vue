<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>
      <el-form :model="form" ref="formRegister" label-width="0" class="register-form">
        <!-- 学号输入框 -->
        <el-form-item>
          <el-input v-model="form.id" placeholder="学号"></el-input>
        </el-form-item>
        <!-- 姓名输入框 -->
        <el-form-item>
          <el-input v-model="form.name" placeholder="姓名"></el-input>
        </el-form-item>
        <!-- 班级下拉列表 -->
        <el-form-item>
          <el-select v-model="form.gradeClass" placeholder="请选择所在班级">
            <el-option
              v-for="classItem in classList"
              :key="classItem.id"
              :label="classItem.name"
              :value="classItem.name"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 手机号输入框 -->
        <el-form-item>
          <el-input v-model="form.phone" placeholder="手机号" maxlength="11"></el-input>
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
          ></el-input>
        </el-form-item>
        <!-- 按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button @click="goToLogin">已有账户？登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios'; // 使用 Axios 请求后端接口

export default {
  data() {
    return {
      form: {
        id: '', // 学号
        name: '', // 姓名
        gradeClass: '', // 班级
        phone: '', // 手机号
        password: '', // 密码
      },
      classList: [], // 班级列表
    };
  },
  methods: {
    // 获取班级列表
    async fetchClassList() {
      try {
        const response = await axios.get('/api/classes'); // 替换为你的后端接口地址
        this.classList = response.data; // 假设返回的是一个班级数组
      } catch (error) {
        console.error('获取班级信息失败：', error);
      }
    },
    // 注册处理
    async handleRegister() {
  try {
    const response = await axios.post('/api/register', this.form);
    this.$message.success(response.data);
    this.$router.push('/login');
  } catch (error) {
    console.error('注册错误:', error.response ? error.response.data : error);
    if (error.response && error.response.status === 400) {
      this.$message.error(error.response.data); // 学号或手机号已注册
    } else {
      this.$message.error('注册失败，请稍后重试');
    }
  }
},

    // 跳转到登录页面
    goToLogin() {
      this.$router.push('/login');
    },
  },
  // 在组件加载时获取班级列表
  created() {
    this.fetchClassList();
  },
};
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('https://ts1.cn.mm.bing.net/th/id/R-C.b2960351966d811ceb98b6d88a23afbc?rik=PeCZbptBqdcK6A&riu=http%3a%2f%2fxy.lnu.edu.cn%2f__local%2fB%2f29%2f60%2f351966D811CEB98B6D88A23AFBC_CBC40D54_33E69.jpg&ehk=aAw%2bliRAcpnFNNRRtkaX8kk3bZUEZ%2fo%2fw7pui58o2cE%3d&risl=&pid=ImgRaw&r=0s');
  background-size: cover;
  background-position: center;
}

.register-box {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 350px; /* 根据需要调整宽度 */
}

.register-form {
  width: 100%;
}

.el-form-item {
  margin-bottom: 20px; /* 增加间距，避免上下挤在一起 */
}

.el-input,
.el-select {
  width: 100%; /* 确保输入框和选择框一致宽度 */
}
</style>
