import { http } from '@zswl/admin'

export default {
  submitChange: (params) => http.post('/contract/flow/change/submit', params, {}),

  // 提前还款 - 获取
  get_earlyRepaymant: (params) => http.post('/contract/prepayment/list', params, {}),
  // 提前还款 - 新增
  add_earlyRepaymant: (params) => http.post('/contract/prepayment/add', params, {}),
  // 提前还款 - 修改
  update_earlyRepaymant: (params) => http.post('/contract/prepayment/modify', params, {}),

  // lpr/展期 - 获取
  get_baseinfo: (params) => http.post('/contract/price/detail', params),
  
  // 合同查询最近别版本报价方案
  get_old_baseinfo: (params) => http.post('/contract/price/old/detail', params),
  // lpr/展期 - 修改
  update_baseinfo: (params) => http.post('/contract/flow/change/conserve', params, {}),

  postDataList: (params) => http.post('/contract/flow/change/down', params),
  postDataUpload: (params) =>
    http.post('/contract/flow/change/upload', params, {
      type: 'upload',
      timeout: 0,
    }),
  deleteMaterialsUpload: (params) => http.post('/contract/flow/change/delete', params, {}),

  // 取消
  cancelFlow: (params) => http.post('/contract/flow/change/cancel', params, {}),

  // 提前还款表-计算
  calculationValue: (params) => http.post('/contract/prepayment/calculation', params, {}),
  // 保存调整说明
  saveAdjustRemark: (params) => http.post('/contract/base/info/adjustremark/save', params, {}),
  // 基本信息
  getBaseInfo: (params) => http.get('/contract/base/info/detail', { params }),
}
