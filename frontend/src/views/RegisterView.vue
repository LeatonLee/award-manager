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
        <!-- 用户角色下拉列表 -->
        <el-form-item>
          <el-select v-model="form.role" placeholder="请选择角色" :disabled="true" >
            <el-option label="普通用户" value="1"></el-option>
          </el-select>
        </el-form-item>
        <!-- 手机号和验证码输入框在一行内 -->
        <el-form-item>
          <el-row :gutter="10">
            <el-col :span="16">
              <el-input v-model="form.phone" placeholder="手机号" maxlength="11"></el-input>
            </el-col>
            <el-col :span="8">
              <el-button
                type="primary"
                :disabled="smsButtonDisabled"
                @click="sendSmsCode"
                class="sms-button"
              >
                {{ smsButtonText }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <!-- 短信验证码输入框 -->
        <el-form-item>
          <el-input v-model="form.smsCode" placeholder="输入短信验证码"></el-input>
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
        <!-- 确认密码输入框 -->
        <el-form-item>
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
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
import axios from 'axios';

export default {
  data() {
    return {
      form: {
        id: '',
        name: '',
        gradeClass: '',
        phone: '',
        smsCode: '',
        password: '',
        confirmPassword: '',
        role: '1', // 新增字段：用户角色
      },
      classList: [], // 班级信息
      smsButtonDisabled: false, // 验证码按钮禁用状态
      smsButtonText: "发送验证码", // 验证码按钮文字
    };
  },
  methods: {
    async fetchClassList() {
      try {
        const response = await axios.get('/api/classes');
        this.classList = response.data;
      } catch (error) {
        console.error('获取班级信息失败：', error);
      }
    },
    async sendSmsCode() {
      try {
        this.smsButtonDisabled = true;
        this.smsButtonText = "发送中...";
        await axios.post('/api/send-sms', { phone: this.form.phone });
        this.$message.success('验证码已发送');
        let count = 60;
        const interval = setInterval(() => {
          count -= 1;
          this.smsButtonText = `${count} 秒后重新发送`;
          if (count === 0) {
            clearInterval(interval);
            this.smsButtonDisabled = false;
            this.smsButtonText = "发送验证码";
          }
        }, 1000);
      } catch (error) {
        this.$message.error('发送验证码失败');
        this.smsButtonDisabled = false;
        this.smsButtonText = "发送验证码";
      }
    },
    // handleRegister 方法
async handleRegister() {
  if (!this.form.password || !this.form.confirmPassword) {
    this.$message.error("密码不能为空");
    return;
  }
  if (this.form.password !== this.form.confirmPassword) {
    this.$message.error("两次输入的密码不一致");
    return;
  }
  try {
    const response = await axios.post("/api/register", this.form);
    this.$message.success(response.data);
    this.$router.push("/login");
  } catch (error) {
    console.error("注册错误:", error.response ? error.response.data : error);
    this.$message.error("注册失败，请稍后重试");
  }
},
    goToLogin() {
      this.$router.push('/login');
    },
  },
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
  width: 400px; /* 根据需要调整宽度 */
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

.sms-button {
  width: 100%; /* 保证按钮宽度固定 */
  white-space: nowrap; /* 避免文字换行 */
  overflow: hidden; /* 超出隐藏 */
  text-overflow: ellipsis; /* 超出显示省略号 */
}
</style>
