/* prettier-ignore-start */
import * as Types from './interface/guess'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 绩效-项目测算表-人员列表
  postPeopleList: (data: Types.GuesspeopleListRequest): Promise<Types.GuesspeopleListResponse> =>
    http.post('/kpi/proj/guess/people/list', data, { mock }),

  // 绩效-项目测算表-人员详情
  postPeopleDetail: (
    data: Types.GuesspeopleDetailRequest,
  ): Promise<Types.GuesspeopleDetailResponse> =>
    http.post('/kpi/proj/guess/people/detail', data, { mock }),

  // 绩效-项目测算表-合同列表
  postContractList: (
    data: Types.GuesscontractListRequest,
  ): Promise<Types.GuesscontractListResponse> =>
    http.post('/kpi/proj/guess/contract/list', data, { mock }),

  // 绩效-项目测算表-合同详情
  postContractDetail: (
    data: Types.GuesscontractDetailRequest,
  ): Promise<Types.GuesscontractDetailResponse> =>
    http.post('/kpi/proj/guess/contract/detail', data, { mock }),

  // 绩效-项目测算表-时间列表
  postTimeList: (data: Types.GuesstimeListRequest): Promise<Types.GuesstimeListResponse> =>
    http.post('/kpi/proj/guess/time/list', data, { mock }),

  // 绩效-项目测算表-测算
  postGuessCalculate: (
    data: Types.ProjguessCalculateRequest,
  ): Promise<Types.ProjguessCalculateResponse> =>
    http.post('/kpi/proj/guess/calculate', data, { mock }),

  // 绩效-项目测算表-部门列表
  postDeptList: (data: Types.GuessdeptListRequest): Promise<Types.GuessdeptListResponse> =>
    http.post('/kpi/proj/guess/dept/list', data, { mock }),

  // 绩效-项目测算表-部门池列表
  postPoolList: (data: Types.DeptpoolListRequest): Promise<Types.DeptpoolListResponse> =>
    http.post('/kpi/proj/guess/dept/pool/list', data, { mock }),

  // 绩效-项目测算表-部门池详情
  postPoolDetail: (data: Types.DeptpoolDetailRequest): Promise<Types.DeptpoolDetailResponse> =>
    http.post('/kpi/proj/guess/dept/pool/detail', data, { mock }),

  // 绩效-项目测算表-项目经理列表
  postManagerList: (data: Types.ProjmanagerListRequest): Promise<Types.ProjmanagerListResponse> =>
    http.post('/kpi/proj/guess/proj/manager/list', data, { mock }),

  // 绩效-项目测算表-项目经理利润完成率列表
  postCompletionList: (
    data: Types.ManagercompletionListRequest,
  ): Promise<Types.ManagercompletionListResponse> =>
    http.post('/kpi/proj/guess/proj/manager/completion/list', data, { mock }),

  // 绩效-项目测算表-项目经理利润完成率详情
  postCompletionDetail: (
    data: Types.ManagercompletionDetailRequest,
  ): Promise<Types.ManagercompletionDetailResponse> =>
    http.post('/kpi/proj/guess/proj/manager/completion/detail', data, { mock }),

  // 绩效-项目测算表-项目经理详情
  postManagerDetail: (
    data: Types.ProjmanagerDetailRequest,
  ): Promise<Types.ProjmanagerDetailResponse> =>
    http.post('/kpi/proj/guess/proj/manager/detail', data, { mock }),
}

/* prettier-ignore-end */
