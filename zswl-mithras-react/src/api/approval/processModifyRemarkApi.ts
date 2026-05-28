/* prettier-ignore-start */
import * as Types from './interface/processModifyRemarkApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 变更流程-附加标记信息-修改
  postRemarkModify: (
    data: Types.RemarkModifyRequest,
    functionCode: string
  ): Promise<Types.RemarkModifyResponse> =>
    http.post('/process/modify/remark/modify', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 变更流程-附加标记信息-所有（包括历史）
  postRemarkAll: (
    data: Types.RemarkAllRequest,
    functionCode: string
  ): Promise<Types.RemarkAllResponse> =>
    http.post('/process/modify/remark/all', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 变更流程-附加标记信息-新增
  postRemarkAdd: (
    data: Types.RemarkAddRequest,
    functionCode: string
  ): Promise<Types.RemarkAddResponse> =>
    http.post('/process/modify/remark/add', data, {
      mock,
      headers: {
        functionCode,
      },
    }),

  // 变更流程-附加标记信息-详情
  postRemarkDetail: (
    data: Types.RemarkDetailRequest,
    functionCode: string
  ): Promise<Types.RemarkDetailResponse> =>
    http.post('/process/modify/remark/detail', data, {
      mock,
      headers: {
        functionCode,
      },
    }),
}

/* prettier-ignore-end */
