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
    <el-table 
      :data="awardList" 
      stripe
      header-align="center"
      align="center"
    >
      <el-table-column prop="name" label="学生姓名" align="center"></el-table-column>
      <el-table-column prop="awardName" label="获奖名称" align="center"></el-table-column>
      <el-table-column prop="awardDate" label="获奖时间" align="center"></el-table-column>
      <el-table-column label="证书" align="center">
        <template slot-scope="scope">
          <el-image
            style="width: 100px; height: 100px"
            :src="scope.row.certificateUrl"
            :preview-src-list="[scope.row.certificateUrl]"
            fit="cover"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <div style="display: flex; justify-content: center">
            <el-button 
              type="danger" 
              size="small"
              @click="deleteAward(scope.row.id)"
            >删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :total="total"
      :page-size="pageSize"
      :current-page.sync="currentPage"
      layout="total, prev, pager, next, jumper"
      @current-change="fetchAwardList"
    />
  </el-card>
</template>

<script>
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export default {
  data() {
    return {
      className: "", // 班级名称
      awardList: [], // 获奖信息列表
      total: 0, // 总条数
      pageSize: 10, // 每页条数
      currentPage: 1, // 当前页码
    };
  },
  mounted() {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        this.className = decoded.className; // 获取班级名称
        this.fetchAwardList(); // 加载获奖信息
      } catch (error) {
        console.error('Token 解码失败', error);
      }
    } else {
      console.error('未找到 token');
    }
  },
  methods: {
    deleteAward(id) {
  this.$confirm('确定删除该获奖记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/award/delete/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    }).then(() => {
      this.$message.success('删除成功');
      this.fetchAwardList(); // 刷新列表
    }).catch(() => { // 移除了 error 参数
      this.$message.error('删除失败');
    });
  });
},
  
fetchAwardList() {
  axios.get('/api/award/list', {
    params: {
      className: this.className,
      page: this.currentPage,      // 确保传递当前页码
      pageSize: this.pageSize      // 传递每页条数
    },
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  }).then(response => {
    this.awardList = response.data.data.rows;
    this.total = response.data.data.total;  // 关键！设置总条数
  }).catch(error => {
    console.error('获取数据失败', error);
  });
}}
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

/* 分页居中 */
.el-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>