/* prettier-ignore-start */
import * as Types from './interface/fastFunctionApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 新增首页工作台-快捷功能-列表
  postShortcutsList: (data: Types.ShortcutsListRequest): Promise<Types.ShortcutsListResponse> =>
    http.post('/workbench/shortcuts/list', data, { mock }),

  // 新增首页工作台-快捷功能-维护
  postShortcutsMaintain: (
    data: Types.ShortcutsMaintainRequest,
  ): Promise<Types.ShortcutsMaintainResponse> =>
    http.post('/workbench/shortcuts/maintain', data, { mock }),
}

/* prettier-ignore-end */
