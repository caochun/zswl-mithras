/* prettier-ignore-start */
import * as Types from './interface/riskCardTargetCalc'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 下载经济数据
  getCardDownLoad: (params: Types.CardDownLoadRequest): Promise<Types.CardDownLoadResponse> =>
    http.get('/risk/control/score/card/downLoad', { params, mock }),

  // 保存计算结果
  postCalculateSave: (data: Types.CalculateSaveRequest): Promise<Types.CalculateSaveResponse> =>
    http.post('/risk/control/score/card/calculate/save', data, { mock }),

  // 地区全量查找
  postAreaSearch: (data: Types.AreaSearchRequest): Promise<Types.AreaSearchResponse> =>
    http.post('/risk/control/score/card/area/search', data, { mock }),

  // 地区全量查找
  postAreaAll: (data: Types.AreaAllRequest): Promise<Types.AreaAllResponse> =>
    http.post('/risk/control/score/card/area/all', data, { mock }),

  // 导入经济数据
  postCardImport: (data: Types.CardImportRequest): Promise<Types.CardImportResponse> =>
    http.post('/risk/control/score/card/import', data, { mock, type: 'upload', timeout: 0 }),

  // 查询地区得分详情
  postCalculateDetail: (
    data: Types.CalculateDetailRequest
  ): Promise<Types.CalculateDetailResponse> =>
    http.post('/risk/control/score/card/calculate/detail', data, { mock }),

  // 生效
  postCardEffect: (data: Types.CardEffectRequest): Promise<Types.CardEffectResponse> =>
    http.post('/risk/control/score/card/effect', data, { mock }),

  // 获取评分卡信息
  postChangeCard: (data: Types.ChangeCardRequest): Promise<Types.ChangeCardResponse> =>
    http.post('/risk/control/score/card/change/card', data, { mock }),

  // 计算
  postCardCalculate: (data: Types.CardCalculateRequest): Promise<Types.CardCalculateResponse> =>
    http.post('/risk/control/score/card/calculate', data, { mock }),

  // 试计算
  postTryCalculate: (data: Types.TryCalculateRequest): Promise<Types.TryCalculateResponse> =>
    http.post('/risk/control/score/card/try/calculate', data, { mock }),
}

/* prettier-ignore-end */
