/* prettier-ignore-start */
import * as Types from './interface/customerRatApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 客户下拉列表
  postClientList: (data: any, functionCode: string): Promise<any> =>
    http.post(
      '/client/list',
      { scene: 'query', ...data },
      {
        timeout: 0,
        headers: {
          functionCode,
        },
      }
    ),

  // 客户信息
  postClientInfo: (data: Types.ClientInfoRequest): Promise<Types.ClientInfoResponse> =>
    http.post('/rating/client/info', data, { mock }),

  // 客户评级修改
  postClientUpdate: (data: Types.ClientUpdateRequest): Promise<Types.ClientUpdateResponse> =>
    http.post('/rating/client/update', data, { mock }),

  // 客户评级准入校验
  postClientAccessCheck: (
    data: Types.ClientAccessCheckRequest
  ): Promise<Types.ClientAccessCheckResponse> =>
    http.post('/rating/client/accessCheck', data, { mock }),

  // 客户评级列表
  postClientPage: (
    data: Types.ClientPageRequest,
    functionCode: string = 'ratingClientPage'
  ): Promise<Types.ClientPageResponse> =>
    http.post('/rating/client/page', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 客户评级删除
  postClientDelete: (data: Types.ClientDeleteRequest): Promise<Types.ClientDeleteResponse> =>
    http.post('/rating/client/delete', data, { mock }),

  // 客户评级新增
  postClientAdd: (data: Types.ClientAddRequest): Promise<Types.ClientAddResponse> =>
    http.post('/rating/client/add', data, { mock }),

  // 客户评级新增校验
  postClientAddCheck: (data: Types.ClientAddCheckRequest): Promise<Types.ClientAddCheckResponse> =>
    http.post('/rating/client/addCheck', data, { mock }),

  // 客户评级详情
  postClientDetail: (data: Types.ClientDetailRequest): Promise<Types.ClientDetailResponse> =>
    http.post('/rating/client/detail', data, { mock }),

  // 指标审批
  postClientIndexApproval: (
    data: Types.ClientIndexApprovalRequest
  ): Promise<Types.ClientIndexApprovalResponse> =>
    http.post('/rating/client/indexApproval', data, { mock }),

  // 推翻记录
  postClientOverturnRecord: (
    data: Types.ClientOverturnRecordRequest
  ): Promise<Types.ClientOverturnRecordResponse> =>
    http.post('/rating/client/overturnRecord', data, { mock }),

  // 提交审批
  postClientEffect: (data: Types.ClientEffectRequest): Promise<Types.ClientEffectResponse> =>
    http.post('/rating/client/effect', data, { mock }),

  // 摘要信息
  postClientAbstract: (data: Types.ClientAbstractRequest): Promise<Types.ClientAbstractResponse> =>
    http.post('/rating/client/abstract', data, { mock }),

  // 模型获取
  postRatingModelQuery: (
    data: Types.RatingModelQueryRequest
  ): Promise<Types.RatingModelQueryResponse> => http.post('/rating/modelQuery', data, { mock }),

  // 确认完成评级\/保存
  postClientFinish: (data: Types.ClientFinishRequest): Promise<Types.ClientFinishResponse> =>
    http.post('/rating/client/finish', data, { mock }),

  // 评级报告
  postClientReport: (data: Types.ClientReportRequest): Promise<Types.ClientReportResponse> =>
    http.post('/rating/client/report', data, { mock }),

  // 评级推翻
  postClientOverturn: (data: Types.ClientOverturnRequest): Promise<Types.ClientOverturnResponse> =>
    http.post('/rating/client/overturn', data, { mock }),

  // 评级推翻审核
  postOverturnApproval: (
    data: Types.OverturnApprovalRequest
  ): Promise<Types.OverturnApprovalResponse> =>
    http.post('/rating/client/overturn/approval', data, { mock }),

  // 评级调整
  postClientAdjust: (data: Types.ClientAdjustRequest): Promise<Types.ClientAdjustResponse> =>
    http.post('/rating/client/adjust', data, { mock }),
  postClientSupplementFileUpload: (data: any, functionCode: string): Promise<any> =>
    http.post('/file/upload', data, {
      mock,
      type: 'upload',
      transformResult: (res) => res.data,
      timeout: 0,
      headers: {
        functionCode,
      },
    }),

  // 试算
  postClientExecute: (data: Types.ClientExecuteRequest): Promise<Types.ClientExecuteResponse> =>
    http.post('/rating/client/execute', data, { mock }),

  // 问卷获取
  postClientParamInfo: (
    data: Types.ClientParamInfoRequest
  ): Promise<Types.ClientParamInfoResponse> =>
    http.post('/rating/client/paramInfo', data, { mock }),
  // /rating/client/indexCheck
  postIndexCheck: (data) => {
    return http.post('/rating/client/indexCheck', data, { mock })
  },
  ///rating/client/area/indicator/modify
  postAreaIndicatorModify: (data) => {
    return http.post('/rating/client/area/indicator/modify', data, { mock })
  },
  // /rating/client/area/indicator/batchmodify
  postAreaIndicatorBatchmodify: (data) => {
    return http.post('/rating/client/area/indicator/batchmodify', data, { mock })
  },
}

/* prettier-ignore-end */
