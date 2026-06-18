/* prettier-ignore-start */
import * as Types from './interface/reportPlanApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 关闭逾期报送计划表
  postBaseClose: (data: Types.BaseCloseRequest): Promise<Types.BaseCloseResponse> =>
    http.post('/finance/overdue/report/base/close', data, { mock }),

  // 新增逾期报送计划表
  postBaseAdd: (data: Types.BaseAddRequest): Promise<Types.BaseAddResponse> =>
    http.post('/finance/overdue/report/base/add', data, { mock }),

  // 逾期报送计划表列表
  postBaseList: (data: Types.BaseListRequest): Promise<Types.BaseListResponse> =>
    http.post('/finance/overdue/report/base/list', data, { mock }),
}

/* prettier-ignore-end */
