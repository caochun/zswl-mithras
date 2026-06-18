import { http } from '@zswl/admin'

export default {
  // /new/ftp/treasury/bond/yield/compare
  postBondCompare: (data) => http.post('/new/ftp/treasury/bond/yield/compare', data),
  //  /new/ftp/shibor/interest/rate/compare
  postShiborCompare: (data) => http.post('/new/ftp/shibor/interest/rate/compare', data),
  //  /new/ftp/lpr/pricing/compare
  postLprCompare: (data) => http.post('/new/ftp/lpr/pricing/compare', data),
  // /new/ftp/detail/treasury/bond/yield
  postBondDetail: (data) => http.post('/new/ftp/detail/treasury/bond/yield', data),
  // /new/ftp/detail/lpr/pricing
  postLprDetail: (data) => http.post('/new/ftp/detail/lpr/pricing', data),
  // /new/ftp/detail/shibor/interest/rate
  postShiborDetail: (data) => http.post('/new/ftp/detail/shibor/interest/rate', data),
  // /new/ftp/guarantee/cost/pricing/draft/flash
  postGuaranteeCost: (data) => http.post('/new/ftp/guarantee/cost/pricing/draft/flash', data),
  //'/new/ftp/guarantee/cost/pricing/draft/list'
  postGuaranteeCostList: (data) => http.post('/new/ftp/guarantee/cost/pricing/draft/list', data),
  // /new/ftp/guarantee/cost/pricing/draft/modify
  postGuaranteeCostModify: (data) =>
    http.post('/new/ftp/guarantee/cost/pricing/draft/modify', data),
  // /new/ftp/guarantee/cost/pricing/compare
  postGuaranteeCostCompare: (data) => http.post('/new/ftp/guarantee/cost/pricing/compare', data),

  ///new/ftp/monthly/deduction/get/flag
  postMonthlyDeductionGetFlag: (data) => http.post('/new/ftp/monthly/deduction/get/flag', data),
}
