/* prettier-ignore-start */
import * as Types from '@/api/afterLease/interface/rentalInspectionPlan'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  keepAlive: (): Promise<any> =>
    http.get('/api/ok?type=1', {
      transformResult: (res) => res.data,
    }),

  // 下载指定的项目报告
  postSpecificDownload: (
    data: Types.SpecificDownloadRequest,
  ): Promise<Types.SpecificDownloadResponse> =>
    http.post('/afterlease/checkplan/project/report/specific/download', data, { mock }),

  // 下载指定部门报告
  postDeptDownload: (data: Types.DeptDownloadRequest): Promise<Types.DeptDownloadResponse> =>
    http.post('/afterlease/checkplan/project/report/dept/download', data, { mock }),

  // 保存需检查项目
  postProjectSave: (data: Types.ProjectSaveRequest): Promise<Types.ProjectSaveResponse> =>
    http.post('/afterlease/checkplan/project/save', data, { mock }),

  // 变更项目报告类型（变更模板）
  postReporttypeChange: (
    data: Types.ReporttypeChangeRequest,
  ): Promise<Types.ReporttypeChangeResponse> =>
    http.post('/afterlease/checkplan/project/reporttype/change', data, { mock }),

  // 新增需检查项目
  postProjectAdd: (data: Types.ProjectAddRequest): Promise<Types.ProjectAddResponse> =>
    http.post('/afterlease/checkplan/project/add', data, { mock }),

  // 移除被选择的检查项目
  postProjectRemove: (data: Types.ProjectRemoveRequest): Promise<Types.ProjectRemoveResponse> =>
    http.post('/afterlease/checkplan/project/remove', data, { mock }),

  // 获取按照业务部门分组的项目信息
  postProjectListGroupByDept: (
    data: Types.ProjectListGroupByDeptRequest,
  ): Promise<Types.ProjectListGroupByDeptResponse> =>
    http.post('/afterlease/checkplan/project/listGroupByDept', data, { mock }),

  // 获取检查计划项目信息
  postInfoGet: (data: Types.InfoGetRequest): Promise<Types.InfoGetResponse> =>
    http.post('/afterlease/checkplan/project/info/get', data, { mock }),

  // 获取检查项目列表
  postProjectList: (data: Types.ProjectListRequest): Promise<Types.ProjectListResponse> =>
    http.post('/afterlease/checkplan/project/list', data, { mock }),

  // 非季度检查计划查询项目
  postProjectQuery: (data: Types.ProjectQueryRequest): Promise<Types.ProjectQueryResponse> =>
    http.post('/afterlease/checkplan/project/query', data, { mock }),

  // 项目检查提交审批
  postProcessSubmit: (data: Types.ProcessSubmitRequest): Promise<Types.ProcessSubmitResponse> =>
    http.post('/afterlease/checkplan/project/process/submit', data, { mock }),
}

/* prettier-ignore-end */
