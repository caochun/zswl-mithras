/* prettier-ignore-start */
import * as Types from './interface/predictConfigApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ecl_业务配置表列表
  postConfigList: (data: Types.ConfigListRequest): Promise<Types.ConfigListResponse> =>
    http.post('/ecl/predict/business/config/list', data, { mock }),

  // 修改ecl_预测业务配置表
  postConfigModify: (data: Types.ConfigModifyRequest): Promise<Types.ConfigModifyResponse> =>
    http.post('/ecl/predict/business/config/modify', data, { mock }),

  // 修改ecl_预测业务配置表详情
  postConfigDetail: (data: Types.ConfigDetailRequest): Promise<Types.ConfigDetailResponse> =>
    http.post('/ecl/predict/business/config/detail', data, { mock }),
}

/* prettier-ignore-end */
