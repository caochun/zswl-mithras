import processModifyRemarkApi from '@/api/approval/processModifyRemarkApi'

export default {
  postRemarkAll: (params) =>
    processModifyRemarkApi.postRemarkAll(params, 'processmodifyremarkallcontract'),
}
