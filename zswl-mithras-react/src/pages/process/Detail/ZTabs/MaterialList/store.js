import { makeAutoObservable } from '@zswl/admin'
import { ReviewDetailApi as Api } from '@/components/Project/ReviewProcessEntries'
import LApi from '@/api/process/detail/flowDetailApi'

class Store {
  constructor() {
    makeAutoObservable(this)
  }
  projectDataDetail

  getProjectDataDetail = async (businessKey, businessVersion) => {
    if (businessKey) {
      const res = await Api.postProjectDataDetail({
        projReviewId: businessKey,
        businessVersion: businessVersion,
      })
      if (res) {
        this.projectDataDetail = res
        return res
      }
    }
  }
  commentGuideLine = async (params) => {
    console.log(params)
    const res = await LApi.commentGuideLine(params)
    return res
  }
}
export default Store
