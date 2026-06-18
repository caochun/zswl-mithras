import contractApi from '@/api/contract/baseInfo'

export default {
  postProjList: (params) => contractApi.postProjList(params, 'contractreviewquery_trackevent'),
}
