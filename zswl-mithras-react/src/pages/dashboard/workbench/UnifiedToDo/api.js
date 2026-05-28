import { http } from '@zswl/admin'

export default {
  // 待办
  postDashboardToDoList: (data) => http.post('/dashboard/todo/list', data),
  // 我发起的
  postDashboardToDoMyProcessApply: (data) => http.post('/dashboard/todo/myprocess/apply', data),
  // 在办
  postDashboardToDoMyProcessDoing: (data) => http.post('/dashboard/todo/myprocess/doing', data),
  // 已办
  postDashboardToDoMyProcessFinish: (data) => http.post('/dashboard/todo/myprocess/finish', data),
  // 抄送
  postDashboardTaskMyReceiveCCList: (data) => http.post('/flow/task/myReceive/cc/list', data),
  // 消息
  postMessageList: (data) => http.post('/message/list', data),
  // 待处理舆情
  postMonitorUnresolved: (data) =>
    http.post('/risk/control/opinion/monitor/unresolved', data, {
      headers: {
        functionCode: 'riskcontrolopinionmonitorunresolved-dashboard',
      },
    }),

  // 数量
  postColumnCount: (data) => http.get('/dashboard/todo/count', data),
}
