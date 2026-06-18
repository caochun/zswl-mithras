import { http } from '@zswl/admin'

const postRemarkAll = (params, functionCode) =>
  http.post('/process/modify/remark/all', params, {
    headers: {
      functionCode,
    },
  })

export default {
  postEstablishRemarkAll: (params) =>
    postRemarkAll(params, 'processmodifyremarkallcreditestablish'),
  postReviewRemarkAll: (params) =>
    postRemarkAll(params, 'processmodifyremarkallcreditreview'),
}
