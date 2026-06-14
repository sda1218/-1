<template>
  <main class="app-shell">
    <header class="page-header">
      <div>
        <h1>无人机列表</h1>
        <p class="muted">管理无人机档案、状态和关键飞行参数。</p>
      </div>
      <router-link class="btn btn-primary" to="/drones/new">添加无人机</router-link>
    </header>

    <section class="panel">
      <h2>查询条件</h2>
      <form class="query-grid" @submit.prevent="search">
        <label>
          型号名称
          <input v-model.trim="query.modelName" type="text" placeholder="支持模糊查询" />
        </label>
        <label>
          制造商
          <input v-model.trim="query.manufacturer" type="text" placeholder="支持模糊查询" />
        </label>
        <label>
          状态
          <select v-model="query.status">
            <option value="">全部</option>
            <option v-for="item in statuses" :key="item.value" :value="item.value">
              {{ item.label }}
            </option>
          </select>
        </label>
        <div class="query-actions">
          <button class="btn btn-primary" type="submit">查询</button>
          <button class="btn" type="button" @click="reset">重置</button>
        </div>
      </form>
    </section>

    <section class="panel">
      <div class="table-toolbar">
        <h2>数据列表</h2>
        <span v-if="page.total > 0" class="muted">
          共 {{ page.total }} 条记录，第 {{ page.pageNum }} / {{ page.pages }} 页
        </span>
      </div>

      <div v-if="state.error" class="alert alert-error">{{ state.error }}</div>
      <div v-if="state.loading" class="state-box">正在加载...</div>

      <div v-else class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>序列号</th>
              <th>型号名称</th>
              <th>制造商</th>
              <th>购买日期</th>
              <th>状态</th>
              <th>飞行时间(分钟)</th>
              <th>飞行距离(米)</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="page.list.length === 0">
              <td colspan="9" class="empty-cell">暂无数据</td>
            </tr>
            <tr v-for="(drone, index) in page.list" :key="drone.id">
              <td>{{ rowNumber(index) }}</td>
              <td>{{ drone.serialNumber }}</td>
              <td>{{ drone.modelName }}</td>
              <td>{{ drone.manufacturer }}</td>
              <td>{{ drone.purchaseDate }}</td>
              <td><span :class="statusClass(drone.status)">{{ drone.status }}</span></td>
              <td>{{ drone.maxFlightTime }}</td>
              <td>{{ formatNumber(drone.maxFlightDistance) }}</td>
              <td>
                <div class="row-actions">
                  <router-link class="link-button" :to="`/drones/${drone.id}`">详情</router-link>
                  <router-link class="link-button warning" :to="`/drones/${drone.id}/edit`">编辑</router-link>
                  <button class="link-button danger" type="button" @click="remove(drone)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pager">
        <button class="btn" type="button" :disabled="state.loading || page.pageNum <= 1" @click="goPage(page.pageNum - 1)">
          上一页
        </button>
        <select v-model.number="query.pageSize" :disabled="state.loading" @change="changePageSize">
          <option :value="10">10 条/页</option>
          <option :value="20">20 条/页</option>
          <option :value="50">50 条/页</option>
        </select>
        <button class="btn" type="button" :disabled="state.loading || page.pageNum >= page.pages" @click="goPage(page.pageNum + 1)">
          下一页
        </button>
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, reactive } from 'vue';
import { deleteDrone, listDrones } from '../api/drones';
import { DRONE_STATUSES, statusClass } from '../utils/status';

const statuses = DRONE_STATUSES;
const query = reactive({
  modelName: '',
  manufacturer: '',
  status: '',
  pageNum: 1,
  pageSize: 20
});
const page = reactive({
  total: 0,
  pageNum: 1,
  pageSize: 20,
  pages: 0,
  list: []
});
const state = reactive({
  loading: false,
  error: ''
});

function requestParams() {
  return {
    modelName: query.modelName || undefined,
    manufacturer: query.manufacturer || undefined,
    status: query.status || undefined,
    pageNum: query.pageNum,
    pageSize: query.pageSize
  };
}

async function load() {
  state.loading = true;
  state.error = '';
  try {
    const result = await listDrones(requestParams());
    page.total = result.total || 0;
    page.pageNum = result.pageNum || query.pageNum;
    page.pageSize = result.pageSize || query.pageSize;
    page.pages = result.pages || 0;
    page.list = result.list || [];
  } catch (err) {
    state.error = err.response?.data?.message || err.message || '查询失败';
  } finally {
    state.loading = false;
  }
}

function search() {
  query.pageNum = 1;
  load();
}

function reset() {
  query.modelName = '';
  query.manufacturer = '';
  query.status = '';
  query.pageNum = 1;
  query.pageSize = 20;
  load();
}

function goPage(pageNum) {
  if (pageNum < 1 || (page.pages > 0 && pageNum > page.pages)) {
    return;
  }
  query.pageNum = pageNum;
  load();
}

function changePageSize() {
  query.pageNum = 1;
  load();
}

async function remove(drone) {
  const confirmed = window.confirm(`确定要删除无人机 "${drone.serialNumber}" 吗？\n此操作不可恢复！`);
  if (!confirmed) {
    return;
  }
  try {
    await deleteDrone(drone.id);
    window.alert('删除成功！');
    load();
  } catch (err) {
    window.alert(err.response?.data?.message || err.message || '删除失败');
  }
}

function rowNumber(index) {
  return (page.pageNum - 1) * page.pageSize + index + 1;
}

function formatNumber(value) {
  return value === null || value === undefined ? '' : new Intl.NumberFormat('zh-CN').format(value);
}

onMounted(load);
</script>
