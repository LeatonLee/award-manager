<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录</h2>
      <el-form :model="form" ref="form" label-width="0" class="login-form">
        <el-form-item>
          <el-radio-group v-model="form.role">
            <el-radio label="user">用户</el-radio>
            <el-radio label="admin">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.id"
            placeholder="学号"
            prefix-icon="el-icon-user"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>

        <!-- 验证码输入框 -->
        <el-form-item>
          <el-row :gutter="20">  <!-- 使用:绑定数字类型的gutter -->
            <el-col :span="12">
              <el-input
                v-model="form.verificationCode"
                placeholder="验证码"
                prefix-icon="el-icon-check"
              ></el-input>
            </el-col>
            <el-col :span="12">
              <img
                :src="captchaImageUrl"
                alt="验证码"
                @click="refreshCaptcha"
                style="cursor: pointer; width: 100%; height: 40px; object-fit: contain;"
              />
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </el-form-item>
        <el-form-item v-if="errorMessage">
          <el-alert
            title="错误"
            type="error"
            :closable="false"
            :style="{ marginTop: '10px' }"
          >
            {{ errorMessage }}
          </el-alert>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { decodeJwt } from '@/utils/auth.js'; // 导入解码函数

export default {
  data() {
    return {
      form: {
        role: 'user', // 默认角色是用户
        id: '',  // 学号
        password: '', // 密码
        verificationCode: '', // 验证码
      },
      errorMessage: '', // 错误信息
      captchaImageUrl: 'http://localhost:7777/api/captcha', // 验证码图片 URL
    };
  },
  methods: {
    async handleLogin() {
      this.errorMessage = ''; // 清空之前的错误信息

      try {
        // 发送登录请求前，验证验证码
        const response = await axios.post('http://localhost:7777/api/login', {
          id: this.form.id,
          password: this.form.password,
          verificationCode: this.form.verificationCode, // 传递验证码
        },{
    withCredentials: true  // 确保带上 Session Cookie
});

        console.log(response); // 调试输出

        if (response.data.code === 1) { // 后端返回的是 Result 类，code = 1 表示成功
          const token = response.data.data; // 获取token
          localStorage.setItem('token', token); // 存储 token

          const user = decodeJwt(token);
          if (this.form.role === 'user' && user.role === '1') {
            this.$router.push('/user'); // 跳转到用户页面
          } else if (this.form.role === 'admin' && user.role === '2') {
            this.$router.push('/admin'); // 跳转到管理员页面
          } else {
            this.errorMessage = '登录的账号与所选角色不匹配';
          }
        } else {
          this.errorMessage = response.data.msg || '登录失败';
        }
      } catch (error) {
        console.error(error); // 输出详细错误信息
        this.errorMessage = '登录请求失败';
      }
    },

    goToRegister() {
      this.$router.push('/register'); // 跳转到注册页面
    },
    
    // 页面加载时生成验证码
    created() {
      this.refreshCaptcha();
    },
    
    // 刷新验证码
    refreshCaptcha() {
      this.captchaImageUrl = 'http://localhost:7777/api/captcha?' + new Date().getTime();
    },
  },
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('https://ts1.cn.mm.bing.net/th/id/R-C.b2960351966d811ceb98b6d88a23afbc?rik=PeCZbptBqdcK6A&riu=http%3a%2f%2fxy.lnu.edu.cn%2f__local%2fB%2f29%2f60%2f351966D811CEB98B6D88A23AFBC_CBC40D54_33E69.jpg&ehk=aAw%2bliRAcpnFNNRRtkaX8kk3bZUEZ%2fo%2fw7pui58o2cE%3d&risl=&pid=ImgRaw&r=0');
  background-size: cover;
  background-position: center;
}

.login-box {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  width: 300px;
}

.login-form {
  width: 100%;
}
</style>
