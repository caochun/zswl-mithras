/* prettier-ignore-start */
import * as Types from '@/api/groupCredit/interface/projectApprovalVersion'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  // 集团授信立项信息版本比较详情（与上一版本比较）
  postComparePreVersion: (
    data: Types.ComparePreVersionRequest,
  ): Promise<Types.ComparePreVersionResponse> =>
    http.post('/group/credit/establish/compare/preVersion', data, { mock }),

  // 集团授信立项信息版本表列
  postVersionList: (data: Types.VersionListRequest): Promise<Types.VersionListResponse> =>
    http.post('/group/credit/establish/version/list', data, { mock }),

  // 集团授信立项信息生效（或提交审批）
  postEstablishEffect: (
    data: Types.EstablishEffectRequest,
  ): Promise<Types.EstablishEffectResponse> =>
    http.post('/group/credit/establish/effect', data, { mock }),
}

/* prettier-ignore-end */
