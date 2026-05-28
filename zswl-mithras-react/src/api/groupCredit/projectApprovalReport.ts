/* prettier-ignore-start */
import * as Types from './interface/projectApprovalReport'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  // 集团授信立项报告文件-下载
  postReportDownload: (
    params: Types.ReportDownloadRequest,
    fileName: string
  ): Promise<Types.ReportDownloadResponse> =>
    http('/group/credit/establish/report/download', {
      params,
      mock,
      type: 'download',
      fileName,
      timeout: 0,
    }),

  // 集团授信立项报告文件-上传
  postReportUpload: (data: Types.ReportUploadRequest, config: any): any =>
    http.post('/group/credit/establish/report/upload', data, {
      mock,
      type: 'upload',
      ...config,
      transformResult: (res) => res.data,
      timeout: 0,
    }),

  // 集团授信立项报告文件-删除
  postReportRemove: (data: Types.ReportRemoveRequest): Promise<Types.ReportRemoveResponse> => {
    console.log(data, 'data')
    return http.post('/group/credit/establish/report/remove', data, {
      mock,
      transformResult: (res) => res.data,
    })
  },

  // 集团授信立项报告文件列表
  postReportList: (data: Types.ReportListRequest): Promise<Types.ReportListResponse> =>
    http.post('/group/credit/establish/report/list', data, { mock }),
}

/* prettier-ignore-end */
