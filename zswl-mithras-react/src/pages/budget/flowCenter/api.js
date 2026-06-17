import bankFlowCapitalApi from '@/api/budget/flowCenter/bankFlowCapitalApi'
import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'

export default {
  manualRecord: flowCenterApi.postManualRecord,
  paymentList: flowCenterApi.postPaymentList,
  paymentSettleDetail: flowCenterApi.postSettleDetail,
  collectionList: flowCenterApi.postCollectionList,
  collectionManualRecord: flowCenterApi.postCollectionManualRecord,
  collectionSettleDetail: flowCenterApi.postCollectionSettleDetail,
  postPaymentCashflowList: bankFlowCapitalApi.postCashFlowList,
  postSubList: bankFlowCapitalApi.postSubList,
  postBankCenterRestore: bankFlowProcessingCenterApi.postBankCenterRestore,
}
