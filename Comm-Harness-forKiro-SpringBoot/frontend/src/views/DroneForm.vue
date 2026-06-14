<template>
  <main class="app-shell">
    <header class="page-header">
      <div>
        <h1>{{ isEdit ? '编辑无人机' : '添加无人机' }}</h1>
        <p class="muted">{{ isEdit ? '修改现有无人机档案。' : '录入新的无人机档案。' }}</p>
      </div>
      <router-link class="btn" to="/drones">返回列表</router-link>
    </header>

    <div v-if="state.error" class="alert alert-error">{{ state.error }}</div>
    <div v-if="state.loading" class="panel state-box">正在加载...</div>

    <form v-else class="form-stack" @submit.prevent="submit">
      <section class="panel">
        <h2>基本信息</h2>
        <div class="form-grid">
          <label :class="{ invalid: errors.serialNumber }">
            序列号
            <input v-model.trim="form.serialNumber" type="text" maxlength="100" placeholder="请输入无人机序列号" />
            <span v-if="errors.serialNumber" class="field-error">{{ errors.serialNumber }}</span>
          </label>

          <label :class="{ invalid: errors.modelName }">
            型号名称
            <input v-model.trim="form.modelName" type="text" maxlength="100" placeholder="例如：DJI Mavic 3" />
            <span v-if="errors.modelName" class="field-error">{{ errors.modelName }}</span>
          </label>

          <label :class="{ invalid: errors.manufacturer }">
            制造商
            <input v-model.trim="form.manufacturer" type="text" maxlength="100" placeholder="例如：DJI、Parrot" />
            <span v-if="errors.manufacturer" class="field-error">{{ errors.manufacturer }}</span>
          </label>

          <label :class="{ invalid: errors.purchaseDate }">
            购买日期
            <input v-model="form.purchaseDate" type="date" />
            <span v-if="errors.purchaseDate" class="field-error">{{ errors.purchaseDate }}</span>
          </label>

          <label :class="{ invalid: errors.status }">
            状态
            <select v-model="form.status">
              <option value="">请选择状态</option>
              <option v-for="item in statuses" :key="item.value" :value="item.value">
                {{ item.label }}
              </option>
            </select>
            <span v-if="errors.status" class="field-error">{{ errors.status }}</span>
          </label>
        </div>
      </section>

      <section class="panel">
        <h2>技术参数</h2>
        <div class="form-grid">
          <label :class="{ invalid: errors.maxFlightTime }">
            最大飞行时间（分钟）
            <input v-model.number="form.maxFlightTime" type="number" min="1" max="999" placeholder="请输入最大飞行时间" />
            <span v-if="errors.maxFlightTime" class="field-error">{{ errors.maxFlightTime }}</span>
          </label>

          <label :class="{ invalid: errors.maxFlightDistance }">
            最大飞行距离（米）
            <input v-model.number="form.maxFlightDistance" type="number" min="1" max="999999" placeholder="请输入最大飞行距离" />
            <span v-if="errors.maxFlightDistance" class="field-error">{{ errors.maxFlightDistance }}</span>
          </label>

          <label :class="{ invalid: errors.weight }">
            重量（克）
            <input v-model.number="form.weight" type="number" min="1" max="999999" placeholder="请输入无人机重量" />
            <span v-if="errors.weight" class="field-error">{{ errors.weight }}</span>
          </label>
        </div>
      </section>

      <section class="panel">
        <h2>备注信息</h2>
        <label :class="{ invalid: errors.remarks }">
          备注
          <textarea v-model="form.remarks" rows="4" maxlength="500" placeholder="请输入备注信息（可选）"></textarea>
          <span class="muted">{{ remarksCount }} / 500 字符</span>
          <span v-if="errors.remarks" class="field-error">{{ errors.remarks }}</span>
        </label>
      </section>

      <div class="form-actions">
        <button class="btn btn-primary" type="submit" :disabled="state.saving">
          {{ state.saving ? '保存中...' : (isEdit ? '保存修改' : '创建无人机') }}
        </button>
        <router-link class="btn" to="/drones">取消</router-link>
        <button class="btn btn-warning" type="button" @click="resetForm">重置</button>
      </div>
    </form>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createDrone, getDrone, updateDrone } from '../api/drones';
