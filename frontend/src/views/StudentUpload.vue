<template>
  <el-container>
    <!-- 侧边栏 -->
    <el-aside width="200px">
      <div class="cloud-user-info" style="display: flex; align-items: center; padding: 20px; justify-content: center;">
        <div style="text-align: center;">
          <!-- 云朵图标 -->
          <i class="el-icon-cloud" style="font-size: 36px; color: #5cadff;"></i>
          <p style="margin-top: 10px;color: #040404; font-size: 20px;">你好，<strong>{{ userName }}</strong></p>
        </div>
      </div>
      <el-menu>
        <el-menu-item index="1">个人信息</el-menu-item>
        <el-menu-item index="2">获奖提交</el-menu-item>
        <el-menu-item index="3">历史获奖</el-menu-item>
        <el-menu-item index="4" @click="logout">退出登录</el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主体部分 -->
    <el-container>
      <el-header>
        <!-- 顶部标题 -->
        <div class="header-container">
          <h1>学生获奖管理助手</h1>
          <!-- 右上角问候语 -->
          <div class="greeting">
            <el-icon><i class="el-icon-user"></i></el-icon>
            <span>你好，{{ userName }}</span>
          </div>
        </div>
      </el-header>

      <el-main>
        <el-card>
          <div class="class-info">
            <el-row>
              <el-col :span="24">
                <h3>您所在的班级：<strong>{{ className }}</strong></h3>
              </el-col>
            </el-row>
          </div>
          <el-form :model="awardForm" ref="awardForm" label-width="120px" class="award-form">
            <el-form-item label="获奖名称" prop="awardName" :rules="[{ required: true, message: '请输入获奖名称', trigger: 'blur' }]">
              <el-input v-model="awardForm.awardName"></el-input>
            </el-form-item>
            <el-form-item label="获奖时间" prop="awardDate" :rules="[{ required: true, message: '请选择获奖时间', trigger: 'change' }]">
              <el-date-picker v-model="awardForm.awardDate" type="date" placeholder="选择获奖时间"></el-date-picker>
            </el-form-item>
            <el-form-item label="上传证书" prop="certificate" :rules="[{ required: true, message: '请上传获奖证书', trigger: 'change' }]">
              <el-upload
                class="upload-demo"
                drag
                action=""
                :on-change="handleFileChange"
                :file-list="fileList"
                :before-upload="beforeUpload"
                :show-file-list="true"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
              </el-upload>
            </el-form-item>
            <el-form-item label="备注" prop="remarks">
              <el-input type="textarea" v-model="awardForm.remarks" placeholder="可选"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitForm">提交</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { jwtDecode } from 'jwt-decode';
