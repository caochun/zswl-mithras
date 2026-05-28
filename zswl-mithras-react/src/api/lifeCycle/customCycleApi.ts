/* prettier-ignore-start */
import * as Types from './interface/customCycleApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 客户全周期卡片
  postLifecycleCard: (data: Types.LifecycleCardRequest): Promise<Types.LifecycleCardResponse> =>
    http.post('/client/lifecycle/card', data, { mock }),

  // 借据详情
  postLifecycleReceipt: (
    data: Types.LifecycleReceiptRequest,
  ): Promise<Types.LifecycleReceiptResponse> =>
    http.post('/client/lifecycle/receipt', data, { mock }),

  // 客户列表
  postLifecycleClientlist: (
    data: Types.LifecycleClientlistRequest,
  ): Promise<Types.LifecycleClientlistResponse> =>
    http.post('/client/lifecycle/clientlist', data, { mock }),

  // 客户详情
  postLifecycleClientdetail: (
    data: Types.LifecycleClientdetailRequest,
  ): Promise<Types.LifecycleClientdetailResponse> =>
    http.post('/client/lifecycle/clientdetail', data, { mock }),

  // 客户项目列表
  postLifecycleProjectlist: (
    data: Types.LifecycleProjectlistRequest,
  ): Promise<Types.LifecycleProjectlistResponse> =>
    http.post('/client/lifecycle/projectlist', data, { mock }),

  // 查询五级分类
  postLifecycleFivelevel: (
    data: Types.LifecycleFivelevelRequest,
  ): Promise<Types.LifecycleFivelevelResponse> =>
    http.post('/client/lifecycle/fivelevel', data, { mock }),
}

/* prettier-ignore-end */
