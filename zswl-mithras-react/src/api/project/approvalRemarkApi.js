import processModifyRemarkApi from '@/api/approval/processModifyRemarkApi'

export default {
  postReviewRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallprojreview'),
}
