import { http } from '@zswl/admin'

export default {
  // 校验评审相关客户财报情况
  checkResult: (params) => http.post('/proj/pricing/client/subjectitem/checkresult', params),
  // 查看财报详情
  checkResultList: (params) =>
    http.post('/proj/pricing/client/subjectitem/checkresult/list', params),
  // 保存原因
  saveReason: (params) =>
    http.post('/proj/pricing/client/subjectitem/checkresult/reason/save', params),
}
