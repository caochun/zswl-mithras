/* prettier-ignore-start */
import * as Types from './interface/queryExternalDataApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 所属企业
  postAffiliatedEnterprise: (
    data: Types.AffiliatedEnterpriseRequest,
    functionCode: string = 'publicBlackQueryAffiliatedEnterprise'
  ): Promise<Types.AffiliatedEnterpriseResponse> =>
    http.post('/public/black/query/affiliated/enterprise', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 批量填充所属企业及下属企业 不要重新生成，改过名了
  postBatchAssociatedEnterprise: (
    data: Types.AssociatedEnterpriseRequest
  ): Promise<Types.AssociatedEnterpriseResponse> =>
    http.post('/public/black/batch/query/associated/enterprise', data, { mock }),

  // 查询下属企业
  postAssociatedEnterprise: (
    data: Types.AssociatedEnterpriseRequest
  ): Promise<Types.AssociatedEnterpriseResponse> =>
    http.post('/public/black/query/associated/enterprise', data, { mock }),

  // 黑灰名单模糊查询企业信息
  postVagueEnterprise: (
    data: Types.VagueEnterpriseRequest,
    functionCode: string = 'queryVagueMain'
  ): Promise<Types.VagueEnterpriseResponse> =>
    http.post('/public/black/query/vague/enterprise', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
  // /black/gray/base/info/library
  getLibrary: (data: any, functionCode: string = 'clientBlackGrayBaseInfoLibrary'): Promise<any> =>
    http.post('/black/gray/base/info/library', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}

/* prettier-ignore-end */
