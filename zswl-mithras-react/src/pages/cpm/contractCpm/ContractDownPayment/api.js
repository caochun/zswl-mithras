import { http } from '@zswl/admin'

export default {
  //项目关联合同明细
  contractDetail: (params) => http.post('/contractcp/contract/detail', params),
  //现金流明细
  cashDetail: (params) => http.post('/contractcp/cash/detail', params),
  //现金流列表
  cashList: (params) => http.post('/contractcp/cash/list', params),
  //项目关联合同列表
  contractList: (params) => http.post('/contractcp/contract/list', params),

  contractDetailList: (params) => http.post('/contractcp/cash/detail', params),

  cashDetailList: (params) =>
    http.post('/contractcp/cash/detail/export', params, {
      type: 'download',
      fileName: '现金流明细.xlsx',
      timeout: 0,
    }),
}
