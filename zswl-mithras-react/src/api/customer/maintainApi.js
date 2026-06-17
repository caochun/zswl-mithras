import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/client/new/list', { ...params }),

  getAllIndustry: (params) => http.get('/select/industry/all', { params }),

  remove: (data) => http.post('/client/remove', data),
  createCorp: (data) =>
    http.post('/client/corp/add', data, {
      transformResult: (res) => res.data,
    }),
  createCorpMuteTyc: (data) => http.post('/client/corp/add', data),
  createNormal: (data) => http.post('/client/normal/add', data),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder',
      },
    }),
  searchOrgs: (params) =>
    http.get('/select/orgs', {
      params,
      headers: {
        functionCode: 'selectorgs',
      },
    }),
  getClientBySponsorId: (data) => http.post('/client/list/bySponsors', data),
  postTransferSubmit: (data) => http.post('/client/transfer/submit', data, {}),
  //获取客户是否可转移按钮状态
  getButtonStatus: (params) => http.post('/currentUser/button/status', params),
  getTransferDetail: (params) => http.post('/client/transfer/detail', params),

  // 客户申办权限申请客户信息
  postClientApplyDetail: (data) => http.post('/client/apply/detail', data),

  // 客户申办权限申请（或提交审批）
  postClientApplyEffect: (data) => http.post('/client/apply/effect', data),

  // 校验客户申办权限申请客户信息
  postClientApplyValidate: (data) => http.post('/client/apply/validate', data),

  // 检查客户是否被占有
  postClientApplyOccupy: (data) => http.post('/client/apply/occupy', data),

  postNewOrgs: (params) =>
    http.get('/select/new/orgs', {
      params,
    }),

  // 客户当前批次版本
  postClientBatchNumber: (data) => http.post('/client/batch/number', data),

  // 客户当前批次版本
  postClientNoauthorityList: (data) => http.post('/client/noauthority/list', data),
  // 客户申办权限信息保存
  postApplyModify: (params) => http.post('/client/apply/modify', params),
}
