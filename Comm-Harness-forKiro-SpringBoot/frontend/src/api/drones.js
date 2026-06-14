import axios from 'axios';

const http = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
});

function unwrap(response) {
  return response.data.data;
}

export function listDrones(params) {
  return http.get('/drones', { params }).then(unwrap);
}

export function getDrone(id) {
  return http.get(`/drones/${id}`).then(unwrap);
}

export function createDrone(payload) {
  return http.post('/drones', payload).then(unwrap);
}

export function updateDrone(id, payload) {
  return http.put(`/drones/${id}`, payload).then(unwrap);
}

export function deleteDrone(id) {
  return http.delete(`/drones/${id}`).then(unwrap);
}
