/* prettier-ignore-start */
import * as Types from './interface/fileList'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 下载报告
  postFileDownload: (data: Types.FileDownloadRequest): Promise<Types.FileDownloadResponse> =>
    http.post('/file/download', data, { mock, type: 'download' }),

  // 批量下载报告
  postBatchDownload: (data: Types.BatchDownloadRequest): Promise<Types.BatchDownloadResponse> =>
    http.post('/file/batch/download', data, { mock, type: 'download' }),

  // 文件下载方式修改-批量下载报告
  getBatchDownload: (params: Types.BatchDownloadRequest): Promise<Types.BatchDownloadResponse> =>
    http.get('/file/batch/download', { params, mock }),

  // 上传文件
  postFileUpload: (
    data: Types.FileUploadRequest,
    functionCode: string
  ): Promise<Types.FileUploadResponse> =>
    http.post('/file/upload', data, {
      mock,
      type: 'upload',
      transformResult: (res) => res.data,
      timeout: 0,
      headers: {
        functionCode,
      },
    }),

  // 下载报告
  getFileDownload: (
    params: Types.FileDownloadRequest,
    functionCode: string
  ): Promise<Types.FileDownloadResponse> =>
    http.get('/file/download', {
      params,
      mock,
      type: 'download',
      headers: {
        functionCode,
      },
    }),
  // 下载模板
  getDownloadTemplate: (
    params: Types.FileDownloadRequest,
    functionCode: string
  ): Promise<Types.FileDownloadResponse> =>
    http.get('/file/download/template', {
      params,
      mock,
      headers: {
        functionCode,
      },
      transformResult: (res) => res.data,
    }),

  // 删除文件
  postFileRemove: (data: Types.FileRemoveRequest): Promise<Types.FileRemoveResponse> =>
    http.post('/file/remove', data, { mock }),
  // 修改文件名
  postRenameFile: (data: any,functionCode:string): Promise<any> => http.post('/file/rename', data, { mock,  headers: {
    functionCode,
  },transformResult: (res) => res.data },),

  // 批量删除文件
  postBatchRemove: (
    data: Types.BatchRemoveRequest,
    functionCode: string
  ): Promise<Types.BatchRemoveResponse> =>
    http.post('/file/batch/remove', data, {
      mock,
      headers: {
        functionCode,
      },
      transformResult: (res) => res.data,
    }),

  // 文件下载方式修改-批量下载报告-本地打包后下载
  getFileDownloads: (params: Types.FileDownloadsRequest): Promise<Types.FileDownloadsResponse> =>
    http.get('/file/downloads', { params, mock, type: 'download' }),

  // 获取文件信息
  postFileList: (
    data: Types.FileListRequest,
    functionCode: string
  ): Promise<Types.FileListResponse> =>
    http.post('/file/list', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  postFileVersionCompare: (data: any, functionCode: string): Promise<any> =>
    http.post('/file/list/version/compare', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 获取文件分组信息
  postListGroup: (
    data: Types.ListGroupRequest,
    functionCode: string
  ): Promise<Types.ListGroupResponse> =>
    http.post('/file/list/group', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
  //  收付款 资料清单
  postFundReceiptReapyList: (data: Types.ListGroupRequest): Promise<Types.ListGroupResponse> =>
    http.post('/materials/fund/receipt/reapy/list', data, { mock }),
  postFileUploadRecord: (data: any, functionCode: string): Promise<any> =>
    http.post('/file/upload/record', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
  // 获取文件上传预签名
  postFileUploadPresigned: (data: any, functionCode: string): Promise<any> =>
    http.post('/file/upload/presigned', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}

/* prettier-ignore-end */
