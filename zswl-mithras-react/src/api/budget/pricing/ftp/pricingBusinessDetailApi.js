import { http } from '@zswl/admin'

export default {
  postBondCompare: (data) => http.post('/new/ftp/treasury/bond/yield/compare', data),
  postShiborCompare: (data) => http.post('/new/ftp/shibor/interest/rate/compare', data),
  postLprCompare: (data) => http.post('/new/ftp/lpr/pricing/compare', data),
  postBondDetail: (data) => http.post('/new/ftp/detail/treasury/bond/yield', data),
  postLprDetail: (data) => http.post('/new/ftp/detail/lpr/pricing', data),
  postShiborDetail: (data) => http.post('/new/ftp/detail/shibor/interest/rate', data),
  postGuaranteeCost: (data) => http.post('/new/ftp/guarantee/cost/pricing/draft/flash', data),
  postGuaranteeCostList: (data) =>
    http.post('/new/ftp/guarantee/cost/pricing/draft/list', data),
  postGuaranteeCostModify: (data) =>
    http.post('/new/ftp/guarantee/cost/pricing/draft/modify', data),
  postGuaranteeCostCompare: (data) =>
    http.post('/new/ftp/guarantee/cost/pricing/compare', data),
  postMonthlyDeductionGetFlag: (data) =>
    http.post('/new/ftp/monthly/deduction/get/flag', data),
}
