<!-- src/views/NotFound.vue -->
<template>
    <div class="not-found">
      <h1>404 - 页面未找到</h1>
      <p>您访问的页面不存在，请检查链接或返回首页。</p>
      <router-link to="/">返回首页</router-link>
    </div>
  </template>
  
  <script>
  export default {
    name: 'NotFound',
  };
  </script>
  
  <style scoped>
  .not-found {
    text-align: center;
    margin-top: 50px;
  }
  </style>
  