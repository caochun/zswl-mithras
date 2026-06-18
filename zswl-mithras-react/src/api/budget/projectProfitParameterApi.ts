/* prettier-ignore-start */
import * as Types from './interface/projectProfitParameterApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 考核部门设置-保存
  postContractAssessDeptSave: (
    data: Types.ContractAssessDeptSaveRequest
  ): Promise<Types.ContractAssessDeptSaveResponse> =>
    http.post('/kpi/parameterconfig/contractAssessDept/save', data, { mock }),

  // 考核部门设置-详情
  postContractAssessDeptGet: (
    data: Types.ContractAssessDeptGetRequest
  ): Promise<Types.ContractAssessDeptGetResponse> =>
    http.post('/kpi/parameterconfig/contractAssessDept/get', data, { mock }),

  // 考核部门设置-删除
  postContractAssessDeptDelete: (
    data: Types.ContractAssessDeptGetRequest
  ): Promise<Types.ContractAssessDeptGetResponse> =>
    http.post('/kpi/parameterconfig/contractAssessDept/delete', data, { mock }),
}

/* prettier-ignore-end */
