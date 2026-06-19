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

  postReviewList: (data?: any): Promise<any> =>
    http.post('/materials/group/credit/review/list', data, { mock }),

  postReviewUpload: (data?: any): Promise<any> =>
    http.post('/materials/upload', data, {
      mock,
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsupload-creditreview',
      },
      type: 'upload',
      timeout: 0,
    }),

  postReviewRemove: (data?: any): Promise<any> =>
    http.post('/materials/remove', data, {
      mock,
      transformResult: (res) => res.data,
      headers: {
        functionCode: 'materialsremove-creditreview',
      },
    }),

  postReviewDownload: (params?: any): Promise<any> =>
    http('/materials/download', {
      params,
      mock,
      type: 'download',
      headers: {
        functionCode: 'materialsdownload-creditreview',
      },
      timeout: 0,
    }),
}
