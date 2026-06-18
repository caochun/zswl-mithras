/* prettier-ignore-start */
import * as Types from './interface/newFtpMonthlyGuidanceExtApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ftp指导报价扩展表（下半部分）列表
  postExtDetail: (data: Types.ExtDetailRequest): Promise<Types.ExtDetailResponse> =>
    http.post('/new/ftp/monthly/guidance/ext/detail', data, { mock }),

  // 修改ftp指导报价扩展表（下半部分）
  postExtModify: (data: Types.ExtModifyRequest): Promise<Types.ExtModifyResponse> =>
    http.post('/new/ftp/monthly/guidance/ext/modify', data, { mock }),
  // ftp 季度最低收益率扩展表（下半部分）列表
  postQuarterlyExtDetail: (data: Types.ExtDetailRequest): Promise<Types.ExtDetailResponse> =>
    http.post('/new/ftp/quarterly/base/pricing/ext/detail', data, { mock }),

  // 修改ftp 季度最低收益率扩展表（下半部分）
  postQuarterlyExtModify: (data: Types.ExtModifyRequest): Promise<Types.ExtModifyResponse> =>
    http.post('/new/ftp/quarterly/base/pricing/ext/modify', data, { mock }),
}

/* prettier-ignore-end */
