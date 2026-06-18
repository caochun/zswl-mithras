import { http } from '@zswl/admin'

const mock = false

type EstablishListRequest = {
  groupCreditEstablishId: string | number
  businessVersion?: string | number
}

type ProjDownloadRequest = {
  ids: Array<string | number>
}

export default {
  // 集团授信立项资料清单-列表
  postEstablishList: (data: EstablishListRequest): Promise<any> =>
    http.post('/materials/group/credit/establish/list', data, { mock }),

  // 立项资料清单-批量下载
  postProjDownload: (params: ProjDownloadRequest): Promise<any> =>
    http('/materials/proj/download', {
      params,
      mock,
      type: 'download',
      timeout: 0,
    }),
}
