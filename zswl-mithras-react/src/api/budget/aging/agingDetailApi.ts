/* prettier-ignore-start */
import * as Types from './interface/agingDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改帐龄-详情表
  postItemModify: (data: Types.ItemModifyRequest): Promise<Types.ItemModifyResponse> =>
    http.post('/finance/account/age/item/modify', data, { mock }),

  // 帐龄基础信息统计
  postBaseInfoCount: (data?: any): Promise<any> =>
    http.post('/finance/account/age/base/info/count', data, { mock }),

  // 删除帐龄-详情表
  postItemRemove: (data: Types.ItemRemoveRequest): Promise<Types.ItemRemoveResponse> =>
    http.post('/finance/account/age/item/remove', data, { mock }),

  // 帐龄-详情表列表
  postItemList: (data: Types.ItemListRequest): Promise<Types.ItemListResponse> =>
    http.post('/finance/account/age/item/list', data, { mock }),

  // 推送至苍穹
  postItemSend: (data: Types.ItemSendRequest): Promise<Types.ItemSendResponse> =>
    http.post('/finance/account/age/item/send', data, { mock }),

  // 新增帐龄-详情表
  postItemAdd: (data: Types.ItemAddRequest): Promise<Types.ItemAddResponse> =>
    http.post('/finance/account/age/item/add', data, { mock }),

  // 重新生成-详情表
  postItemRegeneration: (
    data: Types.ItemRegenerationRequest
  ): Promise<Types.ItemRegenerationResponse> =>
    http.post('/finance/account/age/item/regeneration', data, { mock, timeout: 0 }),
}

/* prettier-ignore-end */
