/* prettier-ignore-start */
import * as Types from './interface/materialsApi'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 资料清单-下载
  postMaterialsDownload: (
    params: Types.MaterialsDownloadRequest,
    functionCode: string
  ): Promise<Types.MaterialsDownloadResponse> =>
    http('/materials/download', {
      params,
      mock,
      type: 'download',
      timeout: 0,
      headers: {
        functionCode,
      },
    }),
  // 资料清单-上传
  postMaterialsUpload: (
    data: Types.MaterialsUploadRequest,
    functionCode: string
  ): Promise<Types.MaterialsUploadResponse> =>
    http.post('/materials/upload', data, {
      mock,
      type: 'upload',
      transformResult: (res) => res.data,
      headers: {
        functionCode,
      },
      timeout: 0,
    }),

  // 资料清单-列表
  postMaterialsList: (data: Types.MaterialsListRequest): Promise<Types.MaterialsListResponse> =>
    http.post('/materials/list', data, { mock }),

  // 资料清单-删除
  postMaterialsRemove: (
    data: Types.MaterialsRemoveRequest,
    functionCode: string
  ): Promise<Types.MaterialsRemoveResponse> =>
    http.post('/materials/remove', data, {
      mock,
      headers: {
        functionCode,
      },
      transformResult: (res) => res.data,
    }),

  // 资料清单预览判断接口
  postMaterialsPreview: (
    data: Types.MaterialsPreviewRequest
  ): Promise<Types.MaterialsPreviewResponse> => http.post('/materials/preview', data, { mock }),
}

/* prettier-ignore-end */
