/* prettier-ignore-start */
import * as Types from './interface/creditSearchProjectApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 征信查询列表
  postProjectList: (data: Types.ProjectListRequest): Promise<Types.ProjectListResponse> =>
    http.post('/creditreport/project/list', data, { mock }),

  // 征信查询删除
  getProjectDelete: (params: Types.ProjectDeleteRequest): Promise<Types.ProjectDeleteResponse> =>
    http.get('/creditreport/project/delete', { params, mock }),

  // 根据项目id反显项目信息和关联客户信息
  getProjectShowCreditReportByProjId: (
    params: Types.ProjectShowCreditReportByProjIdRequest,
  ): Promise<Types.ProjectShowCreditReportByProjIdResponse> =>
    http.get('/creditreport/project/showCreditReportByProjId', { params, mock }),

  // 根据项目id反显项目信息和关联客户信息
  postProjectShowCreditReportByProjId: (
    data: Types.ProjectShowCreditReportByProjIdRequest,
  ): Promise<Types.ProjectShowCreditReportByProjIdResponse> =>
    http.post('/creditreport/project/showCreditReportByProjId', data, { mock }),
}

/* prettier-ignore-end */
