/* prettier-ignore-start */
import * as Types from './interface/newFtpMonthlyGuidanceApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  postGuidanceAdd: (data: any): Promise<any> =>
    http.post('/new/ftp/monthly/guidance/add', data, { mock }),

  // ftp报价表列表
  postGuidanceDetail: (data: Types.GuidanceDetailRequest): Promise<Types.GuidanceDetailResponse> =>
    http.post('/new/ftp/monthly/guidance/detail', data, { mock }),

  // 修改ftp报价表
  postGuidanceModify: (data: Types.GuidanceModifyRequest): Promise<Types.GuidanceModifyResponse> =>
    http.post('/new/ftp/monthly/guidance/modify', data, { mock }),
}

/* prettier-ignore-end */
