/* prettier-ignore-start */
import * as Types from './interface/baseInfo'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 绩效考核-项目分配-基本信息-修改
  postBaseinfoModify: (data: Types.BaseinfoModifyRequest): Promise<Types.BaseinfoModifyResponse> =>
    http.post('/kpi/projectdistribution/baseinfo/modify', data, { mock }),

  // 绩效考核-项目分配-基本信息-详情
  postBaseinfoDetail: (data: Types.BaseinfoDetailRequest): Promise<Types.BaseinfoDetailResponse> =>
    http.post('/kpi/projectdistribution/baseinfo/detail', data, { mock }),
}

/* prettier-ignore-end */
