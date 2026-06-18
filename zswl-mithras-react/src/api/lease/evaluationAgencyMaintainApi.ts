import { http } from '@zswl/admin'

const mock = false

export default {
  postAppraisalRelation: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/relation', data, { mock }),
  postAppraisalAdd: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/add', data, { mock }),
  postAppraisalLasted: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/lasted', data, { mock }),
  postLeaseItemList: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/leaseItem/list', data, { mock }),
  postCompanyList: (data: any): Promise<any> =>
    http.post('/ledger/appraisal/company/list', data, { mock }),
  postAppraisalDetail: (
    data: any,
    functionCode: string = 'ledgerAppraisalDetail'
  ): Promise<any> =>
    http.post('/ledger/appraisal/detail', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}
