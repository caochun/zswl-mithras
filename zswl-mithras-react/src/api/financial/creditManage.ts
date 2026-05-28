/* prettier-ignore-start */
import * as Types from './interface/creditManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改授信
  postCreditModify: (data: Types.CreditModifyRequest): Promise<Types.CreditModifyResponse> =>
    http.post('/fund/credit/modify', data, { mock }),

  // 删除授信
  postCreditRemove: (data: Types.CreditRemoveRequest): Promise<Types.CreditRemoveResponse> =>
    http.post('/fund/credit/remove', data, { mock }),
  // 获取授信额度
  getCreditLimit: (data: { organizationId: Number }): Promise<Types.CreditRemoveResponse> =>
    http.post('/fund/credit/limit', data, { mock }),

  // 授信列表
  postCreditList: (data: Types.CreditListRequest): Promise<Types.CreditListResponse> =>
    http.post('/fund/credit/list', data, { mock }),

  // 授信详情
  postCreditDetail: (data: Types.CreditDetailRequest): Promise<Types.CreditDetailResponse> =>
    http.post('/fund/credit/detail', data, { mock }),

  // 文件上传
  postFileUpload: (data: Types.FileUploadRequest): Promise<Types.FileUploadResponse> =>
    http.post('/fund/credit/file/upload', data, { mock, type: 'upload' }),

  // 文件列表
  postFileList: (data: Types.FileListRequest): Promise<Types.FileListResponse> =>
    http.post('/fund/credit/file/list', data, { mock }),

  // 文件删除
  postFileRemove: (data: Types.FileRemoveRequest): Promise<Types.FileRemoveResponse> =>
    http.post('/fund/credit/file/remove', data, { mock, transformResult: (res) => res.data }),
  // 文件删除
  postFileDownload: (data: Types.FileRemoveRequest): Promise<Types.FileRemoveResponse> =>
    http.post('/fund/credit/file/download', data, { mock, type: 'download' }),

  // 新增授信
  postCreditAdd: (data: Types.CreditAddRequest): Promise<Types.CreditAddResponse> =>
    http.post('/fund/credit/add', data, { mock }),
  // /fund/credit/invalid
  postInvalid: (data: any) => http.post('/fund/credit/invalid', data, { mock }),
  ///fund/credit/limitDetail

  postLimitDetail: (data: any) => http.post('/fund/credit/limitDetail', data, { mock }),
}

/* prettier-ignore-end */
