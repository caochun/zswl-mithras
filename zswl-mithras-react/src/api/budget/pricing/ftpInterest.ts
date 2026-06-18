/* prettier-ignore-start */
import * as Types from '@/api/budget/pricing/interface/ftpInterest'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // FTP计息-基本信息
  postBaseinfoGet: (data: Types.BaseinfoGetRequest): Promise<Types.BaseinfoGetResponse> =>
    http.post('/ftp/interest/baseinfo/get', data, { mock }),

  // FTP计息-每日计息分页列表
  postDetailPagelist: (data: Types.DetailPagelistRequest): Promise<Types.DetailPagelistResponse> =>
    http.post('/ftp/interest/detail/pagelist', data, { mock }),

  // FTP计息分页列表
  postInterestPagelist: (
    data: Types.InterestPagelistRequest
  ): Promise<Types.InterestPagelistResponse> => http.post('/ftp/interest/pagelist', data, { mock }),
}

/* prettier-ignore-end */
