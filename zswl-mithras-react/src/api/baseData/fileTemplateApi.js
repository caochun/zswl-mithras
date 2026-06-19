import { http } from '@zswl/admin'

export default {
  postTemplateTypeList: () => http.post('/file/template/type/list'),

  postTemplateList: (params) => http.post('/file/template/list', params),

  postTemplateAdd: (params) =>
    http.post('/file/template/add', params, { type: 'upload', timeout: 0 }),

  postTemplateReplace: (params) =>
    http.post('/file/template/replace', params, { type: 'upload', timeout: 0 }),

  postTemplateHistoryRollback: (params) => http.post('/file/template/history/rollback', params),

  postTemplateHistoryList: (params) => http.post('/file/template/history/list', params),

  postTemplateTypeAdd: (params) => http.post('/file/template/type/add', params),

  postTemplateTypeRemove: (params) => http.post('/file/template/type/remove', params),

  postFileTemplateUpdate: (params) => http.post('/file/template/update', params),

  getFileDownload: (params) =>
    http.get('/file/download', {
      params,
      type: 'download',
      headers: {
        functionCode: 'filedownload',
      },
    }),
}
