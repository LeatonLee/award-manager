<template>
  <div class="history-container">
    <el-card class="content-card">
      <!-- 搜索和过滤区域 -->
      <div class="filter-container">
        <el-input 
          v-model="searchKeyword"
          placeholder="搜索奖项名称"
          style="width: 300px; margin-right: 20px"
          @keyup.enter="fetchData"  
        />
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table 
        :data="awardList"
        stripe 
        header-align="center"
        align="center"
      >
        <el-table-column prop="awardName" label="奖项名称" align="center" width="300" />
        <el-table-column prop="awardLevel" label="奖项级别" align="center" width="150" />
        <el-table-column prop="awardDate" label="获奖日期" align="center" width="220" :formatter="formatDate" />
        <el-table-column prop="certificateUrl" label="证书" align="center" width="200">
          <template #default="scope">
            <img 
              v-if="scope.row.certificateUrl"  
              :src="scope.row.certificateUrl"  
              alt="证书"
              style="max-width: 100px; max-height: 100px; cursor: zoom-in;"
              @click="previewCertificate(scope.row.certificateUrl)"  
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button 
              type="primary"
              size="mini"
              @click="handleEdit(scope.row)"  
            >
              编辑 
            </el-button>
            <el-button 
              type="danger"
              size="mini"
              @click="handleDelete(scope.row.id)"  
            >
              删除 
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination 
        style="margin-top: 20px"
        background 
        layout="total, prev, pager, next, jumper"
        :total="total"
        :page-size="pageSize"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog 
      title="编辑奖项信息"
      :visible.sync="dialogVisible"
      width="50%"
      @closed="resetForm"
      :modal="false"
      custom-class="custom-dialog"
    >
      <el-form 
        :model="editForm"
        :rules="rules"
        ref="editFormRef"
        label-width="120px"
        class="custom-form"
      >
        <el-form-item label="奖项名称" prop="awardName">
          <el-input v-model="editForm.awardName" placeholder="请输入奖项名称"></el-input>  
        </el-form-item>
        <el-form-item label="奖项级别" prop="awardLevel">
          <el-input v-model="editForm.awardLevel" placeholder="请输入奖项级别"></el-input>  
        </el-form-item>
        <el-form-item label="获奖日期" prop="awardDate">
          <el-date-picker 
            v-model="editForm.awardDate"  
            type="date"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            placeholder="请选择获奖日期"
            style="width: 100%;"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="证书" prop="certificateUrl">
          <div v-if="editForm.certificateUrl" class="certificate-preview">
            <img 
              :src="editForm.certificateUrl"  
              alt="证书预览"
              class="preview-image"
            />
            <span class="delete-icon" @click="removeCertificate">
              <i class="el-icon-close"></i>
            </span>
          </div>
          <el-upload
            v-else
            action=""
            :auto-upload="false"
            :on-change="handleCertificateChange"
            :show-file-list="false"
            accept="image/*"
            drag
            class="upload-area"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button 
            type="primary"
            @click="submitEdit"
            :loading="submitLoading"
          >
            确 定 
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date';
import axios from 'axios';

export default {
  data() {
    return {
      awardList: [],
      searchKeyword: '',
      total: 0,
      page: 1,
      pageSize: 10,
      dialogVisible: false,
      editForm: {
        id: '',
        awardName: '',
        awardLevel: '',
        awardDate: '',
        certificateUrl: '', // 存储证书图片的 URL
        certificateFile: null // 存储上传的文件对象
      },
      rules: {
        awardName: [{ required: true, message: '请输入奖项名称', trigger: 'blur' }],
        awardLevel: [{ required: true, message: '请输入奖项级别', trigger: 'blur' }]
      },
      submitLoading: false
    };
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    async fetchData() {
      try {
        const response = await axios.get('/api/award/user/list', {
          params: {
            page: this.page,
            pageSize: this.pageSize,
            keyword: this.searchKeyword
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`
          }
        });

        this.awardList = response.data.data.rows;
        this.total = response.data.data.total;
      } catch (error) {
        this.$message.error('获取数据失败');
      }
    },
    formatDate(row, column, cellValue) {
      return formatDate(cellValue, 'YYYY-MM-DD');
    },
    handlePageChange(newPage) {
      this.page = newPage;
      this.fetchData();
    },
    handleDelete(id) {
      this.$confirm('确定删除该记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await axios.delete(`/api/award/delete/${id}`);
          this.$message.success('删除成功');
          this.fetchData();
        } catch (error) {
          this.$message.error('删除失败');
        }
      });
    },
    handleEdit(row) {
      this.editForm = {
        id: row.id,
        awardName: row.awardName,
        awardLevel: row.awardLevel,
        awardDate: row.awardDate,
        certificateUrl: row.certificateUrl,
        certificateFile: null
      };
      this.dialogVisible = true;
    },
    handleCertificateChange(file) {
      const isImage = file.raw.type.startsWith('image/');
      if (!isImage) {
        this.$message.error('只能上传图片文件');
        return;
      }

      // 预览图片
      const reader = new FileReader();
      reader.onload = (e) => {
        this.editForm.certificateUrl = e.target.result; // 设置预览 URL
      };
      reader.readAsDataURL(file.raw);

      // 保存文件对象
      this.editForm.certificateFile = file.raw;
    },
    removeCertificate() {
      this.editForm.certificateUrl = '';
      this.editForm.certificateFile = null;
    },
    async submitEdit() {
      this.$refs.editFormRef.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true;

          try {
            const formData = new FormData();
            formData.append('id', this.editForm.id);
            formData.append('awardName', this.editForm.awardName);
            formData.append('awardLevel', this.editForm.awardLevel);
            formData.append('awardDate', this.editForm.awardDate);
            if (this.editForm.certificateFile) {
              formData.append('certificate', this.editForm.certificateFile); // 上传新图片
            }
            const token = localStorage.getItem('token'); // 假设 token 存储在 localStorage 中
        await axios.put(`/api/award/update/${this.editForm.id}`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${token}` // 确保格式为 Bearer <token>
          }
        });

            this.$message.success('编辑成功');
            this.dialogVisible = false;
            this.fetchData();
          } catch (error) {
            this.$message.error('编辑失败');
          } finally {
            this.submitLoading = false;
          }
        }
      });
    },
    resetForm() {
      this.$refs.editFormRef.resetFields();
      this.editForm = {
        id: '',
        awardName: '',
        awardLevel: '',
        awardDate: '',
        certificateUrl: '',
        certificateFile: null
      };
    },
    previewCertificate(url) {
      window.open(url, '_blank');
    }
  }
};
</script>

<style scoped>
.content-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.filter-container {
  margin-bottom: 20px;
}

.el-table {
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.custom-dialog {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.custom-form {
  padding: 20px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}

.certificate-preview {
  position: relative;
  display: inline-block;
  margin-bottom: 10px;
}

.preview-image {
  max-width: 150px;
  max-height: 150px;
  border-radius: 8px;
  object-fit: cover;
}

.delete-icon {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: red;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.upload-area {
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
}

.el-upload__text {
  color: #606266;
  font-size: 14px;
}

.el-upload__text em {
  color: #409eff;
  font-style: normal;
}
</style>