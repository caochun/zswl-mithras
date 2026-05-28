import { http } from '@zswl/admin'

export default {
  // 租赁物清单列表
  postList: (params) => http.post('/contract/leaseitem/list', params),
  postListCompare: (params) => http.post('/contract/leaseitem/list/compare', params),
  // 导入租赁物清单
  postUploadData: (params) =>
    http.post('/contract/leaseitem/import', params, {
      type: 'upload',
      timeout: 0,
    }),

  postDownloadTemp: (params) =>
    http('/contract/leaseitem/template/download', {
      params,
      type: 'download',
      timeout: 0,
    }),
  // 租赁物总金额-保存
  postLeaseItemTotalamountSave: (params) =>
    http.post('/ledger/detail/leaseitem/totalamount/save', params),

  // 租赁物清单预选择
  postLeaseItemPrechoose: (params) => http.post('/contract/leaseitem/prechoose', params),

  // 租赁物清单选择
  postLeaseItemChoose: (params) => http.post('/contract/leaseitem/choose', params),

  // 租赁物审核-变更流程审核
  postReviewModifyEffect: (params) => http.post('/lease/review/modify/effect', params),
  // 导出租赁物清单
  postBatchExport: (params) =>
    http.post('/contract/leaseitem/export', params, {
      type: 'download',
      timeout: 0,
    }),

  // 租赁物清单版本列表
  postLeaseitemVersionList: (params) => http.post('/contract/leaseitem/version/list', params),

  // 保存租赁物总额
  postLeaseitemTotalAmountSave: (params) =>
    http.post('/contract/leaseitem/totalamount/save', params),

  // 检查是否存在审批中的租赁物
  postLeaseItemInprocessCheck: (params) => http.post('/contract/leaseitem/inprocess/check', params),
  // 修改合同基本信息表-租赁物类型
  postLeaseTypesModify: (params) => http.post('/contract/base/info/modify/leaseItem', params, {}),
}
