import { http } from '@zswl/admin'

const mock = false

export default {
  postAssignSubtask: (data: any): Promise<any> => http.post('/subtask/assign', data, { mock }),

  getSameCompanyOrgTree: (): Promise<any> => http.get('/auth/getSameCompanyOrgTree', { mock }),

  getRoleByDeptCode: (params: any): Promise<any> =>
    http.get('/auth/getRoleByDeptCode', { params, mock }),

  getUserByRoleCode: (params: any): Promise<any> =>
    http.get('/auth/getUserByRoleCode', { params, mock }),
}
