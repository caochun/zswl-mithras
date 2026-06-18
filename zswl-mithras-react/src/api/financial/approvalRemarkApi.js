import { http } from '@zswl/admin'

const postRemarkAll = (params, functionCode) =>
  http.post('/process/modify/remark/all', params, {
    headers: {
      functionCode,
    },
  })

export default {
  postFundFinancingRemarkAll: (params) =>
    postRemarkAll(params, 'processmodifyremarkallfundfinancing'),
}
