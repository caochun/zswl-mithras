import { makeAutoObservable } from '@zswl/admin'
import Api from '@/components/Project/ReviewDetail/api'
import LApi from './api'

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
