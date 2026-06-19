import { http } from '@zswl/admin'

const createConfigApi = (code) => ({
  getList: (params) => http.post(`/kpi/parameterconfig/${code}/get`, params, {}),
  saveList: (params) => http.post(`/kpi/parameterconfig/${code}/save`, params, {}),
})

export const provisionRatioApi = createConfigApi('provisionradio')
export const deptProfitFinishRatioApi = createConfigApi('deptprofitfinishradio')
export const expenseRatioApi = createConfigApi('expenseradio')
export const profitAdjustApi = createConfigApi('profitadjust')
export const financialMarketDeptAssessApi = createConfigApi('financialmarketdeptassess')
export const financialMarketDeptRatioApi = createConfigApi('financialmarketdeptradio')
export const taxRateApi = createConfigApi('taxrate')
export const projectRatioApi = createConfigApi('projectradio')
export const businessDeptAssessApi = createConfigApi('businessdeptassess')
export const careerLevelApi = createConfigApi('careerlevel')
export const middleBackDeptAssessApi = createConfigApi('middlebackdeptassess')
