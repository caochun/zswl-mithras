/* prettier-ignore-start */
import * as Types from './interface/ftpMaterialsFile'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 文件下载
  postFileDownload: (data: Types.FileDownloadRequest): Promise<Types.FileDownloadResponse> =>
    http.post('/ftp/materials/file/download', data, { mock, type: 'download' }),

  // 文件上传
  postFileUpload: (data: Types.FileUploadRequest): Promise<Types.FileUploadResponse> =>
    http.post('/ftp/materials/file/upload', data, { mock, type: 'upload' }),

  // 文件列表
  postFileList: (data: Types.FileListRequest): Promise<Types.FileListResponse> =>
    http.post('/ftp/materials/file/list', data, { mock }),

  // 文件删除
  postFileRemove: (data: Types.FileRemoveRequest): Promise<Types.FileRemoveResponse> =>
    http.post('/ftp/materials/file/remove', data, { mock, transformResult: (res) => res.data }),
}

/* prettier-ignore-end */
