/* prettier-ignore-start */
import * as Types from './interface/evaluationAgencyApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 关联评估机构信息
  postAppraisalRelation: (
    data: Types.AppraisalRelationRequest
  ): Promise<Types.AppraisalRelationResponse> =>
    http.post('/ledger/appraisal/relation', data, { mock }),

  // 新增评估机构
  postAppraisalAdd: (data: Types.AppraisalAddRequest): Promise<Types.AppraisalAddResponse> =>
    http.post('/ledger/appraisal/add', data, { mock }),

  // 更新评估机构最新信息
  postAppraisalLasted: (
    data: Types.AppraisalLastedRequest
  ): Promise<Types.AppraisalLastedResponse> =>
    http.post('/ledger/appraisal/lasted', data, { mock }),

  // 模糊搜索评估机构
  postAppraisalQueryCompany: (
    data: Types.AppraisalQueryCompanyRequest
  ): Promise<Types.AppraisalQueryCompanyResponse> =>
    http.post('/ledger/appraisal/queryCompany', data, { mock }),

  // 租赁物内评估机构列表
  postLeaseItemList: (data: Types.LeaseItemListRequest): Promise<Types.LeaseItemListResponse> =>
    http.post('/ledger/appraisal/leaseItem/list', data, { mock }),

  // 获取系统中所有评估机构
  postCompanyList: (data: Types.CompanyListRequest): Promise<Types.CompanyListResponse> =>
    http.post('/ledger/appraisal/company/list', data, { mock }),

  // 评估机构信息详情
  postAppraisalDetail: (
    data: Types.AppraisalDetailRequest,
    functionCode: string = 'ledgerAppraisalDetail'
  ): Promise<Types.AppraisalDetailResponse> =>
    http.post('/ledger/appraisal/detail', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
  // /contract/evaluation/agency/list
  postEvaluationAgencyList: (data: any, functionCode: string): Promise<any> =>
    http.post('/contract/evaluation/agency/list', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}

/* prettier-ignore-end */
