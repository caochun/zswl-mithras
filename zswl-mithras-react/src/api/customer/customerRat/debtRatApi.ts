/* prettier-ignore-start */
import * as Types from './interface/debtRatApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 债项评级修改
  postAmountUpdate: (data: Types.AmountUpdateRequest): Promise<Types.AmountUpdateResponse> =>
    http.post('/rating/amount/update', data, { mock }),

  // 债项评级准入校验
  postAmountAccessCheck: (
    data: Types.AmountAccessCheckRequest,
  ): Promise<Types.AmountAccessCheckResponse> =>
    http.post('/rating/amount/accessCheck', data, { mock }),

  // 债项评级列表
  postAmountPage: (data: Types.AmountPageRequest): Promise<Types.AmountPageResponse> =>
    http.post('/rating/amount/page', data, { mock }),

  // 债项评级删除
  postAmountDelete: (data: Types.AmountDeleteRequest): Promise<Types.AmountDeleteResponse> =>
    http.post('/rating/amount/delete', data, { mock }),

  // 债项评级新增
  postAmountAdd: (data: Types.AmountAddRequest): Promise<Types.AmountAddResponse> =>
    http.post('/rating/amount/add', data, { mock }),

  // 债项评级评估主体下拉框
  postAmountLesseeInfo: (
    data: Types.AmountLesseeInfoRequest,
  ): Promise<Types.AmountLesseeInfoResponse> =>
    http.post('/rating/amount/lesseeInfo', data, { mock }),

  // 债项评级详情
  postAmountDetail: (data: Types.AmountDetailRequest): Promise<Types.AmountDetailResponse> =>
    http.post('/rating/amount/detail', data, { mock }),

  // 债项评级项目信息
  postAmountProjInfo: (data: Types.AmountProjInfoRequest): Promise<Types.AmountProjInfoResponse> =>
    http.post('/rating/amount/projInfo', data, { mock }),

  // 客户评级新增校验
  postAmountAddCheck: (data: Types.AmountAddCheckRequest): Promise<Types.AmountAddCheckResponse> =>
    http.post('/rating/amount/addCheck', data, { mock }),

  // 指标审批
  postAmountIndexApproval: (
    data: Types.AmountIndexApprovalRequest,
  ): Promise<Types.AmountIndexApprovalResponse> =>
    http.post('/rating/amount/indexApproval', data, { mock }),

  // 提交审批
  postAmountEffect: (data: Types.AmountEffectRequest): Promise<Types.AmountEffectResponse> =>
    http.post('/rating/amount/effect', data, { mock }),

  // 确认完成评级\/保存
  postAmountFinish: (data: Types.AmountFinishRequest): Promise<Types.AmountFinishResponse> =>
    http.post('/rating/amount/finish', data, { mock }),

  // 自动匹配模型
  postAmountModelMatch: (
    data: Types.AmountModelMatchRequest,
  ): Promise<Types.AmountModelMatchResponse> =>
    http.post('/rating/amount/modelMatch', data, { mock }),

  // 评级报告
  postAmountReport: (data: Types.AmountReportRequest): Promise<Types.AmountReportResponse> =>
    http.post('/rating/amount/report', data, { mock }),

  // 试算
  postAmountExecute: (data: Types.AmountExecuteRequest): Promise<Types.AmountExecuteResponse> =>
    http.post('/rating/amount/execute', data, { mock }),

  // 问卷获取
  postAmountParamInfo: (
    data: Types.AmountParamInfoRequest,
  ): Promise<Types.AmountParamInfoResponse> =>
    http.post('/rating/amount/paramInfo', data, { mock }),
}

/* prettier-ignore-end */
