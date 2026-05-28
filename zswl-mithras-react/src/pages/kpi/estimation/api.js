import { http } from '@zswl/admin'

export default {
  postExportContractList: (params) =>
    http.get('/kpi/proj/guess/export/contract/list', {
      params,
      type: 'download',
      timeout: 0,
    }),
  postExportTimeList: (params) =>
    http.get('/kpi/proj/guess/export/time/list', {
      params,
      type: 'download',
      timeout: 0,
    }),
  postExportDeptList: (params) =>
    http.get('/kpi/proj/guess/export/dept/list', {
      params,
      type: 'download',
      timeout: 0,
    }),
  postExportPeopleList: (params) =>
    http.get('/kpi/proj/guess/export/people/list', {
      params,
      type: 'download',
      timeout: 0,
    }),
  postExportContractDetail: (params) =>
    http.get('/kpi/proj/guess/export/contract/detail', {
      params,
      type: 'download',
      timeout: 0,
    }),
  postExportPeopleDetail: (params) =>
    http.get('/kpi/proj/guess/export/people/detail', {
      params,
      type: 'download',
      timeout: 0,
    }),
}
