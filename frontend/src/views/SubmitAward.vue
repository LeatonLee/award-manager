<template>
  <el-card>
    <el-form
      :model="awardForm"
      ref="awardForm"
      label-width="100px"
      class="award-form"
      style="max-width: 600px; margin: 0 auto;"
    >
      <el-form-item label="获奖名称" prop="awardName" :rules="[{ required: true, message: '请输入获奖名称', trigger: 'blur' }]">
        <el-input v-model="awardForm.awardName" size="small"></el-input>
      </el-form-item>

      <el-form-item label="获奖时间" prop="awardDate" :rules="[{ required: true, message: '请选择获奖时间', trigger: 'change' }]">
        <el-date-picker
          v-model="awardForm.awardDate"
          type="date"
          placeholder="选择获奖时间"
          size="small"
          style="width: 100%;"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="奖项级别" prop="awardLevel" :rules="[{ required: true, message: '请输入奖项级别', trigger: 'blur' }]">
        <el-input v-model="awardForm.awardLevel" size="small"></el-input>
      </el-form-item>

      <el-form-item label="上传证书" prop="certificateUrl" :rules="[{ required: true, message: '请上传获奖证书', trigger: 'change' }]">
        <el-upload
          class="upload-demo"
          drag
          :action="uploadAction"
          :auto-upload="false"
          :on-change="handleFileChange"
          :file-list="fileList"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        </el-upload>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm" size="small">提交</el-button>
        <el-button @click="resetForm" size="small">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      awardForm: {
        awardName: '',
        awardLevel: '',
        awardDate: '',
        certificateUrl: '', // 添加证书 URL 字段
      },
      fileList: [],
      uploadAction: '/api/award/upload', // 文件上传接口地址
    };
  },
  methods: {
    handleFileChange(file, fileList) {
      this.fileList = fileList.slice(-1); // 只保留最新文件
    },

    submitForm() {
      if (this.fileList.length === 0) {
        this.$message.error('请上传获奖证书');
        return;
      }

      const file = this.fileList[0].raw; // 获取原始文件
      this.uploadFile(file); // 上传文件
    },

    uploadFile(file) {
      const formData = new FormData();
      formData.append('file', file); // 添加文件

      const token = localStorage.getItem('token'); // 从 localStorage 获取 token

      if (!token) {
        this.$message.error('用户未登录或Token已过期');
        return;
      }

      axios.post(this.uploadAction, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${token}`,
        },
      })
      .then(response => {
        this.awardForm.certificateUrl = response.data; // 保存文件 URL
        this.submitAwardInfo(); // 上传成功后，提交奖项信息
      })
      .catch(error => {
        console.error('上传失败:', error.response || error);
        this.$message.error('证书上传失败');
      });
    },

    submitAwardInfo() {
      const token = localStorage.getItem('token'); // 从 localStorage 获取 token

      if (!token) {
        this.$message.error('用户未登录或Token已过期');
        return;
      }

      axios.post('/api/award/add', this.awardForm, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
      })
      .then(response => {
        if (response.data === '奖项添加成功') {
          this.$message.success('奖项添加成功');
        } else {
          this.$message.error(response.data || '奖项添加失败');
        }
      })
      .catch(error => {
        console.error('提交失败:', error);
        this.$message.error('请求失败，请重试');
      });
    },

    resetForm() {
      this.$refs.awardForm.resetFields();
      this.fileList = []; // 重置文件列表
    },
  },
};
</script>

<style scoped>
.award-form .el-date-picker {
  width: 100%;
}
</style>
