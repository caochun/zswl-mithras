/* prettier-ignore-start */
import * as Types from './interface/predictListApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 删除资产减值预测表
  postInfoRemove: (data: Types.InfoRemoveRequest): Promise<Types.InfoRemoveResponse> =>
    http.post('/ecl/execute/predict/base/info/remove', data, { mock }),

  // 删除资产减值预测详情记录表
  postPredictCalculation: (
    data: Types.PredictCalculationRequest
  ): Promise<Types.PredictCalculationResponse> =>
    http.post('/ecl/execute/predict/calculation', data, { mock, timeout: 0 }),

  // 新增资产减值预测表
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/ecl/execute/predict/base/info/add', data, { mock, timeout: 0 }),

  // 资产减值预测表列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/ecl/execute/predict/base/info/list', data, { mock }),
}

/* prettier-ignore-end */
