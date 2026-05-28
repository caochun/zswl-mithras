import { http } from '@zswl/admin'

export default {
  // 收付款模块接口

  // 保单信息列表
  postPaymentPolicyList: (params) => http.post('/payment/policy/info/list', params),
  // 修改保单信息
  postPaymentPolicyModify: (params) =>
    http.post('/payment/policy/info/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 新增保单信息
  postPaymentPolicyAdd: (params) =>
    http.post('/payment/policy/info/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 删除保单信息
  postPaymentPolicyRemove: (params) => http.post('/payment/policy/info/remove', params, {}),
  // 下载模版
  postPaymentPolicyImport: (params, uploadConfig) =>
    http.post('/payment/policy/import', params, { type: 'upload', ...uploadConfig, timeout: 0 }),
  // 删除保单信息
  postPaymentPolicyRemove: (params) => http.post('/payment/policy/info/remove', params, {}),
  // 修改保单信息勾选框
  postChecked: (params) => http.post('/payment/policy/info/modify/flag', params, {}),

  // 待维护保单项目列表
  postPolicyProjList: (params) => http.post('/maintenance/policy/proj/list', params, {}),

  // 保单模块接口
  // 保单信息列表
  postPolicyList: (params) => http.post('/policy/info/list', params),
  // 修改保单信息
  postPolicyModify: (params) =>
    http.post('/policy/info/modify', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 新增保单信息
  postPolicyAdd: (params) =>
    http.post('/policy/info/add', params, {
      type: 'upload',
      timeout: 0,
    }),
  // 删除保单信息
  postPolicyRemove: (params) => http.post('/policy/info/remove', params, {}),

  postPolicyImport: (params, uploadConfig) =>
    http.post('/policy/import', params, { type: 'upload', ...uploadConfig, timeout: 0 }),

  // 批量删除
  postPaymentRemoveBatch: (params) => http.post('/payment/policy/info/removeBatch', params, {}),
  // 批量下载
  postPaymentPolicyBatchExport: (params) =>
    http('/payment/policy/info/export', {
      params,
      type: 'download',
      timeout: 0,
      // fileName: `保单信息.xlsx`,
    }),
}
