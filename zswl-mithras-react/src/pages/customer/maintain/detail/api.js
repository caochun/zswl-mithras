import { http } from '@zswl/admin'

export default {
  //获取客户按钮状态
  getButtonStatus: (params) => http.post('/client/button/status', params),
  // 删除财报
  removeSubjectItem: (params) => http.post('/corp/subject/item/remove', params),

  // 检查客户是否被占有
  postClientApplyOccupy: (data) => http.post('/client/apply/occupy', data),
  // 客户详情页面是否有编辑查看权限
  postClientApplyOwn: (data) => http.post('/client/apply/own', data),

  // 得到客户状态和管控权限
  postClientApplyStatus: (data) => http.post('/client/apply/status', data),
  // 客户权限生效（或提交审批）
  postClientAuthorityEffect: (data) => http.post('/client/authority/effect', data),
  //客户生效（或提交审批）
  clientEffect: (params) => http.post('/client/effect', params),
}
