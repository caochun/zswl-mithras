import processModifyRemarkApi from '@/api/common/approvalRemarkApi'

export default {
  postEstablishRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcreditestablish'),
  postReviewRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcreditreview'),
}
