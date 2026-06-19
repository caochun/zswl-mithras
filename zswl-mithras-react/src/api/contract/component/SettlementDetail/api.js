import { http } from '@zswl/admin'

export default {
  getSettleDetail: (params) => http.post('/contract/settle/plan/latest/get', params),
  updateSettleNormal: (params) => http.post('/contract/settle/plan/normal/save', params, {}),
  updateSettleInadvance: (params) => http.post('/contract/settle/plan/inadvance/save', params, {}),
  submitSettle: (params) => http.post('/contract/flow/settle/submit', params, {}),

  postDataList: (params) => http.post('/contract/settle/extra/file/list', params),
  postDataUpload: (params) =>
    http.post('/contract/settle/extra/file/upload', params, {
      type: 'upload',
      timeout: 0,
    }),

  deleteMaterialsUpload: (params) =>
    http.post('/materials/remove', params, {
      headers: {
        functionCode: 'materialsremove-1',
      },
    }),

  // 下载
  postProjectDataDownload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
      headers: {
        functionCode: 'materialsdownload-2',
      },
    }),

  postImportEstimate: (params) =>
    http.post('/contract/flow/change/upload', params, {
      type: 'upload',
      timeout: 0,
    }),

  // 取消
  cancelFlow: (params) => http.post('/contract/flow/settle/cancel', params, {}),
}
