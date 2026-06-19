import { http } from '@zswl/admin'

const reportChange = (params) => http.post('/cr/account/reportChange', params)

export const guarantorApi = {
  getList: (params, header) => http.post('/cr/guarantor/list', params, header),
  reportChange,
}

export const mortgageApi = {
  getList: (params, header) => http.post('/cr/mortgage/list', params, header),
  reportChange,
}

export const repayApi = {
  getList: (params, header) => http.post('/cr/repay/list', params, header),
  modifyItem: (params) => http.post('/cr/repay/modify', params),
}

export const specialTradeApi = {
  getList: (params, header) => http.post('/cr/special/trade/list', params, header),
  modifyItem: (params) => http.post('/cr/special/trade/modify', params),
  reportItem: (params) => http.post('/cr/special/trade/reportChange', params),
  reportChange,
}

export const clientApi = {
  getList: (params, header) => http.post('/cr/client/list', params, header),
  reportChange,
}

export const overdueRecordApi = {
  getList: (params, header) => http.post('/cr/overdue/record/list', params, header),
  modifyItem: (params) => http.post('/cr/overdue/record/modify', params),
  deleteItem: (params) => http.post('/cr/overdue/record/remove', params),
  cancelItem: (params) => http.post('/cr/overdue/record/cancel/remove', params),
  reportChange,
}

export const accountApi = {
  getList: (params, header) => http.post('/cr/account/list', params, header),
  modifyItem: (params) => http.post('/cr/account/modify', params),
  reportItem: reportChange,
  reportChange,
}

export const pledgeApi = {
  getList: (params, header) => http.post('/cr/pledge/list', params, header),
  reportChange,
}
