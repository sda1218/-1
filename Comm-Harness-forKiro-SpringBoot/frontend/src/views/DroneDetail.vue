<template>
  <main class="app-shell">
    <header class="page-header">
      <div>
        <h1>无人机详情</h1>
        <p class="muted">查看无人机档案和技术参数。</p>
      </div>
      <div class="header-actions">
        <router-link v-if="drone" class="btn btn-warning" :to="`/drones/${drone.id}/edit`">编辑</router-link>
        <button v-if="drone" class="btn btn-danger" type="button" @click="remove">删除</button>
        <router-link class="btn" to="/drones">返回列表</router-link>
      </div>
    </header>

    <div v-if="state.error" class="alert alert-error">{{ state.error }}</div>
    <div v-if="state.loading" class="panel state-box">正在加载...</div>

    <template v-else-if="drone">
      <div class="detail-grid">
        <section class="panel">
          <h2>基本信息</h2>
          <dl class="detail-list">
            <div>
              <dt>序列号</dt>
              <dd><strong>{{ drone.serialNumber }}</strong></dd>
            </div>
            <div>
              <dt>型号名称</dt>
              <dd>{{ drone.modelName }}</dd>
            </div>
            <div>
              <dt>制造商</dt>
              <dd>{{ drone.manufacturer }}</dd>
            </div>
            <div>
              <dt>购买日期</dt>
              <dd>{{ drone.purchaseDate }}</dd>
            </div>
            <div>
              <dt>状态</dt>
              <dd><span :class="statusClass(drone.status)">{{ drone.status }}</span></dd>
            </div>
          </dl>
        </section>

        <section class="panel">
          <h2>技术参数</h2>
          <dl class="detail-list">
            <div>
              <dt>最大飞行时间</dt>
              <dd><strong>{{ drone.maxFlightTime }}</strong> 分钟</dd>
            </div>
            <div>
              <dt>最大飞行距离</dt>
              <dd><strong>{{ formatNumber(drone.maxFlightDistance) }}</strong> 米</dd>
            </div>
            <div>
              <dt>重量</dt>
              <dd><strong>{{ formatNumber(drone.weight) }}</strong> 克</dd>
            </div>
          </dl>
        </section>
      </div>

      <section class="panel">
        <h2>备注信息</h2>
        <p v-if="drone.remarks" class="pre-line">{{ drone.remarks }}</p>
        <p v-else class="muted">暂无备注</p>
      </section>

      <section class="panel">
        <h2>时间信息</h2>
        <div class="time-grid">
          <p><strong>创建时间：</strong>{{ drone.createdAt || '暂无' }}</p>
          <p><strong>更新时间：</strong>{{ drone.updatedAt || '暂无' }}</p>
        </div>
      </section>
    </template>
  </main>
</template>

<script setup>
import { onMounted, ref, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { deleteDrone, getDrone } from '../api/drones';
import { statusClass } from '../utils/status';

const route = useRoute();
const router = useRouter();
const drone = ref(null);
const state = reactive({
  loading: false,
  error: ''
});

async function load() {
  state.loading = true;
  state.error = '';
  try {
    drone.value = await getDrone(route.params.id);
  } catch (err) {
    state.error = err.response?.data?.message || err.message || '查询详情失败';
  } finally {
    state.loading = false;
  }
}

async function remove() {
  const confirmed = window.confirm(`确定要删除无人机 "${drone.value.serialNumber}" 吗？\n此操作不可恢复！`);
  if (!confirmed) {
    return;
  }
  try {
    await deleteDrone(drone.value.id);
    window.alert('删除成功！');
    router.push('/drones');
  } catch (err) {
    window.alert(err.response?.data?.message || err.message || '删除失败');
  }
}

function formatNumber(value) {
  return value === null || value === undefined ? '' : new Intl.NumberFormat('zh-CN').format(value);
}

onMounted(load);
</script>