import { DRONE_STATUSES, toStatusCode } from '../utils/status';

const route = useRoute();
const router = useRouter();
const statuses = DRONE_STATUSES;
const isEdit = computed(() => Boolean(route.params.id));
const state = reactive({
  loading: false,
  saving: false,
  error: ''
});
const form = reactive(emptyForm());
const original = reactive(emptyForm());
const errors = reactive({});
const remarksCount = computed(() => form.remarks.length);

function emptyForm() {
  return {
    serialNumber: '',
    modelName: '',
    manufacturer: '',
    purchaseDate: '',
    status: '',
    maxFlightTime: null,
    maxFlightDistance: null,
    weight: null,
    remarks: ''
  };
}

function assignForm(target, source) {
  Object.assign(target, {
    serialNumber: source.serialNumber || '',
    modelName: source.modelName || '',
    manufacturer: source.manufacturer || '',
    purchaseDate: source.purchaseDate || '',
    status: toStatusCode(source.status),
    maxFlightTime: source.maxFlightTime ?? null,
    maxFlightDistance: source.maxFlightDistance ?? null,
    weight: source.weight ?? null,
    remarks: source.remarks || ''
  });
}

async function load() {
  if (!isEdit.value) {
    assignForm(original, emptyForm());
    return;
  }
  state.loading = true;
  state.error = '';
  try {
    const drone = await getDrone(route.params.id);
    assignForm(form, drone);
    assignForm(original, drone);
  } catch (err) {
    state.error = err.response?.data?.message || err.message || '加载无人机失败';
  } finally {
    state.loading = false;
  }
}

function resetErrors() {
  Object.keys(errors).forEach((key) => {
    delete errors[key];
  });
}

function validate() {
  resetErrors();
  if (!form.serialNumber) {
    errors.serialNumber = '序列号不能为空';
  } else if (form.serialNumber.length > 100) {
    errors.serialNumber = '序列号长度不能超过100个字符';
  }
  if (!form.modelName) {
    errors.modelName = '型号名称不能为空';
  } else if (form.modelName.length > 100) {
    errors.modelName = '型号名称长度不能超过100个字符';
  }
  if (!form.manufacturer) {
    errors.manufacturer = '制造商不能为空';
  } else if (form.manufacturer.length > 100) {
    errors.manufacturer = '制造商长度不能超过100个字符';
  }
  if (!form.purchaseDate) {
    errors.purchaseDate = '购买日期不能为空';
  }
  if (!form.status) {
    errors.status = '请选择状态';
  }
  if (!form.maxFlightTime || Number(form.maxFlightTime) < 1) {
    errors.maxFlightTime = '最大飞行时间必须大于0';
  }
  if (!form.maxFlightDistance || Number(form.maxFlightDistance) < 1) {
    errors.maxFlightDistance = '最大飞行距离必须大于0';
  }
  if (!form.weight || Number(form.weight) < 1) {
    errors.weight = '重量必须大于0';
  }
  if (form.remarks.length > 500) {
    errors.remarks = '备注长度不能超过500个字符';
  }
  return Object.keys(errors).length === 0;
}

function payload() {
  return {
    serialNumber: form.serialNumber,
    modelName: form.modelName,
    manufacturer: form.manufacturer,
    purchaseDate: form.purchaseDate,
    status: form.status,
    maxFlightTime: Number(form.maxFlightTime),
    maxFlightDistance: Number(form.maxFlightDistance),
    weight: Number(form.weight),
    remarks: form.remarks
  };
}

async function submit() {
  if (!validate()) {
    return;
  }
  state.saving = true;
  state.error = '';
  try {
    const saved = isEdit.value
      ? await updateDrone(route.params.id, payload())
      : await createDrone(payload());
    window.alert(isEdit.value ? '修改成功！' : '创建成功！');
    router.push(saved?.id ? `/drones/${saved.id}` : '/drones');
  } catch (err) {
    const data = err.response?.data?.data;
    if (data && typeof data === 'object') {
      Object.assign(errors, data);
    }
    state.error = err.response?.data?.message || err.message || '保存失败';
  } finally {
    state.saving = false;
  }
}

function resetForm() {
  assignForm(form, original);
  resetErrors();
  state.error = '';
}

onMounted(load);
</script>
