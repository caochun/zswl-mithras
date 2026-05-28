/* prettier-ignore-start */
import * as Types from './interface/dataSet'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 现金流数据设置
  postSettingEdit: (data: Types.SettingEditRequest): Promise<Types.SettingEditResponse> =>
    http.post('/capital/flow/setting/edit', data, { mock }),

  // 现金流数据设置详情
  postSettingDetail: (data: Types.SettingDetailRequest): Promise<Types.SettingDetailResponse> =>
    http.post('/capital/flow/setting/detail', data, { mock }),
}

/* prettier-ignore-end */
