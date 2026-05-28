/* prettier-ignore-start */
import * as Types from './interface/baseInfoApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // \/liquidity\/test
  postLiquidityTest: (data: Types.LiquidityTestRequest): Promise<Types.LiquidityTestResponse> =>
    http.post('/liquidity/test', data, { mock }),

  // \/liquidity\/testSetting
  postLiquidityTestSetting: (
    data: Types.LiquidityTestSettingRequest
  ): Promise<Types.LiquidityTestSettingResponse> =>
    http.post('/liquidity/testSetting', data, { mock }),

  // 回款账户配置列表
  postAccountSettingList: (
    data: Types.AccountSettingListRequest
  ): Promise<Types.AccountSettingListResponse> =>
    http.post('/liquidity/accountSetting/list', data, { mock }),

  // 回款账户配置编辑
  postAccountSettingModify: (
    data: Types.AccountSettingModifyRequest
  ): Promise<Types.AccountSettingModifyResponse> =>
    http.post('/liquidity/accountSetting/modify', data, { mock }),

  // 回款账户配置账户还原
  postAccountSettingRestore: (
    data: Types.AccountSettingRestoreRequest
  ): Promise<Types.AccountSettingRestoreResponse> =>
    http.post('/liquidity/accountSetting/restore', data, { mock }),

  // 基础参数配置
  postSettingParameterBase: (
    data: Types.SettingParameterBaseRequest
  ): Promise<Types.SettingParameterBaseResponse> =>
    http.post('/liquidity/setting/parameterBase', data, { mock }),

  // 基础参数配置编辑
  postParameterBaseModify: (
    data: Types.ParameterBaseModifyRequest
  ): Promise<Types.ParameterBaseModifyResponse> =>
    http.post('/liquidity/setting/parameterBase/modify', data, { mock }),

  // 基础参数配置详情
  postParameterBaseDetail: (
    data: Types.ParameterBaseDetailRequest
  ): Promise<Types.ParameterBaseDetailResponse> =>
    http.post('/liquidity/setting/parameterBase/detail', data, { mock }),

  // 流动性指标配置
  postSettingParameterIndex: (
    data: Types.SettingParameterIndexRequest
  ): Promise<Types.SettingParameterIndexResponse> =>
    http.post('/liquidity/setting/parameterIndex', data, { mock }),

  // 流动性指标配置编辑
  postParameterIndexModify: (
    data: Types.ParameterIndexModifyRequest
  ): Promise<Types.ParameterIndexModifyResponse> =>
    http.post('/liquidity/setting/parameterIndex/modify', data, { mock }),

  // 流动性指标配置详情
  postParameterIndexDetail: (
    data: Types.ParameterIndexDetailRequest
  ): Promise<Types.ParameterIndexDetailResponse> =>
    http.post('/liquidity/setting/parameterIndex/detail', data, { mock }),

  // 账户余额明细列表
  postAccountBalanceList: (
    data: Types.AccountBalanceListRequest
  ): Promise<Types.AccountBalanceListResponse> =>
    http.post('/liquidity/accountBalance/list', data, { mock }),

  // 账户余额明细导入
  postAccountBalanceImport: (
    data: Types.AccountBalanceImportRequest
  ): Promise<Types.AccountBalanceImportResponse> =>
    http.post('/liquidity/accountBalance/import', data, { mock, type: 'upload' }),

  // 账户余额明细编辑
  postAccountBalanceModify: (
    data: Types.AccountBalanceModifyRequest
  ): Promise<Types.AccountBalanceModifyResponse> =>
    http.post('/liquidity/accountBalance/modify', data, { mock }),
}

/* prettier-ignore-end */
