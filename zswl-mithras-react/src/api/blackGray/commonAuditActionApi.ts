import { http } from '@zswl/admin'

const mock = false

export default {
  getCurrentNodeAuditUsers: (params: any): Promise<any> =>
    http.get('/audit/common/select/currentNodeAuditUsers', { params, mock }),

  getCurrentNodeInfo: (params: any): Promise<any> =>
    http.get('/audit/common/exec/currentNodeInfo', { params, mock }),

  postTransfer: (data: any): Promise<any> =>
    http.post('/audit/common/exec/transfer', data, { mock }),

  postAudit: (data: any): Promise<any> => http.post('/audit/common/exec/audit', data, { mock }),

  getAuditUsers: (params: any): Promise<any> =>
    http.get('/audit/common/select/auditUsers', { params, mock }),

  postLastNodeSelect: (data: any): Promise<any> =>
    http.post('/audit/common/select/lastNodeSelect', data, { mock }),

  postWithdraw: (data: any): Promise<any> =>
    http.post('/audit/common/exec/withdraw', data, { mock }),

  getComments: (params: any): Promise<any> =>
    http.get('/audit/common/comment/get', { params, mock }),

  postSaveComments: (data: any): Promise<any> =>
    http.post('/audit/common/comment/save', data, { mock }),

  getRecord: (params: any): Promise<any> =>
    http.get('/audit/common/record/get', { params, mock }),
}