export default {
  data() {
    return {
      userName: '', // 存储用户姓名
      awardForm: {
        awardName: '',
        awardDate: '',
        remarks: '',
        certificate: null
      },
      fileList: []
    };
  },
  mounted() {
  // 从 localStorage 获取存储的 JWT Token
  const token = localStorage.getItem('token');
  
  if (token) {
    try {
      // 解析 JWT 获取用户名
      const decoded = jwtDecode(token);
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
},
  created() {
    this.fetchUserName(); // 初始化时获取用户姓名
  },
  methods: {
    // 模拟后端接口调用获取用户姓名
    async fetchUserName() {
      try {
        const response = await fetch('/api/user/info'); // 调用后端接口
        const data = await response.json();
        if (data.success) {
          this.userName = data.name; // 设置用户名
        } else {
          this.$message.error('获取用户信息失败');
        }
      } catch (error) {
        console.error('Error fetching user info:', error);
        this.$message.error('获取用户信息失败');
      }
    },
    handleFileChange(file, fileList) {
      this.fileList = fileList;
      this.awardForm.certificate = file.raw;
    },
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG) {
        this.$message.error('上传证书图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传证书图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },
    submitForm() {
      this.$refs.awardForm.validate((valid) => {
        if (valid) {
          console.log('表单数据:', this.awardForm);
          this.$message.success('提交成功！');
        } else {
          console.log('表单验证失败');
        }
      });
    },
    resetForm() {
      this.$refs.awardForm.resetFields();
      this.fileList = [];
    },
    logout() {
      localStorage.removeItem('token');
      this.$router.push('/login');
    }
  }
};
</script>

<style scoped>
/* 整体容器的布局 */
.el-container {
  background-color: #f5f7fa;
  padding: 20px;
}

/* 侧边栏样式 */
.el-aside {
  background-color: #ffffff;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px 20px;  /* 调整了 padding，稍微让侧边栏向下 */
  border-radius: 10px;
  margin-top: 20px;  /* 微调侧边栏位置 */
}

/* 顶部标题样式 */
.el-header {
  background-color: #409EFF;
  color: #ffffff;
  padding: 30px 30px;  /* 保持蓝框的 padding 不变 */
  border-radius: 20px;
  text-align: center;
  font-size: 24px;
  margin-bottom: 10px; /* 保持底部间距 */
}

.el-header h1 {
  position: relative;  /* 使用 relative 定位 */
  top: -45px; /* 向上移动标题 */
}

/* 卡片样式 */
.el-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

/* 班级信息显示区域 */
.class-info {
  margin-bottom: 30px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
  background-color: #f0f4f7;
  padding: 15px;
  border-radius: 8px;
}

/* 表格样式 */
.el-table {
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

/* 表格列标题 */
.el-table th {
  background-color: #f1f6fc;
  color: #333;
  font-weight: bold;
  text-align: center;
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

.user-info {
  font-size: 18px; /* 调大字体 */
  font-weight: bold; /* 加粗文字 */
  color: #ffffff; /* 白色字体 */
  background: linear-gradient(90deg, #409EFF, #66b1ff); /* 添加渐变背景 */
  padding: 10px 20px; /* 增加内边距 */
  border-radius: 20px; /* 圆角 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2); /* 添加阴影 */
  display: inline-block; /* 让背景适配内容大小 */
  text-align: center; /* 居中对齐 */
}
.cloud-user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #e0f7ff, #cceeff); /* 渐变背景 */
  border-radius: 10px; /* 圆角样式 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 轻微阴影 */
}

.cloud-user-info i {
  font-size: 48px; /* 云朵图标的大小 */
  color: #5cadff; /* 图标颜色 */
}

.cloud-user-info p {
  margin-top: 10px;
  font-size: 16px; /* 用户名字体大小 */
  font-weight: bold;
  color: #333; /* 文本颜色 */
}

/* 表单和输入框样式 */
.award-form {
  max-width: 600px;
  margin: 20px auto;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input, .el-date-picker, .el-upload {
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

.el-button.success {
  background-color: #67C23A;
  border-color: #67C23A;
  color: white;
}

.el-button.success:hover {
  background-color: #85D36B;
  border-color: #85D36B;
}

.el-button.danger {
  background-color: #f56c6c;
  border-color: #f56c6c;
  color: white;
}

.el-button.danger:hover {
  background-color: #f79c9c;
  border-color: #f79c9c;
}

.upload-demo {
  cursor: pointer;
}

.upload-demo i {
  font-size: 30px;
  color: #409EFF;
}

.el-upload__tip {
  font-size: 12px;
  color: #999;
}

.el-row {
  margin-bottom: 20px;
}

/* 使表单和表格间距更加和谐 */
.el-form-item {
  margin-bottom: 20px;
}

/* 退出登录按钮悬停效果 */
.el-menu-item:last-child {
  transition: background-color 0.3s ease;
}

.el-menu-item:last-child:hover {
  background-color: #f56c6c; /* 设置悬停时的背景颜色为红色 */
  color: white; /* 悬停时文字颜色为白色 */
}

</style>
