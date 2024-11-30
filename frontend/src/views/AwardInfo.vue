<template>
  <el-card>
    <!-- 班级标识 -->
    <div class="class-info">
      <el-row>
        <el-col :span="24">
          <h3>您管理的班级：<strong>{{ className }}</strong></h3>
        </el-col>
      </el-row>
    </div>

    <!-- 学生获奖信息列表 -->
    <el-table :data="awardList" stripe>
      <el-table-column prop="name" label="学生姓名" width="180"></el-table-column>
      <el-table-column prop="awardName" label="获奖名称" width="250"></el-table-column>
      <el-table-column prop="awardDate" label="获奖时间" width="180"></el-table-column>
      <el-table-column prop="remarks" label="备注" width="200"></el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button @click="editAward(scope.row)" type="primary" size="small">编辑</el-button>
          <el-button @click="deleteAward(scope.row.id)" type="danger" size="small">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量导出 -->
    <el-row>
      <el-col :span="12">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 100%;"
        />
      </el-col>
      <el-col :span="12">
        <el-button type="success" @click="downloadAwards">下载获奖信息</el-button>
      </el-col>
    </el-row>

    <!-- 添加获奖信息弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="新增获奖信息">
      <el-form :model="awardForm" ref="awardForm" label-width="120px">
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
          <el-button @click="closeDialog">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import { jwtDecode } from 'jwt-decode';
export default {
  data() {
    return {
      className: "", // 班级名称
      awardList: [], // 班级学生获奖信息列表
      awardForm: {
        awardName: '',
        awardDate: '',
        remarks: '',
        certificate: null,
      },
      fileList: [],
      dialogVisible: false, // 控制新增获奖信息的弹窗
      dateRange: [], // 日期范围选择器
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
  methods: {
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
          this.awardList.push({ ...this.awardForm, id: Date.now() });
          this.$message.success('提交成功！');
          this.closeDialog();
        }
      });
    },
    closeDialog() {
      this.dialogVisible = false;
      this.$refs.awardForm.resetFields();
    },
    editAward(award) {
      this.awardForm = { ...award };
      this.dialogVisible = true;
    },
    deleteAward(id) {
      this.awardList = this.awardList.filter((item) => item.id !== id);
      this.$message.success('删除成功！');
    },
    downloadAwards() {
      if (this.dateRange.length === 0) {
        this.$message.error('请选择日期范围');
        return;
      }
      const [startDate, endDate] = this.dateRange;
      console.log(`下载 ${startDate} 到 ${endDate} 的获奖信息`);
      this.$message.success('获奖信息下载成功！');
    },
  },
};
</script>

<style scoped>
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

/* 卡片样式 */
.el-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

/* 表格样式 */
.el-table {
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}
/* 侧边栏样式 */
.el-aside {
  background-color: #ffffff;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px 20px;
  border-radius: 10px;
  margin-top: 20px;
  width: 200px; /* 固定宽度 */
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

/* 表单和输入框样式 */
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
</style>
