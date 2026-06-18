import processModifyRemarkApi from '@/api/approval/processModifyRemarkApi'

export default {
  postFundFinancingRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallfundfinancing'),
}
