/* prettier-ignore-start */
import * as Types from './interface/ftpYieldApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 资金管理-融资管理-ftp收益表列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/ftp/income/base/info/list', data, { mock }),

  // 资金管理-融资管理-ftp收益表统计
  postInfoCount: (data: Types.InfoCountRequest): Promise<Types.InfoCountResponse> =>
    http.post('/ftp/income/base/info/count', data, { mock }),

  // 资金管理-融资管理-ftp收益表详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/ftp/income/base/info/detail', data, { mock }),

  // 资金管理-融资管理-ftp收益记录获取融资机构
  postOrganizationList: (
    data: Types.OrganizationListRequest,
  ): Promise<Types.OrganizationListResponse> =>
    http.post('/ftp/income/organization/list', data, { mock }),

  // 资金管理-融资管理-ftp收益记录表列表
  postRecordList: (data: Types.RecordListRequest): Promise<Types.RecordListResponse> =>
    http.post('/ftp/income/detail/record/list', data, { mock }),
}

/* prettier-ignore-end */
