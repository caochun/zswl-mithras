import { http } from '@zswl/admin'

export default {
  // 新的客户移交审批详情
  postClientNewTransferDetail: (data) => http.post('/client/newTransfer/detail', data),
  // 新的提交转移指定用户负责的客户
  postClientNewTransferSubmit: (data) => http.post('/client/newTransfer/submit', data),
  // 新的客户移交审批详情
  postClientMewListBySponsors: (data) => http.post('/client/newList/BySponsors', data),
  // 新的提交转移编辑用户负责的客户
  postClientMewTransferModify: (data) => http.post('/client/newTransfer/modify', data),
  //批量编辑
  postClientMewTransferModifyBatch: (data) => http.post('/client/newTransfer/modifyBatch', data),

  // 导出
  postClientTransferExport: (data) =>
    http.post('/client/transfer/export', data, {
      type: 'download',
    }),

  postNewTransferRemove: (data) => http.post('/client/newTransfer/remove', data),
  postTransferApplyCreate: (params) => http.post('/client/transfer/apply/create', params),
  postTransferApplyQueryByBatchNo: (params) =>
    http.post('/client/transfer/apply/queryByBatchNo', params),
  postClientNewTransferDetailModify: (params) =>
    http.post('/client/newTransfer/detail/modify', params),
}
