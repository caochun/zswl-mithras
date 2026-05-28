/* prettier-ignore-start */
import * as Types from './interface/weight'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 绩效考核-项目分配-分配比重-保存
  postWeightSave: (data: Types.WeightSaveRequest): Promise<Types.WeightSaveResponse> =>
    http.post('/kpi/projectdistribution/weight/save', data, { mock }),

  // 绩效考核-部分分配-分配比重-保存
  postDeptWeightSave: (data) =>
    http.post('/kpi/project/distribution/dept/weight/save', data, { mock }),

  // 绩效考核-项目分配-分配比重-详情
  postWeightDetail: (data: Types.WeightDetailRequest): Promise<Types.WeightDetailResponse> =>
    http.post('/kpi/projectdistribution/weight/detail', data, { mock }),
}

/* prettier-ignore-end */
