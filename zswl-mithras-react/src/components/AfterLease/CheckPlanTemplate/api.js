import { http } from '@zswl/admin'

export default {
  // 租后检查报告页面增加「审批快照」按钮
  checkplanPreselect: (params) => http.post('/afterlease/checkplan/preselect', params, {}),
}
