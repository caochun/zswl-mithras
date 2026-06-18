import processModifyRemarkApi from '@/api/common/approvalRemarkApi'

export default {
  postRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcontract'),
}
