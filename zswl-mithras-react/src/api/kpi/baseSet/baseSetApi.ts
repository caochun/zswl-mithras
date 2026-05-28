/* prettier-ignore-start */
import * as Types from './interface/baseSetApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 删除绩效考核-参数设置基本表
  postBaseRemove: (data: Types.BaseRemoveRequest): Promise<Types.BaseRemoveResponse> =>
    http.post('/kpi/parameter/base/remove', data, { mock }),

  // 删除绩效考核-参数设置基本表
  postBaseRemove: (data: Types.BaseRemoveRequest): Promise<Types.BaseRemoveResponse> =>
    http.post('/kpi/parameter/base/remove', data, { mock }),

  // 复制到绩效考核-参数设置基本表
  postBaseCopy: (data: Types.BaseCopyRequest): Promise<Types.BaseCopyResponse> =>
    http.post('/kpi/parameter/base/copy', data, { mock }),

  // 新增绩效考核-参数设置基本表
  postBaseAdd: (data: Types.BaseAddRequest): Promise<Types.BaseAddResponse> =>
    http.post('/kpi/parameter/base/add', data, { mock }),

  // 绩效考核-参数设置基本表-关闭
  postBaseClose: (data: Types.BaseCloseRequest): Promise<Types.BaseCloseResponse> =>
    http.post('/kpi/parameter/base/close', data, { mock }),

  // 绩效考核-参数设置基本表-生效
  postBaseEffect: (data: Types.BaseEffectRequest): Promise<Types.BaseEffectResponse> =>
    http.post('/kpi/parameter/base/effect', data, { mock }),

  // 绩效考核-参数设置基本表列表
  postBaseList: (data: Types.BaseListRequest): Promise<Types.BaseListResponse> =>
    http.post('/kpi/parameter/base/list', data, { mock }),
}

/* prettier-ignore-end */
