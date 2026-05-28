import { http } from '@zswl/admin'

export default {
  //业务工作台-业绩排名-部门间排名
  postDashboardPerformanceDeptShipSort: (data) =>
    http.post('/dashboard/performance/dept/ship/sort', data),

  //业务工作台-业绩排名-部门内排名
  postDashboardPerformanceDeptInSort: (data) =>
    http.post('/dashboard/performance/dept/in/sort', data),
}
