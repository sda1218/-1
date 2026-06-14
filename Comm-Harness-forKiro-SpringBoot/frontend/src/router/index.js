import { createRouter, createWebHistory } from 'vue-router';
import DroneList from '../views/DroneList.vue';
import DroneDetail from '../views/DroneDetail.vue';
import DroneForm from '../views/DroneForm.vue';

const routes = [
  {
    path: '/',
    redirect: '/drones'
  },
  {
    path: '/drones',
    name: 'DroneList',
    component: DroneList
  },
  {
    path: '/drones/new',
    name: 'DroneCreate',
    component: DroneForm
  },
  {
    path: '/drones/:id',
    name: 'DroneDetail',
    component: DroneDetail
  },
  {
    path: '/drones/:id/edit',
    name: 'DroneEdit',
    component: DroneForm
  }
];

export default createRouter({
  history: createWebHistory(),
  routes
});
