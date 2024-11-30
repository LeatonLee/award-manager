<template>
  <el-card>
    <!-- 班级信息 -->
    <div class="class-info">
      <el-row>
        <el-col :span="24">
          <h3>您管理的班级：<strong>{{ className }}</strong></h3>
        </el-col>
      </el-row>
    </div>
 
    <!-- 简化版班级成员表格 -->
    <el-table :data="classMembers" stripe>
      <el-table-column prop="id" label="学号" width="180"></el-table-column>
      <el-table-column prop="name" label="成员姓名" width="180"></el-table-column>
      <el-table-column prop="gradeClass" label="班级" width="180"></el-table-column>
      <el-table-column prop="phone" label="电话" width="180"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="200"></el-table-column>
    </el-table>

    <!-- 添加成员按钮 -->
    <el-button type="primary" @click="openDialog">添加成员</el-button>

    <!-- 添加/编辑成员弹窗 -->
    <el-dialog :visible.sync="dialogVisible" title="成员信息">
      <el-form :model="memberForm" ref="memberForm" label-width="100px">
        <el-form-item label="姓名" prop="name" :rules="[{ required: true, message: '请输入姓名', trigger: 'blur' }]">
          <el-input v-model="memberForm.name"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role" :rules="[{ required: true, message: '请选择角色', trigger: 'change' }]">
          <el-select v-model="memberForm.role" placeholder="选择角色">
            <el-option label="学生" value="2"></el-option>
            <el-option label="教师" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitMember">提交</el-button>
          <el-button @click="closeDialog">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </el-card>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  props: ['className'], // 通过 props 接收动态参数
  data() {
    return {
      localClassName: this.className, // 将传入的 className 赋值给本地变量
      classMembers: [],  // 班级成员列表
      memberForm: {
        id: null,
        name: '',
        role: '',
      },
      dialogVisible: false, // 控制弹窗显示
      userName: '', // 登录者姓名
    };
  },
  mounted() {
    console.log(this.$route); // 打印当前路由信息
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        this.userName = decoded.name; // 获取用户名
        this.localClassName = decoded.className; // 获取班级名称
        // 获取班级成员数据
        this.fetchClassMembers();
      } catch (error) {
        console.error('Token 解码失败', error);
      }
    } else {
      console.error('未找到 token');
    }
  },
  methods: {
    // 获取班级成员数据
    fetchClassMembers() {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('Token 不存在');
        return;
      }
      axios.get(`/api/classes/${this.className}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      .then(response => {
        console.log('班级成员数据：', response.data);  // 打印返回的数据，确认数据结构
        if (Array.isArray(response.data)) {
          this.classMembers = response.data;  // 赋值给 classMembers
          this.$nextTick(() => {
            console.log('班级成员数据已更新：', this.classMembers);
          });
        } else {
          console.error('返回的数据不是数组');
        }
      })
      .catch(error => {
        console.error('获取班级成员失败', error);
      });
    },

    // 格式化日期函数
    formatDate(date) {
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`;
    },

    // 打开添加/编辑成员弹窗
    openDialog() {
      this.dialogVisible = true;
      this.memberForm = { id: null, name: '', role: '' }; // 重置表单
    },

    // 关闭弹窗
    closeDialog() {
      this.dialogVisible = false;
    },

    // 提交成员（添加/更新）
    submitMember() {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('Token 不存在');
        return;
      }

      if (this.memberForm.id) {
        // 更新成员
        axios.put(`/api/classes/members/${this.memberForm.id}`, this.memberForm)
          .then(() => {
            this.$message.success('成员更新成功');
            this.fetchClassMembers();
            this.closeDialog();
          })
          .catch(error => {
            console.error('更新成员失败', error);
          });
      } else {
        // 添加新成员
        axios.post(`/api/classes/${this.className}/members`, this.memberForm, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        })
          .then(() => {
            this.$message.success('成员添加成功');
            this.fetchClassMembers();
            this.closeDialog();
          })
          .catch(error => {
            console.error('添加成员失败', error);
          });
      }
    },

    // 编辑成员
    editMember(member) {
      this.memberForm = { ...member };
      this.dialogVisible = true;
    },

    // 删除成员
    deleteMember(id) {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('Token 不存在');
        return;
      }

      axios.delete(`/api/classes/members/${id}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        }
      })
        .then(() => {
          this.$message.success('成员删除成功');
          this.fetchClassMembers();
        })
        .catch(error => {
          console.error('删除成员失败', error);
        });
    }
  }
};
</script>

<style scoped>
/* 表格列标题 */
.el-table th {
  background-color: #409EFF;
  color: white;
  font-weight: bold;
  text-align: center;
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
.class-info {
    margin-bottom: 30px;
    font-size: 18px;
    font-weight: bold;
    color: #333;
    background-color: #f0f4f7;
    padding: 15px;
    border-radius: 8px;
  }

/* 操作按钮样式 */
.el-button {
  margin: 5px 0; /* 竖直和水平间距 */
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

/* 修正布局冲突样式 */
.el-card {
  background-color: white;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.el-table {
  border-radius: 8px;
  margin-top: 20px;
  margin-bottom: 20px;
  width: 100%;  /*确保表格占满父容器宽度 */
  overflow-y: auto;
  z-index: 9999;
  position: relative;  /* 确保表格是可定位的 */
}

/* 使表单和表格间距更加和谐 */
.el-form-item {
  margin-bottom: 20px;
}
</style>
