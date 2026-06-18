import processModifyRemarkApi from '@/api/approval/processModifyRemarkApi'

export default {
  postEstablishRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcreditestablish'),
  postReviewRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcreditreview'),
}
