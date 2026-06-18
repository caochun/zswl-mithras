import processModifyRemarkApi from '@/api/common/approvalRemarkApi'

export default {
  postFundFinancingRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallfundfinancing'),
}
