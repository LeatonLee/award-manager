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
          <el-table-column prop="awardDate" label="获奖日期" align="center" width="200" :formatter="formatDate" />
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
          v-model="dialogVisible"
          width="50%"
          @closed="resetForm"
        >
          <el-form 
            :model="editForm"
            :rules="rules"
            ref="editFormRef"
            label-width="120px"
          >
            <el-form-item label="奖项名称" prop="awardName">
              <el-input v-model="editForm.awardName"></el-input>  
            </el-form-item>
            <el-form-item label="奖项级别" prop="awardLevel">
              <el-input v-model="editForm.awardLevel"></el-input>  
            </el-form-item>
            <el-form-item label="获奖日期" prop="awardDate">
              <el-date-picker 
                v-model="editForm.awardDate"  
                type="date"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              ></el-date-picker>
            </el-form-item>
            <el-form-item label="证书" prop="certificateUrl">
              <el-input v-model="editForm.certificateUrl"></el-input>  
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
  import { formatDate } from '@/utils/date'
  import axios from 'axios'
   
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
          certificateUrl: ''
        },
        rules: {
          awardName: [{ required: true, message: '请输入奖项名称', trigger: 'blur' }],
          awardLevel: [{ required: true, message: '请输入奖项级别', trigger: 'blur' }]
        },
        submitLoading: false 
      }
    },
    mounted() {
      this.fetchData()  
    },
    methods: {
      async fetchData() {
        try {
          const response = await axios.get('/api/award/user/list',   {
            params: {
              page: this.page,  
              pageSize: this.pageSize,  
              keyword: this.searchKeyword   
            },
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`  
            }
          })
   
          this.awardList   = response.data.data.rows   
          this.total   = response.data.data.total   
        } catch (error) {
          this.$message.error('  获取数据失败')
        }
      },
      formatDate(row, column, cellValue) {
        return formatDate(cellValue, 'YYYY-MM-DD')
      },
      handlePageChange(newPage) {
        this.page   = newPage 
        this.fetchData()  
      },
      handleDelete(id) {
        this.$confirm('确定删除该记录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          try {
            await axios.delete(`/api/award/delete/${id}`)  
            this.$message.success('  删除成功')
            this.fetchData()  
          } catch (error) {
            this.$message.error('  删除失败')
          }
        })
      },
      handleEdit(row) {
        // 使用深拷贝避免直接修改原对象 
        this.editForm  = {
          id: row.id,  
          awardName: row.awardName,  
          awardLevel: row.awardLevel,  
          awardDate: row.awardDate,  
          certificateUrl: row.certificateUrl   
        }
        this.dialogVisible  = true 
      },
      submitEdit() {
        this.$refs.editFormRef.validate(async   (valid) => {
          if (valid) {
            this.submitLoading  = true 
            try {
              await axios.put(`/api/award/update/${this.editForm.id}`,   this.editForm)  
              this.$message.success('  编辑成功')
              this.dialogVisible  = false 
              this.fetchData()  
            } catch (error) {
              this.$message.error('  编辑失败')
            } finally {
              this.submitLoading  = false 
            }
          }
        })
      },
      resetForm() {
        this.$refs.editFormRef.resetFields()  
        this.editForm  = {
          id: '',
          awardName: '',
          awardLevel: '',
          awardDate: '',
          certificateUrl: ''
        }
      },
      previewCertificate(url) {
        window.open(url,   '_blank')
      }
    }
  }
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
   
   
  .el-form-item {
    margin-bottom: 15px;
  }
   
  .dialog-footer {
    text-align: right;
  }
 
  /* AwardHistory.vue 的 <style> */
.el-overlay {
  z-index: 3000 !important;
}

.el-dialog {
  z-index: 3001 !important;
  position: relative;
}
  </style>