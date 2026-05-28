import { http } from '@zswl/admin'

export default {
    getList: (data) => http.post('/stampDuty/list', data),
    getContractList: (data) => http.post('/stampDuty/contract/list', data),
    // 批量导出
    download: (data) => http.post('/stampDuty/export', data, { type: 'download' }),
    // 模板下载
    downloadTemplate : (templateName, moduleType) => http.get(`/file/download/template?templateName=${templateName}&moduleType=${moduleType}`,{ type: 'download', headers:{functionCode:'stampDutyTemplateDownload'} }),
    upload: (data) => http.post('/stampDuty/import', data, {
        type: 'upload',
        transformResult: (res) => res.data,
        timeout: 0,
    }),
    delete: (data) => http.post('/stampDuty/delete', data),
    add: (data) => http.post('/stampDuty/add', data),
}