<template>
  <el-card>
    <div class="search-bar">
      <!-- 姓名模糊搜索 -->
      <el-input 
        v-model="searchParams.name" 
        placeholder="按姓名搜索" 
        clearable 
        style="width: 200px; margin-right: 10px;"
      />
      
      <!-- 学号模糊搜索 -->
      <el-input 
        v-model="searchParams.id" 
        placeholder="按学号搜索" 
        clearable 
        style="width: 200px; margin-right: 10px;"
      />
      
      <!-- 排序 -->
      <el-select v-model="searchParams.sortByAwards" placeholder="按获奖数量排序" style="width: 200px;">
        <el-option label="升序" value="0"></el-option>
        <el-option label="降序" value="1"></el-option>
      </el-select>

      <el-button type="primary" @click="searchMembers">搜索</el-button>
      <el-button @click="clearSearch">清除</el-button>
    </div>

    <!-- 班级信息 -->
    <div class="class-info">
      <el-row>
        <el-col :span="24">
          <h3>您管理的班级：<strong>{{ localClassName }}</strong></h3>
        </el-col>
      </el-row>
    </div>

    <!-- 简化版班级成员表格 -->
    <el-table :data="classMembers" stripe>
      <el-table-column prop="id" label="学号" width="180"></el-table-column>
      <el-table-column prop="name" label="成员姓名" width="180"></el-table-column>
      <el-table-column prop="gradeClass" label="班级" width="180"></el-table-column>
      <el-table-column prop="phone" label="电话" width="180"></el-table-column>
      <el-table-column prop="awardCount" label="获奖总数" width="180"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="200"></el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :total="totalMembers"
      :page-size="pageSize"
      :current-page.sync="currentPage"
      layout="total, prev, pager, next, jumper"
      @current-change="handlePageChange"
    />

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
      totalMembers: 0,   // 总成员数量
      pageSize: 10,      // 每页显示的成员数量
      currentPage: 1,    // 当前页码
      memberForm: {
        id: null,
        name: '',
        role: '',
      },
      dialogVisible: false, // 控制弹窗显示
      userName: '', // 登录者姓名
      searchParams: {
        name: '',
        id: '',
        sortByAwards: '' // 排序条件：升序或降序
      },
    };
  },
  mounted() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        this.userName = decoded.name; // 获取用户名
        this.localClassName = decoded.className || this.className; // 获取班级名称
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
    // 搜索成员
    searchMembers() {
      this.fetchClassMembers(1); // 调用获取成员的方法并重新加载第一页数据
    },
    // 获取班级成员数据
    fetchClassMembers(page = this.currentPage) {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('Token 不存在');
        return;
      }

      axios.get(`/api/classes/${this.localClassName}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          page: page,                   // 当前页码
          pageSize: this.pageSize,      // 每页显示的条数
          name: this.searchParams.name, // 姓名过滤
          id: this.searchParams.id,     // 学号过滤
          sortByAwards: this.searchParams.sortByAwards, // 按获奖数量排序
        }
      })
      .then(response => {
        console.log('班级成员数据：', response.data);  // 打印返回的数据，确认数据结构
        if (response.data.code === 1 && response.data.data) {
          const { total, rows } = response.data.data; 
          this.classMembers = rows;          // 成员数据
          this.totalMembers = total;         // 总成员数量
          this.pageSize = 10;                // 每页大小
          this.currentPage = page;           // 当前页码
        } else {
          console.error('返回的数据结构错误');
        }
      })
      .catch(error => {
        console.error('获取班级成员失败', error);
      });
    },
    // 翻页
    handlePageChange(page) {
      this.fetchClassMembers(page);  // 获取新一页数据
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
    // 清除所有搜索条件
    clearSearch() {
      this.searchParams = {
        name: '',
        id: '',
        sortByAwards: ''
      };
      this.fetchClassMembers(1); // 清除后重新加载第一页数据
    }
  }
}
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
.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-bar .el-input,
.search-bar .el-select {
  width: 200px;
}

.search-bar .el-button {
  margin-left: 10px;
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
