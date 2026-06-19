import { http } from '@zswl/admin'

export default {
  //业务工作台-我的业绩-个人业绩
  postDashboardPerformancePersonal: (data) => http.post('/dashboard/performance/personal', data),

  //业务工作台-我的业绩-部门业绩
  postDashboardPerformanceDept: (data) => http.post('/dashboard/performance/dept', data),

  //业务工作台-业绩排名-部门间排名
  postDashboardPerformanceDeptShipSort: (data) =>
    http.post('/dashboard/performance/dept/ship/sort', data),

  //业务工作台-业绩排名-部门内排名
  postDashboardPerformanceDeptInSort: (data) =>
    http.post('/dashboard/performance/dept/in/sort', data),
}
