import { http } from '@zswl/admin'

export default {
  getLegalAddressList: (params) => http.post('/corp/address/list', params),
  getApprovalAddressList: (params) => http.post('/corp/address/list/compare', params),
  getMaterialsList: (params) => http.post('/materials/list', params),
  removeAddress: (params) => http.post('/corp/address/remove', params),
  createAddress: (params) => http.post('/corp/address/add', params),
  editAddress: (params) => http.post('/corp/address/modify', params),
  commerceDetail: (params) => http.post('/corp/commerce/detail', params),
  commerceModify: (params) => http.post('/corp/commerce/modify', params),
  naturalModify: (params) => http.post('/normal/base/info/modify', params),
  naturalDetail: (params) => http.post('/normal/base/info/detail', params),
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
  materialsUpload: (params, config) =>
    http.post('/materials/upload', params, {
      headers: {
        functionCode: 'materialsupload',
      },
      ...config,
      timeout: 0,
      type: 'upload',
    }),
  postClientMaterialsUpload: (params) =>
    http.post('/materials/upload', params, {
      headers: {
        functionCode: 'materialsupload',
      },
      transformResult: (res) => res.data,
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
  synchronization: (params) => http.post('/client/sync', params),
  getRegionList: (params) => http.get('/select/region/child', { params }),
  createcommerceInfo: (params) => http.post('/client/tyc/commerceInfo', params),
  getApprovalShareholderList: (params) => http.post('/corp/shareholder/info/list/compare', params),
  getApprovalCommercedetail: (params) => http.post('/corp/commerce/detail/compare', params),
  getApprovalNormaldetail: (params) => http.post('/normal/base/info/detai/compare', params),
  getShareholderList: (params) => http.post('/corp/shareholder/info/list', params),
  addShareholder: (params) => http.post('/corp/shareholder/info/add', params),
  removeShareholder: (params) => http.post('/corp/shareholder/info/remove', params),
  editShareholder: (params) => http.post('/corp/shareholder/info/modify', params),
  getRelatedList: (params) => http.post('/corp/related/enterprise/list', params),
  addEnterprise: (params) => http.post('/corp/related/enterprise/add', params),
  removeEnterprise: (params) => http.post('/corp/related/enterprise/remove', params),
  editEnterprise: (params) => http.post('/corp/related/enterprise/modify', params),
  postClientApplyOwn: (data) => http.post('/client/apply/own', data),
  postClientApplyStatus: (data) => http.post('/client/apply/status', data),
  postClientAuthorityEffect: (data) => http.post('/client/authority/effect', data),
}
