import { http } from '@zswl/admin'

export default {
  getReportList: (params) => http.post('/afterlease/checkplan/report/summary/list', params, {}),
  uploadFile: (params) =>
    http.post('/materials/upload', params, {
      type: 'upload',
      timeout: 0,
      headers: {
        functionCode: 'afterleasesummarymaterialsupload',
      },
    }),

  downFile: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      timeout: 0,
      headers: {
        functionCode: 'afterleasesummarymaterialsdownload',
      },
    }),

  removeFile: (params) =>
    http.post('/materials/remove', params, {
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'afterleasesummarymaterialsremove',
      },
    }),
}
