export const DRONE_STATUSES = [
  { value: 'AVAILABLE', label: '可用' },
  { value: 'UNDER_MAINTENANCE', label: '维修中' },
  { value: 'SCRAPPED', label: '已报废' }
];

export function statusClass(status) {
  if (status === '可用' || status === 'AVAILABLE') {
    return 'status-tag status-available';
  }
  if (status === '维修中' || status === 'UNDER_MAINTENANCE') {
    return 'status-tag status-maintenance';
  }
  if (status === '已报废' || status === 'SCRAPPED') {
    return 'status-tag status-scrapped';
  }
  return 'status-tag';
}

export function toStatusCode(status) {
  if (status === '可用') {
    return 'AVAILABLE';
  }
  if (status === '维修中') {
    return 'UNDER_MAINTENANCE';
  }
  if (status === '已报废') {
    return 'SCRAPPED';
  }
  return status || '';
}
