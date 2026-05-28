/* prettier-ignore-start */
import * as Types from './interface/eclBusinessApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ecl_业务配置表列表
  postConfigList: (data: Types.ConfigListRequest): Promise<Types.ConfigListResponse> =>
    http.post('/ecl/business/config/list', data, { mock }),

  // ecl_业务配置表版本列表
  postVersionList: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/ecl/business/config/version/list', data, { mock }),

  // ecl_业务配置表版本详情
  postVersionDetail: (data: Types.VersionDetailRequest): Promise<Types.VersionDetailResponse> =>
    http.post('/ecl/business/config/version/detail', data, { mock }),

  // ecl_业务配置表详情
  postConfigDetail: (data: Types.ConfigDetailRequest): Promise<Types.ConfigDetailResponse> =>
    http.post('/ecl/business/config/detail', data, { mock }),

  // 修改ecl_业务配置表
  postConfigModify: (data: Types.ConfigModifyRequest): Promise<Types.ConfigModifyResponse> =>
    http.post('/ecl/business/config/modify', data, { mock }),
}

/* prettier-ignore-end */
