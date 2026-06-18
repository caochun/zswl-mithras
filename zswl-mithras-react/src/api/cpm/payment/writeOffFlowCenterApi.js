import bankFlowProcessingCenterApi from '@/api/budget/flowCenter/bankFlowProcessingCenterApi'
import flowCenterApi from '@/api/budget/flowCenter/flowCenterApi'

export default {
  postCenterList: bankFlowProcessingCenterApi.postCenterList,
  postCollectionFlowCenterBusinessPaymentManualCashFlowList:
    bankFlowProcessingCenterApi.postCollectionFlowCenterBusinessPaymentManualCashFlowList,
  postManualRecord: flowCenterApi.postManualRecord,
  postPullFlow: bankFlowProcessingCenterApi.postPullFlow,
  postWriteOff: bankFlowProcessingCenterApi.postWriteOff,
}
