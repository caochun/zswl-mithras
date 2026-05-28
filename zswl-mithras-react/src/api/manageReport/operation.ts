/* prettier-ignore-start */
import * as Types from './interface/operation'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 业务运行分析-明细列表
  postYewuyunyingfenxiDetail: (
    data: Types.ManagereportyewuyunyingfenxiDetailRequest,
  ): Promise<Types.ManagereportyewuyunyingfenxiDetailResponse> =>
    http.post('/managereport/yewuyunyingfenxi/detail', data, { mock }),

  // 业务运行分析-统计列表
  postYewuyunyingfenxiStatistic: (
    data: Types.ManagereportyewuyunyingfenxiStatisticRequest,
  ): Promise<Types.ManagereportyewuyunyingfenxiStatisticResponse> =>
    http.post('/managereport/yewuyunyingfenxi/statistic', data, { mock }),

  // 运营待办-明细列表
  postYunyingdaibanDetail: (
    data: Types.ManagereportyunyingdaibanDetailRequest,
  ): Promise<Types.ManagereportyunyingdaibanDetailResponse> =>
    http.post('/managereport/yunyingdaiban/detail', data, { mock }),

  // 运营待办-统计列表
  postYunyingdaibanStatistic: (
    data: Types.ManagereportyunyingdaibanStatisticRequest,
  ): Promise<Types.ManagereportyunyingdaibanStatisticResponse> =>
    http.post('/managereport/yunyingdaiban/statistic', data, { mock }),
}

/* prettier-ignore-end */
