import { http } from '@zswl/admin'

export default {
  postProjectDataDetail: (params) => http.post('/materials/proj/list', params),
  postProjectBaseInfoDetail: (params) => http.post('/proj/establish/base/info/detail', params),
  postProjectBaseInfoDetailCompare: (params) =>
    http.post('/proj/establish/base/info/detail/compare', params),
  postProjectBaseInfoModify: (params) =>
    http.post('/proj/establish/base/info/modify', params, {
      transformResult: (res) => res.data,
    }),
  postProjectBaseInfoExposure: (params) => http.post('/proj/establish/base/info/exposure', params),
  submitApproval: (params) =>
    http.post('/proj/establish/effect', { ...params }, {
      transformResult: (res) => res.data,
    }),
  postGetAddressByClientId: (params) =>
    http.post('/proj/establish/get/client/address', params, {
      headers: {
        functionCode: 'projEstablishGetClientAddress',
      },
    }),
}
