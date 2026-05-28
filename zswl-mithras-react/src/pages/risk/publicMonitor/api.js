/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  postMonitorList: (params) => http.post('/risk/control/opinion/monitor/list', params),
  postMonitorDetail: (params) => http.post('/risk/control/opinion/monitor/detail', params),
  postMonitorNotice: (params) => http.post('/risk/control/opinion/monitor/notice', params),
  postMonitorSend: (params) => http.post('/risk/control/opinion/monitor/send', params),
  postMonitorHandle: (params) => http.post('/risk/control/opinion/monitor/handle', params),
  postMonitorFlowHandle: (params) => http.post('/risk/control/opinion/monitor/advisement', params),
  postMonitorModify: (params) => http.post('/risk/warn/monitor/modify', params),
  postMonitorUnResolved: (params) =>
    http.post('/risk/control/opinion/monitor/unresolved', params, {
      mock: false,
      headers: {
        functionCode: 'riskcontrolopinionmonitorunresolved',
      },
    }),

  getChangeInfo: (params) => http.get('/risk/opinion/info/extra/changeInfo', { params }),
  getCourtAnnounce: (params) => http.get('/risk/opinion/info/extra/courtAnnounce', { params }),
  getCourtSession: (params) => http.get('/risk/opinion/info/extra/courtSession', { params }),
  getCaseInfo: (params) => http.get('/risk/opinion/info/extra/caseInfo', { params }),

  getProcessInstanceId: (params) => http.post('/risk/control/opinion/monitor/view', params),
  // /risk/control/opinion/monitor/close
  postMonitorClose: (params) => http.post('/risk/control/opinion/monitor/close', params),
  getClientList: (params) => http.post('/client/list', { scene: 'query', ...params }, { headers: { functionCode: 'clientlist-4' }}),
  // 新增舆情
  postMonitorConfirm: (params) => http.post('/risk/control/opinion/monitor/confirm', params),
  // 人工录入舆情-删除
  postMonitorDelete: (params) => http.post('/risk/control/opinion/monitor/delete', params),
  // 人工录入舆情-编辑保存
  postMonitorSave: (params) => http.post('/risk/control/opinion/monitor/save', params),
  // 人工录入舆情-提交
  postMonitorSubmit: (params) => http.post('/risk/control/opinion/monitor/submit', params),
}
