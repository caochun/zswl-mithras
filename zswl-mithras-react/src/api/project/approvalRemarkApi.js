import processModifyRemarkApi from '@/api/common/approvalRemarkApi'

export default {
  postReviewRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallprojreview'),
}
