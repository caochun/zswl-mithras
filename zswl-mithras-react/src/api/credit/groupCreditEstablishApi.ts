/* prettier-ignore-start */
import * as Types from '@/api/groupCredit/interface/projectApprovalBaseinfo'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  // 修改集团授信立项基本信息
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/group/credit/establish/base/info/modify', data, { mock }),

  // 新增集团授信立项基本信息
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/group/credit/establish/base/info/add', data, { mock }),

  // 获取集团授信存量风险敞口
  postCreditExposure: (data: Types.CreditExposureRequest): Promise<Types.CreditExposureResponse> =>
    http.post('/group/credit/exposure', data, { mock }),

  // 集团授信立项基本信息列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/group/credit/establish/base/info/list', data, { mock }),

  // 集团授信立项基本信息详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/group/credit/establish/base/info/detail', data, { mock }),
  // 集团授信立项基本信息对比
  postInfoDetailCompare: (data: Types.InfoDetailRequest): Promise<any> =>
    http.post('/group/credit/establish/base/info/detail/compare', data, { mock }),
}

/* prettier-ignore-end */
