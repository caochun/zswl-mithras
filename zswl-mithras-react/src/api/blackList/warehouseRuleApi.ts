/* prettier-ignore-start */
import * as Types from './interface/warehouseRuleApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改黑灰名单库-入库原因参数配置
  postConfigModify: (data: Types.ConfigModifyRequest): Promise<Types.ConfigModifyResponse> =>
    http.post('/black/gray/warehouse/rule/config/modify', data, { mock }),

  // 删除黑灰名单库-入库原因参数配置
  postConfigRemove: (data: Types.ConfigRemoveRequest): Promise<Types.ConfigRemoveResponse> =>
    http.post('/black/gray/warehouse/rule/config/remove', data, { mock }),

  // 新增黑灰名单库-入库原因参数配置
  postConfigAdd: (data: Types.ConfigAddRequest): Promise<Types.ConfigAddResponse> =>
    http.post('/black/gray/warehouse/rule/config/add', data, { mock }),

  // 黑灰名单库-入库原因参数配置列表
  postConfigList: (data: Types.ConfigListRequest): Promise<Types.ConfigListResponse> =>
    http.post('/black/gray/warehouse/rule/config/list', data, { mock }),

  // 黑灰名单库-入库原因参数配置启用停用接口
  postConfigSwitch: (data: Types.ConfigSwitchRequest): Promise<Types.ConfigSwitchResponse> =>
    http.post('/black/gray/warehouse/rule/config/switch', data, { mock }),

  // 黑灰名单库-入库原因参数配置详情
  postConfigDetail: (data: Types.ConfigDetailRequest): Promise<Types.ConfigDetailResponse> =>
    http.post('/black/gray/warehouse/rule/config/detail', data, { mock }),
}

/* prettier-ignore-end */
