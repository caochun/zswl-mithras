/* prettier-ignore-start */
import { http } from '@zswl/admin'

export default {
  // 首页
  postCreate: (params) => http.post('/fund/financing/create', params),
  postList: (params) => http.post('/fund/financing/pagelist', params),
  postOrgList: (params) => http.post('/fund/organization/list', params),
  postDirectFinancingProductSelect: (params) =>
    http.post('/fund/direct/financing/product/select', params),
  postEffectList: (params) => http.post('/fund/credit/effect/list', params),
  postCancel: (params) => http.post('/fund/financing/close', params),
  postRemove: (params) => http.post('/fund/financing/delete', params),

  // 审批流
  postFlowEffect: (params) => http.post('/fund/financing/effect', params),
  postFlowCarryinterest: (params) => http.post('/fund/financing/carryinterest', params),
  postFlowLpr: (params) => http.post('/fund/financing/change/lpr/confirm', params),
  postFlowEarlysettle: (params) => http.post('/fund/financing/change/earlysettle/submit', params),
  postFlowCancel: (params) => http.post('/fund/financing/change/cancel', params),

  // 基本信息
  postBaseDetail: (params) => http.post('/fund/financing/baseinfo/detail', params),
  postBaseDetailCompare: (params) => http.post('/fund/financing/base/info/detail/compare', params),
  postBaseDetailModify: (params) => http.post('/fund/financing/baseinfo/modify', params),
  postCalcRemainingamount: (params) =>
    http.post('/fund/financing/baseinfo/guaranteeinfo/remainingamount', params),

  // 融资方案
  postPlanDetail: (params) => http.post('/fund/financing/plan/detail', params),
  postPlanDetailCompare: (params) => http.post('/fund/financing/plan/detail/compare', params),
  postPlanModify: (params) => http.post('/fund/financing/plan/modify', params),
  getLprLast: (params) => http.post('/basedata/lpr/latest', params),
  postPlanCalc: (params) => http.post('/fund/financing/plan/guaranteefee/calculate', params),

  // 质押
  postPledgeList: (params) => http.post('/fund/financing/pledge/list', params),
  postPledgeDetail: (params) => http.post('/fund/financing/pledge/detail', params),
  postPledgeListCompare: (params) => http.post('/fund/financing/pledge/list/compare', params),
  postPledgeCreate: (params) => http.post('/fund/financing/pledge/create', params),
  postPledgeModify: (params) => http.post('/fund/financing/pledge/modify', params),
  postPledgeDelete: (params) => http.post('/fund/financing/pledge/delete', params),
  postContractList: (params) => http.post('/fund/financing/pledge/contract/list', params),
  postContractSearch: (params) => http.post('/fund/financing/pledge/contract/search', params),
  postProjList: (params) => http.post('/fund/financing/pledge/proj/list', params),

  // 生效
  postEffectDetail: (params) => http.post('/fund/financing/carryinterest', params),
  postEffectModify: (params) => http.post('/fund/financing/carryinterest', params),
  postCarryintersetInfo: (params) =>
    http.post('/fund/financing/baseinfo/carryinterest/info', params),
  // 直融对方收款

  postDirectCollectionList: (params) =>
    http.post('/fund/direct/financing/collect/account/list', params),
  postDirectCollectionListCompare: (params) =>
    http.post('/fund/direct/financing/collect/account/list/compare', params),
  postDirectCollectionDelete: (params) =>
    http.post('/fund/direct/financing/collect/account/remove', params),
  postDirectCollectionCreate: (params) =>
    http.post('/fund/direct/financing/collect/account/add', params),
  postDirectCollectionModify: (params) =>
    http.post('/fund/direct/financing/collect/account/modify', params),

  // 对方收款
  postCollectionList: (params) => http.post('/fund/financing/collection/account/list', params),
  postCollectionListCompare: (params) =>
    http.post('/fund/financing/collection/account/list/compare', params),
  postCollectionDelete: (params) => http.post('/fund/financing/collection/account/delete', params),
  postCollectionCreate: (params) => http.post('/fund/financing/collection/account/create', params),
  postCollectionModify: (params) => http.post('/fund/financing/collection/account/modify', params),

  // 我方还款账户
  postPayAccountList: (params) => http.post('/fund/financing/pay/account/list', params),
  postPayAccountListCompare: (params) =>
    http.post('/fund/financing/pay/account/list/compare', params),
  postPayAccountDelete: (params) => http.post('/fund/financing/pay/account/delete', params),
  postPayAccountCreate: (params) => http.post('/fund/financing/pay/account/create', params),
  postPayAccountModify: (params) => http.post('/fund/financing/pay/account/modify', params),
  postPayAccountBackList: (params) => http.post('/fund/financing/pay/account/bank', params),
  postPayAccountBackInfoList: (params) =>
    http.post('/fund/financing/pay/account/bank/info', params),

  // 变更
  postLprDetail: (params) => http.post('/fund/financing/plan/lpr/get', params),
  postLprSave: (params) => http.post('/fund/financing/plan/lpr/change', params),
  postSettleDetail: (params) => http.post('/fund/financing/earlysettle/plan/get', params),
  postSettleSave: (params) => http.post('/fund/financing/earlysettle/plan/save', params),
  postChangeCheck: (params) => http.post('/fund/financing/change/precheck', params),

  // 实际
  getActualList: (params) => http.post('/fund/financing/repay/actual/list', params),
  getActualListCompare: (params) => http.post('/fund/financing/repay/actual/list/compare', params),
  postActualExportTable: (params) =>
    http.post('/fund/financing/repay/actual/export', params, {
      type: 'download',
      timeout: 0,
    }),
  postActualImportTable: (params) =>
    http.post('/fund/financing/repay/actual/import', params, {
      type: 'upload',
      timeout: 0,
    }),

  postActualLoanDateModify: (params) =>
    http.post('/fund/financing/baseinfo/actualloandate/modify', params),

  // 概算
  getEstimateList: (params) => http.post('/fund/financing/repay/estimate/list', params),
  getEstimateListCompare: (params) =>
    http.post('/fund/financing/repay/estimate/list/compare', params),
  postEstimateImportTable: (params) =>
    http.post('/fund/financing/repay/estimate/import', params, {
      type: 'upload',
      timeout: 0,
    }),

  postEstimateExportTable: (params) =>
    http.post('/fund/financing/repay/estimate/export', params, {
      type: 'download',
      timeout: 0,
    }),
  postEstimatePlanDateModify: (params) =>
    http.post('/fund/financing/baseinfo/planloandate/modify', params, {}),

  // 版本列表
  getVersionList: (params) => http.post('/fund/financing/version/list', params),
  getPreVersion: (params) => http.post('/fund/financing/compare/preVersion', params),
  // 直间融默认值
  getDefaultValues: (params) => http.post('/basedata/bankaccount/init', params),
  download: (params) =>
    http.get('/fund/financing/baseinfo/download', { params, type: 'download', timeout: 0 }),
  batchDownload: (params) =>
    http.post(
      '/fund/financing/baseinfo/batchDownload',
      {
        ...params,
        type: 'download',
        timeout: 0,
      },
      { type: 'download' }
    ),
}
