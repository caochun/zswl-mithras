import { http } from '@zswl/admin'

export default {
  getLegalAddressList: (params) => http.post('/corp/address/list', params),
  getApprovalAddressList: (params) => http.post('/corp/address/list/compare', params),
  //getBackAccountList: (params) => http.post('/corp/bank/account/list', params),
  getMaterialsList: (params) => http.post('/materials/list', params),
  removeAddress: (params) => http.post('/corp/address/remove', params),
  createAddress: (params) => http.post('/corp/address/add', params),
  editAddress: (params) => http.post('/corp/address/modify', params),
  //工商信息接口
  commerceDetail: (params) => http.post('/corp/commerce/detail', params),
  commerceModify: (params) => http.post('/corp/commerce/modify', params),
  //自然人信息
  naturalModify: (params) => http.post('/normal/base/info/modify', params),
  naturalDetail: (params) => http.post('/normal/base/info/detail', params),

  //客户生效（或提交审批）
  clientEffect: (params) => http.post('/client/effect', params),

  getClientGroupList: (params) => http.post('/client/group/list', { ...params }),

  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-9',
        },
      }
    ),

  //资料清单
  materialsUpload: (params, config) =>
    http.post('/materials/upload', params, {
      headers: {
        functionCode: 'materialsupload',
      },
      ...config,
      timeout: 0,
      type: 'upload',
    }),
  deleteMaterialsUpload: (params) =>
    http.post('/materials/remove', params, {
      headers: {
        functionCode: 'materialsremove',
      },
    }),
  downloadMaterialsUpload: (params) =>
    http('/materials/download', {
      params,
      type: 'download',
      fileName: params.filename,
      timeout: 0,
      headers: {
        functionCode: 'materialsdownload',
      },
    }),

  getAllIndustry: (params) => http.get('/select/industry/all', { params }),

  //基本信息同步
  synchronization: (params) => http.post('/client/sync', params),

  // 地区获取省/市/区列表
  getRegionList: (params) => http.get('/select/region/child', { params }),

  createcommerceInfo: (params) => http.post('/client/tyc/commerceInfo', params),

  //审批流基本信息 变更日志的对比
  getApprovalShareholderList: (params) => http.post('/corp/shareholder/info/list/compare', params),
  //法人
  getApprovalCommercedetail: (params) => http.post('/corp/commerce/detail/compare', params),
  //自然人
  getApprovalNormaldetail: (params) => http.post('/normal/base/info/detai/compare', params),

  getShareholderList: (params) => http.post('/corp/shareholder/info/list', params),
  addShareholder: (params) => http.post('/corp/shareholder/info/add', params),
  removeShareholder: (params) => http.post('/corp/shareholder/info/remove', params),
  editShareholder: (params) => http.post('/corp/shareholder/info/modify', params),

  getRelatedList: (params) => http.post('/corp/related/enterprise/list', params),
  addEnterprise: (params) => http.post('/corp/related/enterprise/add', params),
  removeEnterprise: (params) => http.post('/corp/related/enterprise/remove', params),
  editEnterprise: (params) => http.post('/corp/related/enterprise/modify', params),

  // 客户详情页面是否有编辑查看权限
  postClientApplyOwn: (data) => http.post('/client/apply/own', data),

  // 得到客户状态和管控权限
  postClientApplyStatus: (data) => http.post('/client/apply/status', data),
  // 客户权限生效（或提交审批）
  postClientAuthorityEffect: (data) => http.post('/client/authority/effect', data),
}
