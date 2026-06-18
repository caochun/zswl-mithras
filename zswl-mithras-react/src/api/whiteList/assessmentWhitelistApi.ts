/* prettier-ignore-start */
import * as Types from './interface/assessmentWhitelistApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 评估机构白名单-保存出库原因
  postReasonSave: (data: Types.ReasonSaveRequest): Promise<Types.ReasonSaveResponse> =>
    http.post('/appraisalcompany/whitelist/out/reason/save', data, { mock }),

  // 评估机构白名单-分页列表
  postWhitelistPagelist: (
    data: Types.WhitelistPagelistRequest,
  ): Promise<Types.WhitelistPagelistResponse> =>
    http.post('/appraisalcompany/whitelist/pagelist', data, { mock }),

  // 评估机构白名单-删除
  postWhitelistDelete: (
    data: Types.WhitelistDeleteRequest,
  ): Promise<Types.WhitelistDeleteResponse> =>
    http.post('/appraisalcompany/whitelist/delete', data, { mock }),

  // 评估机构白名单-取消操作
  postWhitelistCancel: (
    data: Types.WhitelistCancelRequest,
  ): Promise<Types.WhitelistCancelResponse> =>
    http.post('/appraisalcompany/whitelist/cancel', data, { mock }),

  // 评估机构白名单-提交出库申请
  postOutSubmit: (data: Types.OutSubmitRequest): Promise<Types.OutSubmitResponse> =>
    http.post('/appraisalcompany/whitelist/out/submit', data, { mock }),

  // 评估机构白名单-提交审批
  postWhitelistSubmit: (
    data: Types.WhitelistSubmitRequest,
  ): Promise<Types.WhitelistSubmitResponse> =>
    http.post('/appraisalcompany/whitelist/submit', data, { mock }),

  // 评估机构白名单-新增
  postWhitelistAdd: (data: Types.WhitelistAddRequest): Promise<Types.WhitelistAddResponse> =>
    http.post('/appraisalcompany/whitelist/add', data, { mock }),

  // 评估机构白名单-更新工商信息
  postCommerceRefresh: (
    data: Types.CommerceRefreshRequest,
  ): Promise<Types.CommerceRefreshResponse> =>
    http.post('/appraisalcompany/whitelist/commerce/refresh', data, { mock }),

  // 评估机构白名单-评估机构详情
  postWhitelistDetail: (
    data: Types.WhitelistDetailRequest,
  ): Promise<Types.WhitelistDetailResponse> =>
    http.post('/appraisalcompany/whitelist/detail', data, { mock }),
}

/* prettier-ignore-end */
