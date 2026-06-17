import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 绩效考核-项目分配-分配比重-保存
  postWeightSave: (data) => http.post('/finance/projectdistribution/weight/save', data, { mock }),

  // 绩效考核-部分分配-分配比重-保存
  postDeptWeightSave: (data) =>
    http.post('/finance/project/distribution/dept/weight/save', data, { mock }),

  // 绩效考核-项目分配-分配比重-详情
  postWeightDetail: (data) =>
    http.post('/finance/project/distribution/dept/weight/detail', data, { mock }),
}
