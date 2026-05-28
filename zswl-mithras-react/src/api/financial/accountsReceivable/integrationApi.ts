/* prettier-ignore-start */
import * as Types from './interface/integrationApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改应收逾期集成表
  postIntegrationModify: (
    data: Types.IntegrationModifyRequest,
  ): Promise<Types.IntegrationModifyResponse> =>
    http.post('/finance/overdue/integration/modify', data, { mock }),

  // 删除应收逾期集成表
  postIntegrationRemove: (
    data: Types.IntegrationRemoveRequest,
  ): Promise<Types.IntegrationRemoveResponse> =>
    http.post('/finance/overdue/integration/remove', data, { mock }),

  // 应收逾期集成表列表
  postIntegrationList: (
    data: Types.IntegrationListRequest,
  ): Promise<Types.IntegrationListResponse> =>
    http.post('/finance/overdue/integration/list', data, { mock }),

  // 推送应收逾期集成表
  postIntegrationPush: (
    data: Types.IntegrationPushRequest,
  ): Promise<Types.IntegrationPushResponse> =>
    http.post('/finance/overdue/integration/push', data, { mock }),
}

/* prettier-ignore-end */
