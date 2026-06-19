import { http } from '@zswl/admin'

const mock = false

export default {
  // 绩效考核-项目分配-基本信息-详情
  postBaseinfoDetail: (data) =>
    http.post('/finance/projectdistribution/baseinfo/detail', data, { mock }),
}
